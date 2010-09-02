package ro.redeul.google.go.lang.psi.impl.toplevel;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementImpl;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeSpec;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Aug 30, 2010
 * Time: 8:59:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class GoTypeDeclarationImpl extends GoPsiElementImpl implements GoTypeDeclaration {

    public GoTypeDeclarationImpl(@NotNull ASTNode node) {
        super(node);
    }

    public GoTypeSpec[] getTypeSpecs() {
        return findChildrenByClass(GoTypeSpec.class);
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitTypeDeclaration(this);
    }

    @Override
    public boolean processDeclarations(@NotNull PsiScopeProcessor processor, @NotNull ResolveState state, PsiElement lastParent, @NotNull PsiElement place) {
        return processor.execute(this, state);
    }
}

