package ro.redeul.google.go.lang.psi.typing;

import ro.redeul.google.go.lang.psi.types.GoPsiTypeMap;

public class GoTypeMap
    extends GoTypePsiBacked<GoPsiTypeMap>
    implements GoType {

    private final GoType keyType;
    private final GoType elementType;

    public GoTypeMap(GoPsiTypeMap type) {
        super(type);

        keyType = types().fromPsiType(type.getKeyType());
        elementType = types().fromPsiType(type.getElementType());
    }

    @Override
    public boolean isIdentical(GoType type) {
        if (!(type instanceof GoTypeMap))
            return false;

        GoTypeMap typeMap = (GoTypeMap) type;

        return getKeyType().isIdentical(typeMap.getKeyType()) &&
                getElementType().isIdentical(typeMap.getElementType());
    }

    @Override
    public <T> T accept(TypeVisitor<T> visitor) {
        return visitor.visitMap(this);
    }

    public GoType getKeyType() {
        return keyType;
    }

    public GoType getElementType() {
        return elementType;
    }

    @Override
    public String toString() {
        return String.format("map[%s]%s", getKeyType(), getElementType());
    }
}
