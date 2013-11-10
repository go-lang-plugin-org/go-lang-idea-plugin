package ro.redeul.google.go.lang.psi.types.underlying;

public class GoUnderlyingTypeFunction implements GoUnderlyingType {


    public GoUnderlyingTypeFunction() {
    }

    @Override
    public boolean isIdentical(GoUnderlyingType other) {
        if ( other instanceof GoUnderlyingTypeFunction) {
            GoUnderlyingTypeFunction otherArray =
                (GoUnderlyingTypeFunction) other;

            return true;
        }

        return false;
    }
}
