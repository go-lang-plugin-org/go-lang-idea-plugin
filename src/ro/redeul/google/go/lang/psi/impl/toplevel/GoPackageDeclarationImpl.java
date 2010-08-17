package ro.redeul.google.go.lang.psi.impl.toplevel;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
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
    
    public GoPackageDeclarationImpl(@NotNull ASTNode node) {
        super(node);
    }

    public String toString() {
      return "package Declaration";
    }

    public GoIdentifier getPackage() {
        return findChildByClass(GoIdentifier.class);
    }
}
