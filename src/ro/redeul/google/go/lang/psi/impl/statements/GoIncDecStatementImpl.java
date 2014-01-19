package ro.redeul.google.go.lang.psi.impl.statements;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.lexer.GoTokenTypeSets;
import ro.redeul.google.go.lang.lexer.GoTokenTypes;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.impl.GoDocumentedPsiElementBase;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.statements.GoIncDecStatement;
import ro.redeul.google.go.lang.psi.visitors.GoTypedVisitor;

/**
 * <p/>
 * Created on Jan-04-2014 00:17
 *
 * @author <a href="mailto:mtoader@gmail.com">Mihai Toader</a>
 */
public class GoIncDecStatementImpl extends GoDocumentedPsiElementBase implements GoIncDecStatement {

    public GoIncDecStatementImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public GoExpr getExpression() {
        return findChildByClass(GoExpr.class);
    }

    @Override
    public <T, S> T accept(GoTypedVisitor<T, S> visitor, S data) {
        return visitor.visitStatementIncDec(this, data);
    }

    @Nullable
    @Override
    public Op getOperator() {
        PsiElement childByType = findChildByType(GoTokenTypeSets.INC_DEC_OPS);
        if (childByType == null)
            return Op.Null;

        if (childByType.getNode().getElementType() == GoTokenTypes.oPLUS_PLUS)
            return Op.Inc;

        if (childByType.getNode().getElementType() == GoTokenTypes.oMINUS_MINUS)
            return Op.Dec;

        return Op.Null;
    }
}
