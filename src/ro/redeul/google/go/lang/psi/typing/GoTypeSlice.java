package ro.redeul.google.go.lang.psi.typing;

import ro.redeul.google.go.lang.psi.types.GoPsiTypeSlice;
import ro.redeul.google.go.lang.psi.types.underlying.GoUnderlyingTypeSlice;
import ro.redeul.google.go.lang.psi.types.underlying.GoUnderlyingTypes;

/**
 * // TODO: mtoader ! Please explain yourself.
 */
public class GoTypeSlice extends GoTypePsiBacked<GoPsiTypeSlice, GoUnderlyingTypeSlice> implements GoType {

    private final GoType elementType;

    public GoTypeSlice(GoPsiTypeSlice type) {
        super(type);

        elementType = GoTypes.fromPsiType(type.getElementType());

        setUnderlyingType(GoUnderlyingTypes.getSlice(elementType.getUnderlyingType()));
    }

    @Override
    public boolean isIdentical(GoType type) {
        if ( !(type instanceof GoTypeSlice) )
            return false;

        GoTypeSlice otherSlice = (GoTypeSlice)type;
        return elementType.isIdentical(otherSlice.getElementType());
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitTypeSlice(this);
    }

    public GoType getElementType() {
        return elementType;
    }
}
