package ro.redeul.google.go.lang.psi.typing;

import ro.redeul.google.go.lang.psi.types.GoPsiTypeSlice;

public class GoTypeSlice extends GoTypePsiBacked<GoPsiTypeSlice> implements GoType {

    private final GoType elementType;

    public GoTypeSlice(GoPsiTypeSlice type) {
        super(type);

        elementType = types().fromPsiType(type.getElementType());
    }

    @Override
    public boolean isIdentical(GoType type) {
        if ( !(type instanceof GoTypeSlice) )
            return false;

        GoTypeSlice otherSlice = (GoTypeSlice)type;

        return elementType.isIdentical(otherSlice.getElementType());
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitSlice(this);
    }

    public GoType getElementType() {
        return elementType;
    }

    @Override
    public String toString() {
        return String.format("[]%s", getElementType());
    }

    public GoType getKeyType() {
        return types().getBuiltin(GoTypes.Builtin.Int);
    }
}
