package ro.redeul.google.go.lang.psi.impl.types;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.inspection.InspectionUtil;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.literals.composite.GoLiteralCompositeValue;
import ro.redeul.google.go.lang.psi.impl.GoPsiPackagedElementBase;
import ro.redeul.google.go.lang.psi.impl.expressions.literals.composite.GoLiteralCompositeValueImpl;
import ro.redeul.google.go.lang.psi.types.GoPsiType;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeArray;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeName;
import ro.redeul.google.go.lang.psi.types.underlying.GoUnderlyingType;
import ro.redeul.google.go.lang.psi.types.underlying.GoUnderlyingTypeArray;
import ro.redeul.google.go.lang.psi.utils.GoPsiUtils;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

import static ro.redeul.google.go.lang.psi.utils.GoTypeUtils.resolveToFinalType;
import static ro.redeul.google.go.inspection.FunctionCallInspection.getNumberValueFromLiteralExpr;

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
    public int getArrayLength() {
        GoExpr child = GoPsiUtils.findChildOfClass(this, GoExpr.class);
        if (child != null) {
            Number value = getNumberValueFromLiteralExpr(child);
            if (value != null && (value instanceof Integer || value.intValue() == value.floatValue()))
                return value.intValue();

        } else {
            // for this case [...]type{el1, el2, el3}
            GoLiteralCompositeValue compositeValue = GoPsiUtils.findChildOfClass(this.getParent(), GoLiteralCompositeValueImpl.class);
            if (compositeValue != null)
                return compositeValue.getChildren().length;
        }
        return InspectionUtil.UNKNOWN_COUNT;
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
        return new GoUnderlyingTypeArray(getElementType().getUnderlyingType(), getArrayLength());
    }

    @Override
    public boolean isIdentical(GoPsiType goType) {
        if (goType instanceof GoPsiTypeName) {
            goType = resolveToFinalType(goType);
        }
        if (!(goType instanceof GoPsiTypeArray)) {
            return false;
        }
        GoPsiTypeArray otherTypeArray = (GoPsiTypeArray) goType;
        return getUnderlyingType().isIdentical(otherTypeArray.getUnderlyingType());
    }

    @NotNull
    @Override
    public String getPresentationText() {
        return String.format("[]%s", getElementType().getPresentationText());
    }
}
