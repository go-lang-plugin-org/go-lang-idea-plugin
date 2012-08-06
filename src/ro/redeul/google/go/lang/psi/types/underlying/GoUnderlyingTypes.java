package ro.redeul.google.go.lang.psi.types.underlying;

import ro.redeul.google.go.lang.psi.types.GoPsiTypeFunction;
import ro.redeul.google.go.lang.psi.typing.GoTypeChannel;
import ro.redeul.google.go.lang.psi.typing.GoTypes;

public class GoUnderlyingTypes {

    public static GoUnderlyingTypeMap getMap(GoUnderlyingType keyType,
                                          GoUnderlyingType elementType) {
        return new GoUnderlyingTypeMap(keyType, elementType);
    }

    public static GoUnderlyingType getPredeclared(GoTypes.Builtin type) {
        return GoUnderlyingTypePredeclared.getForType(type);
    }

    public static GoUnderlyingTypePointer getPointer(GoUnderlyingType target) {
        return new GoUnderlyingTypePointer(target);
    }

    public static GoUnderlyingTypeSlice getSlice(GoUnderlyingType memberType) {
        return new GoUnderlyingTypeSlice(memberType);
    }

    public static GoUnderlyingTypeArray getArray(GoUnderlyingType memberType, int size) {
        return new GoUnderlyingTypeArray(memberType, size);
    }

    public static GoUnderlyingTypeFunction getFunction(GoPsiTypeFunction functionType) {
        return new GoUnderlyingTypeFunction(functionType);
    }

    public static GoUnderlyingTypeInterface getInterface() {
        return new GoUnderlyingTypeInterface();
    }

    public static GoUnderlyingTypeStruct getStruct() {
        return new GoUnderlyingTypeStruct();
    }

    public static GoUnderlyingTypeChannel getChannel(GoTypeChannel.ChannelType channelType, GoUnderlyingType elementType) {
        return new GoUnderlyingTypeChannel(channelType, elementType);
    }
}
