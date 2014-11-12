package ro.redeul.google.go.lang.psi.impl.expressions.literals;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralChar;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.utils.GoPsiUtils;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

public class GoLiteralCharImpl extends GoPsiElementBase implements GoLiteralChar
{
    public GoLiteralCharImpl(@NotNull ASTNode node) {
        super(node);
    }

    @NotNull
    @Override
    public Character getValue() {
        Integer value = GoPsiUtils.getRuneValue(getText());
        return value == null ? Character.MIN_VALUE : (char) value.shortValue();
    }

    @Override
    public Type getType() {
        return Type.Char;
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitLiteralChar(this);
    }
}
