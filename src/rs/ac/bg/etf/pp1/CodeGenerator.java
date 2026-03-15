package rs.ac.bg.etf.pp1;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import rs.ac.bg.etf.pp1.ast.*;
import rs.etf.pp1.mj.runtime.Code;
import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;

public class CodeGenerator extends VisitorAdaptor {

	private int mainPC;
	private ArrayList<Integer> falsec = new ArrayList<Integer>();
	private ArrayList<Integer> elsecs = new ArrayList<Integer>();
	private ArrayList<Integer> condfacts = new ArrayList<Integer>();
	private ArrayList<Integer> conds = new ArrayList<Integer>();
	private final Struct boolType = Tab.find("bool").getType();
	private Obj currentMethod = null;

	public int getkkmainPc() {
		return mainPC;
	}

	public CodeGenerator() {
		generateChrAndOrd();
		generateLen();
	}

	// UGRADJENE METODE
	private void generateChrAndOrd() {
		visitMethdName(Tab.chrObj);
		Tab.ordObj.setAdr(Tab.chrObj.getAdr());
		Code.put(Code.load_n);
		Code.put(Code.exit);
		Code.put(Code.return_);
	}

	private void generateLen() {
		visitMethdName(Tab.lenObj);
		Code.put(Code.load_n);
		Code.put(Code.arraylength);
		Code.put(Code.exit);
		Code.put(Code.return_);
	}

	// METODI
	@Override
	public void visit(TypeMethodName m) {
		visitMethdName(m.obj);
	}

	@Override
	public void visit(VoidMethodName m) {
		visitMethdName(m.obj);
	}

	private void visitMethdName(Obj methodObj) {
		currentMethod = methodObj;
		switchLevel = 0;

		// 1. Pročitaj dubinu switch-a iz adr polja
		int maxSwitchDepth = methodObj.getAdr();

		// 2. Postavi PC adresu (ovo prebriše dubinu u tabeli simbola)
		methodObj.setAdr(Code.pc);

		int fpCount = methodObj.getLevel();

		// 3. Ručno izbroj nVars (formalni + lokalni)
		int nVars = methodObj.getLocalSymbols().size();

		// 4. scopeSize = svi deklarisani simboli + prostor za switch registre
		int scopeSize = nVars + maxSwitchDepth;

		Code.put(Code.enter);
		Code.put(fpCount);
		Code.put(scopeSize);
	}

	@Override
	public void visit(MethodDecl meth) {
		if (SemAnalyzer.proveriMain(meth)) {
			mainPC = meth.getMethodName().obj.getAdr();
			Code.mainPc = mainPC;
		}
		Code.put(Code.exit);
		Code.put(Code.return_);
		currentMethod = null;
	}

	// DESIGNATOR DEO
	@Override
	public void visit(DesignatorName name) {
		Obj base = name.obj;
		if (name.getParent() instanceof Designator) {
			Designator d = (Designator) name.getParent();
			if (d.getDesignatorMore() instanceof DesignatorMore_Array) {
				Code.load(base);
			} else if (d.getDesignatorMore() instanceof DesignatorMore_Len) {
				Code.load(base);
			}
		}
	}

	@Override
	public void visit(DesignatorMore_Len m) {
		Code.put(Code.arraylength);
	}

	@Override
	public void visit(DesignatorStatement_Assign ds) {
		Code.store(ds.getDesignator().obj);
	}

	private void posetiIncDec(Obj designatorObj, boolean inc) {
		if (designatorObj.getKind() == Obj.Elem) {
			Code.put(Code.dup2);
		}
		Code.load(designatorObj);
		Code.loadConst(1);
		Code.put(inc ? Code.add : Code.sub);
		Code.store(designatorObj);
	}

	public void visit(DesignatorStatement_Inc designInc) {
		posetiIncDec(designInc.getDesignator().obj, true);
	}

	public void visit(DesignatorStatement_Dec designDec) {
		posetiIncDec(designDec.getDesignator().obj, false);
	}

	@Override
	public void visit(DesignatorStatement_Call func) {
		Obj funcObj = func.getDesignator().obj;
		int offset = funcObj.getAdr() - Code.pc;
		Code.put(Code.call);
		Code.put2(offset);
		if (!funcObj.getType().equals(Tab.noType)) {
			Code.put(Code.pop);
		}
	}

	@Override
	public void visit(Factor_Design f) {
		Designator d = f.getDesignator();
		if (d.getDesignatorMore() instanceof DesignatorMore_Len) {
			return;
		}
		Code.load(d.obj);
	}

