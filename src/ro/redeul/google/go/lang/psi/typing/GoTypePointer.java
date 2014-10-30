package ro.redeul.google.go.lang.psi.typing;

import ro.redeul.google.go.lang.psi.types.GoPsiTypePointer;
import ro.redeul.google.go.lang.psi.types.underlying.GoUnderlyingTypePointer;
import ro.redeul.google.go.lang.psi.types.underlying.GoUnderlyingTypes;

public class GoTypePointer extends GoAbstractType<GoUnderlyingTypePointer> implements GoType {

    private final GoType targetType;

    public GoTypePointer(GoType targetType) {
        this.targetType = targetType;
        setUnderlyingType(GoUnderlyingTypes.getPointer(targetType.getUnderlyingType()));
    }

    public GoTypePointer(GoPsiTypePointer type) {
        this(GoTypes.fromPsiType(type.getTargetType()));
    }

    public GoType getTargetType() {
        return targetType;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitPointer(this);
    }

    @Override
    public boolean isIdentical(GoType type) {
        return false;
    }
}
