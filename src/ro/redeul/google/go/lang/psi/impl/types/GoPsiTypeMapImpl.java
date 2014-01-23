package ro.redeul.google.go.lang.psi.impl.types;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.impl.GoPsiPackagedElementBase;
import ro.redeul.google.go.lang.psi.types.GoPsiType;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeMap;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeName;
import ro.redeul.google.go.lang.psi.types.underlying.GoUnderlyingType;
import ro.redeul.google.go.lang.psi.types.underlying.GoUnderlyingTypes;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.childAt;
import static ro.redeul.google.go.lang.psi.utils.GoTypeUtils.resolveToFinalType;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Sep 2, 2010
 * Time: 12:53:17 PM
 */
public class GoPsiTypeMapImpl extends GoPsiPackagedElementBase implements
                                                            GoPsiTypeMap {

    public GoPsiTypeMapImpl(@NotNull ASTNode node) {
        super(node);
    }

    public GoPsiType getKeyType() {
        return childAt(0, findChildrenByClass(GoPsiType.class));
    }

    public GoPsiType getElementType() {
        return childAt(1, findChildrenByClass(GoPsiType.class));
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitMapType(this);
    }

    @Override
    public GoUnderlyingType getUnderlyingType() {
        return GoUnderlyingTypes.getMap(
        );
    }

    @Override
    public boolean isIdentical(GoPsiType goType) {
        if (goType instanceof GoPsiTypeName) {
            goType =  resolveToFinalType(goType);
        }
        if (!(goType instanceof GoPsiTypeMap))
            return false;
        GoPsiTypeMap otherTypeMap = (GoPsiTypeMap)goType;
        if (!(getKeyType().isIdentical(otherTypeMap.getKeyType()))){
            return false;
        }
        if (!(getElementType().isIdentical(otherTypeMap.getElementType()))){
            return false;
        }

        return true;
    }

    @Override
    public String getPresentationTailText() {
        return String.format("map[%s]%s",
                             getKeyType().getPresentationTailText(),
                             getElementType().getPresentationTailText());
    }
}