	@Override
	public void visit(Factor_ActPars func) {
		int offset = func.getDesignator().obj.getAdr() - Code.pc;
		Code.put(Code.call);
		Code.put2(offset);
	}

	@Override
	public void visit(Factor_Num factorNumber) {
		Code.loadConst(factorNumber.getN1());
	}

	@Override
	public void visit(Factor_Char factorChar) {
		Code.loadConst(factorChar.getC1());
	}

	@Override
	public void visit(Factor_Bool factorBool) {
		Code.loadConst((factorBool.getB1()) > 0 ? 1 : 0);
	}

	@Override
	public void visit(Factor_New_Array fN) {
		Struct type = fN.getType().struct;
		Code.put(Code.newarray);
		Code.put(type.equals(Tab.charType) ? 0 : 1);
	}

	@Override
	public void visit(Term_Mul_Fact term) {
		if (term.getMulop() instanceof Mulop_Mul)
			Code.put(Code.mul);
		else if (term.getMulop() instanceof Mulop_Div)
			Code.put(Code.div);
		else if (term.getMulop() instanceof Mulop_Rem)
			Code.put(Code.rem);
	}

	@Override
	public void visit(BasicExpr_Addop a) {
		if (a.getAddop() instanceof Addop_Plus)
			Code.put(Code.add);
		else if (a.getAddop() instanceof Addop_Minus)
			Code.put(Code.sub);
	}

	@Override
	public void visit(BasicExpr_Unary u) {
		Code.put(Code.neg);
	}

	/* RELOP DEO */
	private int relops(Relop ro) {
		if (ro instanceof Relop_Greater)
			return Code.gt;
		if (ro instanceof Relop_GreaterEq)
			return Code.ge;
		if (ro instanceof Relop_Less)
			return Code.lt;
		if (ro instanceof Relop_LessEq)
			return Code.le;
		if (ro instanceof Relop_Equal)
			return Code.eq;
		if (ro instanceof Relop_NotEqual)
			return Code.ne;
		return -1;
	}

//	@Override
//	public void visit(CondFact_Basic c) {
//		if (c.getBasicExpr().struct.equals(boolType)) {
//			Code.loadConst(1);
//			Code.putFalseJump(Code.eq, 0);
//			thencs.add(Code.pc - 2); // Stavljamo u thencs da bi Ternary video
//		}
//	}

	@Override
	public void visit(CondFact_Basic c) {
		if (c.getBasicExpr().struct.equals(boolType)) {
			// Umesto ručnog dodavanja const_1 i jne, koristite standardni false jump
			// koji poredi vrednost na vrhu steka (koja je već 0 ili 1) sa nulom.
			Code.loadConst(0);
			Code.putFalseJump(Code.ne, 0);
			condfacts.add(Code.pc - 2);
		}
	}

	public void visit(CondFact_Relop c) {
		Code.putFalseJump(relops(c.getRelop()), 0);
		condfacts.add(Code.pc - 2);
	}

	@Override
	public void visit(CondTerm c) {
		// AND-zatvaranje: samo ako je bilo CondFact-ova koji su emitovali skokove
		if (!condfacts.isEmpty()) {
			Code.putJump(0);
			conds.add(Code.pc - 2);
			while (!condfacts.isEmpty())
				Code.fixup(condfacts.remove(condfacts.size() - 1));
		}
		// ako nema condfacts → ništa
	}

	@Override
	public void visit(Condition c) {
		// OR-zatvaranje: samo ako je bilo CondTerm-ova koji su emitovali jump
		if (!conds.isEmpty()) {
			Code.putJump(0);
			falsec.add(Code.pc - 2);
			while (!conds.isEmpty())
				Code.fixup(conds.remove(conds.size() - 1));
		}
		// ako nema conds → ništa
	}

	public void visit(Statement_If stmt) {
		if (!falsec.isEmpty())
			Code.fixup(falsec.remove(falsec.size() - 1));
	}

	public void visit(Statement_IfElse stmt) {
		if (!elsecs.isEmpty())
			Code.fixup(elsecs.remove(elsecs.size() - 1));
	}

	public void visit(Else e) {
		Code.putJump(0);
		elsecs.add(Code.pc - 2);
		if (!falsec.isEmpty())
			Code.fixup(falsec.remove(falsec.size() - 1));
	}

	/* TERNARNI OPERATOR */
	private final Stack<Integer> ternaryElseFix = new Stack<>();
	private final Stack<Integer> ternaryAfterFix = new Stack<>();

	@Override
	public void visit(TernaryStart t) {
		if (!falsec.isEmpty()) {
			int elseFix = falsec.remove(falsec.size() - 1);
			ternaryElseFix.push(elseFix);
		}
	}

