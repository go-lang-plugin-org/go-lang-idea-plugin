package ro.redeul.google.go.lang.psi.expressions.binary;

public interface GoRelationalExpression extends GoBinaryExpression<GoRelationalExpression.Op> {

    public enum Op implements GoBinaryExpression.BinaryOp {
        None,

        Eq, NotEq, Less, LessOrEqual, Greater, GreaterOrEqual;

        @Override
        public String getText() {
            switch (this) {
                case Eq:
                    return "==";
                case NotEq:
                    return "!=";
                case Less:
                    return "<";
                case LessOrEqual:
                    return "<=";
                case Greater:
                    return ">";
                case GreaterOrEqual:
                    return ">=";
            }

            return "";
        }
    }
}
