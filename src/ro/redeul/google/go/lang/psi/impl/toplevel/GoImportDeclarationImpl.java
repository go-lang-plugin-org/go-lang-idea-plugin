package ro.redeul.google.go.lang.psi.impl.toplevel;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementImpl;
import ro.redeul.google.go.lang.psi.toplevel.GoImportDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoImportSpec;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Jul 24, 2010
 * Time: 11:29:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class GoImportDeclarationImpl extends GoPsiElementImpl implements GoImportDeclaration {

    public GoImportDeclarationImpl(@NotNull ASTNode node) {
        super(node);
    }

    public GoImportSpec[] getImports() {
        return findChildrenByClass(GoImportSpec.class); 
    }

//    public String toString() {
//      return "import Declaration";
//    }
}
