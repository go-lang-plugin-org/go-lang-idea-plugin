package ro.redeul.google.go.lang.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Jul 24, 2010
 * Time: 10:26:02 PM
 */
public class GoPsiElementBase extends ASTWrapperPsiElement
    implements GoPsiElement {

    private SearchScope searchScope;

    public GoPsiElementBase(@NotNull ASTNode node) {
        super(node);
    }

    public IElementType getTokenType() {
        return getNode().getElementType();
    }

    public String toString() {
        if (getClass() != GoPsiElementBase.class)
            return getTokenType().toString() + "Impl";

        return getTokenType().toString();
    }

    public void accept(GoElementVisitor visitor) {
        visitor.visitElement(this);
    }

    public void acceptChildren(GoElementVisitor visitor) {
        PsiElement child = getFirstChild();
        while (child != null) {
            if (child instanceof GoPsiElement) {
                ((GoPsiElement) child).accept(visitor);
            }

            child = child.getNextSibling();
        }
    }

    protected <GoPsi extends GoPsiElement> GoPsi findChildByClass(
        Class<GoPsi> psiType, int pos) {
        GoPsi children[] = findChildrenByClass(psiType);
        return children.length > pos ? children[pos] : null;
    }

    @Override
    public void setUseScope(SearchScope scope) {
        searchScope = scope;
    }

    @NotNull
    @Override
    public SearchScope getUseScope() {
        return searchScope != null ? searchScope : super.getUseScope();
    }
}
