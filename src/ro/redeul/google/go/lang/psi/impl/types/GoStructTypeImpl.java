package ro.redeul.google.go.lang.psi.impl.types;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.types.GoSliceType;
import ro.redeul.google.go.lang.psi.types.GoStructType;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 5/27/11
 * Time: 12:17 AM
 */
public class GoStructTypeImpl extends GoPsiElementBase implements GoStructType {

    public GoStructTypeImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public GoPsiElement[] getMembers() {
        GoPsiElement childPsiElements[] = new GoPsiElement[getChildren().length];
        PsiElement[] children = getChildren();
        for (int i = 0, childrenLength = children.length; i < childrenLength; i++) {
            childPsiElements[i] = (GoPsiElement) children[i];
        }

        return childPsiElements;
    }
}
