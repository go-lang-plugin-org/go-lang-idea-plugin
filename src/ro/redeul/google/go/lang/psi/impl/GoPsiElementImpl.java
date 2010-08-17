package ro.redeul.google.go.lang.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.GoPsiElement;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Jul 24, 2010
 * Time: 10:26:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class GoPsiElementImpl extends ASTWrapperPsiElement implements GoPsiElement {
    public GoPsiElementImpl(@NotNull ASTNode node) {
        super(node);
    }

    public IElementType getTokenType() {
        return getNode().getElementType();
    }

    public String toString() {
        return getTokenType().toString();
    }
}
