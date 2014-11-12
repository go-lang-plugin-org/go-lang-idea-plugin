package ro.redeul.google.go.lang.psi.typing;

import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.types.GoPsiType;

public abstract class GoTypePsiBacked<PsiType extends GoPsiType> extends GoAbstractType {

    private final PsiType psiType;

    GoTypePsiBacked(PsiType psiType) {
        this.psiType = psiType;
    }

    public PsiType getPsiType() {
        return psiType;
    }

    protected GoTypes types() {
        return GoTypes.getInstance(psiType.getProject());
    }

    @Override
    public String toString() {
        return GoTypes.getRepresentation(this, (GoFile) psiType.getContainingFile());
    }
}
