package rs.ac.bg.etf.pp1;

import java.awt.Label;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;

import org.apache.log4j.Logger;

import rs.ac.bg.etf.pp1.ast.*;
import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;
import rs.etf.pp1.symboltable.factory.SymbolTableFactory;

public class SemAnalyzer extends VisitorAdaptor {

	Logger log = Logger.getLogger(SemAnalyzer.class);
	
	private int maxSwitchDepth = 0;

	private boolean errorDetected = false;

	private Struct currentType = null;
	private Obj currentMethod = null;
	private boolean returnFound = false;
	private int mainPostoji = 0;

	private int forDepth = 0;
	private boolean insideFor = false;
	private boolean insideBreak = false;

	private Stack<List<Struct>> actParsStack = new Stack<>();
	private List<Struct> currentActPars = new ArrayList<>();

	private Struct boolType = Tab.find("bool").getType();

	int nVars = 0;

	private Struct currentEnumStruct = null;
	private int currentEnumAddr = 0;
	
	private int switchDepth = 0;
	private Stack<HashSet<Integer>> switchValuesStack = new Stack<>();
	
	private int currentSwitchDepth = 0;

	/* LOG MESSAGES */

	public void report_info(String message, SyntaxNode info) {
		StringBuilder msg = new StringBuilder("Na liniji ");
		int line = (info == null) ? 0 : info.getLine();
		if (line != 0)
			msg.append(line).append(": ").append(message);
		log.info(msg.toString());
	}

	public void report_error(String message, SyntaxNode info) {
		errorDetected = true;
		StringBuilder msg = new StringBuilder("\nSemanticka greska: ");
		msg.append(message);
		int line = (info == null) ? 0 : info.getLine();
		if (line != 0)
			msg.append(" na liniji ").append(line);
		log.error(msg.toString());

	}

	public boolean passed() {
		return !errorDetected;
	}

	/* SEMANTIC PASS CODE */

	@Override
	public void visit(ProgramName progName) {
		// Ne koristi currentMethod ovde!
		Obj progObj = Tab.insert(Obj.Prog, progName.getProgName(), Tab.noType);
		progName.obj = progObj;
		Tab.openScope();
	}

	@Override
	public void visit(Program program) {
		nVars = Tab.currentScope.getnVars();

		// Uzimamo obj koji smo sačuvali u ProgramName
		Obj progObj = program.getProgramName().obj;
		if (progObj != null) {
			Tab.chainLocalSymbols(progObj);
		}

		Tab.closeScope();
		if (mainPostoji < 1) {
			report_error("Ne postoji void main() metoda ", program);
		}
	}

	public void visit(Type type) {
		Obj typeNode = Tab.find(type.getTypeName());
		if (typeNode == Tab.noObj) {
			report_error("Nije pronadjen tip " + type.getTypeName() + " u tabeli simbola", type);
			type.struct = currentType = Tab.noType;
		} else if (typeNode.getKind() != Obj.Type) {
			report_error("Ime " + type.getTypeName() + " ne predstavlja tip", type);
			type.struct = currentType = Tab.noType;
		} else {
			type.struct = currentType = typeNode.getType();
		}
	}

	@Override
	public void visit(Constant_Number constant) {
		if (currentType.equals(Tab.intType)) {
			dodajKonstantu(constant.getConstName(), constant.getN1(), constant);
		} else {
			report_error("Tip konstante " + constant.getConstName() + " mora biti int", constant);
		}
	}

	@Override
	public void visit(Constant_Char constant) {
		if (currentType.equals(Tab.charType)) {
			dodajKonstantu(constant.getConstName(), constant.getC1(), constant);
		} else {
			report_error("Tip konstante " + constant.getConstName() + " mora biti char", constant);
		}
	}

	@Override
	public void visit(Constant_Bool constant) {
		if (currentType.equals(boolType)) {
			dodajKonstantu(constant.getConstName(), constant.getB1(), constant);
		} else {
			report_error("Tip konstante " + constant.getConstName() + " mora biti bool", constant);
		}
	}

	// Pomoćna metoda da ne ponavljaš kod
	private void dodajKonstantu(String name, int val, SyntaxNode node) {
		if (Tab.currentScope.findSymbol(name) == null) {
			Obj constantObj = Tab.insert(Obj.Con, name, currentType);
			constantObj.setAdr(val);
			report_info("Deklarisana konstanta " + name  , node);
		} else {
			report_error("Simbol " + name + " je vec deklarisan", node);
		}
	}

	/* Var promenljive */

	@Override
	public void visit(VarPart_Scalar s) {
		Obj obj = Tab.currentScope.findSymbol(s.getVarName());
		if (obj == null) {
			Obj c =Tab.insert(Obj.Var, s.getVarName(), currentType);
			if(currentMethod==null) {
				report_info("Deklarisana globalna promenljiva " + s.getVarName(), s);
			}
			
		} else {
			report_error("Simbol " + s.getVarName() + " je vec deklarisan", s);
		}
	}

