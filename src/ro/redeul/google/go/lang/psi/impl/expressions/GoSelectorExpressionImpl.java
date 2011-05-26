package ro.redeul.google.go.lang.psi.impl.expressions;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.scope.util.PsiScopesUtil;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.GoIdentifier;
import ro.redeul.google.go.lang.psi.expressions.GoPsiExpression;
import ro.redeul.google.go.lang.psi.expressions.GoSelectorExpression;
import ro.redeul.google.go.lang.psi.processors.GoResolveStates;
import ro.redeul.google.go.lang.psi.processors.IdentifierVariantsCollector;
import ro.redeul.google.go.lang.psi.types.GoType;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 5/19/11
 * Time: 10:58 PM
 */
public class GoSelectorExpressionImpl extends GoPsiExpressionImpl implements GoSelectorExpression {

    public GoSelectorExpressionImpl(@NotNull ASTNode node) {
        super(node);
    }

    public String toString() {
        return "SelectorExpression";
    }

    public void accept(GoElementVisitor visitor) {
//        visitor.visitIdentifier(this);
    }

    @Override
    protected GoType resolveType() {
        return null;
    }

    @Override
    public GoPsiExpression getExpressionContext() {
        return findChildByClass(GoPsiExpression.class);
    }

    @Override
    public PsiElement getElement() {
        return this;
    }

    @Override
    public TextRange getRangeInElement() {

        GoPsiExpression context = getExpressionContext();

        return context != null ? new TextRange(context.getTextLength() + 1, getTextLength()) : null;
    }

    @Override
    public PsiElement resolve() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @NotNull
    @Override
    public String getCanonicalText() {
        return getText();
    }

    @Override
    public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
        if (isReferenceTo(element))
            return this;

        throw new IncorrectOperationException("Cannot bind to:" + element + " of class " + element.getClass());
    }

    @Override
    public PsiReference getReference() {
        return this;
    }

    @Override
    public boolean isReferenceTo(PsiElement element) {
        return true;
    }

    @NotNull
    @Override
    public Object[] getVariants() {

        GoType contextType = getExpressionContext().getType();

        if ( contextType == null ) {
            return new Object[0];
        }

        return contextType.getMembers();
    }

    @Override
    public boolean isSoft() {
        return true;
    }
}
