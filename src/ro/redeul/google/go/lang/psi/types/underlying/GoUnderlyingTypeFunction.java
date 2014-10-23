package ro.redeul.google.go.lang.psi.types.underlying;

public class GoUnderlyingTypeFunction implements GoUnderlyingType {


    public GoUnderlyingTypeFunction() {
    }

    @Override
    public boolean isIdentical(GoUnderlyingType other) {
        return other instanceof GoUnderlyingTypeFunction;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitFunction(this);
    }
}