	@Override
	public void visit(VarPart_Array s) {
		Obj obj = Tab.currentScope.findSymbol(s.getVarName());
		if (obj == null) {
			// KLJUČNA ISPRAVKA: Kreiramo nizovnu strukturu
			Struct arrayType = new Struct(Struct.Array, currentType);
			Obj c = Tab.insert(Obj.Var, s.getVarName(), arrayType);
			if(currentMethod==null) {
				report_info("Deklarisan globalni niz  " + s.getVarName(), s);
			}
			
		} else {
			report_error("Simbol " + s.getVarName() + " je vec deklarisan", s);
		}
	}

	// Enumi
	@Override
	public void visit(EnumName enumName) {
	    if (Tab.currentScope.findSymbol(enumName.getEnumName()) != null) {
	        report_error("Ime " + enumName.getEnumName() + " je vec zauzeto", enumName);
	        // Ne pravimo novi struct ako ime postoji, dodeljujemo noType da sprečimo pucanje
	        currentEnumStruct = Tab.noType; 
	    } else {
	        currentEnumStruct = new Struct(Struct.Enum);
	        Tab.insert(Obj.Type, enumName.getEnumName(), currentEnumStruct);
	        report_info("Deklarisan enum " + enumName.getEnumName(), enumName);
	    }
	    currentEnumAddr = 0;
	}

	@Override
	public void visit(EnumDecl enumDecl) {
		// Ovde samo "čistiš" nakon što je ceo enum obiđen
		
		currentEnumStruct = null;
	}

	@Override
	public void visit(EnumPart_NoValue enumPart) {
		// Proveravamo da li polje sa istim imenom već postoji SAMO unutar ovog enuma
		if (currentEnumStruct.getMembersTable().searchKey(enumPart.getFieldName()) != null) {
			report_error("Greska: Ime " + enumPart.getFieldName() + " je vec deklarisano unutar enuma", enumPart);
		} else {
			// Kreiramo Obj čvor tipa Con (konstanta), ali ga NE stavljamo u globalnu tabelu
			// (Tab.insert)
			// već ga samo dodajemo u lokalni MembersTable enuma.
			Obj enumField = new Obj(Obj.Con, enumPart.getFieldName(), currentEnumStruct);
			enumField.setAdr(currentEnumAddr++);
			currentEnumStruct.getMembersTable().insertKey(enumField);

			report_info("Deklarisano polje enuma " + enumPart.getFieldName() + " (automatska vrednost)", enumPart);
		}
	}

	@Override
	public void visit(EnumPart_Value enumPart) {
		if (currentEnumStruct.getMembersTable().searchKey(enumPart.getFieldName()) != null) {
			report_error("Greska: Ime " + enumPart.getFieldName() + " je vec deklarisano unutar enuma", enumPart);
		} else {
			currentEnumAddr = enumPart.getN1();
			Obj enumField = new Obj(Obj.Con, enumPart.getFieldName(), currentEnumStruct);
			enumField.setAdr(currentEnumAddr++);
			currentEnumStruct.getMembersTable().insertKey(enumField);

			report_info("Deklarisano polje enuma " + enumPart.getFieldName() + " (vrednost " + enumPart.getN1() + ")",
					enumPart);
		}
	}

	// METODE
	public static boolean proveriMain(MethodDecl method) {
		return (method.getFormPars() instanceof FormalParameters_Empty
				&& method.getMethodName() instanceof VoidMethodName
				// VoidMethodName je subklasa MethodName, koji dobijamo kad
				// nad MethodDecl objektom pozovem getMethodName
				// pa jos jednom getMethosName za izvedenu klasu
				&& "main".equals(((VoidMethodName) method.getMethodName()).getMethodName()));
	}

	@Override
	public void visit(TypeMethodName methodName) {
		// 1. Provera da li ime već postoji
		if (Tab.find(methodName.getMethodName()) != Tab.noObj) {
			report_error("Greska: Ime " + methodName.getMethodName() + " je vec deklarisano", methodName);
			methodName.obj = Tab.noObj;
		} else {
			// 2. Kreiranje Obj čvora za metodu
			// Tip metode je tip koji je naveden (npr. int ili Enum)
			methodName.obj = Tab.insert(Obj.Meth, methodName.getMethodName(), methodName.getType().struct);
			report_info("Obrada funkcije: " + methodName.getMethodName() , methodName);
		}

		// 3. Postavljanje globalnog pokazivača i otvaranje novog opsega (scope)
		currentMethod = methodName.obj;
		Tab.openScope();
	}

	@Override
	public void visit(VoidMethodName methodName) {
		if (Tab.find(methodName.getMethodName()) != Tab.noObj) {
			report_error("Ime " + methodName.getMethodName() + " je vec deklarisano", methodName);
			methodName.obj = Tab.noObj;
		} else {
			// VOID metoda ima Tab.noType
			methodName.obj = Tab.insert(Obj.Meth, methodName.getMethodName(), Tab.noType);
			report_info("Obrada void funkcije: " + methodName.getMethodName(), methodName);
		}

		currentMethod = methodName.obj;
		Tab.openScope();
	}

