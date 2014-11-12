package ro.redeul.google.go.lang.psi.typing;

import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeInterface;

import java.util.HashMap;
import java.util.Map;

public class GoTypeInterface
    extends GoTypePsiBacked<GoPsiTypeInterface>
    implements GoType {

    public GoTypeInterface(GoPsiTypeInterface psiType) {
        super(psiType);
    }

    @Override
    public boolean isIdentical(GoType type) {
        if ( !(type instanceof GoTypeInterface) )
            return false;

        GoTypeInterface other = (GoTypeInterface) type;

        Map<String, GoType> myMethodTypes = getMethodSetTypes();
        Map<String, GoType> otherMethodTypes = other.getMethodSetTypes();

        for (Map.Entry<String, GoType> entry : myMethodTypes.entrySet()) {
            if (!(otherMethodTypes.containsKey(entry.getKey())))
                return false;

            if (!entry.getValue().isIdentical(otherMethodTypes.get(entry.getKey())))
                return false;

            otherMethodTypes.remove(entry.getKey());
        }

        return otherMethodTypes.size() == 0 ;
    }

    protected Map<String, GoType> getMethodSetTypes() {
        GoFunctionDeclaration functions[] = getPsiType().getMethodSet();

        Map<String, GoType> methodsMap = new HashMap<String, GoType>();
        for (GoFunctionDeclaration function : functions) {
            GoType methodType = types().fromPsiType(function);
            if (methodType != null)
                methodsMap.put(function.getName(), methodType);
        }

        return methodsMap;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitInterface(this);
    }

    @Override
    public boolean isAssignableFrom(GoType source) {
        return false;
    }

    @Override
    public String toString() {
        return "interface{ /* ... */ }";
    }
}