	@Override
	public void visit(TernaryMiddle t) {
		Code.putJump(0);
		ternaryAfterFix.push(Code.pc - 2);
		if (!ternaryElseFix.isEmpty())
			Code.fixup(ternaryElseFix.pop());
	}

	@Override
	public void visit(Expr_Ternary e) {
		if (!ternaryAfterFix.isEmpty())
			Code.fixup(ternaryAfterFix.pop());
	}

//	@Override
//	public void visit(Expr_BasicExpr e) {
//		if (e.getCondition().struct.equals(boolType)) {
//			Code.loadConst(1);
//			Code.putJump(0);
//			int afterTrue = Code.pc - 2;
//			if (!thencs.isEmpty())
//				Code.fixup(thencs.remove(thencs.size() - 1));
//			Code.loadConst(0);
//			Code.fixup(afterTrue);
//		}
//	}

	@Override
	public void visit(Expr_BasicExpr e) {
		// Ako je Condition zapravo samo logički izraz koji treba da postane 0 ili 1
		// (npr. t = a < b;), tek tada radimo materijalizaciju.
		if (e.getParent() instanceof Expr_Ternary)
			return;
		if (e.getCondition().struct.equals(boolType)) {
			// Ako falsec nije prazan, znači da imamo skokove koje treba pretvoriti u 0/1
			if (!falsec.isEmpty()) {
				Code.putJump(0);
				int adrAfterTrue = Code.pc - 2;
				while (!falsec.isEmpty()) {
					Code.fixup(falsec.remove(falsec.size() - 1));
				}
				Code.loadConst(0); // False grana
				Code.putJump(0);
				int adrAfterAll = Code.pc - 2;
				Code.fixup(adrAfterTrue);
				Code.loadConst(1); // True grana
				Code.fixup(adrAfterAll);
			}
		}
	}

	/* FOR PETLJA */
	private Stack<List<Integer>> forSkipStack = new Stack<>();
	private Stack<List<Integer>> forBreakStack = new Stack<>();
	private Stack<Integer> forGoStack = new Stack<>();
	private Stack<Integer> jumpFC = new Stack<>();
	private Stack<Integer> jumpFA = new Stack<>();

	public void visit(ForTerm forTerm) {
		forSkipStack.push(new ArrayList<Integer>());
		forBreakStack.push(new ArrayList<Integer>());
	}

	public void visit(ForInit_Yes f) {
		jumpFC.push(Code.pc);
	}

	public void visit(ForInit_No f) {
		jumpFC.push(Code.pc);
	}

	@Override
	public void visit(OptCondition_Yes optCond) {
		List<Integer> currentSkips = forSkipStack.peek();
		while (!falsec.isEmpty()) {
			currentSkips.add(falsec.remove(falsec.size() - 1));
		}
		Code.putJump(0);
		forGoStack.push(Code.pc - 2);
		jumpFA.push(Code.pc);
	}

	@Override
	public void visit(OptCondition_No optCond) {
		Code.putJump(0);
		forGoStack.push(Code.pc - 2);
		jumpFA.push(Code.pc);
	}

	public void visit(ForAfter_Yes forAfter) {
		Code.putJump(jumpFC.pop());
		Code.fixup(forGoStack.pop());
	}

	public void visit(ForAfter_No forAfter) {
		Code.putJump(jumpFC.pop());
		Code.fixup(forGoStack.pop());
	}

	public void visit(Statement_For stmt) {
		Code.putJump(jumpFA.pop());
		for (int addr : forSkipStack.pop())
			Code.fixup(addr);
		for (int addr : forBreakStack.pop())
			Code.fixup(addr);
	}

	public void visit(Statement_Continue stmt) {
		if (!jumpFA.isEmpty())
			Code.putJump(jumpFA.peek());
	}

	@Override
	public void visit(Statement_Return st) {
		Code.put(Code.exit);
		Code.put(Code.return_);
	}

	@Override
	public void visit(Statement_ReturnExpr st) {
		Code.put(Code.exit);
		Code.put(Code.return_);
	}

	@Override
	public void visit(Statement_Read readStmt) {
		Obj designatorObj = readStmt.getDesignator().obj;
		Code.put(designatorObj.getType().equals(Tab.charType) ? Code.bread : Code.read);
		Code.store(designatorObj);
	}

	@Override
	public void visit(Statement_Print pstmt) {
		posetiPrintStmt(0, pstmt.getExpr().struct);
	}

