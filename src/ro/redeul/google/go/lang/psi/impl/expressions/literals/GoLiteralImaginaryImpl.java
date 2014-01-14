package ro.redeul.google.go.lang.psi.impl.expressions.literals;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralImaginary;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.visitors.GoTypedVisitor;

import static ro.redeul.google.go.lang.lexer.GoTokenTypes.litFLOAT_I;

public class GoLiteralImaginaryImpl extends GoPsiElementBase
    implements GoLiteralImaginary {
    public GoLiteralImaginaryImpl(@NotNull ASTNode node) {
        super(node);
    }

    @NotNull
    @Override
    public Number getValue() {
        return null;
    }

    @Override
    public Type getType() {
        return findChildByType(litFLOAT_I) != null
             ? Type.ImaginaryFloat: Type.ImaginaryInt;
    }

    @Override
    public <T, S> T accept(GoTypedVisitor<T, S> visitor, S data) {
        return visitor.visitLiteralImaginary(this, data);
    }
}
