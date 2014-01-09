package ro.redeul.google.go.lang.psi.impl.types;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.primary.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.impl.GoPsiPackagedElementBase;
import ro.redeul.google.go.lang.psi.types.GoPsiType;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeArray;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeName;
import ro.redeul.google.go.lang.psi.types.underlying.GoUnderlyingType;
import ro.redeul.google.go.lang.psi.types.underlying.GoUnderlyingTypeArray;
import ro.redeul.google.go.lang.psi.utils.GoPsiUtils;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

import static ro.redeul.google.go.lang.psi.utils.GoTypeUtils.resolveToFinalType;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Aug 30, 2010
 * Time: 9:07:51 PM
 */
public class GoPsiTypeArrayImpl extends GoPsiPackagedElementBase implements
        GoPsiTypeArray {

    public GoPsiTypeArrayImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public String getArrayLength() {
        return GoPsiUtils.findChildOfClass(this, GoLiteralExpression.class).getLiteral().getText();
    }

    public GoPsiType getElementType() {
        return findChildByClass(GoPsiType.class);
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitArrayType(this);
    }

    @Override
    public GoUnderlyingType getUnderlyingType() {
        return new GoUnderlyingTypeArray(getElementType().getUnderlyingType(), 10);
    }

    @Override
    public boolean isIdentical(GoPsiType goType) {
        if (goType instanceof GoPsiTypeName) {
            goType =  resolveToFinalType(goType);
        }
        if (!(goType instanceof GoPsiTypeArray)) {
            return false;
        }

        GoPsiTypeArray otherTypeArray = (GoPsiTypeArray) goType;

        if (!(getArrayLength().equals(otherTypeArray.getArrayLength()))) {
            return false;
        }
        return getElementType().isIdentical(otherTypeArray.getElementType());
    }

    @NotNull
    @Override
    public String getPresentationText() {
        return String.format("[]%s", getElementType().getPresentationText());
    }
}
