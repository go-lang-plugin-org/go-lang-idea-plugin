package ro.redeul.google.go.lang.psi.impl.toplevel;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeSpec;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Aug 30, 2010
 * Time: 8:59:20 PM
 */
public class GoTypeDeclarationImpl extends GoPsiElementBase implements GoTypeDeclaration {

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

        GoTypeSpec typeSpecs[] = getTypeSpecs();
        for (GoTypeSpec typeSpec : typeSpecs) {
            if ( typeSpec != lastParent ) {
                if ( ! typeSpec.processDeclarations(processor, state, null, place) ) {
                    return false;
                }
            }
        }

        return true;
    }
}

