package ro.redeul.google.go.lang.psi.impl.expressions.literals;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralInteger;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.visitors.GoTypedVisitor;

public class GoLiteralIntegerImpl extends GoPsiElementBase
    implements GoLiteralInteger {

    public GoLiteralIntegerImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public <T, S> T accept(GoTypedVisitor<T, S> visitor, S data) {
        return visitor.visitLiteralInteger(this, data);
    }

    @NotNull
    @Override
    public Integer getValue() {
        return null;
    }

    @Override
    public Type getType() {
        return Type.Int;
    }
}
