package ro.redeul.google.go.lang.psi.impl.expressions.binary;

import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.lexer.GoTokenTypeSets;
import ro.redeul.google.go.lang.lexer.GoTokenTypes;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.expressions.binary.GoAdditiveExpression;
import ro.redeul.google.go.lang.psi.utils.GoTokenSets;
import ro.redeul.google.go.lang.psi.visitors.GoTypedVisitor;

import static ro.redeul.google.go.lang.psi.expressions.binary.GoAdditiveExpression.Op;

public class GoAdditiveExpressionImpl extends GoBinaryExpressionImpl<Op> implements GoAdditiveExpression {

    public GoAdditiveExpressionImpl(@NotNull ASTNode node) {
        super(node, Op.values(), GoElementTypes.OPS_ADD);
    }

    @Override
    public <T, S> T accept(GoTypedVisitor<T, S> visitor, S data) {
        return visitor.visitExpressionAdditive(this, data);
    }
}

