package ro.redeul.google.go.lang.psi.resolve.references;

import com.intellij.patterns.ElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.psi.scope.util.PsiScopesUtil;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.expressions.primary.GoCallOrConvExpression;
import ro.redeul.google.go.lang.psi.expressions.primary.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.processors.GoResolveStates;
import ro.redeul.google.go.lang.psi.resolve.MethodOrTypeNameResolver;
import ro.redeul.google.go.lang.psi.statements.GoShortVarDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameter;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeNameDeclaration;
import ro.redeul.google.go.lang.psi.types.GoTypeFunction;
import static com.intellij.patterns.PlatformPatterns.psiElement;

public class CallOrConversionReference
    extends GoPsiReference<GoLiteralIdentifier> {

    public static ElementPattern<GoLiteralIdentifier> MATCHER =
        psiElement(GoLiteralIdentifier.class)
            .withParent(
                psiElement(GoLiteralExpression.class)
                    .withParent(psiElement(GoCallOrConvExpression.class))
                    .atStartOf(psiElement(GoCallOrConvExpression.class)));

    public CallOrConversionReference(GoLiteralIdentifier identifier) {
        super(identifier);
    }

    @Override
    public PsiElement resolve() {
        GoLiteralIdentifier identifier = getElement();
        if ( identifier == null )
            return null;

        MethodOrTypeNameResolver processor =
            new MethodOrTypeNameResolver(this);

        PsiScopesUtil.treeWalkUp(
            processor,
            identifier, identifier.getContainingFile(),
            GoResolveStates.initial());

        return processor.getDeclaration();
    }

    @Override
    public boolean isReferenceTo(PsiElement element) {

        GoLiteralIdentifier literalElement = getElement();
        if (literalElement == null)
            return false;

        if (element instanceof GoTypeNameDeclaration) {
            return matchesVisiblePackageName(element, literalElement.getName());
        }

        if (element instanceof GoFunctionDeclaration) {
            GoFunctionDeclaration funcDeclaration =
                (GoFunctionDeclaration) element;

            if (funcDeclaration.getNameIdentifier() != null) {
                return matchesVisiblePackageName(
                    funcDeclaration.getUserData(GoResolveStates.VisiblePackageName),
                    funcDeclaration.getNameIdentifier(),
                    literalElement.getName());
            }
        }

        if (
            psiElement(GoLiteralIdentifier.class)
                .withParent(
                    psiElement(GoFunctionParameter.class)
                        .withChild(psiElement(GoTypeFunction.class))
                ).accepts(element) ) {
            return matchesVisiblePackageName(element, literalElement.getName());
        }

        if (
            psiElement(GoLiteralIdentifier.class)
                .withParent(
                    psiElement(GoShortVarDeclaration.class)
                        .withChild(
                            psiElement(GoLiteralExpression.class)
                                .withChild(psiElement(GoTypeFunction.class)))
                ).accepts(element) ) {
            return matchesVisiblePackageName(element, literalElement.getName());
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
