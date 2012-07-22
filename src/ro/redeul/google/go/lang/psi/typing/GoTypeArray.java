package ro.redeul.google.go.lang.psi.typing;

import ro.redeul.google.go.lang.psi.types.GoPsiTypeArray;
import ro.redeul.google.go.lang.psi.types.underlying.GoUnderlyingType;
import ro.redeul.google.go.lang.psi.types.underlying.GoUnderlyingTypeArray;
import ro.redeul.google.go.lang.psi.types.underlying.GoUnderlyingTypes;

/**
 * // TODO: mtoader Implement this
 */
public class GoTypeArray extends GoTypePsiBacked<GoPsiTypeArray, GoUnderlyingTypeArray> implements GoType {
    public GoTypeArray(GoPsiTypeArray type) {
        super(type);
        setUnderlyingType(GoUnderlyingTypes.getArray(GoUnderlyingType.Undefined, 1));
    }

    @Override
    public boolean isIdentical(GoType type) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public GoType getElementType() {
        return GoType.Unknown;
    }
}
