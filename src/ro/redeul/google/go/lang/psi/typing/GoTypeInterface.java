package ro.redeul.google.go.lang.psi.typing;

import ro.redeul.google.go.lang.psi.types.GoPsiTypeInterface;
import ro.redeul.google.go.lang.psi.types.underlying.GoUnderlyingTypeInterface;
import ro.redeul.google.go.lang.psi.types.underlying.GoUnderlyingTypes;

public class GoTypeInterface
    extends GoTypePsiBacked<GoPsiTypeInterface, GoUnderlyingTypeInterface>
    implements GoType {

    public GoTypeInterface(GoPsiTypeInterface psiType) {
        super(psiType);
        setUnderlyingType(GoUnderlyingTypes.getInterface());
    }

    @Override
    public boolean isIdentical(GoType type) {
        return false;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitTypeInterface(this);
    }
}