	@Override
	public void visit(Statement_PrintWidth pstmt) {
		posetiPrintStmt(pstmt.getN2(), pstmt.getExpr().struct);
	}

	private void posetiPrintStmt(int numCon, Struct type) {
		Code.loadConst(numCon);
		Code.put(type.equals(Tab.charType) ? Code.bprint : Code.print);
	}

	/* SWITCH CASE */
	private static class SwitchCtx {
		List<Integer> nextCaseFixups = new ArrayList<>();
		List<Integer> breakFixups = new ArrayList<>();
		int varAddr;
	}
	/*
	 * Posto mogu imati switch unutar switch-a, svaki nivo dobija svoj SwitchCtx
	 * objekat sa svojim listama skokova
	 */

	private Stack<SwitchCtx> switchStack = new Stack<>();
	private int switchLevel = 0;

	@Override
	public void visit(SwitchStart s) {
		/*
		 * Kada udjem u Switch(x) vrednost X je vec na steku i treba mi slobodno mesto u
		 * mem da sakrijem tu vrednost
		 */
		switchLevel++;
		SwitchCtx ctx = new SwitchCtx();
		int localCount = 0;
		if (currentMethod != null) {
			for (Obj o : currentMethod.getLocalSymbols())
				localCount++;
		}
		ctx.varAddr = localCount + switchLevel; // treba mi slobodno mesto u mem da sakrijem tu vrednost
		Code.put(Code.store); // opcode za store
		Code.put(ctx.varAddr);// Uzimaš X sa steka i upisuješ ga na tu adresu
		switchStack.push(ctx);
	}

	private void beginCaseTestWithIntValue(int caseVal) {
		SwitchCtx ctx = switchStack.peek();
		Code.put(Code.load); // Vraćaš sakriveni X na stek
		Code.put(ctx.varAddr);
		Code.loadConst(caseVal);// stavlja na stek vrednost case-a
		Code.putFalseJump(Code.eq, 0); // Ako X NIJE jednako caseVal, skoči!
		ctx.nextCaseFixups.add(Code.pc - 2); // Pamtiš adresu tog skoka. Ako test ne prođe, program beži na sledeći case
	}

	@Override
	public void visit(CaseValue_Number c) {
		SwitchCtx ctx = switchStack.peek();
		int jumpToBody = -1;
		if (!ctx.nextCaseFixups.isEmpty()) {
			/*
			 * case 1: // (Prethodni case) print('1'); case 2: // (Trenutni case u kojem se
			 * nalazi tvoj vizitor) print('2'); Mi ne želimo da on ponovo radi test (jer on
			 * već "propada" unutra).
			 * 
			 * Zato mu kažemo: "Preskoči test za dvojku i idi pravo na print('2')!".
			 */
			Code.putJump(0); /*ti koji si izvrsio case 1 a nemas break
			skoci preko testa za x==2 za case 2, ne treba da proveravas vec samo
			da utones u telo za case2 
			*/
			jumpToBody = Code.pc - 2;
			/*Ti koji nisi bio jedinica, sleti OVDE. Sad ćemo da vidimo da li si dvojka*/
			Code.fixup(ctx.nextCaseFixups.remove(ctx.nextCaseFixups.size() - 1));
		}
		beginCaseTestWithIntValue(c.getN1());
		/*Sada pišeš instrukcije: load X, loadConst 2, if_ne skoči_na_sledeći.

Ovde Grupacija B proverava svoju sudbinu. Ako su 2, nastavljaju peške nadole. Ako nisu, beže dalje.*/
		if (jumpToBody != -1)
			Code.fixup(jumpToBody);
		/*Sada zoveš onu Grupaciju A (koja je preskočila test u Koraku 1) da sleti ovde.

Rezultat: I oni koji su "propali" odozgo, i oni koji su upravo "prošli test" za dvojku, sada stoje na početku tela koda za case 2.*/
	}

	@Override
	public void visit(Statement_Switch sw) {
		SwitchCtx ctx = switchStack.pop();
		for (int addr : ctx.breakFixups)
			Code.fixup(addr);
		if (!ctx.nextCaseFixups.isEmpty())
			Code.fixup(ctx.nextCaseFixups.remove(ctx.nextCaseFixups.size() - 1));
		switchLevel--;
	}

	@Override
	public void visit(Statement_Break stmt) {
		Code.putJump(0);
		int jumpAddr = Code.pc - 2;
		if (!switchStack.isEmpty())
			switchStack.peek().breakFixups.add(jumpAddr);
		else if (!forBreakStack.isEmpty())
			forBreakStack.peek().add(jumpAddr);
	}
}