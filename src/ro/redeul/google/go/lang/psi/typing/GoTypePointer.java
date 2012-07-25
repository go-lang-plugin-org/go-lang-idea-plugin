package ro.redeul.google.go.lang.psi.typing;

import ro.redeul.google.go.lang.psi.types.GoPsiTypePointer;
import ro.redeul.google.go.lang.psi.types.underlying.GoUnderlyingTypePointer;
import ro.redeul.google.go.lang.psi.types.underlying.GoUnderlyingTypes;

/**
 * // TODO: mtoader ! Implement this.
 */
public class GoTypePointer extends GoTypePsiBacked<GoPsiTypePointer, GoUnderlyingTypePointer> implements GoType {

    GoType targetType;

    public GoTypePointer(GoPsiTypePointer type) {
        super(type);
        targetType = GoTypes.fromPsiType(type.getTargetType());

        setUnderlyingType(GoUnderlyingTypes.getPointer(targetType.getUnderlyingType()));
    }

    public GoType getTargetType() {
        return targetType;
    }

    @Override
    public boolean isIdentical(GoType type) {
        return false;
    }
}
