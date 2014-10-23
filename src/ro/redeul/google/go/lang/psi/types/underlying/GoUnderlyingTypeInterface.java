package ro.redeul.google.go.lang.psi.types.underlying;

public class GoUnderlyingTypeInterface implements GoUnderlyingType {
    @Override
    public boolean isIdentical(GoUnderlyingType other) {
        return false;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitInterface(this);
    }
}
