package ro.redeul.google.go.lang.psi.typing;

import ro.redeul.google.go.lang.psi.types.GoPsiType;
import ro.redeul.google.go.lang.psi.types.underlying.GoUnderlyingType;

/**
 * // TODO: mtoader ! Please explain yourself.
 */
public abstract class GoTypePsiBacked<
    PsiType extends GoPsiType,
    UnderlyingType extends GoUnderlyingType
    > extends GoAbstractType<UnderlyingType> {

    PsiType psiType;

    protected GoTypePsiBacked(PsiType psiType) {
        this.psiType = psiType;
    }

    public PsiType getPsiType() {
        return psiType;
    }

}
