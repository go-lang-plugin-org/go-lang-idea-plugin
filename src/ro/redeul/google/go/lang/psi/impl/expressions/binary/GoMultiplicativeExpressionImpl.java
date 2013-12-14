package ro.redeul.google.go.lang.psi.impl.expressions.binary;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.binary.GoMultiplicativeExpression;

public class GoMultiplicativeExpressionImpl extends GoBinaryExpressionImpl
        implements GoMultiplicativeExpression {

    public GoMultiplicativeExpressionImpl(@NotNull ASTNode node) {
        super(node);
    }

}
