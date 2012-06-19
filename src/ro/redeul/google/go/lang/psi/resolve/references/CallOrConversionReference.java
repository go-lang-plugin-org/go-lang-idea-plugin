package ro.redeul.google.go.lang.psi.resolve.references;

import com.intellij.patterns.ElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.psi.scope.util.PsiScopesUtil;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.expressions.primary.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.processors.GoResolveStates;
import ro.redeul.google.go.lang.psi.resolve.MethodOrTypeNameResolver;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeSpec;
import static com.intellij.patterns.PlatformPatterns.psiElement;

public class CallOrConversionReference extends GoPsiReference<GoLiteralIdentifier> {

    public static ElementPattern<GoLiteralIdentifier> MATCHER =
        psiElement(GoLiteralIdentifier.class)
            .withParent(
                psiElement(GoLiteralExpression.class)
                    .insideStarting(psiElement(GoElementTypes.CALL_OR_CONVERSION_EXPRESSION)));

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
        String ourName = getElement().getName();
        if (ourName == null) {
            ourName = "";
        }

        if (element instanceof GoTypeSpec) {
            GoTypeSpec spec = (GoTypeSpec) element;
            return ourName.equals(spec.getName());
        }

        if (element instanceof GoFunctionDeclaration) {
            GoFunctionDeclaration declaration = (GoFunctionDeclaration) element;
            return ourName.equals(declaration.getFunctionName());
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
