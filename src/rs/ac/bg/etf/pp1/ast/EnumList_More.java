// generated with ast extension for cup
// version 0.8
// 16/1/2026 0:17:11


package rs.ac.bg.etf.pp1.ast;

public class EnumList_More extends EnumList {

    private EnumList EnumList;
    private EnumPart EnumPart;

    public EnumList_More (EnumList EnumList, EnumPart EnumPart) {
        this.EnumList=EnumList;
        if(EnumList!=null) EnumList.setParent(this);
        this.EnumPart=EnumPart;
        if(EnumPart!=null) EnumPart.setParent(this);
    }

    public EnumList getEnumList() {
        return EnumList;
    }

    public void setEnumList(EnumList EnumList) {
        this.EnumList=EnumList;
    }

    public EnumPart getEnumPart() {
        return EnumPart;
    }

    public void setEnumPart(EnumPart EnumPart) {
        this.EnumPart=EnumPart;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(EnumList!=null) EnumList.accept(visitor);
        if(EnumPart!=null) EnumPart.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(EnumList!=null) EnumList.traverseTopDown(visitor);
        if(EnumPart!=null) EnumPart.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(EnumList!=null) EnumList.traverseBottomUp(visitor);
        if(EnumPart!=null) EnumPart.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("EnumList_More(\n");

        if(EnumList!=null)
            buffer.append(EnumList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(EnumPart!=null)
            buffer.append(EnumPart.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [EnumList_More]");
        return buffer.toString();
    }
}
