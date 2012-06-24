package ro.redeul.google.go.lang.psi.types.underlying;

public class GoUnderlyingTypeMap implements GoUnderlyingType {

    private final GoUnderlyingType keyType;
    private final GoUnderlyingType elementType;

    public GoUnderlyingTypeMap(GoUnderlyingType keyType,
                               GoUnderlyingType elementType) {
        this.keyType = keyType;
        this.elementType = elementType;
    }

    @Override
    public boolean isIdentical(GoUnderlyingType other) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
