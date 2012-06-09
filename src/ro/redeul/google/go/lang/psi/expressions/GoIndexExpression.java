package ro.redeul.google.go.lang.psi.expressions;

public interface GoIndexExpression extends GoExpr {

    GoExpr getIndexed();

    GoExpr getIndex();
}
