package ro.redeul.google.go.lang.psi.statements;

import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.typing.GoType;

public interface GoForWithRangeStatement extends GoForStatement {

    GoExpr getKey();

    GoExpr getValue();

    GoExpr getRangeExpression();

    GoType[] getKeyType();

    GoType[] getValueType();
}
