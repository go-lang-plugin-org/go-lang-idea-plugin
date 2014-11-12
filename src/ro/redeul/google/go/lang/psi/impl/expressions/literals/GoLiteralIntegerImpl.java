package ro.redeul.google.go.lang.psi.impl.expressions.literals;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralInteger;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

import java.math.BigInteger;

public class GoLiteralIntegerImpl extends GoPsiElementBase implements GoLiteralInteger {

    public GoLiteralIntegerImpl(@NotNull ASTNode node) {
        super(node);
    }

    BigInteger value;

    @NotNull
    @Override
    public BigInteger getValue() {
        if (value == null) {
            int radix = 10;
            String textValue = getText();
            if (textValue.length() > 1) {
                if (textValue.startsWith("0x") || textValue.startsWith("0X")) {
                    radix = 16;
                    textValue = textValue.substring(2);
                } else if (textValue.startsWith("0")) {
                    radix = 8;
                    textValue = textValue.substring(1);
                }
            }
            try {
                value = new BigInteger(textValue, radix);
            } catch (NumberFormatException e) {
                value = BigInteger.ZERO;
            }
        }

        return value;
    }

    @Override
    public Type getType() {
        return Type.Int;
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitLiteralInteger(this);
    }
}
