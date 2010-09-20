package ro.redeul.google.go.lang.psi.impl;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.GoPackageReference;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Sep 4, 2010
 * Time: 10:41:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class GoPackageReferenceImpl extends GoPsiElementImpl implements GoPackageReference {

    public GoPackageReferenceImpl(@NotNull ASTNode node) {
        super(node);
    }

    public boolean isBlank() {
        return getString().equals("_");
    }

    public boolean isLocal() {
        return getString().equals(".");
    }

    public String getString() {
        return getText();
    }


}
