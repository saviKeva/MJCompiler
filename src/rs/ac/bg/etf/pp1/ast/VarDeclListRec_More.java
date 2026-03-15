// generated with ast extension for cup
// version 0.8
// 16/1/2026 0:17:11


package rs.ac.bg.etf.pp1.ast;

public class VarDeclListRec_More extends VarDeclListRec {

    private VarDeclListRec VarDeclListRec;
    private VarDecl VarDecl;

    public VarDeclListRec_More (VarDeclListRec VarDeclListRec, VarDecl VarDecl) {
        this.VarDeclListRec=VarDeclListRec;
        if(VarDeclListRec!=null) VarDeclListRec.setParent(this);
        this.VarDecl=VarDecl;
        if(VarDecl!=null) VarDecl.setParent(this);
    }

    public VarDeclListRec getVarDeclListRec() {
        return VarDeclListRec;
    }

    public void setVarDeclListRec(VarDeclListRec VarDeclListRec) {
        this.VarDeclListRec=VarDeclListRec;
    }

    public VarDecl getVarDecl() {
        return VarDecl;
    }

    public void setVarDecl(VarDecl VarDecl) {
        this.VarDecl=VarDecl;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(VarDeclListRec!=null) VarDeclListRec.accept(visitor);
        if(VarDecl!=null) VarDecl.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(VarDeclListRec!=null) VarDeclListRec.traverseTopDown(visitor);
        if(VarDecl!=null) VarDecl.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(VarDeclListRec!=null) VarDeclListRec.traverseBottomUp(visitor);
        if(VarDecl!=null) VarDecl.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("VarDeclListRec_More(\n");

        if(VarDeclListRec!=null)
            buffer.append(VarDeclListRec.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(VarDecl!=null)
            buffer.append(VarDecl.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [VarDeclListRec_More]");
        return buffer.toString();
    }
}
