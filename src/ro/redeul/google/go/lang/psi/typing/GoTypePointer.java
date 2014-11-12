package ro.redeul.google.go.lang.psi.typing;

import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.types.GoPsiTypePointer;

public class GoTypePointer extends GoAbstractType implements GoType {

    private final GoType targetType;

    public GoTypePointer(@NotNull GoType targetType) {
        this.targetType = targetType;
    }

    public GoTypePointer(GoPsiTypePointer type) {
        this(GoTypes.getInstance(type.getProject()).fromPsiType(type.getTargetType()));
    }

    @NotNull
    public GoType getTargetType() {
        return targetType;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitPointer(this);
    }

    @Override
    public boolean isIdentical(GoType type) {
        return type instanceof GoTypePointer && (getTargetType().isIdentical(((GoTypePointer) type).getTargetType()));
    }

    @Override
    public boolean isAssignableFrom(GoType source) {
        return super.isAssignableFrom(source);
    }

    @Override
    public String toString() {
        return String.format("*%s", getTargetType());
    }
}
