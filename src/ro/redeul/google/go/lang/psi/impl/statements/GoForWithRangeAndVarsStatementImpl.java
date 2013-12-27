package ro.redeul.google.go.lang.psi.impl.statements;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.lexer.GoTokenTypes;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.statements.GoForWithRangeAndVarsStatement;
import ro.redeul.google.go.lang.psi.statements.GoForWithRangeStatement;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeName;
import ro.redeul.google.go.lang.psi.typing.*;
import ro.redeul.google.go.lang.psi.utils.GoTypeUtils;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;
import ro.redeul.google.go.lang.stubs.GoNamesCache;

import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.hasPrevSiblingOfType;

public class GoForWithRangeAndVarsStatementImpl extends GoAbstractForWithRangeStatementImpl
        implements GoForWithRangeAndVarsStatement {

    public GoForWithRangeAndVarsStatementImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public GoLiteralIdentifier getKey() {
        return findChildByClass(GoLiteralIdentifier.class, 0);
    }

    @Override
    public GoLiteralIdentifier getValue() {
        GoLiteralIdentifier[] identifiers = findChildrenByClass(GoLiteralIdentifier.class);

        if (identifiers.length > 2) {
            return identifiers[1];
        }

        if (identifiers.length == 2 &&
                !hasPrevSiblingOfType(identifiers[1], GoTokenTypes.kRANGE)) {
            return identifiers[1];
        }

        return null;
    }

    public GoExpr getRangeExpression() {
        return findChildByClass(GoExpr.class);
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitForWithRangeAndVars(this);
    }

    @Override
    public boolean processDeclarations(@NotNull PsiScopeProcessor processor,
                                       @NotNull ResolveState state,
                                       PsiElement lastParent,
                                       @NotNull PsiElement place) {

        // HACK very very ugly hack. I need to learn how to properly use scopes.
        if (getBlock() != null && getBlock().getTextRange().contains(place.getTextRange())) {
            if (getValue() != null) {
                if (!getValue().processDeclarations(processor, state, null, place))
                    return false;
            }

            if (getKey() != null) {
                if (!getKey().processDeclarations(processor, state, null, place))
                    return false;
            }
        }

        return true;
    }
}
