// generated with ast extension for cup
// version 0.8
// 16/1/2026 0:17:11


package rs.ac.bg.etf.pp1.ast;

public class ActParameters extends OptActPars {

    private ActParsBegin ActParsBegin;
    private ActPars ActPars;

    public ActParameters (ActParsBegin ActParsBegin, ActPars ActPars) {
        this.ActParsBegin=ActParsBegin;
        if(ActParsBegin!=null) ActParsBegin.setParent(this);
        this.ActPars=ActPars;
        if(ActPars!=null) ActPars.setParent(this);
    }

    public ActParsBegin getActParsBegin() {
        return ActParsBegin;
    }

    public void setActParsBegin(ActParsBegin ActParsBegin) {
        this.ActParsBegin=ActParsBegin;
    }

    public ActPars getActPars() {
        return ActPars;
    }

    public void setActPars(ActPars ActPars) {
        this.ActPars=ActPars;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(ActParsBegin!=null) ActParsBegin.accept(visitor);
        if(ActPars!=null) ActPars.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(ActParsBegin!=null) ActParsBegin.traverseTopDown(visitor);
        if(ActPars!=null) ActPars.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(ActParsBegin!=null) ActParsBegin.traverseBottomUp(visitor);
        if(ActPars!=null) ActPars.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("ActParameters(\n");

        if(ActParsBegin!=null)
            buffer.append(ActParsBegin.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(ActPars!=null)
            buffer.append(ActPars.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [ActParameters]");
        return buffer.toString();
    }
}
