package ro.redeul.google.go.lang.psi.typing;

import ro.redeul.google.go.lang.psi.GoPackage;

public class GoTypePackage extends GoAbstractType implements GoType {

    private GoPackage goPackage;

    public GoTypePackage(GoPackage goPackage) {
        this.goPackage = goPackage;
    }

    @Override
    public GoType getUnderlyingType() {
        return this;
    }

    @Override
    public boolean isIdentical(GoType type) {
        return false;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitPackage(this);
    }

    public GoPackage getPackage() {
        return goPackage;
    }

    @Override
    public boolean isAssignableFrom(GoType source) {
        return false;
    }
}
