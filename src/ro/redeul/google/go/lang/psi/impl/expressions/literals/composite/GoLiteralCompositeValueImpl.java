package ro.redeul.google.go.lang.psi.impl.expressions.literals.composite;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.literals.composite.GoLiteralCompositeElement;
import ro.redeul.google.go.lang.psi.expressions.literals.composite.GoLiteralCompositeValue;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

public class GoLiteralCompositeValueImpl extends GoPsiElementBase
    implements GoLiteralCompositeValue
{
    public GoLiteralCompositeValueImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public GoLiteralCompositeElement[] getElements() {
        return findChildrenByClass(GoLiteralCompositeElement.class);
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitLiteralCompositeVal(this);
    }
}
