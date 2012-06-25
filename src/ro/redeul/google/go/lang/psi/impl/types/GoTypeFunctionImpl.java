package ro.redeul.google.go.lang.psi.impl.types;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.impl.GoPsiPackagedElementBase;
import ro.redeul.google.go.lang.psi.types.GoType;
import ro.redeul.google.go.lang.psi.types.GoTypeFunction;
import ro.redeul.google.go.lang.psi.types.underlying.GoUnderlyingType;

public class GoTypeFunctionImpl extends GoPsiPackagedElementBase
    implements GoTypeFunction {
    public GoTypeFunctionImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public GoUnderlyingType getUnderlyingType() {
        return GoUnderlyingType.Undefined;
    }

    @Override
    public boolean isIdentical(GoType goType) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
