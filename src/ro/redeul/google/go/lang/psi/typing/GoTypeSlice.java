package ro.redeul.google.go.lang.psi.typing;

import ro.redeul.google.go.lang.psi.types.GoPsiTypeSlice;
import ro.redeul.google.go.lang.psi.types.underlying.GoUnderlyingType;
import ro.redeul.google.go.lang.psi.types.underlying.GoUnderlyingTypeSlice;
import ro.redeul.google.go.lang.psi.types.underlying.GoUnderlyingTypes;

/**
 * // TODO: mtoader ! Please explain yourself.
 */
public class GoTypeSlice extends GoTypePsiBacked<GoPsiTypeSlice, GoUnderlyingTypeSlice> implements GoType {

    public GoTypeSlice(GoPsiTypeSlice type) {
        super(type);
        setUnderlyingType(
            GoUnderlyingTypes.getSlice(GoUnderlyingType.Undefined)
        );
    }

    @Override
    public boolean isIdentical(GoType type) {
        return false;
    }

    public GoType getElementType() {
        return GoType.Unknown;
    }
}
