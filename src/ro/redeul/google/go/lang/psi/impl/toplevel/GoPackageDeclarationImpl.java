package ro.redeul.google.go.lang.psi.impl.toplevel;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.lexer.GoTokenTypes;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.toplevel.GoPackageDeclaration;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

/**
 * User: mtoader
 * Date: Jul 24, 2010
 * Time: 10:29:46 PM
 */
public class GoPackageDeclarationImpl extends GoPsiElementBase implements GoPackageDeclaration {

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
        if (identifier == null) {
            return "";
        }

        return identifier.getText();
    }

    public boolean isMainPackage() {
        return getPackageName().equals(MAIN_PACKAGE_NAME);
    }

    public void accept(GoElementVisitor visitor) {
        visitor.visitPackageDeclaration(this);
    }
}
