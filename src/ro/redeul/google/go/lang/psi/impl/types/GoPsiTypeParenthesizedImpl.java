package ro.redeul.google.go.lang.psi.impl.types;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.impl.GoPsiPackagedElementBase;
import ro.redeul.google.go.lang.psi.types.GoPsiType;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeParenthesized;
import ro.redeul.google.go.lang.psi.types.underlying.GoUnderlyingType;

public class GoPsiTypeParenthesizedImpl extends GoPsiTypeImpl implements GoPsiTypeParenthesized
{
    public GoPsiTypeParenthesizedImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public GoPsiType getInnerType() {
        return findChildByClass(GoPsiType.class);
    }

    @Override
    public GoUnderlyingType getUnderlyingType() {
        return getInnerType().getUnderlyingType();
    }

    @Override
    public boolean isIdentical(GoPsiType goType) {
        // TODO: implement this
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getLookupTailText() {
        return String.format("(%s)", getInnerType().getLookupTailText());
    }
}
