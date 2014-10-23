package ro.redeul.google.go.lang.psi.types.underlying;

public class GoUnderlyingTypeStruct implements GoUnderlyingType {
    @Override
    public boolean isIdentical(GoUnderlyingType other) {
        if (!(other instanceof GoUnderlyingTypeStruct))
            return false;

        // TODO: implement this.
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitStruct(this);
    }
}
