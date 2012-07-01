package ro.redeul.google.go.lang.psi.impl.types;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.impl.GoPsiPackagedElementBase;
import ro.redeul.google.go.lang.psi.types.GoType;
import ro.redeul.google.go.lang.psi.types.GoTypePointer;
import ro.redeul.google.go.lang.psi.types.underlying.GoUnderlyingType;
import ro.redeul.google.go.lang.psi.types.underlying.GoUnderlyingTypePointer;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 5/26/11
 * Time: 11:53 PM
 */
public class GoTypePointerImpl extends GoPsiPackagedElementBase implements GoTypePointer {

    public GoTypePointerImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitPointerType(this);
    }

    @Override
    public GoType getTargetType() {
        return findChildByClass(GoType.class);
    }

    @Override
    public GoUnderlyingType getUnderlyingType() {
        return new GoUnderlyingTypePointer(getTargetType().getUnderlyingType());
    }

    @Override
    public boolean isIdentical(GoType goType) {
        if (!(goType instanceof GoTypePointer))
            return false;

        GoTypePointer otherTypePointer = (GoTypePointer) goType;

        return otherTypePointer.isIdentical(goType);
    }

    @Override
    public String getPresentationTailText() {
        return String.format("*%s", getTargetType().getPresentationTailText());
    }
}