	@Override
	public void visit(MethodDecl meth) {
		// 1. Provera da li je ova metoda validan main
		if (proveriMain(meth)) {
			mainPostoji++;
		}

		// 2. Tvoj uslov: Odmah javi grešku ako je main deklarisan više puta
		if (mainPostoji > 1 && currentMethod.getName().equals("main")) {
			report_error("Greska: Metoda main je vec deklarisana", meth);
		}

		// 3. Provera da li metoda koja nije void ima return statement
		if (!returnFound && !currentMethod.getType().equals(Tab.noType)) {
			// Koristimo tvoj ispis sa imenom metode preko Obj čvora
			report_error("Metoda " + currentMethod.getName() + " nema return statement! ", meth);
		}

		// 4. Preveži locals (parametri + lokalne promenljive) u Obj čvor metode
		Tab.chainLocalSymbols(currentMethod);
		
		// Čuvamo max dubinu u adr polju Obj čvora metode pre nego što CodeGenerator tu upiše PC
	    if (currentMethod != null) {
	        currentMethod.setAdr(maxSwitchDepth);
	    }

		// 5. Zatvori otvoreni scope i izađi iz metode
		Tab.closeScope();

		// 6. OBAVEZNO: Anuliraj currentMethod i resetuj returnFound po izlasku
		currentMethod = null;
		returnFound = false;
		maxSwitchDepth=0;
		switchDepth=0;
	}

	// FORMALNI PARAMETRI

	@Override
	public void visit(FormParam_Scalar s) {
		Obj obj = Tab.currentScope.findSymbol(s.getParamName());
		if (obj == null) {
			/* (FormParam_Scalar) Type:paramType IDENT:paramName */
			obj = Tab.insert(Obj.Var, s.getParamName(), currentType);
			// mogu da koristim currentType jer desno od Type:paramType nije bila ni
			// jedna smena koja je taj isti Type mogla da mi sjebe

			// fpPos - uvek je 1 da oznaci da sam ja FORMALNI parametar metode
			obj.setFpPos(1);
			currentMethod.setLevel(currentMethod.getLevel() + 1);

			// u Level se za metode cuva broj lokalnih parametara
			// koliki da je uvecaj ga za jedan

			report_info("Deklarisan formalni parametar  " + s.getParamName() , s);

		} else {
			report_error("Simbol " + s.getParamName() + " je vec deklarisan ", s);
		}
	}

	@Override
	public void visit(FormParam_Array s) {
		
		Obj obj = Tab.currentScope.findSymbol(s.getParamName());
		if (obj == null) {
			/* (FormParam_Array) Type:paramType IDENT:paramName LBRACKET RBRACKET */
			/*
			 * NIZOVI imaju ovaj PROXY array cvor izmedju sebe i cvora tipa Var niz |___ |
			 * Struct Array |_____ | currentType
			 * 
			 */
			obj = Tab.insert(Obj.Var, s.getParamName(), new Struct(Struct.Array, currentType));
			// mogu da koristim currentType jer desno od Type:paramType nije bila ni
			// jedna smena koja je taj isti Type mogla da mi sjebe

			// fpPos - uvek je 1 da oznaci da sam ja FORMALNI parametar metode
			obj.setFpPos(1);
			currentMethod.setLevel(currentMethod.getLevel() + 1);

			// u Level se za metode cuva broj lokalnih parametara
			// koliki da je uvecaj ga za jedan

			report_info("Deklarisan formalni parametar (array)  ", s);

		} else {
			report_error("Simbol " + s.getParamName() + " je vec deklarisan ", s);
		}
	}

	@Override
	public void visit(DesignatorName designatorName) {
		Obj obj = Tab.find(designatorName.getDesignName());
		if (obj == Tab.noObj) {
			report_error("Simbol " + designatorName.getDesignName() + " nije pronadjen", designatorName);
		}
		designatorName.obj = obj; // OVO JE KLJUČNO
	}

