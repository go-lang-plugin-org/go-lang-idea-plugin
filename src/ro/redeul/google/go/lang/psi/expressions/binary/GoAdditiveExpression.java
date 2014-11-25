package ro.redeul.google.go.lang.psi.expressions.binary;

public interface GoAdditiveExpression extends GoBinaryExpression<GoAdditiveExpression.Op> {

    enum Op implements GoBinaryExpression.BinaryOp {
        None, Plus, Minus, BitOr, BitXor;

        @Override
        public String getText() {
            switch (this) {
                case Plus:
                    return "+";
                case Minus:
                    return "-";
                case BitOr:
                    return "|";
                case BitXor:
                    return "^";
                default:
                    return "";
            }
        }
    }
}
