package ro.redeul.google.go.lang.psi.impl.types;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.types.GoType;
import ro.redeul.google.go.lang.psi.types.GoTypeInterface;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 5/29/11
 * Time: 2:14 PM
 */
public class GoTypeInterfaceImpl extends GoPsiElementBase implements GoTypeInterface {

    public GoTypeInterfaceImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public GoPsiElement[] getMembers() {
        return new GoPsiElement[0];
    }

    @Override
    public GoType getMemberType(String name) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
