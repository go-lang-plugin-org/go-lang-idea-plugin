package ro.redeul.google.go.lang.psi.impl.types.struct;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.GoIdentifier;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.types.GoType;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructField;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 5/29/11
 * Time: 12:28 PM
 */
public class GoTypeStructFieldImpl extends GoPsiElementBase implements GoTypeStructField {

    public GoTypeStructFieldImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public String getName() {
//        return getIdentifiers().getText();
        return "x";
    }

    @Override
    public PsiElement setName(@NonNls @NotNull String name) throws IncorrectOperationException {
        return null;
    }

    @Override
    public boolean isBlank() {
        GoIdentifier identifiers[] = getIdentifiers();

        return identifiers.length == 1 && identifiers[0].isBlank();
    }

    @Override
    public GoIdentifier[] getIdentifiers() {
        return findChildrenByClass(GoIdentifier.class);
    }

    @Override
    public GoType getType() {
        return findChildByClass(GoType.class);
    }
}