	public void visit(Designator designator) {
		Obj root = designator.getDesignatorName().obj;
		DesignatorMore more = designator.getDesignatorMore();

		if (root == Tab.noObj) {
			designator.obj = Tab.noObj;
			return;
		}
		
		if (root.getKind() == Obj.Var && root.getFpPos() > 0) {
			// Proveravamo da li je promenljiva (Var) i da li je parametar (fpPos > 0)
		    if (root.getKind() == Obj.Var && root.getFpPos() > 0) {
		        // Moramo dodati root.toString() u poruku jer tvoj report_info to ne radi sam
		        report_info("Koriscenje formalnog parametra " + root.getName(), designator);
		    }
	    }

		if (more instanceof DesignatorMore_Empty) {
			designator.obj = root;
		} else if (more instanceof DesignatorMore_Field) {
			DesignatorMore_Field field = (DesignatorMore_Field) more;

			// Slučaj: ImeEnuma.Polje (npr. Tip1.A)
			// Proveravamo da li je koren zapravo tip podataka (Obj.Type) i da li je taj tip
			// Enum
			if (root.getKind() == Obj.Type && root.getType().getKind() == Struct.Enum) {
				// Tražimo polje isključivo u tabeli članova tog konkretnog enuma
				Obj found = root.getType().getMembersTable().searchKey(field.getFieldName());

				if (found == null) {
					report_error("Enum " + root.getName() + " nema polje " + field.getFieldName(), designator);
					designator.obj = Tab.noObj;
				} else {
					designator.obj = found;
					//report_info("Pristup polju " + field.getFieldName() + " enuma " + root.getName(), designator);
				}
			} else {
				report_error(root.getName() + " nije enum tip i nema polja.", designator);
				designator.obj = Tab.noObj;
			}
		} else if (more instanceof DesignatorMore_Array) {
			DesignatorMore_Array arrayPart = (DesignatorMore_Array) more;

			// 1. Provera da li je koren uopšte niz
			if (root.getType().getKind() != Struct.Array) {
				report_error("Simbol " + root.getName() + " nije niz", designator);
				designator.obj = Tab.noObj;
			} else {
				// 2. Provera da li je index unutar [] tipa int (ili enum)
				Struct indexType = arrayPart.getExpr().struct;
				if (indexType != Tab.intType && indexType.getKind() != Struct.Enum) {
					report_error("Indeks niza mora biti tipa int ( i enum je int)", designator);
				}

				// 3. Postavljam obj designatora na Obj.Elem
				// Tip ovog objekta je tip ELEMENTA niza (npr. ako je niz int[], tip je int)
				designator.obj = new Obj(Obj.Elem, root.getName() + "_elem", root.getType().getElemType());

				report_info("Pristup elementu niza " + root.getName() + "[Kind : " + designator.obj.getKind() + " ]", designator);
			}
		}
		// 4. DUŽINA NIZA (npr. a.len)
		else if (more instanceof DesignatorMore_Len) {
			if (root.getType().getKind() != Struct.Array) {
				report_error("Samo nizovi imaju .length", designator);
				designator.obj = Tab.noObj;
			} else {
				designator.obj = new Obj(Obj.Con, "len", Tab.intType);
				// ISPIS: "Citanje duzine niza a"
				//report_info("Citanje duzine niza " + root.getName() + "[Kind: " + designator.obj.getKind() + "]", designator);
			}
		}
	}

	@Override
	public void visit(Factor_Design fV) {
		fV.struct = fV.getDesignator().obj.getType();
	}

	// // Slučaj 1: x = 3 + globalnaFja(a, b);
	@Override
	public void visit(Factor_ActPars funcP) {
		Obj funcObj = funcP.getDesignator().obj;
		if (funcObj.getKind() != Obj.Meth) {
			report_error("Simbol " + funcObj.getName() + " nije funkcija!", funcP);
			funcP.struct = Tab.noType;
		} else {
			funcP.struct = funcObj.getType();
			
			// NIVO B: Provera da li je funkcija globalna (nivo 0)
	        if (funcObj.getLevel() == 0) {
	            
	        	report_info("Pronadjen poziv globalne funkcije " + funcObj.getName(), funcP);
	        }

			proveriStvarneParametre(funcObj, funcP);
		}
	}

	private void proveriStvarneParametre(Obj funcO, SyntaxNode fNode) {
	    // 1. Broj formalnih parametara čitamo iz 'level' polja metode
	    int expectedParamsCount = funcO.getLevel(); 
	    int actualParamsCount = currentActPars.size();

	    if (expectedParamsCount != actualParamsCount) {
	        report_error("Neodgovarajuci broj parametara pri pozivu metode " + funcO.getName(), fNode);
	        return; // Prekidamo jer nema smisla porediti tipove ako ih nema dovoljno
	    }

	    // 2. Uzimamo sve lokalne simbole (parametri su uvek na početku liste)
	    List<Obj> allLocals = new ArrayList<>(funcO.getLocalSymbols());

	    for (int i = 0; i < expectedParamsCount; i++) {
	        Struct actualParType = currentActPars.get(i);
	        Struct formalParType = allLocals.get(i).getType();

	        // 3. Provera specifičnih uslova sa slike
	        if (funcO.getName().equals("len")) {
	            // len(a); a mora biti niz ili znakovni niz
	            boolean isArray = (actualParType.getKind() == Struct.Array);
	            // Znakovni niz je obično niz char-ova, ali proveravamo kind za svaki slučaj
	            if (!isArray) {
	                report_error("Argument metode len mora biti niz ili znakovni niz!", fNode);
	            }
	        } 
	        else if (funcO.getName().equals("chr")) {
	            // chr(e); e mora biti izraz tipa int
	            if (!actualParType.equals(Tab.intType)) {
	                report_error("Argument metode chr mora biti tipa int!", fNode);
	            }
	        }
	        else if (funcO.getName().equals("ord")) {
	            // ord(c); c mora biti tipa char
	            if (!actualParType.equals(Tab.charType)) {
	                report_error("Argument metode ord mora biti tipa char!", fNode);
	            }
	        }
	        else {
	            // Standardna provera za sve ostale (korisničke) funkcije
	            if (!isAssignable(formalParType, actualParType)) {
	                report_error("Neodgovarajuci tip parametra na poziciji " + (i + 1) + 
	                             " pri pozivu metode " + funcO.getName(), fNode);
	            }
	        }
	    }
	}

	@Override
	public void visit(Factor_Num factorNumber) {
		factorNumber.struct = Tab.intType;
	}

	@Override
	public void visit(Factor_Char factorChar) {
		factorChar.struct = Tab.charType;
	}

	@Override
	public void visit(Factor_Bool factorBool) {
		factorBool.struct = boolType;
	}

