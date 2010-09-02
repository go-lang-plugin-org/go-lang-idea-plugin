package ro.redeul.google.go.lang.psi.impl.toplevel;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementImpl;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeNameDeclaration;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Sep 2, 2010
 * Time: 1:43:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class GoTypeNameDeclarationImpl extends GoPsiElementImpl implements GoTypeNameDeclaration {

    public GoTypeNameDeclarationImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    @NotNull
    public String getName() {
        return getText();
    }

    public PsiElement setName(@NonNls String name) throws IncorrectOperationException {
        return null;
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.acceptTypeNameDeclaration(this);
    }

    @Override
    public String toString() {
        return "NamedType(" + getName() + ")";
    }
}
