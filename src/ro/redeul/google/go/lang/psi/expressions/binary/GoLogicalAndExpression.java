package ro.redeul.google.go.lang.psi.expressions.binary;

public interface GoLogicalAndExpression extends GoBinaryExpression<GoLogicalAndExpression.Op> {

    public enum Op implements GoBinaryExpression.BinaryOp {
        None, LogicalAnd;

        @Override
        public String getText() {
            switch (this) {
                case LogicalAnd:
                    return "&&";
                default:
                    return "";
            }
        }
    }
}
