package ro.redeul.google.go.lang.psi.impl.toplevel;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameter;
import ro.redeul.google.go.lang.psi.toplevel.GoMethodDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoMethodReceiver;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

public class GoMethodDeclarationImpl extends GoFunctionDeclarationImpl
    implements GoMethodDeclaration {

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

    @Override
    public GoMethodReceiver getMethodReceiver() {
        return findChildByClass(GoMethodReceiver.class);
    }

    @Override
    public boolean processDeclarations(@NotNull PsiScopeProcessor processor,
                                       @NotNull ResolveState state,
                                       PsiElement lastParent,
                                       @NotNull PsiElement place) {
        if (lastParent == null) {
            return processor.execute(this, state);
        }

        if (!processor.execute(getMethodReceiver(), state)) {
            return false;
        }

        for (GoFunctionParameter functionParameter : getParameters()) {
            if (!processor.execute(functionParameter, state)) {
                return false;
            }
        }


        for (GoFunctionParameter returnParameter : getResults()) {
            if (!processor.execute(returnParameter, state)) {
                return false;
            }
        }

        return true;
    }
}
