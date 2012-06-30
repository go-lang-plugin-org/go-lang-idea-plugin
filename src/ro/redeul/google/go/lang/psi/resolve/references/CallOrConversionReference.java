package ro.redeul.google.go.lang.psi.resolve.references;

import com.intellij.patterns.ElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.psi.scope.util.PsiScopesUtil;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.expressions.primary.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.processors.GoResolveStates;
import ro.redeul.google.go.lang.psi.resolve.MethodOrTypeNameResolver;
import ro.redeul.google.go.lang.psi.statements.GoShortVarDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameter;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeNameDeclaration;
import ro.redeul.google.go.lang.psi.types.GoTypeFunction;
import static com.intellij.patterns.PlatformPatterns.psiElement;
import static ro.redeul.google.go.lang.parser.GoElementTypes.CALL_OR_CONVERSION_EXPRESSION;

public class CallOrConversionReference
    extends GoPsiReference<GoLiteralIdentifier> {

    public static ElementPattern<GoLiteralIdentifier> MATCHER =
        psiElement(GoLiteralIdentifier.class)
            .withParent(
                psiElement(GoLiteralExpression.class)
                    .withParent(psiElement(CALL_OR_CONVERSION_EXPRESSION))
                    .atStartOf(psiElement(CALL_OR_CONVERSION_EXPRESSION)));

    public CallOrConversionReference(GoLiteralIdentifier identifier) {
        super(identifier);
    }

    @Override
    public PsiElement resolve() {

        MethodOrTypeNameResolver processor =
            new MethodOrTypeNameResolver(this);

        PsiScopesUtil.treeWalkUp(
            processor,
            getElement(), getElement().getContainingFile(),
            GoResolveStates.initial());

        return processor.getDeclaration();
    }

    @Override
    public boolean isReferenceTo(PsiElement element) {
        if (element instanceof GoTypeNameDeclaration) {
            return matchesVisiblePackageName(element, getElement().getName());
        }

        if (element instanceof GoFunctionDeclaration) {
            GoFunctionDeclaration funcDeclaration =
                (GoFunctionDeclaration) element;

            if (funcDeclaration.getNameIdentifier() != null) {
                return matchesVisiblePackageName(
                    funcDeclaration.getUserData(
                        GoResolveStates.VisiblePackageName),
                    funcDeclaration.getNameIdentifier(),
                    getElement().getName());
            }
        }

        if (
            psiElement(GoLiteralIdentifier.class)
                .withParent(
                    psiElement(GoFunctionParameter.class)
                        .withChild(psiElement(GoTypeFunction.class))
                ).accepts(element) ) {
            return matchesVisiblePackageName(element, getElement().getName());
        }

        if (
            psiElement(GoLiteralIdentifier.class)
                .withParent(
                    psiElement(GoShortVarDeclaration.class)
                        .withChild(
                            psiElement(GoLiteralExpression.class)
                                .withChild(psiElement(GoTypeFunction.class)))
                ).accepts(element) ) {
            return matchesVisiblePackageName(element, getElement().getName());
        }

        return false;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return new Object[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isSoft() {
        return false;
    }
}
