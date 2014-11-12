package ro.redeul.google.go.lang.psi.expressions.binary;

public interface GoLogicalAndExpression extends GoBinaryExpression<GoLogicalAndExpression.Op> {

    public enum Op {
        None, LogicalAnd
    }
}
