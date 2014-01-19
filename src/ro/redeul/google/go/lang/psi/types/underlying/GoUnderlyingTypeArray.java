package ro.redeul.google.go.lang.psi.types.underlying;

import ro.redeul.google.go.inspection.InspectionUtil;

public class GoUnderlyingTypeArray implements GoUnderlyingType {

    private final GoUnderlyingType elementType;
    private final int length;

    public GoUnderlyingTypeArray(GoUnderlyingType elementType, int length) {
        this.elementType = elementType;
        this.length = length;
    }

    @Override
    public boolean isIdentical(GoUnderlyingType other) {
        if ( other instanceof GoUnderlyingTypeArray) {
            GoUnderlyingTypeArray otherArray =
                (GoUnderlyingTypeArray) other;
            if (this.length == InspectionUtil.UNKNOWN_COUNT || otherArray.length == InspectionUtil.UNKNOWN_COUNT)
                return false;
            return length == otherArray.length &&
                otherArray.elementType.isIdentical(elementType);
        }

        return false;
    }
}
