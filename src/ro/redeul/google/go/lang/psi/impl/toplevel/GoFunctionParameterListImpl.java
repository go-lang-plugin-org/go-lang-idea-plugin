package ro.redeul.google.go.lang.psi.impl.toplevel;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameter;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameterList;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 5/26/11
 * Time: 11:01 PM
 */
public class GoFunctionParameterListImpl extends GoPsiElementBase implements GoFunctionParameterList {

    public GoFunctionParameterListImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.acceptFunctionParameterList(this);
    }

    @Override
    @NotNull
    public GoFunctionParameter[] getFunctionParameters() {
        return findChildrenByClass(GoFunctionParameter.class);
    }

    @Override
    public boolean processDeclarations(@NotNull PsiScopeProcessor processor, @NotNull ResolveState state,
                                       PsiElement lastParent,
                                       @NotNull PsiElement place)
    {

        GoFunctionParameter[] parameters = getFunctionParameters();

        for (GoFunctionParameter parameter : parameters) {
            if ( parameter != lastParent ) {
                if ( ! processor.execute(parameter, state) ) {
                    return false;
                }
            }
        }

        return true;
    }
}
