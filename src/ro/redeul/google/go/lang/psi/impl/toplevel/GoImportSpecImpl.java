package ro.redeul.google.go.lang.psi.impl.toplevel;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.GoIdentifier;
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

    public GoIdentifier getPackageName() {
        return null;
    }

    public String getImportPath() {
        return "";
    }

//    @Override
//    public String toString() {
//        return String.format("import as %s form %s", "XXX", "XXX");
//    }
}
