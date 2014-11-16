package ro.redeul.google.go.lang.psi.impl.types;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoMethodDeclaration;
import ro.redeul.google.go.lang.psi.types.GoPsiType;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeInterface;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeName;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 5/29/11
 * Time: 2:14 PM
 */
public class GoPsiTypeInterfaceImpl extends GoPsiTypeImpl implements GoPsiTypeInterface {

    public GoPsiTypeInterfaceImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitInterfaceType(this);
    }

    @Override
    public GoMethodDeclaration[] getMethodSet() {
        return GoMethodDeclaration.EMPTY_ARRAY;
    }

    @Override
    public GoFunctionDeclaration[] getFunctionDeclarations() {
        return findChildrenByClass(GoFunctionDeclaration.class);
    }

    @Override
    public GoPsiTypeName[] getTypeNames() {
        return findChildrenByClass(GoPsiTypeName.class);
    }


    @Override
    public String getLookupTailText() {
        return "interface{}";
    }
}
