package ro.redeul.google.go.lang.psi.expressions.binary;

public interface GoMultiplicativeExpression extends GoBinaryExpression<GoMultiplicativeExpression.Op> {
    public enum Op {
        None,

        Mul, Quotient, Remainder, ShiftLeft, ShiftRight, BitAnd, BitClear
    }
}
