package ro.redeul.google.go.lang.psi.impl.expressions;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.GoPsiExpression;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementImpl;
import ro.redeul.google.go.lang.psi.types.GoType;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 5/19/11
 * Time: 10:58 PM
 */
public abstract class GoPsiExpressionImpl extends GoPsiElementImpl implements GoPsiExpression {

    GoType type;

    protected GoPsiExpressionImpl(@NotNull ASTNode node) {
        super(node);
    }

    public String getString() {
        return getText();
    }

    public String toString() {
        return getNode().getElementType().toString();
    }

    @Override
    public GoType getType() {
        return resolveType();
    }

    protected abstract GoType resolveType();

    public void accept(GoElementVisitor visitor) {
//        visitor.visitIdentifier(this);
    }
}
