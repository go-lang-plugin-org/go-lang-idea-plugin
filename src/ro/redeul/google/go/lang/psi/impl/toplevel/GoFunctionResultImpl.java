package ro.redeul.google.go.lang.psi.impl.toplevel;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameter;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionResult;
import ro.redeul.google.go.lang.psi.visitors.GoTypedVisitor;

/**
 * <p/>
 * Created on Jan-13-2014 23:03
 *
 * @author <a href="mailto:mtoader@gmail.com">Mihai Toader</a>
 */
public class GoFunctionResultImpl extends GoPsiElementBase implements GoFunctionResult {

    public GoFunctionResultImpl(@NotNull ASTNode node) {
        super(node);
    }

    @NotNull
    @Override
    public GoFunctionParameter[] getResults() {
        return findChildrenByClass(GoFunctionParameter.class);
    }

    @Override
    public <T, S> T accept(GoTypedVisitor<T, S> visitor, S data) {
        return visitor.visitFunctionResult(this, data);
    }
}