	@Override
	public void visit(Factor_New_Array newArray) {
		Struct sizeType = newArray.getExpr().struct;
		if (sizeType != Tab.intType && sizeType.getKind() != Struct.Enum) {
			report_error("Velicina niza mora biti int (i enum polje je int)", newArray);
		}
		newArray.struct = new Struct(Struct.Array, newArray.getType().struct);
	}

	@Override
	public void visit(Factor_Paren factorExpr) {
		factorExpr.struct = factorExpr.getExpr().struct;
	}

	@Override
	public void visit(Term_Single fh) {
		fh.struct = fh.getFactor().struct;
	}

	// Term_Mul_Factor = Term MULOP FactorHelp
	public void visit(Term_Mul_Fact term) {
		Struct t = term.getTerm().struct;
		Struct f = term.getFactor().struct;

		// Dozvoljavamo množenje ako su oba int ILI enum
		boolean tValid = (t == Tab.intType || t.getKind() == Struct.Enum);
		boolean fValid = (f == Tab.intType || f.getKind() == Struct.Enum);

		if (tValid && fValid) {
			term.struct = Tab.intType; // Rezultat operacije je uvek int
		} else {
			report_error("Operandi operacije mnozenja moraju biti tipa int", term);
			term.struct = Tab.noType;
		}
	}

	@Override
	public void visit(BasicExpr_Term t) {
		t.struct = t.getTerm().struct;
	}

	@Override
	public void visit(BasicExpr_Addop expr) {
		Struct t1 = expr.getBasicExpr().struct;
		Struct t2 = expr.getTerm().struct;

		// Provera: Da li su operandi int ili bilo koji enum?
		boolean t1Valid = (t1 == Tab.intType || t1.getKind() == Struct.Enum);
		boolean t2Valid = (t2 == Tab.intType || t2.getKind() == Struct.Enum);

		if (t1Valid && t2Valid) {
			// REZULTAT JE UVEK INT
			// Čak i ako sabereš dva enuma, rezultat više nije enum nego broj
			expr.struct = Tab.intType;
		} else {
			report_error("Operandi za sabiranje moraju biti int", expr);
			expr.struct = Tab.noType;
		}
	}

	@Override
	public void visit(BasicExpr_Unary fu) {
		if (!fu.getTerm().struct.equals(Tab.intType)) {
			report_error("Term mora biti tipa int", fu);
			fu.struct = Tab.noType;
		} else {
			fu.struct = Tab.intType;
		}
	}

	// EXPR

	/* PROVERA ZA TERNARNI OPERATOR */

	@Override
	public void visit(CondFact_Basic cond) {
		cond.struct = cond.getBasicExpr().struct;

		// Uncomment ako se trazi da pojedinacan Expr bude bool
		// Struct type = cond.getExpr().struct;
		// if (!type.equals(SymbolTable.boolType)) {
		// report_error("Tip uslova mora biti bool", cond);
		// cond.struct = SymbolTable.noType;
		// } else {
		// cond.struct = type;
		// }
	}

	@Override
	public void visit(Expr_Ternary ternary) {
	    Struct condType = ternary.getCondition().struct;
	    Struct t1 = ternary.getExpr().struct;    // prva grana
	    Struct t2 = ternary.getExpr1().struct;   // druga grana

	    // 1. Provera uslova (bool)
	    if (!condType.equals(boolType)) {
	        report_error("Uslov ternarnog operatora mora biti tipa bool", ternary);
	    }

	    // 2. Provera kompatibilnosti grana
	    if (t1.equals(t2)) {
	        ternary.struct = t1;
	    } 
	    // Dozvoli miks int i enum (rezultat je int)
	    else if ((t1 == Tab.intType || t1.getKind() == Struct.Enum) && 
	             (t2 == Tab.intType || t2.getKind() == Struct.Enum)) {
	        ternary.struct = Tab.intType;
	    } 
	    else {
	        report_error("Izrazi u granama ternarnog operatora moraju biti kompatibilni", ternary);
	        ternary.struct = Tab.noType;
	    }
	}

	@Override
	public void visit(Expr_BasicExpr basic) {
		// Ovde je bitno: tvoja gramatika kaže Expr ::= (Expr_BasicExpr) Condition
		// Ako Condition vrati bool, onda je i Expr tipa bool
		basic.struct = basic.getCondition().struct;
	}

	// propagacija na gore: Condition -> ConTerm -> CondFact

	@Override
	public void visit(CondFact_Relop condFact) {
		Struct t1 = condFact.getBasicExpr().struct;
		Struct t2 = condFact.getBasicExpr1().struct;

		// 1. Provera kompatibilnosti (uključujući tvoja Enum pravila preko isAssignable
		// ili compatibleWith)
		// Za relacione operatore koristimo compatibleWith, ali pazimo na Enum/Int
		boolean bothIntOrEnum = (t1 == Tab.intType || t1.getKind() == Struct.Enum)
				&& (t2 == Tab.intType || t2.getKind() == Struct.Enum);

		if (!bothIntOrEnum && !t1.compatibleWith(t2)) {
			report_error("Tipovi u izrazu nisu kompatibilni za poredjenje", condFact);
			return;
		}

		// 2. Restrikcija za referencne tipove (nizovi i klase)
		if (t1.isRefType() || t2.isRefType()) {
			Relop op = condFact.getRelop();
			if (!(op instanceof Relop_Equal) && !(op instanceof Relop_NotEqual)) {
				report_error("Uz nizove i klase mogu se koristiti samo != i ==", condFact);
			}
		}

		condFact.struct = boolType;
	}

