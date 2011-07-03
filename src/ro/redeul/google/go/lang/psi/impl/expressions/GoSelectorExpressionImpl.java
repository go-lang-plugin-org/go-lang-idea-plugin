package ro.redeul.google.go.lang.psi.impl.expressions;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.ide.GoProjectSettings;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.GoSelectorExpression;
import ro.redeul.google.go.lang.psi.expressions.literals.GoIdentifier;
import ro.redeul.google.go.lang.psi.types.GoType;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructField;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 5/19/11
 * Time: 10:58 PM
 */
public class GoSelectorExpressionImpl extends GoExpressionBase implements GoSelectorExpression {

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

        PsiElement psiElement = resolve();

        if (psiElement != null && psiElement instanceof GoType) {
            return (GoType) psiElement;
        }

        return null;
    }

    @Override
    public GoExpr getExpressionContext() {
        return findChildByClass(GoExpr.class);
    }

    @Override
    public PsiElement getElement() {
        return this;
    }

    @Override
    public TextRange getRangeInElement() {

        GoExpr context = getExpressionContext();

        return context != null ? new TextRange(context.getTextLength() + 1, getTextLength()) : null;
    }

    @Override
    public PsiElement resolve() {

        if (GoProjectSettings.getInstance(getProject()).getState().enableVariablesCompletion) {
            GoType contextType = getExpressionContext().getType();

            if (contextType == null) {
                return null;
            }

            return contextType.getMemberType(getText().substring(getText().indexOf(".") + 1));
        }

        return null;
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

        if (GoProjectSettings.getInstance(getProject()).getState().enableVariablesCompletion) {
            GoType contextType = getExpressionContext().getType();

            if (contextType == null) {
                return new Object[0];
            }

            return convertToPresentation(contextType, contextType.getMembers());
        }

        return new Object[0];
    }

    private Object[] convertToPresentation(GoType type, GoPsiElement[] members) {

        Object[] presentations = new Object[members.length];

        for (int i = 0, membersLength = members.length; i < membersLength; i++) {

            GoPsiElement member = members[i];

            if (member instanceof GoIdentifier) {
                presentations[i] = getFieldPresentation(type, (GoIdentifier) member);
            } else {
                presentations[i] = member;
            }
        }

        return presentations;
    }

    private LookupElementBuilder getFieldPresentation(GoType type, GoIdentifier id) {

        String name = id.getName();

        LookupElementBuilder builder = LookupElementBuilder.create(id, name);

        GoType ownerType = null;
        if (id.getParent() != null && id.getParent() instanceof GoTypeStructField) {
            GoTypeStructField structField = (GoTypeStructField) id.getParent();
            ownerType = (GoType) structField.getParent();
        }

        if (ownerType == null) {
            return builder;
        }

        return builder
                .setBold()
                .setTailText(String.format(" (defined by: %s)", ownerType.getQualifiedName()))
                .setTypeText("<field>", ownerType != type);
    }


    @Override
    public boolean isSoft() {
        return true;
    }
}
