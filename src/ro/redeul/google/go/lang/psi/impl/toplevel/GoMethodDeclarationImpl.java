package ro.redeul.google.go.lang.psi.impl.toplevel;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.toplevel.GoMethodDeclaration;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

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

    @Override
    public GoPsiElement getMethodReceiver() {
        return (GoPsiElement) findChildByType(GoElementTypes.METHOD_RECEIVER);
    }
}
