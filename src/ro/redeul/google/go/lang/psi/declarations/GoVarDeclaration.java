package ro.redeul.google.go.lang.psi.declarations;

import com.intellij.psi.PsiNamedElement;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.statements.GoStatement;
import ro.redeul.google.go.lang.psi.types.GoPsiType;
import ro.redeul.google.go.lang.psi.typing.GoType;

public interface GoVarDeclaration extends GoPsiElement, GoStatement, PsiNamedElement {

    GoLiteralIdentifier[] getIdentifiers();

    @Nullable
    GoPsiType getIdentifiersType();

    @Nullable
    GoType getIdentifierType(GoLiteralIdentifier identifier);

    GoExpr[] getExpressions();
}
