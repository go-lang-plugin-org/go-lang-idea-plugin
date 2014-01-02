package ro.redeul.google.go.lang.psi.impl.declarations;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclaration;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.types.GoPsiType;
import ro.redeul.google.go.lang.psi.typing.GoType;
import ro.redeul.google.go.lang.psi.typing.GoTypes;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class GoVarDeclarationImpl extends GoPsiElementBase implements GoVarDeclaration {

    public GoVarDeclarationImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public GoLiteralIdentifier[] getIdentifiers() {
        return findChildrenByClass(GoLiteralIdentifier.class);
    }

    @Override
    public GoPsiType getIdentifiersType() {
        return findChildByClass(GoPsiType.class);
    }

    @Override
    @NotNull
    public GoExpr[] getExpressions() {
        return findChildrenByClass(GoExpr.class);
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitVarDeclaration(this);
    }

    @Override
    public boolean processDeclarations(@NotNull PsiScopeProcessor processor,
                                       @NotNull ResolveState state,
                                       PsiElement lastParent,
                                       @NotNull PsiElement place) {
        return  processor.execute(this, state);
    }

    @Override
    public GoType getIdentifierType(GoLiteralIdentifier identifier) {
        GoPsiType identifiersType = getIdentifiersType();
        if (identifiersType != null) {
            return GoTypes.fromPsiType(identifiersType);
        }

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
