package ro.redeul.google.go.lang.psi.resolve.references;

import com.intellij.patterns.ElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteral;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.expressions.primary.GoCallOrConvExpression;
import ro.redeul.google.go.lang.psi.expressions.primary.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.processors.GoResolveStates;
import ro.redeul.google.go.lang.psi.resolve.GoResolveResult;
import ro.redeul.google.go.lang.psi.statements.GoShortVarDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameter;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeNameDeclaration;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeFunction;
import ro.redeul.google.go.lang.psi.types.underlying.GoUnderlyingTypeFunction;
import ro.redeul.google.go.lang.psi.typing.GoType;

import static com.intellij.patterns.PlatformPatterns.psiElement;

public abstract class AbstractCallOrConversionReference<Reference extends AbstractCallOrConversionReference<Reference>>
    extends GoPsiReference.Single<GoLiteralExpression, Reference> {

    public static final ElementPattern<GoLiteralExpression> MATCHER =
        psiElement(GoLiteralExpression.class)
            .withChild(psiElement(GoLiteralIdentifier.class))
            .withParent(psiElement(GoCallOrConvExpression.class))
            .atStartOf(psiElement(GoCallOrConvExpression.class));


    AbstractCallOrConversionReference(GoLiteralExpression identifier,
                                      ResolveCache.AbstractResolver<Reference, GoResolveResult> resolver) {
        super(identifier, resolver);
    }

    @NotNull
    @Override
    public String getCanonicalText() {
        GoLiteral literal = getElement().getLiteral();

        if (literal != null && literal.getType() == GoLiteral.Type.Identifier) {
            GoLiteralIdentifier identifier = (GoLiteralIdentifier) literal;
            String identifierName = identifier.getName();
            if (identifierName != null) {
                return getElement().getText();
            }
        }

        return getElement().getText();
    }

    @Override
    public boolean isReferenceTo(PsiElement element) {
        String myName = getCanonicalText();

        if (element instanceof GoTypeNameDeclaration) {
            GoTypeNameDeclaration typeNameDeclaration = (GoTypeNameDeclaration)element;
            return matchesVisiblePackageName(typeNameDeclaration, myName);
        }

        if (element instanceof GoFunctionDeclaration) {
            GoFunctionDeclaration funcDeclaration =
                (GoFunctionDeclaration) element;

            if (funcDeclaration.getNameIdentifier() != null) {
                return matchesVisiblePackageName(
                    funcDeclaration.getUserData(GoResolveStates.VisiblePackageName),
                    funcDeclaration.getNameIdentifier(), myName);
            }
        }

        if (
            psiElement(GoLiteralIdentifier.class)
                .withParent(
                    psiElement(GoFunctionParameter.class)
                        .withChild(psiElement(GoPsiTypeFunction.class))
                ).accepts(element)) {
            return matchesVisiblePackageName(element, myName);
        }

        if (IDENT_IN_SHORT_VAR.accepts(element)) {
            GoShortVarDeclaration shortVars = (GoShortVarDeclaration) element.getParent();

            GoType identifierType =
                shortVars.getIdentifierType((GoLiteralIdentifier) element);

            if (identifierType != null &&
                identifierType.getUnderlyingType() instanceof GoUnderlyingTypeFunction)
                return matchesVisiblePackageName(element, myName);
        }

        return false;
    }

    private static final ElementPattern IDENT_IN_SHORT_VAR =
        psiElement(GoLiteralIdentifier.class)
            .withParent(psiElement(GoShortVarDeclaration.class));

    @Override
    public boolean isSoft() {
        return true;
    }
}
