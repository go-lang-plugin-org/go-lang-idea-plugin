package ro.redeul.google.go.lang.psi.impl.types;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.impl.GoPsiPackagedElementBase;
import ro.redeul.google.go.lang.psi.types.GoPsiType;
import ro.redeul.google.go.lang.psi.types.GoPsiTypePointer;
import ro.redeul.google.go.lang.psi.types.underlying.GoUnderlyingType;
import ro.redeul.google.go.lang.psi.types.underlying.GoUnderlyingTypePointer;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 5/26/11
 * Time: 11:53 PM
 */
public class GoPsiTypePointerImpl extends GoPsiPackagedElementBase implements
        GoPsiTypePointer {

    public GoPsiTypePointerImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitPointerType(this);
    }

    @Override
    public GoPsiType getTargetType() {
        return findChildByClass(GoPsiType.class);
    }

    @Override
    public GoUnderlyingType getUnderlyingType() {
        return new GoUnderlyingTypePointer(getTargetType().getUnderlyingType());
    }

    @Override
    public boolean isIdentical(GoPsiType goType) {
        return goType instanceof GoPsiTypePointer && this.getTargetType().isIdentical(((GoPsiTypePointer) goType).getTargetType());
    }

    @Override
    public String getPresentationTailText() {
        return String.format("*%s", getTargetType().getPresentationTailText());
    }
}
