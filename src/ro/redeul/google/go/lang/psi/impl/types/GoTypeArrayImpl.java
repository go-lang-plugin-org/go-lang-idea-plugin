package ro.redeul.google.go.lang.psi.impl.types;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.impl.GoPsiPackagedElementBase;
import ro.redeul.google.go.lang.psi.types.GoType;
import ro.redeul.google.go.lang.psi.types.GoTypeArray;
import ro.redeul.google.go.lang.psi.types.underlying.GoUnderlyingTypeArray;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;
import ro.redeul.google.go.lang.psi.types.underlying.GoUnderlyingType;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Aug 30, 2010
 * Time: 9:07:51 PM
 */
public class GoTypeArrayImpl extends GoPsiPackagedElementBase implements GoTypeArray {

    public GoTypeArrayImpl(@NotNull ASTNode node) {
        super(node);
    }

    public GoType getElementType() {
        return findChildByClass(GoType.class);
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitArrayType(this);
    }

    @Override
    public GoPsiElement[] getMembers() {
        return new GoPsiElement[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public GoType getMemberType(String name) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public GoUnderlyingType getUnderlyingType() {
        return new GoUnderlyingTypeArray(getElementType().getUnderlyingType(), 10);
    }

    @Override
    public boolean isIdentical(GoType goType) {
        if (!(goType instanceof GoTypeArray)) {
            return false;
        }

        GoTypeArray otherTypeArray = (GoTypeArray) goType;

        // TODO: added check to the length here.
        return getElementType().isIdentical(otherTypeArray.getElementType());
    }
}
