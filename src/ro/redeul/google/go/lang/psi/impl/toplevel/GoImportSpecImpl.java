package ro.redeul.google.go.lang.psi.impl.toplevel;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.lexer.GoTokenTypes;
import ro.redeul.google.go.lang.psi.GoPackageReference;
import ro.redeul.google.go.lang.psi.GoQualifiedNameElement;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementImpl;
import ro.redeul.google.go.lang.psi.toplevel.GoImportSpec;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Jul 24, 2010
 * Time: 11:31:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class GoImportSpecImpl extends GoPsiElementImpl implements GoImportSpec {
    public GoImportSpecImpl(@NotNull ASTNode node) {
        super(node);
    }

    public GoPackageReference getPackageReference() {
        return findChildByClass(GoPackageReference.class);
    }

    public String getImportPath() {
        PsiElement stringLiteral = findChildByType(GoTokenTypes.litSTRING);
        
        return stringLiteral != null ? stringLiteral.getText() : "";
    }

    public void accept(GoElementVisitor visitor) {
        visitor.visitImportSpec(this);
    }

    @Override
    public boolean processDeclarations(@NotNull PsiScopeProcessor processor, @NotNull ResolveState state, PsiElement lastParent, @NotNull PsiElement place) {

        // import _ "a"; ( no definitions )
        if ( getPackageReference() != null && getPackageReference().isBlank() ) {
            return true;
        }

        if ( place instanceof GoQualifiedNameElement) {

            GoQualifiedNameElement qualifiedNameElement = (GoQualifiedNameElement) place;

            GoPackageReference importedPackageReference = getPackageReference();
            GoPackageReference elementReference = qualifiedNameElement.getPackageReference();

            // import "a"; var x a.T;
            if ( importedPackageReference == null && elementReference != null && elementReference.getString().equals(defaultPackageNameFromImport(getImportPath())) ) {
                if ( ! processor.execute(this, state) ) {
                    return false;
                }
            }

            // import . "a"; var x T; // T is defined inside package a
            if ( importedPackageReference != null && importedPackageReference.isLocal() && elementReference == null ) {
                if ( ! processor.execute(this, state) ) {
                    return false;
                }
            }

            // import x "a"; var x.T;
            if ( importedPackageReference != null && elementReference != null && elementReference.getString().equals(importedPackageReference.getString())) {
                if ( ! processor.execute(this, state) ) {
                    return false;
                }
            }
        }

        return true;
    }

    private String defaultPackageNameFromImport(String importPath) {
        return importPath.replaceAll("\"", "").replaceAll("(?:[a-zA-Z]+/)+", "");
    }
}
