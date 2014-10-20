package ro.redeul.google.go.lang.psi.impl.statements;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.Condition;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.util.containers.ContainerUtil;
import com.siyeh.ig.psiutils.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.impl.declarations.GoVarDeclarationImpl;
import ro.redeul.google.go.lang.psi.resolve.refs.ShortVarSolver;
import ro.redeul.google.go.lang.psi.statements.GoShortVarDeclaration;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

import java.util.ArrayList;
import java.util.List;

import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.resolveSafely;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 5/31/11
 * Time: 11:28 PM
 */
public class GoShortVarDeclarationImpl extends GoVarDeclarationImpl
        implements GoShortVarDeclaration {

    private GoLiteralIdentifier[] declarations;

    public GoShortVarDeclarationImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitShortVarDeclaration(this);
    }

    @Override
    public boolean processDeclarations(@NotNull PsiScopeProcessor processor,
                                       @NotNull ResolveState state,
                                       PsiElement lastParent,
                                       @NotNull PsiElement place) {
        if (lastParent != null)
            return true;
        return processor.execute(this, state);
    }

    @Override
    public GoLiteralIdentifier[] getDeclarations() {

        if (declarations == null) {
            declarations = ContainerUtil.findAllAsArray(
                    ContainerUtil.filter(getIdentifiers(), new Condition<GoLiteralIdentifier>() {
                        public boolean value(GoLiteralIdentifier ident) { return resolveSafely(ident) == null;  }
                    }),
            GoLiteralIdentifier.class);
        }

        return declarations;
    }
}
