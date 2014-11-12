package ro.redeul.google.go.lang.psi.expressions.binary;

public interface GoRelationalExpression extends GoBinaryExpression<GoRelationalExpression.Op> {

    public enum Op {
        None,

        Eq, NotEq, Less, LessOrEqual, Greater, GreaterOrEqual;

        public String tokenText() {
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
