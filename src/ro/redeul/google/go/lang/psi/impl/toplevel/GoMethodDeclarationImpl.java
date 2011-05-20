package ro.redeul.google.go.lang.psi.impl.toplevel;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;
import ro.redeul.google.go.lang.psi.toplevel.GoMethodDeclaration;

public class GoMethodDeclarationImpl extends GoFunctionDeclarationImpl implements GoMethodDeclaration {

    public GoMethodDeclarationImpl(@NotNull ASTNode node) {
        super(node);
    }

    public boolean isMain() {
        return false;
    }

    public String toString() {
      return "MethodDeclaration(" + getFunctionName() + ")";
    }

    public void accept(GoElementVisitor visitor) {
        visitor.visitMethodDeclaration(this);
    }
}
