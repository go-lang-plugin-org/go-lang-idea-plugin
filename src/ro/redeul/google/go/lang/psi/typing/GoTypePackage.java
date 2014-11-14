package ro.redeul.google.go.lang.psi.typing;

import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.GoPackage;

public class GoTypePackage extends GoAbstractType implements GoType {

    private GoPackage goPackage;

    public GoTypePackage(GoPackage goPackage) {
        this.goPackage = goPackage;
    }

    @NotNull
    @Override
    public GoType underlyingType() {
        return this;
    }

    @Override
    public <T> T accept(TypeVisitor<T> visitor) {
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
