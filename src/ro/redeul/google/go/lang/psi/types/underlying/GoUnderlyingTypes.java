package ro.redeul.google.go.lang.psi.types.underlying;

import ro.redeul.google.go.lang.psi.types.GoTypes;

public class GoUnderlyingTypes {

    public static GoUnderlyingType getMap(GoUnderlyingType keyType,
                                          GoUnderlyingType elementType) {
        return new GoUnderlyingTypeMap(keyType, elementType);
    }

    public static GoUnderlyingType getPredeclared(GoTypes.Builtin type) {
        return GoUnderlyingTypePredeclared.getForType(type);
    }

    public static GoUnderlyingType getPointer(GoUnderlyingType target) {
        return new GoUnderlyingTypePointer(target);
    }

    public static GoUnderlyingType getSlice(GoUnderlyingType memberType) {
        return new GoUnderlyingTypeSlice(memberType);
    }

    public static GoUnderlyingType getArray(GoUnderlyingType memberType, int size) {
        return new GoUnderlyingTypeArray(memberType, size);
    }

    public static GoUnderlyingType getFunction() {
        return null;  //To change body of created methods use File | Settings | File Templates.
    }

    public static GoUnderlyingType getInterface() {
        return new GoUnderlyingTypeInterface();
    }

    public static GoUnderlyingType getStruct() {
        return new GoUnderlyingTypeStruct();
    }
}
