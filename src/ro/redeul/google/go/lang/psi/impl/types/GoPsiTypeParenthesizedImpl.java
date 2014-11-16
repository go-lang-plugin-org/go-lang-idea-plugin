package ro.redeul.google.go.lang.psi.impl.types;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.types.GoPsiType;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeParenthesized;

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
    public String getLookupTailText() {
        return String.format("(%s)", getInnerType().getLookupTailText());
    }
}
