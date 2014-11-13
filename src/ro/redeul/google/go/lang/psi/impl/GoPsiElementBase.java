package ro.redeul.google.go.lang.psi.impl;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLock;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitorWithData;
import ro.redeul.google.go.util.LookupElementUtil;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Jul 24, 2010
 * Time: 10:26:02 PM
 */
public class GoPsiElementBase extends ASTWrapperPsiElement
    implements GoPsiElement {

    protected PsiReference[] myReferences = null;


    public GoPsiElementBase(@NotNull ASTNode node) {
        super(node);
//        System.out.println("" + hashCode() + " constructor: " + node.getText());
    }

    protected IElementType getTokenType() {
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

    @Override
    public <T> T accept(GoElementVisitorWithData<T> visitor) {
        accept((GoElementVisitor) visitor);
        return visitor.getData();
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

    /**
     * Always implement {@link #defineReferences()}
     * @return a null reference
     */
    @Override
    final public PsiReference getReference() {
        return null;
    }

    @NotNull
    @Override
    final public PsiReference[] getReferences() {
//        System.out.println("" + hashCode() + ", refs: " + myReferences + " text: " + getText() + " ");
//        if (myReferences == null)
            myReferences = defineReferences();

        return myReferences;
    }

    @NonNls
    protected PsiReference[] defineReferences() {
        return PsiReference.EMPTY_ARRAY;
    }

    protected PsiReference[] refs(PsiReference ... references) {
        return myReferences;
    }

    @NotNull
    @Override
    public String getLookupText() {
        String name = getName();

        return name != null ? name : "";
    }

    @Nullable
    @Override
    public String getLookupTailText() {
        return "";
    }

    @Override
    public String getLookupTypeText() {
        return "";
    }

    @Override
    public GoPsiElement getReferenceContext() {
        return this;
    }

    @Override
    @Nullable
    public LookupElementBuilder getLookupPresentation() {
        return LookupElementUtil.createLookupElement(this);
    }

    @Override
    @Nullable
    public LookupElementBuilder getLookupPresentation(GoPsiElement child) {
        return LookupElementUtil.createLookupElement(this, child);
    }

    protected <GoPsi extends GoPsiElement> GoPsi findChildByClass(
        Class<GoPsi> psiType, int pos) {
        GoPsi children[] = findChildrenByClass(psiType);
        return children.length > pos ? children[pos] : null;
    }

    @Override
    public boolean processDeclarations(@NotNull PsiScopeProcessor processor,
                                       @NotNull ResolveState state,
                                       PsiElement lastParent,
                                       @NotNull PsiElement place) {
        return true;
    }

    @Override
    protected Object clone() {
        GoPsiElementBase clone = (GoPsiElementBase)super.clone();
        synchronized (PsiLock.LOCK) {
            clone.myReferences = null;
        }

        return clone;

    }
}
