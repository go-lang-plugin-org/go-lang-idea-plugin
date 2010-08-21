package ro.redeul.google.go.lang.psi.impl.toplevel;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.groovy.lang.lexer.GroovyTokenTypes;
import ro.redeul.google.go.lang.lexer.GoTokenTypes;
import ro.redeul.google.go.lang.psi.toplevel.GoPackageDefinition;
import ro.redeul.google.go.lang.psi.expressions.GoIdentifier;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementImpl;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Jul 24, 2010
 * Time: 10:29:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class GoPackageDeclarationImpl extends GoPsiElementImpl implements GoPackageDefinition {

    private static final String MAIN_PACKAGE_NAME = "main";

    public GoPackageDeclarationImpl(@NotNull ASTNode node) {
        super(node);
    }

    public String toString() {
      return "PackageDeclaration(" + getPackageName() + ")";
    }

    @NotNull
    public String getPackageName() {

        PsiElement identifier = findChildByType(GoTokenTypes.mIDENT);
        if ( identifier == null ) {
            return "";
        }

        return identifier.getText();
    }

    public boolean isMainPackage() {
        return getPackageName().equals(MAIN_PACKAGE_NAME);
    }
}
