package ro.redeul.google.go.lang.psi.impl.expressions;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.GoCallOrConversionExpression;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.types.GoType;

public class GoCallOrConversionExpressionImpl extends GoPsiElementBase
    implements GoCallOrConversionExpression
{
    public GoCallOrConversionExpressionImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public GoType getType() {
        return null;
    }
}
