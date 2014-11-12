package ro.redeul.google.go.lang.psi.impl.expressions.literals;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralImaginary;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;
import ro.redeul.google.go.util.GoNumber;

import static ro.redeul.google.go.lang.lexer.GoTokenTypes.litFLOAT_I;

public class GoLiteralImaginaryImpl extends GoPsiElementBase implements GoLiteralImaginary {
    public GoLiteralImaginaryImpl(@NotNull ASTNode node) {
        super(node);
    }

    private GoNumber value;

    @NotNull
    @Override
    public GoNumber getValue() {

        if ( value == null ) {
            String text = getText().replaceAll("[iI]$", "");

            switch (getType()) {
                case ImaginaryFloat:
                    value = GoNumber.parseLiteralFloat(text);
                    break;
                case ImaginaryInt:
                    value = GoNumber.parseLiteralInt(text);
                    break;
            }
        }

        return value;
    }

    @Override
    public Type getType() {
        return findChildByType(litFLOAT_I) != null
                ? Type.ImaginaryFloat
                : Type.ImaginaryInt;
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitLiteralImaginary(this);
    }
}
