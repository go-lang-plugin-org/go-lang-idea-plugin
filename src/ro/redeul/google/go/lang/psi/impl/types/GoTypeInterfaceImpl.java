package ro.redeul.google.go.lang.psi.impl.types;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.impl.GoPsiPackagedElementBase;
import ro.redeul.google.go.lang.psi.toplevel.GoMethodDeclaration;
import ro.redeul.google.go.lang.psi.types.GoType;
import ro.redeul.google.go.lang.psi.types.GoTypeInterface;
import ro.redeul.google.go.lang.psi.types.GoTypeName;
import ro.redeul.google.go.lang.psi.types.underlying.GoUnderlyingType;
import ro.redeul.google.go.lang.psi.types.underlying.GoUnderlyingTypes;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 5/29/11
 * Time: 2:14 PM
 */
public class GoTypeInterfaceImpl extends GoPsiPackagedElementBase implements GoTypeInterface {

    public GoTypeInterfaceImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public GoUnderlyingType getUnderlyingType() {
        return GoUnderlyingTypes.getInterface();
    }

    @Override
    public boolean isIdentical(GoType goType) {
        if ( !(goType instanceof GoTypeInterface))
            return false;

        // TODO: implement this.
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitInterfaceType(this);
    }

    @Override
    public GoMethodDeclaration[] getMethodSet() {
//        return declarations;
        return GoMethodDeclaration.EMPTY_ARRAY;
    }

    @Override
    public GoMethodDeclaration[] getMethodDeclarations() {
        return findChildrenByClass(GoMethodDeclaration.class);
    }

    @Override
    public GoTypeName[] getTypeNames() {
        return findChildrenByClass(GoTypeName.class);
    }


    @Override
    public String getPresentationTailText() {
        return "interface{}";
    }
}
