package ro.redeul.google.go.lang.psi.typing;

import ro.redeul.google.go.lang.psi.types.GoPsiTypeStruct;
import ro.redeul.google.go.lang.psi.types.underlying.GoUnderlyingTypeStruct;
import ro.redeul.google.go.lang.psi.types.underlying.GoUnderlyingTypes;

/**
 * // TODO: mtoader ! Finish this implementation.
 */
public class GoTypeStruct extends GoTypePsiBacked<GoPsiTypeStruct, GoUnderlyingTypeStruct> implements GoType {

    public GoTypeStruct(GoPsiTypeStruct type) {
        super(type);
        setUnderlyingType(GoUnderlyingTypes.getStruct());
    }

    @Override
    public boolean isIdentical(GoType type) {
        if (!(type instanceof GoTypeStruct))
            return false;

        return false;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitTypeStruct(this);
    }
}
