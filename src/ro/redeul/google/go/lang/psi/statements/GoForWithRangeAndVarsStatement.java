package ro.redeul.google.go.lang.psi.statements;

import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.typing.GoType;

public interface GoForWithRangeAndVarsStatement extends GoForStatement {

    @Nullable
    GoLiteralIdentifier getKey();

    GoType[] getKeyType();

    @Nullable
    GoLiteralIdentifier getValue();

    GoType[] getValueType();

    GoExpr getRangeExpression();
}
