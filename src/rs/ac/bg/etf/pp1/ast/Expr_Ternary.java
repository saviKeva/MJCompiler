// generated with ast extension for cup
// version 0.8
// 16/1/2026 0:17:11


package rs.ac.bg.etf.pp1.ast;

public class Expr_Ternary extends Expr {

    private Condition Condition;
    private TernaryStart TernaryStart;
    private Expr Expr;
    private TernaryMiddle TernaryMiddle;
    private Expr Expr1;

    public Expr_Ternary (Condition Condition, TernaryStart TernaryStart, Expr Expr, TernaryMiddle TernaryMiddle, Expr Expr1) {
        this.Condition=Condition;
        if(Condition!=null) Condition.setParent(this);
        this.TernaryStart=TernaryStart;
        if(TernaryStart!=null) TernaryStart.setParent(this);
        this.Expr=Expr;
        if(Expr!=null) Expr.setParent(this);
        this.TernaryMiddle=TernaryMiddle;
        if(TernaryMiddle!=null) TernaryMiddle.setParent(this);
        this.Expr1=Expr1;
        if(Expr1!=null) Expr1.setParent(this);
    }

    public Condition getCondition() {
        return Condition;
    }

    public void setCondition(Condition Condition) {
        this.Condition=Condition;
    }

    public TernaryStart getTernaryStart() {
        return TernaryStart;
    }

    public void setTernaryStart(TernaryStart TernaryStart) {
        this.TernaryStart=TernaryStart;
    }

    public Expr getExpr() {
        return Expr;
    }

    public void setExpr(Expr Expr) {
        this.Expr=Expr;
    }

    public TernaryMiddle getTernaryMiddle() {
        return TernaryMiddle;
    }

    public void setTernaryMiddle(TernaryMiddle TernaryMiddle) {
        this.TernaryMiddle=TernaryMiddle;
    }

    public Expr getExpr1() {
        return Expr1;
    }

    public void setExpr1(Expr Expr1) {
        this.Expr1=Expr1;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(Condition!=null) Condition.accept(visitor);
        if(TernaryStart!=null) TernaryStart.accept(visitor);
        if(Expr!=null) Expr.accept(visitor);
        if(TernaryMiddle!=null) TernaryMiddle.accept(visitor);
        if(Expr1!=null) Expr1.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(Condition!=null) Condition.traverseTopDown(visitor);
        if(TernaryStart!=null) TernaryStart.traverseTopDown(visitor);
        if(Expr!=null) Expr.traverseTopDown(visitor);
        if(TernaryMiddle!=null) TernaryMiddle.traverseTopDown(visitor);
        if(Expr1!=null) Expr1.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(Condition!=null) Condition.traverseBottomUp(visitor);
        if(TernaryStart!=null) TernaryStart.traverseBottomUp(visitor);
        if(Expr!=null) Expr.traverseBottomUp(visitor);
        if(TernaryMiddle!=null) TernaryMiddle.traverseBottomUp(visitor);
        if(Expr1!=null) Expr1.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("Expr_Ternary(\n");

        if(Condition!=null)
            buffer.append(Condition.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(TernaryStart!=null)
            buffer.append(TernaryStart.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(Expr!=null)
            buffer.append(Expr.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(TernaryMiddle!=null)
            buffer.append(TernaryMiddle.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(Expr1!=null)
            buffer.append(Expr1.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [Expr_Ternary]");
        return buffer.toString();
    }
}
