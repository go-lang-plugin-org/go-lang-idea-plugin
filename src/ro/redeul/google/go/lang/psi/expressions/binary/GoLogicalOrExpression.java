package ro.redeul.google.go.lang.psi.expressions.binary;

public interface GoLogicalOrExpression extends GoBinaryExpression<GoLogicalOrExpression.Op> {

    public enum Op {
        None, LogicalOr
    }
}
