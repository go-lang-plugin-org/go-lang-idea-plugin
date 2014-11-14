package ro.redeul.google.go.lang.psi.typing;


import org.jetbrains.annotations.NotNull;

public final class GoTypeVariadic extends GoAbstractType implements GoType {
    private GoType targetType;

    GoTypeVariadic(GoType type) {
        setTargetType(type);
    }

    @Override
    public boolean isIdentical(GoType type) {
        if (!(type instanceof GoTypeVariadic))
            return false;

        return getTargetType().isIdentical(((GoTypeVariadic) type).getTargetType());
    }

    @NotNull
    @Override
    public GoType underlyingType() {
        return this;
    }

    @Override
    public <T> T accept(TypeVisitor<T> visitor) {
        return visitor.visitVariadic(this);
    }

    public GoType getTargetType() {
        return targetType;
    }

    public void setTargetType(GoType targetType) {
        this.targetType = targetType;
    }
}
