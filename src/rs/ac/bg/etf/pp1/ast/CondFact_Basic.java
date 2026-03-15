// generated with ast extension for cup
// version 0.8
// 16/1/2026 0:17:11


package rs.ac.bg.etf.pp1.ast;

public class CondFact_Basic extends CondFact {

    private BasicExpr BasicExpr;

    public CondFact_Basic (BasicExpr BasicExpr) {
        this.BasicExpr=BasicExpr;
        if(BasicExpr!=null) BasicExpr.setParent(this);
    }

    public BasicExpr getBasicExpr() {
        return BasicExpr;
    }

    public void setBasicExpr(BasicExpr BasicExpr) {
        this.BasicExpr=BasicExpr;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(BasicExpr!=null) BasicExpr.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(BasicExpr!=null) BasicExpr.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(BasicExpr!=null) BasicExpr.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("CondFact_Basic(\n");

        if(BasicExpr!=null)
            buffer.append(BasicExpr.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [CondFact_Basic]");
        return buffer.toString();
    }
}
