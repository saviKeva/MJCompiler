// generated with ast extension for cup
// version 0.8
// 16/1/2026 0:17:11


package rs.ac.bg.etf.pp1.ast;

public class CondFact_Relop extends CondFact {

    private BasicExpr BasicExpr;
    private Relop Relop;
    private BasicExpr BasicExpr1;

    public CondFact_Relop (BasicExpr BasicExpr, Relop Relop, BasicExpr BasicExpr1) {
        this.BasicExpr=BasicExpr;
        if(BasicExpr!=null) BasicExpr.setParent(this);
        this.Relop=Relop;
        if(Relop!=null) Relop.setParent(this);
        this.BasicExpr1=BasicExpr1;
        if(BasicExpr1!=null) BasicExpr1.setParent(this);
    }

    public BasicExpr getBasicExpr() {
        return BasicExpr;
    }

    public void setBasicExpr(BasicExpr BasicExpr) {
        this.BasicExpr=BasicExpr;
    }

    public Relop getRelop() {
        return Relop;
    }

    public void setRelop(Relop Relop) {
        this.Relop=Relop;
    }

    public BasicExpr getBasicExpr1() {
        return BasicExpr1;
    }

    public void setBasicExpr1(BasicExpr BasicExpr1) {
        this.BasicExpr1=BasicExpr1;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(BasicExpr!=null) BasicExpr.accept(visitor);
        if(Relop!=null) Relop.accept(visitor);
        if(BasicExpr1!=null) BasicExpr1.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(BasicExpr!=null) BasicExpr.traverseTopDown(visitor);
        if(Relop!=null) Relop.traverseTopDown(visitor);
        if(BasicExpr1!=null) BasicExpr1.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(BasicExpr!=null) BasicExpr.traverseBottomUp(visitor);
        if(Relop!=null) Relop.traverseBottomUp(visitor);
        if(BasicExpr1!=null) BasicExpr1.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("CondFact_Relop(\n");

        if(BasicExpr!=null)
            buffer.append(BasicExpr.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(Relop!=null)
            buffer.append(Relop.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(BasicExpr1!=null)
            buffer.append(BasicExpr1.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [CondFact_Relop]");
        return buffer.toString();
    }
}
