package ro.redeul.google.go.lang.psi.impl.types;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.impl.GoPsiPackagedElementBase;
import ro.redeul.google.go.lang.psi.types.GoType;
import ro.redeul.google.go.lang.psi.types.GoTypeParenthesized;
import ro.redeul.google.go.lang.psi.types.underlying.GoUnderlyingType;

public class GoTypeParenthesizedImpl extends GoPsiPackagedElementBase
    implements GoTypeParenthesized
{
    public GoTypeParenthesizedImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public GoType getInnerType() {
        return findChildByClass(GoType.class);
    }

    @Override
    public GoUnderlyingType getUnderlyingType() {
        return getInnerType().getUnderlyingType();
    }

    @Override
    public boolean isIdentical(GoType goType) {
        // TODO: implement this
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
