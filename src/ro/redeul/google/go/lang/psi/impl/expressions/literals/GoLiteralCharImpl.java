package ro.redeul.google.go.lang.psi.impl.expressions.literals;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralChar;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;

public class GoLiteralCharImpl extends GoPsiElementBase
    implements GoLiteralChar
{
    public GoLiteralCharImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public Character getValue() {
        return null;
    }

    @Override
    public Type getType() {
        return Type.Char;
    }
}
