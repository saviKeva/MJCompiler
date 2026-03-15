// generated with ast extension for cup
// version 0.8
// 16/1/2026 0:17:11


package rs.ac.bg.etf.pp1.ast;

public class CondTerm implements SyntaxNode {

    private SyntaxNode parent;
    private int line;
    public rs.etf.pp1.symboltable.concepts.Struct struct = null;

    private CondFactL CondFactL;

    public CondTerm (CondFactL CondFactL) {
        this.CondFactL=CondFactL;
        if(CondFactL!=null) CondFactL.setParent(this);
    }

    public CondFactL getCondFactL() {
        return CondFactL;
    }

    public void setCondFactL(CondFactL CondFactL) {
        this.CondFactL=CondFactL;
    }

    public SyntaxNode getParent() {
        return parent;
    }

    public void setParent(SyntaxNode parent) {
        this.parent=parent;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line=line;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(CondFactL!=null) CondFactL.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(CondFactL!=null) CondFactL.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(CondFactL!=null) CondFactL.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("CondTerm(\n");

        if(CondFactL!=null)
            buffer.append(CondFactL.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [CondTerm]");
        return buffer.toString();
    }
}
