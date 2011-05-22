package ro.redeul.google.go.lang.psi.processors;

import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.BaseScopeProcessor;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeNameDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeSpec;
import ro.redeul.google.go.lang.psi.types.GoTypeName;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Sep 21, 2010
 * Time: 4:47:46 AM
 */
public class NamedTypeResolver extends BaseScopeProcessor {

    private PsiElement resolvedTypeName;
    private GoTypeName targetName;

    public NamedTypeResolver(GoTypeName targetName) {
        this.targetName = targetName;
    }

    public boolean execute(PsiElement element, ResolveState state) {

        if ( element instanceof GoTypeSpec ) {
            if ( tryFindTypeName((GoTypeSpec)element, state) ) {
                return false;
            }
        }

        return true;
    }

    private boolean tryFindTypeName(GoTypeSpec typeSpec, ResolveState state) {
        GoTypeNameDeclaration typeNameDeclaration = typeSpec.getTypeNameDeclaration();

        if ( typeNameDeclaration == null ) {
            return false;
        }

        String declaredTypeName = typeNameDeclaration.getName();
        String visiblePackageName = state.get(GoResolveStates.VisiblePackageName);

        String fqm = String.format("%s%s", visiblePackageName != null ? visiblePackageName + "." : "", declaredTypeName);

        if ( fqm.equals(this.targetName.getText())) {
            resolvedTypeName = typeNameDeclaration;
            return true;
        }

        return false;
    }

    public PsiElement getResolvedTypeName() {
        return resolvedTypeName;
    }
}
