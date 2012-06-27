package ro.redeul.google.go.lang.psi.impl.toplevel;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameter;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameterList;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

public class GoFunctionParameterListImpl extends GoPsiElementBase
    implements GoFunctionParameterList {
    public GoFunctionParameterListImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public GoFunctionParameter[] getFunctionParameters() {
        return findChildrenByClass(GoFunctionParameter.class);
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitFunctionParameterList(this);
    }
}
