package ro.redeul.google.go.lang.psi.types.underlying;

public class GoUnderlyingTypePointer implements GoUnderlyingType {

    private GoUnderlyingType baseType;

    public GoUnderlyingTypePointer(GoUnderlyingType baseType) {
        this.baseType = baseType;
    }

    public GoUnderlyingType getBaseType() {
        return baseType;
    }

    @Override
    public boolean isIdentical(GoUnderlyingType other) {
        if (other instanceof GoUnderlyingTypePointer) {
            GoUnderlyingTypePointer otherPointer =
                (GoUnderlyingTypePointer) other;

            return baseType.isIdentical(otherPointer.baseType);
        }

        return false;
    }
}
