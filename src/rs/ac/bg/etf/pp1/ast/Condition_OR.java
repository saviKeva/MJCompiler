// generated with ast extension for cup
// version 0.8
// 16/1/2026 0:17:11


package rs.ac.bg.etf.pp1.ast;

public class Condition_OR extends CondTermL {

    private CondTermL CondTermL;
    private CondTerm CondTerm;

    public Condition_OR (CondTermL CondTermL, CondTerm CondTerm) {
        this.CondTermL=CondTermL;
        if(CondTermL!=null) CondTermL.setParent(this);
        this.CondTerm=CondTerm;
        if(CondTerm!=null) CondTerm.setParent(this);
    }

    public CondTermL getCondTermL() {
        return CondTermL;
    }

    public void setCondTermL(CondTermL CondTermL) {
        this.CondTermL=CondTermL;
    }

    public CondTerm getCondTerm() {
        return CondTerm;
    }

    public void setCondTerm(CondTerm CondTerm) {
        this.CondTerm=CondTerm;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(CondTermL!=null) CondTermL.accept(visitor);
        if(CondTerm!=null) CondTerm.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(CondTermL!=null) CondTermL.traverseTopDown(visitor);
        if(CondTerm!=null) CondTerm.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(CondTermL!=null) CondTermL.traverseBottomUp(visitor);
        if(CondTerm!=null) CondTerm.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("Condition_OR(\n");

        if(CondTermL!=null)
            buffer.append(CondTermL.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(CondTerm!=null)
            buffer.append(CondTerm.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [Condition_OR]");
        return buffer.toString();
    }
}
