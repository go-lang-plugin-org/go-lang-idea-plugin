package ro.redeul.google.go.lang.psi.impl.statements;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.lexer.GoTokenTypes;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.statements.GoForWithRangeStatement;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeName;
import ro.redeul.google.go.lang.psi.typing.*;
import ro.redeul.google.go.lang.psi.utils.GoTypeUtils;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;
import ro.redeul.google.go.lang.stubs.GoNamesCache;

import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.hasPrevSiblingOfType;

public class GoForWithRangeStatementImpl extends GoAbstractForWithRangeStatementImpl<GoForWithRangeStatementImpl>
        implements GoForWithRangeStatement {

    public GoForWithRangeStatementImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public GoExpr getKey() {
        return findChildByClass(GoExpr.class, 0);
    }

    @Override
    public GoExpr getValue() {
        GoExpr[] expressions = findChildrenByClass(GoExpr.class);

        if (expressions.length > 2) {
            return expressions[1];
        }

        if (expressions.length == 2 &&
                !hasPrevSiblingOfType(expressions[1], GoTokenTypes.kRANGE)) {
            return expressions[1];
        }

        return null;
    }

    public GoExpr getRangeExpression() {
        GoExpr[] expressions = findChildrenByClass(GoExpr.class);

        if (expressions.length > 2) {
            return expressions[2];
        }

        if (expressions.length == 2 &&
                hasPrevSiblingOfType(expressions[1], GoTokenTypes.kRANGE)) {
            return expressions[1];
        }

        return null;
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitForWithRange(this);
    }
}
