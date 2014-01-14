package ro.redeul.google.go.lang.psi.impl.toplevel;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameter;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameterList;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;
import ro.redeul.google.go.lang.psi.visitors.GoTypedVisitor;

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
    public GoFunctionParameter[] getElements() {
        return getFunctionParameters();
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitFunctionParameterList(this);
    }

    @Override
    public <T, S> T accept(GoTypedVisitor<T, S> visitor, S data) {
        return visitor.visitFunctionParameterList(this, data);
    }
}
