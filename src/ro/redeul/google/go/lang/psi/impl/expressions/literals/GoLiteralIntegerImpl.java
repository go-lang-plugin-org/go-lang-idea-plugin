package ro.redeul.google.go.lang.psi.impl.expressions.literals;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralInteger;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;

public class GoLiteralIntegerImpl extends GoPsiElementBase
    implements GoLiteralInteger {

    public GoLiteralIntegerImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public Integer getValue() {
        return null;
    }

    @Override
    public Type getType() {
        return Type.Int;
    }
}
