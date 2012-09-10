package ro.redeul.google.go.lang.psi.typing;

import ro.redeul.google.go.lang.psi.types.GoPsiTypeMap;
import ro.redeul.google.go.lang.psi.types.underlying.GoUnderlyingTypeMap;
import ro.redeul.google.go.lang.psi.types.underlying.GoUnderlyingTypes;

public class GoTypeMap
    extends GoTypePsiBacked<GoPsiTypeMap, GoUnderlyingTypeMap>
    implements GoType {

    GoType keyType;
    GoType elementType;

    public GoTypeMap(GoPsiTypeMap type) {
        super(type);

        keyType = GoTypes.fromPsiType(type.getKeyType());
        elementType = GoTypes.fromPsiType(type.getElementType());

        setUnderlyingType(
            GoUnderlyingTypes.getMap(keyType.getUnderlyingType(),
                                     elementType.getUnderlyingType()));

    }

    @Override
    public boolean isIdentical(GoType type) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitTypeMap(this);
    }

    public GoType getKeyType() {
        return keyType;
    }

    public GoType getElementType() {
        return elementType;
    }
}
