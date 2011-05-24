package ro.redeul.google.go.lang.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;
import ro.redeul.google.go.lang.psi.GoPsiElement;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Jul 24, 2010
 * Time: 10:26:02 PM
 */
public class GoPsiElementBase extends ASTWrapperPsiElement implements GoPsiElement {

    public GoPsiElementBase(@NotNull ASTNode node) {
        super(node);
    }

    public IElementType getTokenType() {
        return getNode().getElementType();
    }

    public String toString() {
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
}
