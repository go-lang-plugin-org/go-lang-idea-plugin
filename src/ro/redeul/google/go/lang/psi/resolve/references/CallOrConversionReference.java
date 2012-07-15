package ro.redeul.google.go.lang.psi.resolve.references;

import java.util.ArrayList;
import java.util.List;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.ElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.psi.scope.util.PsiScopesUtil;
import com.intellij.psi.util.PsiUtilCore;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.expressions.primary.GoCallOrConvExpression;
import ro.redeul.google.go.lang.psi.expressions.primary.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.processors.GoResolveStates;
import ro.redeul.google.go.lang.psi.resolve.MethodOrTypeNameResolver;
import ro.redeul.google.go.lang.psi.statements.GoShortVarDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameter;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeNameDeclaration;
import ro.redeul.google.go.lang.psi.types.GoType;
import ro.redeul.google.go.lang.psi.types.GoTypeFunction;
import ro.redeul.google.go.lang.psi.types.underlying.GoUnderlyingTypeFunction;
import static com.intellij.patterns.PlatformPatterns.psiElement;
import static ro.redeul.google.go.util.LookupElementUtil.createLookupElement;

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

    @NotNull
    @Override
    public String getCanonicalText() {
        return getElement().getCanonicalName();
    }

    @Override
    public PsiElement resolve() {
        MethodOrTypeNameResolver processor =
            new MethodOrTypeNameResolver(this);

        PsiScopesUtil.treeWalkUp(
            processor,
            getElement(), getElement().getContainingFile(),
            GoResolveStates.initial());

        return processor.getChildDeclaration();
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
                        .withChild(psiElement(GoTypeFunction.class))
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

    @NotNull
    @Override
    public Object[] getVariants() {

        GoLiteralIdentifier identifier = getElement();
        if ( identifier == null )
            return EMPTY_ARRAY;

        final List<LookupElementBuilder> variants = new ArrayList<LookupElementBuilder>();

        MethodOrTypeNameResolver processor =
            new MethodOrTypeNameResolver(this) {
                @Override
                protected boolean addDeclaration(PsiElement declaration, PsiElement child) {
                    String name = PsiUtilCore.getName(child);

                    String visiblePackageName =
                        getState().get(GoResolveStates.VisiblePackageName);

                    if ( visiblePackageName != null ) {
                        name = visiblePackageName + "." + name;
                    }
                    if (name == null) {
                        return true;
                    }

                    GoPsiElement goPsi = (GoPsiElement) declaration;
                    GoPsiElement goChildPsi = (GoPsiElement) child;
                    variants.add(createLookupElement(goPsi, name, goChildPsi));
                    return true;
                }
            };

        PsiScopesUtil.treeWalkUp(
            processor,
            identifier, identifier.getContainingFile(),
            GoResolveStates.initial());

        return variants.toArray();
    }

    @Override
    public boolean isSoft() {
        return false;
    }
}
