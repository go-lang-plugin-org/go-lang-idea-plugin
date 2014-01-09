package ro.redeul.google.go.lang.psi.impl.types;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.impl.GoPsiPackagedElementBase;
import ro.redeul.google.go.lang.psi.types.GoPsiType;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeName;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeSlice;
import ro.redeul.google.go.lang.psi.types.underlying.GoUnderlyingType;
import ro.redeul.google.go.lang.psi.types.underlying.GoUnderlyingTypeSlice;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

import static ro.redeul.google.go.lang.psi.utils.GoTypeUtils.resolveToFinalType;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Sep 2, 2010
 * Time: 12:50:44 PM
 */
public class GoPsiTypeSliceImpl extends GoPsiPackagedElementBase implements
                                                              GoPsiTypeSlice {
    public GoPsiTypeSliceImpl(@NotNull ASTNode node) {
        super(node);
    }

    public GoPsiType getElementType() {
        return findChildByClass(GoPsiType.class);
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitSliceType(this);
    }

    @Override
    public GoUnderlyingType getUnderlyingType() {
        return new GoUnderlyingTypeSlice(getElementType().getUnderlyingType());
    }

    @Override
    public boolean isIdentical(GoPsiType goType) {
        if (goType instanceof GoPsiTypeName) {
            goType =  resolveToFinalType(goType);
        }
        if (!(goType instanceof GoPsiTypeSlice))
            return false;

        GoPsiTypeSlice otherTypeSlice = (GoPsiTypeSlice)goType;

        return getElementType().isIdentical(otherTypeSlice.getElementType());
    }

    @NotNull
    @Override
    public String getPresentationTailText() {
        return String.format("[]%s", getElementType().getPresentationTailText());
    }
}
