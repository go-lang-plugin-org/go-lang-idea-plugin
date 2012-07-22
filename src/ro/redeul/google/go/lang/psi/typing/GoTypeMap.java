package ro.redeul.google.go.lang.psi.typing;

import ro.redeul.google.go.lang.psi.types.GoPsiTypeMap;
import ro.redeul.google.go.lang.psi.types.underlying.GoUnderlyingType;
import ro.redeul.google.go.lang.psi.types.underlying.GoUnderlyingTypeMap;
import ro.redeul.google.go.lang.psi.types.underlying.GoUnderlyingTypes;

/**
 * // TODO: mtoader ! Please explain yourself.
 */
public class GoTypeMap
    extends GoTypePsiBacked<GoPsiTypeMap, GoUnderlyingTypeMap>
    implements GoType {

    public GoTypeMap(GoPsiTypeMap type) {
        super(type);
        setUnderlyingType(
            GoUnderlyingTypes.getMap(GoUnderlyingType.Undefined,
                                     GoUnderlyingType.Undefined));
    }

    @Override
    public boolean isIdentical(GoType type) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public GoType getElementType() {
        return GoType.Unknown;
    }
}
