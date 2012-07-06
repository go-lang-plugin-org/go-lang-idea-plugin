package ro.redeul.google.go.lang.psi.impl.statements;

import java.util.List;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.statements.GoReturnStatement;
import ro.redeul.google.go.lang.psi.utils.GoPsiUtils;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

public class GoReturnStatementImpl extends GoPsiElementBase
    implements GoReturnStatement {
    public GoReturnStatementImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public GoExpr[] getExpressions() {
        PsiElement list = findChildByType(GoElementTypes.EXPRESSION_LIST);
        if ( list != null ) {
            List<GoExpr> arguments =
                GoPsiUtils.findChildrenOfType(list, GoExpr.class);
            return arguments.toArray(new GoExpr[arguments.size()]);
        }

        return findChildrenByClass(GoExpr.class);
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitReturnStatement(this);
    }
}
