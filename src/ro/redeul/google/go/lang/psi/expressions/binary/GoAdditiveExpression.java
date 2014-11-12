package ro.redeul.google.go.lang.psi.expressions.binary;

public interface GoAdditiveExpression extends GoBinaryExpression<GoAdditiveExpression.Op> {

    enum Op {
        None, Plus, Minus, BitOr, BitAnd
    }

}
