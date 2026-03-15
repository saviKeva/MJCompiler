// generated with ast extension for cup
// version 0.8
// 16/1/2026 0:17:11


package rs.ac.bg.etf.pp1.ast;

public class NoActParameters extends OptActPars {

    private ActParsBegin ActParsBegin;

    public NoActParameters (ActParsBegin ActParsBegin) {
        this.ActParsBegin=ActParsBegin;
        if(ActParsBegin!=null) ActParsBegin.setParent(this);
    }

    public ActParsBegin getActParsBegin() {
        return ActParsBegin;
    }

    public void setActParsBegin(ActParsBegin ActParsBegin) {
        this.ActParsBegin=ActParsBegin;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(ActParsBegin!=null) ActParsBegin.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(ActParsBegin!=null) ActParsBegin.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(ActParsBegin!=null) ActParsBegin.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("NoActParameters(\n");

        if(ActParsBegin!=null)
            buffer.append(ActParsBegin.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [NoActParameters]");
        return buffer.toString();
    }
}
