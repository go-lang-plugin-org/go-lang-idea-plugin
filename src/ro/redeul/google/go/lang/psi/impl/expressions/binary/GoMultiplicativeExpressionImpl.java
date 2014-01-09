package ro.redeul.google.go.lang.psi.impl.expressions.binary;

import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.expressions.binary.GoMultiplicativeExpression;

import static ro.redeul.google.go.lang.psi.expressions.binary.GoMultiplicativeExpression.Op;

public class GoMultiplicativeExpressionImpl extends GoBinaryExpressionImpl<Op> implements GoMultiplicativeExpression {

    public GoMultiplicativeExpressionImpl(@NotNull ASTNode node) {
        super(node, Op.values(), GoElementTypes.OPS_MUL);
    }
}
