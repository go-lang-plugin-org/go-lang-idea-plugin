package ro.redeul.google.go.lang.psi.impl.expressions.literals;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralString;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;

public class GoLiteralStringImpl extends GoPsiElementBase
    implements GoLiteralString
{
    public GoLiteralStringImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    @NotNull
    public String getValue() {
        return getText();
    }

    @Override
    public Type getType() {
        return getValue().startsWith("`")
            ? Type.RawString : Type.InterpretedString;
    }
}
