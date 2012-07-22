package ro.redeul.google.go.lang.psi.types.underlying;

import ro.redeul.google.go.lang.psi.types.GoPsiTypeFunction;

public class GoUnderlyingTypeFunction implements GoUnderlyingType {


    private GoPsiTypeFunction functionType;

    public GoUnderlyingTypeFunction(GoPsiTypeFunction functionType) {
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
