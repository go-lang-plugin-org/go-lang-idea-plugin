package ro.redeul.google.go.lang.psi.impl.declarations;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.util.ReflectionCache;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclaration;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.literals.GoIdentifier;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

public class GoConstDeclarationImpl extends GoPsiElementBase implements GoConstDeclaration {

    public GoConstDeclarationImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public GoIdentifier[] getIdentifiers() {
        return findChildrenByClass(GoIdentifier.class);
    }

    @Override
    @NotNull
    public GoExpr[] getExpressions() {
        PsiElement list =
            findChildByType(GoElementTypes.EXPRESSION_LIST);

        if (list == null) {
            return new GoExpr[0];
        }

        List<GoExpr> result = new ArrayList<GoExpr>();
        for (PsiElement cur = list.getFirstChild(); cur != null; cur = cur.getNextSibling()) {
            if (ReflectionCache.isInstance(cur, GoExpr.class)) result.add((GoExpr)cur);
        }
        return result.toArray((GoExpr[]) Array.newInstance(GoExpr.class, result.size()));
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitConstDeclaration(this);
    }

    @Override
    public boolean processDeclarations(@NotNull PsiScopeProcessor processor, @NotNull ResolveState state, PsiElement lastParent, @NotNull PsiElement place) {
        return  processor.execute(this, state);
    }

}
