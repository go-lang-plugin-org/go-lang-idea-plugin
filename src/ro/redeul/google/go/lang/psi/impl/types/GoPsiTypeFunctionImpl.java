package ro.redeul.google.go.lang.psi.impl.types;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.impl.GoPsiPackagedElementBase;
import ro.redeul.google.go.lang.psi.types.GoPsiType;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeFunction;
import ro.redeul.google.go.lang.psi.types.underlying.GoUnderlyingType;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

public class GoPsiTypeFunctionImpl extends GoPsiPackagedElementBase
    implements GoPsiTypeFunction {
    public GoPsiTypeFunctionImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public GoUnderlyingType getUnderlyingType() {
        return GoUnderlyingType.Undefined;
    }

    @Override
    public boolean isIdentical(GoPsiType goType) {
        return false;
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitFunctionType(this);
    }
}
