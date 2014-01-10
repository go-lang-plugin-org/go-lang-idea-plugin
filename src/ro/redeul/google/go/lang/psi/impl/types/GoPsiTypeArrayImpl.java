package ro.redeul.google.go.lang.psi.impl.types;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteral;
import ro.redeul.google.go.lang.psi.expressions.literals.composite.GoLiteralCompositeValue;
import ro.redeul.google.go.lang.psi.expressions.primary.GoLiteralExpression;
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
        GoLiteralExpression child = GoPsiUtils.findChildOfClass(this, GoLiteralExpression.class);
        if (child != null) {
            GoLiteral literal = child.getLiteral();
            String length = literal.getText();
            try {
                return Integer.parseInt(length);
            } catch (NumberFormatException e){
                // return default value
            }
        } else {
            GoLiteralCompositeValue compositeValue = GoPsiUtils.findChildOfClass(this.getParent(), GoLiteralCompositeValueImpl.class);
            if (compositeValue != null)
                return compositeValue.getChildren().length;
        }
        return 0;
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
