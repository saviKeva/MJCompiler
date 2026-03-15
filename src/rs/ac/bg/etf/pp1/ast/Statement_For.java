// generated with ast extension for cup
// version 0.8
// 16/1/2026 0:17:11


package rs.ac.bg.etf.pp1.ast;

public class Statement_For extends Statement {

    private ForTerm ForTerm;
    private ForInit ForInit;
    private OptCondition OptCondition;
    private ForAfter ForAfter;
    private Statement Statement;

    public Statement_For (ForTerm ForTerm, ForInit ForInit, OptCondition OptCondition, ForAfter ForAfter, Statement Statement) {
        this.ForTerm=ForTerm;
        if(ForTerm!=null) ForTerm.setParent(this);
        this.ForInit=ForInit;
        if(ForInit!=null) ForInit.setParent(this);
        this.OptCondition=OptCondition;
        if(OptCondition!=null) OptCondition.setParent(this);
        this.ForAfter=ForAfter;
        if(ForAfter!=null) ForAfter.setParent(this);
        this.Statement=Statement;
        if(Statement!=null) Statement.setParent(this);
    }

    public ForTerm getForTerm() {
        return ForTerm;
    }

    public void setForTerm(ForTerm ForTerm) {
        this.ForTerm=ForTerm;
    }

    public ForInit getForInit() {
        return ForInit;
    }

    public void setForInit(ForInit ForInit) {
        this.ForInit=ForInit;
    }

    public OptCondition getOptCondition() {
        return OptCondition;
    }

    public void setOptCondition(OptCondition OptCondition) {
        this.OptCondition=OptCondition;
    }

    public ForAfter getForAfter() {
        return ForAfter;
    }

    public void setForAfter(ForAfter ForAfter) {
        this.ForAfter=ForAfter;
    }

    public Statement getStatement() {
        return Statement;
    }

    public void setStatement(Statement Statement) {
        this.Statement=Statement;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(ForTerm!=null) ForTerm.accept(visitor);
        if(ForInit!=null) ForInit.accept(visitor);
        if(OptCondition!=null) OptCondition.accept(visitor);
        if(ForAfter!=null) ForAfter.accept(visitor);
        if(Statement!=null) Statement.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(ForTerm!=null) ForTerm.traverseTopDown(visitor);
        if(ForInit!=null) ForInit.traverseTopDown(visitor);
        if(OptCondition!=null) OptCondition.traverseTopDown(visitor);
        if(ForAfter!=null) ForAfter.traverseTopDown(visitor);
        if(Statement!=null) Statement.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(ForTerm!=null) ForTerm.traverseBottomUp(visitor);
        if(ForInit!=null) ForInit.traverseBottomUp(visitor);
        if(OptCondition!=null) OptCondition.traverseBottomUp(visitor);
        if(ForAfter!=null) ForAfter.traverseBottomUp(visitor);
        if(Statement!=null) Statement.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("Statement_For(\n");

        if(ForTerm!=null)
            buffer.append(ForTerm.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(ForInit!=null)
            buffer.append(ForInit.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(OptCondition!=null)
            buffer.append(OptCondition.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(ForAfter!=null)
            buffer.append(ForAfter.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(Statement!=null)
            buffer.append(Statement.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [Statement_For]");
        return buffer.toString();
    }
}
