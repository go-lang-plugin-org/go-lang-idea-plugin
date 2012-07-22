package ro.redeul.google.go.lang.psi.typing;

import ro.redeul.google.go.lang.psi.types.GoPsiTypeFunction;
import ro.redeul.google.go.lang.psi.types.underlying.GoUnderlyingTypeFunction;
import ro.redeul.google.go.lang.psi.types.underlying.GoUnderlyingTypes;

/**
 * // TODO: mtoader ! Please explain yourself.
 */
public class GoTypeFunction
    extends GoTypePsiBacked<GoPsiTypeFunction, GoUnderlyingTypeFunction>
    implements GoType {

    protected GoTypeFunction(GoPsiTypeFunction type) {
        super(type);
        setUnderlyingType(GoUnderlyingTypes.getFunction(type));
    }

    @Override
    public boolean isIdentical(GoType type) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
