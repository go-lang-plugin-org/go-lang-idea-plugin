package ro.redeul.google.go.lang.psi.impl;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.GoDocumentedPsiElement;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitorWithData;
import ro.redeul.google.go.util.LookupElementUtil;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Jul 24, 2010
 * Time: 10:26:02 PM
 */
public class GoPsiElementBase extends ASTWrapperPsiElement
    implements GoPsiElement {


    protected PsiReference[] refs(PsiReference ... references) {
        return references;
    }

    public GoPsiElementBase(@NotNull ASTNode node) {
        super(node);
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
        accept((GoElementVisitor)visitor);
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

    @NotNull
    @Override
    public String getPresentationText() {
        String name = getName();

        return name != null ? name : "";
    }

    @Nullable
    @Override
    public String getPresentationTailText() {
        return "";
    }

    @Override
    public String getPresentationTypeText() {
        return "";
    }

    @Override
    final public LookupElementBuilder getCompletionPresentation() {
        return LookupElementUtil.createLookupElement(this);
    }

    @Override
    public LookupElementBuilder getCompletionPresentation(GoPsiElement child) {
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

    public boolean isDocumentationPart(PsiElement child) {
        if (this instanceof GoDocumentedPsiElement) {
            PsiElement myChild = getFirstChild();
            while ( myChild != null & myChild instanceof PsiComment) {
                if ( myChild == child )
                    return true;

                myChild = myChild.getNextSibling();
            }
        }

        return false;
    }

    public List<PsiComment> getDocumentation() {
        if (this instanceof GoDocumentedPsiElement) {
            List<PsiComment> documentationComments = new LinkedList<PsiComment>();

            PsiElement child = getFirstChild();
            while ( child != null & child instanceof PsiComment) {
                documentationComments.add((PsiComment) child);
                child = child.getNextSibling();
            }
            return documentationComments;
        }
        else {
            return Collections.emptyList();
        }
    }

}
