package ro.redeul.google.go.lang.psi.types.underlying;

public class GoUnderlyingTypeMap implements GoUnderlyingType {

    public GoUnderlyingTypeMap() {
    }

    @Override
    public boolean isIdentical(GoUnderlyingType other) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitMap(this);
    }
}
