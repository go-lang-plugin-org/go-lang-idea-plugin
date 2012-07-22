package ro.redeul.google.go.lang.psi.typing;

import ro.redeul.google.go.lang.psi.types.GoPsiType;
import ro.redeul.google.go.lang.psi.types.underlying.GoUnderlyingType;

/**
 * // TODO: mtoader ! Please explain yourself.
 */
public abstract class GoTypePsiBacked<
    Type extends GoPsiType,
    UnderlyingType extends GoUnderlyingType
    > implements GoType {

    UnderlyingType underlyingType;

    protected GoTypePsiBacked(Type type) {
    }

    @Override
    public GoUnderlyingType getUnderlyingType() {
        return underlyingType;
    }

    public void setUnderlyingType(UnderlyingType underlyingType) {
        this.underlyingType = underlyingType;
    }
}
