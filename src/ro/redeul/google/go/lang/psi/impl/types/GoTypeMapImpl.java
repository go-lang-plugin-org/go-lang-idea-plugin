package ro.redeul.google.go.lang.psi.impl.types;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.impl.GoPsiPackagedElementBase;
import ro.redeul.google.go.lang.psi.types.GoType;
import ro.redeul.google.go.lang.psi.types.GoTypeMap;
import ro.redeul.google.go.lang.psi.types.underlying.GoUnderlyingType;
import ro.redeul.google.go.lang.psi.types.underlying.GoUnderlyingTypes;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Sep 2, 2010
 * Time: 12:53:17 PM
 */
public class GoTypeMapImpl extends GoPsiPackagedElementBase implements GoTypeMap {

    public GoTypeMapImpl(@NotNull ASTNode node) {
        super(node);
    }

    public GoType getKeyType() {
        return findChildrenByClass(GoType.class)[0];
    }

    public GoType getElementType() {
        return findChildrenByClass(GoType.class)[1];
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitMapType(this);
    }

    @Override
    public GoUnderlyingType getUnderlyingType() {
        return GoUnderlyingTypes.getMap(getKeyType().getUnderlyingType(),
                                        getElementType().getUnderlyingType());
    }

    @Override
    public boolean isIdentical(GoType goType) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
