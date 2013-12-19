package ro.redeul.google.go.lang.psi.impl.toplevel;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.search.SearchScope;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.processors.GoNamesUtil;
import ro.redeul.google.go.lang.psi.processors.GoResolveStates;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeNameDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeSpec;
import ro.redeul.google.go.lang.psi.types.GoPsiType;
import ro.redeul.google.go.lang.psi.utils.GoPsiUtils;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Aug 30, 2010
 * Time: 9:01:30 PM
 */
public class GoTypeSpecImpl extends GoPsiElementBase implements GoTypeSpec {

    public GoTypeSpecImpl(@NotNull ASTNode node) {
        super(node);
    }

    public GoTypeNameDeclaration getTypeNameDeclaration() {
        return findChildByClass(GoTypeNameDeclaration.class);
    }

    public GoPsiType getType() {
        return findChildByClass(GoPsiType.class);
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitTypeSpec(this);
    }

    @Override
    public boolean processDeclarations(@NotNull PsiScopeProcessor processor,
                                       @NotNull ResolveState state,
                                       PsiElement lastParent,
                                       @NotNull PsiElement place) {
        return !"builtin".equals(state.get(GoResolveStates.PackageName)) && !state.get(GoResolveStates.IsOriginalPackage) && !GoNamesUtil.isExportedName(getName()) || processor.execute(this, state);

    }

    @Override
    public PsiElement setName(@NonNls @NotNull String name) throws IncorrectOperationException {
        return null;
    }

    @Override
    public String getName() {
        if (getTypeNameDeclaration() == null) {
            return "";
        }

        return getTypeNameDeclaration().getName();
    }

    @NotNull
    @Override
    public SearchScope getUseScope() {
        return GoPsiUtils.getGlobalElementSearchScope(this, getName());
    }
}
