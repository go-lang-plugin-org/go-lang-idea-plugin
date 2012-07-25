package ro.redeul.google.go.lang.psi.typing;

import ro.redeul.google.go.lang.psi.types.GoPsiType;
import ro.redeul.google.go.lang.psi.types.underlying.GoUnderlyingType;

/**
 * // TODO: mtoader ! Please explain yourself.
 */
public abstract class GoTypePsiBacked<
    PsiType extends GoPsiType,
    UnderlyingType extends GoUnderlyingType
    > implements GoType {

    UnderlyingType underlyingType;
    PsiType psiType;

    protected GoTypePsiBacked(PsiType psiType) {
        this.psiType = psiType;
    }

    public PsiType getPsiType() {
        return psiType;
    }

    @Override
    public GoUnderlyingType getUnderlyingType() {
        return
            underlyingType != null
                ? underlyingType
                : GoUnderlyingType.Undefined;
    }

    public void setUnderlyingType(UnderlyingType underlyingType) {
        this.underlyingType = underlyingType;
    }
}