	@Override
	public void visit(CondFList_Fact f) {
		f.struct = f.getCondFact().struct;
	}

	@Override
	public void visit(CondFSingle_And c) {
		c.struct = c.getCondFact().struct;
	}

	// COND TERM
	@Override
	public void visit(CondTerm cond) {
		cond.struct = cond.getCondFactL().struct;
	}

	// COND TERM LIST
	@Override
	public void visit(Condition_Term cond) {
		cond.struct = cond.getCondTerm().struct;
	}

	@Override
	public void visit(Condition_OR cond) {
		cond.struct = cond.getCondTerm().struct;
	}

	@Override
	public void visit(Condition cond) {
		cond.struct = cond.getCondTermL().struct;
	}

	// ACT PARS
	@Override
	public void visit(ActParsBegin actParsBegin) {
		actParsStack.push(new ArrayList<>());
	}

	@Override
	public void visit(SingleActParam actParam) {
		actParsStack.peek().add(actParam.getExpr().struct);
	}

	@Override
	public void visit(ActParamList actParam) {
		actParsStack.peek().add(actParam.getExpr().struct);
	}

	@Override
	public void visit(ActParameters actParams) {
		currentActPars = actParsStack.pop();
	}

	//i kada imas i kada nemas parametre moras izbaciti  ArrayListu sa steka
	@Override
	public void visit(NoActParameters actParams) {
		currentActPars = actParsStack.pop();
	}

	/* DESIGNATOR STATEMENTI */
	@Override
	public void visit(DesignatorStatement_Assign assign) {
		Obj dstObj = assign.getDesignator().obj;
		if (dstObj == null || dstObj == Tab.noObj) {
			// Greška je već prijavljena u visit(DesignatorName) ili visit(Designator)
			// zastita od NullPointer Exception - a
			return;
		}
		//dodatna provera za enume
		int kind = dstObj.getKind();
	    if (kind != Obj.Var && kind != Obj.Elem && kind != Obj.Fld) {
	        report_error("Vrednost se moze dodeliti samo promenljivoj, elementu niza ili polju rekorda", assign);
	        return;
	    }
		Struct dst = assign.getDesignator().obj.getType();
		Struct src = assign.getExpr().struct;

		//System.out.println("Assign: dst=" + dstObj.getName() + ", srcStruct=" + assign.getExpr().struct);
		if (!isAssignable(dst, src)) {
			report_error("Tipovi nekompatibilni pri dodeli !", assign);
		}
	}

	// DesignatorStatement_Call
	// Slučaj 2: globalnaFja(a, b);
	@Override
	public void visit(DesignatorStatement_Call designFunc) {
		Obj funcObj = designFunc.getDesignator().obj;
		if (funcObj.getKind() != Obj.Meth) {
			report_error("Simbol " + funcObj.getName() + " nije funkcija", designFunc);
		} else {
			
			if (funcObj.getLevel() == 0) {
	            report_info("Pronadjen poziv globalne funkcije " + funcObj.getName(),designFunc);
	        }
			proveriStvarneParametre(funcObj, designFunc);
		}
	}

	@Override
	public void visit(DesignatorStatement_Inc designInc) {
		Obj deisgnatorObj = designInc.getDesignator().obj;
		if (!proveriDezignInt(deisgnatorObj)) {
			report_error("Promenljiva ili element niza mora biti tipa int za operaciju inc", designInc);
		}
	}

	@Override
	public void visit(DesignatorStatement_Dec designDec) {
		Obj deisgnatorObj = designDec.getDesignator().obj;
		if (!proveriDezignInt(deisgnatorObj)) {
			report_error("Promenljiva ili element niza mora biti tipa int za operaciju dec", designDec);
		}
	}

	private boolean proveriDezignInt(Obj desObj) {
		Struct type = desObj.getType();
		// Dozvoljavamo samo int skalar ili element int niza
		// Enumi su po definiciji Obj.Con (konstante) pa se nad njima ne radi ++
		return type.equals(Tab.intType) || (type.getKind() == Struct.Array && type.getElemType().equals(Tab.intType));
	}

	/* READ */
	@Override
	public void visit(Statement_Read readStmt) {
		Obj designatorObj = readStmt.getDesignator().obj;
		if (!designatorObj.getType().equals(Tab.intType) && !designatorObj.getType().equals(Tab.charType)
				&& !designatorObj.getType().equals(boolType)) {
			report_error("Metoda read kao parametar prima samo int, char i bool tipove", readStmt);
		} else if (designatorObj.getKind() != Obj.Var && designatorObj.getKind() != Obj.Elem
				&& designatorObj.getKind() != Obj.Fld) {
			report_error("Metoda read kao parametar prima samo promenljive, elemente niza ili polja", readStmt);
		}
	}
	
