package ro.redeul.google.go.lang.psi.impl.expressions.literals;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralString;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.utils.GoPsiUtils;

public class GoLiteralStringImpl extends GoPsiElementBase
    implements GoLiteralString
{
    public GoLiteralStringImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    @NotNull
    public String getValue() {
        return GoPsiUtils.getStringLiteralValue(getText());
    }

    @Override
    public Type getType() {
        return getText().startsWith("`")
            ? Type.RawString : Type.InterpretedString;
    }
}
