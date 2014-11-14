package ro.redeul.google.go.lang.psi.typing;

import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameter;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeFunction;
import ro.redeul.google.go.lang.psi.utils.GoFunctionDeclarationUtils;

public class GoTypeFunction
        extends GoTypePsiBacked<GoPsiTypeFunction>
        implements GoType {

    public GoType[] getResultTypes() {
        return GoFunctionDeclarationUtils.getParameterTypes(getPsiType().getResults());
    }

    public GoType[] getParameterTypes() {
        return GoFunctionDeclarationUtils.getParameterTypes(getPsiType().getParameters());
    }

    GoTypeFunction(GoPsiTypeFunction type) { super(type); }

    @Override
    public boolean isIdentical(GoType type) {
        if ( !(type instanceof GoTypeFunction) )
            return false;

        GoTypeFunction func = (GoTypeFunction) type;

        return GoTypes.areIdentical(getParameterTypes(), func.getParameterTypes()) &&
                GoTypes.areIdentical(getResultTypes(), func.getResultTypes());
    }

    @Nullable
    public GoType getParameterType(int pos) {

        int index = pos;
        for (GoFunctionParameter parameter : getPsiType().getParameters()) {
            if (index < 0)
                return null;

            index -= Math.max(1, parameter.getIdentifiers().length);

            if (index < 0) {
                GoType type = types().fromPsiType(parameter.getType());
                return parameter.isVariadic() ? new GoTypeVariadic(type) : type;
            }
        }

        return null;
    }

    @Override
    public <T> T accept(TypeVisitor<T> visitor) {
        return visitor.visitFunction(this);
    }

    @Override
    public String toString() {
        return String.format("func ... ");
    }
}
