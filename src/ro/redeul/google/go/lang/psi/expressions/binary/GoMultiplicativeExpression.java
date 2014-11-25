package ro.redeul.google.go.lang.psi.expressions.binary;

public interface GoMultiplicativeExpression extends GoBinaryExpression<GoMultiplicativeExpression.Op> {

    public enum Op implements GoBinaryExpression.BinaryOp {
        None,

        Mul, Quotient, Remainder, ShiftLeft, ShiftRight, BitAnd, BitClear;

        @Override
        public String getText() {
            switch (this) {
                case Mul:
                    return "*";
                case Quotient:
                    return "/";
                case Remainder:
                    return "%";
                case ShiftLeft:
                    return "<<";
                case ShiftRight:
                    return ">>";
                case BitAnd:
                    return "&";
                case BitClear:
                    return "&^";
                default:
                    return "";
            }
        }
    }
}
