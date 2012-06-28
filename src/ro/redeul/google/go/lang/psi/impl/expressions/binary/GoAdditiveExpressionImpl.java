package ro.redeul.google.go.lang.psi.impl.expressions.binary;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.binary.GoAdditiveExpression;

public class GoAdditiveExpressionImpl extends GoBinaryExpressionImpl implements
                                                               GoAdditiveExpression {
    public GoAdditiveExpressionImpl(@NotNull ASTNode node) {
        super(node);
    }

}

