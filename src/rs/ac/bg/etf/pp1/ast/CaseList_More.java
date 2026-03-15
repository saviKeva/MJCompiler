// generated with ast extension for cup
// version 0.8
// 16/1/2026 0:17:11


package rs.ac.bg.etf.pp1.ast;

public class CaseList_More extends CaseList {

    private CaseList CaseList;
    private CaseValue CaseValue;
    private StatementList StatementList;

    public CaseList_More (CaseList CaseList, CaseValue CaseValue, StatementList StatementList) {
        this.CaseList=CaseList;
        if(CaseList!=null) CaseList.setParent(this);
        this.CaseValue=CaseValue;
        if(CaseValue!=null) CaseValue.setParent(this);
        this.StatementList=StatementList;
        if(StatementList!=null) StatementList.setParent(this);
    }

    public CaseList getCaseList() {
        return CaseList;
    }

    public void setCaseList(CaseList CaseList) {
        this.CaseList=CaseList;
    }

    public CaseValue getCaseValue() {
        return CaseValue;
    }

    public void setCaseValue(CaseValue CaseValue) {
        this.CaseValue=CaseValue;
    }

    public StatementList getStatementList() {
        return StatementList;
    }

    public void setStatementList(StatementList StatementList) {
        this.StatementList=StatementList;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(CaseList!=null) CaseList.accept(visitor);
        if(CaseValue!=null) CaseValue.accept(visitor);
        if(StatementList!=null) StatementList.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(CaseList!=null) CaseList.traverseTopDown(visitor);
        if(CaseValue!=null) CaseValue.traverseTopDown(visitor);
        if(StatementList!=null) StatementList.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(CaseList!=null) CaseList.traverseBottomUp(visitor);
        if(CaseValue!=null) CaseValue.traverseBottomUp(visitor);
        if(StatementList!=null) StatementList.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("CaseList_More(\n");

        if(CaseList!=null)
            buffer.append(CaseList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(CaseValue!=null)
            buffer.append(CaseValue.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(StatementList!=null)
            buffer.append(StatementList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [CaseList_More]");
        return buffer.toString();
    }
}