	/*otkomentarisati ako read moxe da cita polje enuma*/
	
//	@Override
//	public void visit(Statement_Read readStmt) {
//	    Obj designatorObj = readStmt.getDesignator().obj;
//	    Struct type = designatorObj.getType();
//	    
//	    // 1. Provera tipa (dozvoljavamo i Enum)
//	    boolean typeValid = type.equals(Tab.intType) || 
//	                        type.equals(Tab.charType) || 
//	                        type.equals(boolType) || 
//	                        type.getKind() == Struct.Enum;
//
//	    if (!typeValid) {
//	        report_error("Metoda read kao parametar prima samo int, char, bool ili enum tipove", readStmt);
//	    } 
//	    
//	    // 2. Provera da li je odredište upisivo (Var, Elem ili Fld)
//	    // Ovo će baciti grešku za Broj.NULA jer je on Obj.Con!
//	    else if (designatorObj.getKind() != Obj.Var && 
//	             designatorObj.getKind() != Obj.Elem && 
//	             designatorObj.getKind() != Obj.Fld) {
//	        report_error("Metoda read moze upisivati samo u promenljive, elemente niza ili polja", readStmt);
//	    }
//	}

	/* PRINT */
	@Override
	public void visit(Statement_Print printStmt) {
		visitPrintStatement(printStmt.getExpr().struct, printStmt);
	}

	@Override
	public void visit(Statement_PrintWidth printStmt) {
		visitPrintStatement(printStmt.getExpr().struct, printStmt);
	}

//	private void visitPrintStatement(Struct type, SyntaxNode printStmt) {
//		if (!type.equals(Tab.intType) && !type.equals(Tab.charType) && !type.equals(boolType)) {
//			report_error("Metoda print moze da se zove samo za tipove int, char ili bool", printStmt);
//		}
//	}
	
	private void visitPrintStatement(Struct type, SyntaxNode printStmt) {
	    // Proveravamo da li je int, char, bool ILI bilo koji Enum
	    boolean isValid = type.equals(Tab.intType) || 
	                      type.equals(Tab.charType) || 
	                      type.equals(boolType) || 
	                      type.getKind() == Struct.Enum;

	    if (!isValid) {
	        report_error("Metoda print moze da se zove samo za tipove int, char, bool", printStmt);
	    }
	}

	/* RETURN */
	@Override
	public void visit(Statement_ReturnExpr returnExpr) {
		if (currentMethod == null) {
			report_error("Return naredba se mora nalaziti unutar metode!", returnExpr);
			return;
		}

		Struct currMethRetType = currentMethod.getType();
		Struct actualRetType = returnExpr.getExpr().struct;

		// Provera kompatibilnosti povratne vrednosti (Enum -> Int je dozvoljeno)
		if (!isAssignable(currMethRetType, actualRetType)) {
			report_error("Tip izraza u return naredbi se ne slaze sa povratnim tipom metode "
					+ currentMethod.getName(), returnExpr);
		}
		returnFound = true;
	}

	// Ne zaboravi i obican return; bez izraza (za void metode)
	@Override
	public void visit(Statement_Return returnNoExpr) {
		if (currentMethod == null) {
			report_error("Return naredba se mora nalaziti unutar metode!", returnNoExpr);
			return;
		}
		if (currentMethod.getType() != Tab.noType) {
			report_error("Metoda " + currentMethod.getName() + " mora da vrati vrednost!", returnNoExpr);
		}
		returnFound = true;
	}

	/* BREAK */
	@Override
	public void visit(Statement_Break breakStmt) {
	    // Proveravamo samo dubinu. Ako smo u petlji, forDepth > 0.
	    // (Napomena: Ako tvoj jezik podržava switch, break može i tamo, 
	    // ali za MicroJava for je obično jedini context za break)
		if (forDepth == 0 && switchDepth == 0) {
		    report_error("Break iskaz se moze koristiti samo unutar for petlje ili switch naredbe", breakStmt);
		}
	}

	/* CONTINUE */
	@Override
	public void visit(Statement_Continue continueStmt) {
	    if (forDepth == 0) {
	        report_error("Continue iskaz se moze koristiti samo unutar for petlje", continueStmt);
	    }
	}

	/* IF I IF-ELSE PROVERE */

	@Override
	public void visit(Statement_If ifStmt) {
		Struct condType = ifStmt.getCondition().struct;
	    if (condType == null) {
	        // Ne prijavljuj novu gresku jer je verovatno vec prijavljena u CondFact
	        return; 
	    }
		if (!ifStmt.getCondition().struct.equals(boolType)) {
			report_error("Uslov u if naredbi mora biti tipa bool", ifStmt);
		}
	}

	@Override
	public void visit(Statement_IfElse ifElseStmt) {
		if (!ifElseStmt.getCondition().struct.equals(boolType)) {
			report_error("Uslov u if naredbi mora biti tipa bool", ifElseStmt);
		}
	}

	/* FOR NAREDBA */
	@Override
	public void visit(ForTerm forTerm) {
	    forDepth++; 
	}

