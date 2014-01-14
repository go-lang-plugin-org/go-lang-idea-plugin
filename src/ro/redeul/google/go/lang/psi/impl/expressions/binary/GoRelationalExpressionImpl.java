package ro.redeul.google.go.lang.psi.impl.expressions.binary;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.expressions.binary.GoRelationalExpression;
import ro.redeul.google.go.lang.psi.typing.GoType;
import ro.redeul.google.go.lang.psi.typing.GoTypes;
import ro.redeul.google.go.lang.psi.visitors.GoTypedVisitor;
import ro.redeul.google.go.lang.stubs.GoNamesCache;

import static ro.redeul.google.go.lang.psi.expressions.binary.GoRelationalExpression.Op;

public class GoRelationalExpressionImpl extends GoBinaryExpressionImpl<Op> implements GoRelationalExpression
{
    public GoRelationalExpressionImpl(@NotNull ASTNode node) {
        super(node, Op.values(), GoElementTypes.OPS_REL);
    }

    @Override
    protected GoType[] resolveTypes() {
        return new GoType[]{
                GoTypes.getBuiltin(
                        GoTypes.Builtin.Bool,
                        GoNamesCache.getInstance(getProject()))
        };
    }

    @Override
    public <T, S> T accept(GoTypedVisitor<T, S> visitor, S data) {
        return visitor.visitExpressionRelational(this, data);
    }
}

