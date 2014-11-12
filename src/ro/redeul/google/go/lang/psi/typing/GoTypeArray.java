package ro.redeul.google.go.lang.psi.typing;

import ro.redeul.google.go.lang.psi.types.GoPsiTypeArray;

public class GoTypeArray extends GoTypePsiBacked<GoPsiTypeArray> implements GoType {

    private final GoType elementType;

    public GoTypeArray(GoPsiTypeArray type) {
        super(type);
        elementType = types().fromPsiType(type.getElementType());
    }

    @Override
    public boolean isIdentical(GoType type) {
        if (!(type instanceof GoTypeArray)) {
            return false;
        }

        GoTypeArray other = (GoTypeArray) type;

        return getLength() == other.getLength() && elementType.isIdentical(other.getElementType());
    }

    public GoType getElementType() {
        return elementType;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitArray(this);
    }

    public int getLength() {
        return getPsiType().getArrayLength();
    }

    @Override
    public String toString() {
        return String.format("[%d]%s", getLength(), getElementType());
    }

    public GoType getKeyType() {
        return types().getBuiltin(GoTypes.Builtin.Int);
    }
}
