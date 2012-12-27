package ro.redeul.google.go.lang.psi.typing;

import ro.redeul.google.go.lang.psi.types.GoPsiTypeArray;
import ro.redeul.google.go.lang.psi.types.underlying.GoUnderlyingTypeArray;
import ro.redeul.google.go.lang.psi.types.underlying.GoUnderlyingTypes;

public class GoTypeArray extends GoAbstractType<GoUnderlyingTypeArray> implements GoType {

    GoType elementType;

    public GoTypeArray(GoPsiTypeArray type) {
        this(GoTypes.fromPsiType(type.getElementType()));
    }

    public GoTypeArray(GoType elementType) {
        this.elementType = elementType;
        setUnderlyingType(GoUnderlyingTypes.getArray(elementType.getUnderlyingType(), 1));
    }

    @Override
    public boolean isIdentical(GoType type) {
        if ( !(type instanceof GoTypeArray) ) {
            return false;
        }

        GoTypeArray otherArray = (GoTypeArray)type;

        return elementType.isIdentical(otherArray.getElementType());
    }

    public GoType getElementType() {
        return elementType;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitTypeArray(this);
    }
}
