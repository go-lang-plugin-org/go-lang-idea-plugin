package ro.redeul.google.go.lang.psi.impl.statements.switches;

import java.util.List;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.lexer.GoTokenTypes;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.patterns.GoElementPatterns;
import ro.redeul.google.go.lang.psi.statements.GoStatement;
import ro.redeul.google.go.lang.psi.statements.switches.GoSwitchTypeClause;
import ro.redeul.google.go.lang.psi.types.GoPsiType;
import ro.redeul.google.go.lang.psi.utils.GoPsiUtils;

public class GoSwitchTypeClauseImpl extends GoPsiElementBase
    implements GoSwitchTypeClause {

    public GoSwitchTypeClauseImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public boolean isDefault() {
        return findChildByType(GoTokenTypes.kDEFAULT) != null;
    }

    @NotNull
    @Override
    public GoPsiType[] getTypes() {
        PsiElement list = findChildByType(GoElementTypes.TYPE_LIST);
        if ( list != null ) {
            List<GoPsiType> arguments =
                GoPsiUtils.findChildrenOfType(list, GoPsiType.class);
            return arguments.toArray(new GoPsiType[arguments.size()]);
        }

        return findChildrenByClass(GoPsiType.class);
    }

    @NotNull
    @Override
    public GoStatement[] getStatements() {
        return findChildrenByClass(GoStatement.class);
    }

    @Override
    public boolean processDeclarations(@NotNull PsiScopeProcessor processor,
                                       @NotNull ResolveState state,
                                       PsiElement lastParent,
                                       @NotNull PsiElement place) {
        PsiElement node =
            lastParent != null
                ? lastParent.getPrevSibling()
                : this.getLastChild();

        while (node != null) {
            if (GoElementPatterns.BLOCK_DECLARATIONS.accepts(node)) {
                if (!node.processDeclarations(processor, state, null, place)) {
                    return false;
                }
            }
            node = node.getPrevSibling();
        }

        return true;
    }
}
