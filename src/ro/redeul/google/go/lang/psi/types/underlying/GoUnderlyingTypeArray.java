package ro.redeul.google.go.lang.psi.types.underlying;

public class GoUnderlyingTypeArray implements GoUnderlyingType {

    GoUnderlyingType elementType;
    int length;

    public GoUnderlyingTypeArray(GoUnderlyingType elementType, int length) {
        this.elementType = elementType;
        this.length = length;
    }

    @Override
    public boolean isIdentical(GoUnderlyingType other) {
        if ( other instanceof GoUnderlyingTypeArray) {
            GoUnderlyingTypeArray otherArray =
                (GoUnderlyingTypeArray) other;

            return length == otherArray.length &&
                otherArray.elementType.isIdentical(elementType);
        }

        return false;
    }
}
