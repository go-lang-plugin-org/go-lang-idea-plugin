package ro.redeul.google.go.lang.psi.resolve.references;

import com.intellij.patterns.ElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import org.jetbrains.annotations.NotNull;
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
    extends GoPsiReference<GoLiteralIdentifier, Reference> {

    public static ElementPattern<GoLiteralIdentifier> MATCHER =
        psiElement(GoLiteralIdentifier.class)
            .withParent(
                psiElement(GoLiteralExpression.class)
                    .withParent(psiElement(GoCallOrConvExpression.class))
                    .atStartOf(psiElement(GoCallOrConvExpression.class)));


    protected AbstractCallOrConversionReference(GoLiteralIdentifier identifier,
                                                ResolveCache.AbstractResolver<Reference, GoResolveResult> resolver) {
        super(identifier, resolver);
    }

    @NotNull
    @Override
    public String getCanonicalText() {
        return getElement().getCanonicalName();
    }

    @Override
    public boolean isReferenceTo(PsiElement element) {
        GoLiteralIdentifier identifier = getElement();
        String str = getCanonicalText();

        if (element instanceof GoTypeNameDeclaration) {
            return matchesVisiblePackageName(element, identifier.getName());
        }

        if (element instanceof GoFunctionDeclaration) {
            GoFunctionDeclaration funcDeclaration =
                (GoFunctionDeclaration) element;

            if (funcDeclaration.getNameIdentifier() != null) {
                return matchesVisiblePackageName(
                    funcDeclaration.getUserData(
                        GoResolveStates.VisiblePackageName),
                    funcDeclaration.getNameIdentifier(),
                    identifier.getName());
            }
        }

        if (
            psiElement(GoLiteralIdentifier.class)
                .withParent(
                    psiElement(GoFunctionParameter.class)
                        .withChild(psiElement(GoPsiTypeFunction.class))
                ).accepts(element) ) {
            return matchesVisiblePackageName(element, identifier.getName());
        }

        if ( IDENT_IN_SHORT_VAR.accepts(element) ) {
            GoShortVarDeclaration shortVars = (GoShortVarDeclaration) element.getParent();

            GoType identifierType =
                shortVars.getIdentifierType((GoLiteralIdentifier) element);

            if  (identifierType != null &&
                identifierType.getUnderlyingType() instanceof GoUnderlyingTypeFunction )
                return matchesVisiblePackageName(element, identifier.getName());
        }

        return false;
    }

    static ElementPattern IDENT_IN_SHORT_VAR =
        psiElement(GoLiteralIdentifier.class)
            .withParent(psiElement(GoShortVarDeclaration.class));

    @Override
    public boolean isSoft() {
        return false;
    }
}
