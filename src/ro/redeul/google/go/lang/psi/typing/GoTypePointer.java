package ro.redeul.google.go.lang.psi.typing;

import ro.redeul.google.go.lang.psi.types.GoPsiTypePointer;
import ro.redeul.google.go.lang.psi.types.underlying.GoUnderlyingType;
import ro.redeul.google.go.lang.psi.types.underlying.GoUnderlyingTypePointer;
import ro.redeul.google.go.lang.psi.types.underlying.GoUnderlyingTypes;

/**
 * // TODO: mtoader ! Implement this.
 */
public class GoTypePointer extends GoTypePsiBacked<GoPsiTypePointer, GoUnderlyingTypePointer> implements GoType {

    public GoTypePointer(GoPsiTypePointer type) {
        super(type);
        setUnderlyingType(
            GoUnderlyingTypes.getPointer(GoUnderlyingType.Undefined));
    }

    @Override
    public boolean isIdentical(GoType type) {
        return false;
    }

    public GoType getTargetType() {
        return GoType.Unknown;
    }
}