	@Override
	public void visit(Statement_For forStmt) {
	    forDepth--;

	    // Provera opcionog uslova
	    if (forStmt.getOptCondition() instanceof OptCondition_Yes) {
	        OptCondition_Yes optCond = (OptCondition_Yes) forStmt.getOptCondition();
	        if (optCond.getCondition().struct != boolType) {
	            report_error("Uslov u for petlji mora biti tipa bool", forStmt);
	        }
	    }
	}

	/* pomocna metoda za ispitivsnje Enuma */
	// Ključna metoda za proveru kompatibilnosti tipova
	private boolean isAssignable(Struct dst, Struct src) {
		if (dst == null || src == null)
			return false; // ZAŠTITA OD NULL
		if (dst == Tab.noType || src == Tab.noType)
			return true; // Da ne gomilamo greške

		if (dst.equals(src))
			return true;
		if (dst == Tab.intType && src.getKind() == Struct.Enum)
			return true;
		if (dst.getKind() == Struct.Enum && src == Tab.intType)
			return false;

		return src.assignableTo(dst);
	}
	
	/* SWITCH */
	@Override
	public void visit(SwitchStart switchStart) {
	    switchDepth++;
	    if(switchDepth>maxSwitchDepth) maxSwitchDepth = switchDepth;
	    switchValuesStack.push(new HashSet<>());
	}
	
	@Override
	public void visit(Statement_Switch switchStmt) {
	    // Ovde proveravamo tip izraza switch(Expr)
	    Struct exprType = switchStmt.getExpr().struct;
	    if (exprType != Tab.intType && exprType.getKind() != Struct.Enum) {
	        report_error("Izraz u switch naredbi mora biti tipa int", switchStmt);
	    }
	    
	    // Čišćenje steka pri izlasku
	    switchDepth--;
	    if (!switchValuesStack.isEmpty()) {
	        switchValuesStack.pop();
	    }
	}
	
	@Override
	public void visit(CaseValue_Number caseNum) {
	    // Креирамо синтетички Obj.Con само за потребе генерације кода/дупликат провере
	    Obj o = new Obj(Obj.Con, "#case_num", Tab.intType);
	    o.setAdr(caseNum.getN1());
	    caseNum.obj = o;                // <-- САД имаш Obj
	    // (опционо) report_info(...) ако желиш
	}

	@Override
	public void visit(CaseValue_Design cv) {
		Obj o = cv.getDesignator().obj;
	    if(o.getKind()!=Obj.Con) {
	    	report_error("Case vrednost mora biti constanta", cv);
	    	cv.obj = Tab.noObj;
	    	return;
	    }  
	    boolean isInt = (o.getType()==Tab.intType);
	    boolean isEnum =( o.getType().getKind()==Struct.Enum);
	    
	    if(!isInt && !isEnum) {
	    	report_error("Case vrednost mora biti tipa int!", cv);
	    	cv.obj = Tab.noObj;
	    	return;
	    }
	    cv.obj = o;
	}
	
	
	
	
	@Override
	public void visit(CaseList_More caseMore) {
	    Obj o = caseMore.getCaseValue().obj;
	    if (o == null || o == Tab.noObj || o.getKind() != Obj.Con) {
	        // Greska je već prijavljena u posetama CaseValue_*
	        return;
	    }
	    
	 // Dozvoljavamo i int i enum tipove
	    boolean isValidType = (o.getType() == Tab.intType || o.getType().getKind() == Struct.Enum);

	    if (!isValidType) {
	        // Ova poruka se verovatno neće ni videti jer CaseValue to već filtrira, 
	        // ali je dobra kao "backstop" provera.
	        return;
	    }
	    int value = o.getAdr();

	    if (!switchValuesStack.isEmpty()) {
	        HashSet<Integer> currentSet = switchValuesStack.peek();
	        if (currentSet.contains(value)) {
	            report_error("Duplirana case vrednost: " + value, caseMore);
	        } else {
	            currentSet.add(value);
	        }
	    }
	}
	
	
	// Dodaj ovo na dno klase SemAnalyzer
	private String formatObj(Obj obj) {
	    if (obj == null) return "null";
	    StringBuilder sb = new StringBuilder(" ");
	    
	    // Ovo simulira format iz Tab.dump()
	    // Kind. Name: Type, Addr, Level
	    switch (obj.getKind()) {
	        case Obj.Con:  sb.append("Con "); break;
	        case Obj.Var:  sb.append("Var "); break;
	        case Obj.Type: sb.append("Type "); break;
	        case Obj.Meth: sb.append("Meth "); break;
	        case Obj.Prog: sb.append("Prog "); break;
	    }
	    
	    sb.append(obj.getName()).append(": ");
	    
	    // Za tip koristimo getKind() strukture ako je specifičan (Enum, Array...)
	    if (obj.getType() == Tab.intType) sb.append("int");
	    else if (obj.getType() == Tab.charType) sb.append("char");
	    else if (obj.getType().getKind() == Struct.Enum) sb.append("Enum");
	    else if (obj.getType().getKind() == Struct.Array) sb.append("Arr of ").append(obj.getType().getElemType() == Tab.intType ? "int" : "char");
	    else sb.append("notype");
	    
	    sb.append(", ").append(obj.getAdr()).append(", ").append(obj.getLevel());
	    
	    return sb.toString();
	}

}
