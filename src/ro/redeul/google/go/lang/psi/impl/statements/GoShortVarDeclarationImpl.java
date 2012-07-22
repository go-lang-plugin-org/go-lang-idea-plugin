package ro.redeul.google.go.lang.psi.impl.statements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.impl.declarations.GoVarDeclarationImpl;
import ro.redeul.google.go.lang.psi.statements.GoShortVarDeclaration;
import ro.redeul.google.go.lang.psi.typing.GoType;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 5/31/11
 * Time: 11:28 PM
 */
public class GoShortVarDeclarationImpl extends GoVarDeclarationImpl
    implements GoShortVarDeclaration {

    public GoShortVarDeclarationImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitShortVarDeclaration(this);
    }

    @Override
    public boolean mayRedeclareVariable() {
        return true;
    }

    @Override
    public boolean processDeclarations(@NotNull PsiScopeProcessor processor,
                                       @NotNull ResolveState state,
                                       PsiElement lastParent,
                                       @NotNull PsiElement place) {
        if (lastParent != null)
            return true;

        GoLiteralIdentifier identifiers[] = getIdentifiers();
        for (GoLiteralIdentifier identifier : identifiers) {
            if (!processor.execute(identifier, state))
                return false;
        }

        return true;
    }

    @Override
    public GoType getIdentifierType(GoLiteralIdentifier identifier) {
        GoLiteralIdentifier[] identifiers = getIdentifiers();
        GoExpr[] expressions = getExpressions();

        List<GoType> types = new ArrayList<GoType>();
        for (GoExpr expression : expressions) {
            Collections.addAll(types, expression.getType());
        }

        for (int i = 0; i < identifiers.length; i++) {
            GoLiteralIdentifier ident = identifiers[i];
            if (ident.isEquivalentTo(identifier) && types.size() > i)
                return types.get(i);
        }

        return null;
    }
}
