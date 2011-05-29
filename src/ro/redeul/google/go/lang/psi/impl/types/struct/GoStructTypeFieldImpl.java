package ro.redeul.google.go.lang.psi.impl.types.struct;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.GoIdentifier;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.types.GoType;
import ro.redeul.google.go.lang.psi.types.struct.GoStructTypeField;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 5/29/11
 * Time: 12:28 PM
 */
public class GoStructTypeFieldImpl extends GoPsiElementBase implements GoStructTypeField {

    public GoStructTypeFieldImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public String getName() {
        return getIdentifier().getText();
    }

    @Override
    public PsiElement setName(@NonNls @NotNull String name) throws IncorrectOperationException {
        return null;
    }

    @Override
    public GoIdentifier getIdentifier() {
        return findChildByClass(GoIdentifier.class);
    }

    @Override
    public GoType getType() {
        return findChildByClass(GoType.class);
    }
}
