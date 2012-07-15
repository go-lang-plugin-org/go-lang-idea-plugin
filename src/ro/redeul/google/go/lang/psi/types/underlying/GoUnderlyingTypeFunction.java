package ro.redeul.google.go.lang.psi.types.underlying;

import ro.redeul.google.go.lang.psi.types.GoTypeFunction;

public class GoUnderlyingTypeFunction implements GoUnderlyingType {


    private GoTypeFunction functionType;

    public GoUnderlyingTypeFunction(GoTypeFunction functionType) {
        this.functionType = functionType;
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
