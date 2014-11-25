package ro.redeul.google.go.lang.psi.expressions.binary;

public interface GoLogicalOrExpression extends GoBinaryExpression<GoLogicalOrExpression.Op> {

    public enum Op implements GoBinaryExpression.BinaryOp {
        None, LogicalOr;

        @Override
        public String getText() {
            switch (this) {
                case LogicalOr: return "||";
                default: return "";
            }
        }
    }
}
