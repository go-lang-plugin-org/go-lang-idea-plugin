package ro.redeul.google.go.lang.psi.typing;

import ro.redeul.google.go.lang.packages.GoPackages;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.GoPackage;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeInterface;

import java.util.HashMap;
import java.util.Map;

public class GoTypeInterface extends GoTypePsiBacked<GoPsiTypeInterface> implements GoType {

    public GoTypeInterface(GoPsiTypeInterface psiType) {
        super(psiType);
    }

    @Override
    public boolean isIdentical(GoType type) {
        if ( !(type instanceof GoTypeInterface) )
            return false;

        GoTypeInterface her = (GoTypeInterface) type;

        Map<String, GoTypeFunction> ourMethods = getMethodSetTypes();
        Map<String, GoTypeFunction> herMethods = her.getMethodSetTypes();

        for (Map.Entry<String, GoTypeFunction> entry : ourMethods.entrySet()) {
            if (!(herMethods.containsKey(entry.getKey())))
                return false;

            if (!entry.getValue().isIdentical(herMethods.get(entry.getKey())))
                return false;

            herMethods.remove(entry.getKey());
        }

        return herMethods.size() == 0 ;
    }

    public boolean isImplementedBy(GoType type) {
        Map<String, GoTypeFunction> myMethodSet = getMethodSetTypes();

        GoPackage myPackage = getPackage();

        Map<String, GoTypeFunction> otherMethodSet = type.getDeclaredMethods(myPackage);

        for (Map.Entry<String, GoTypeFunction> myMethod : myMethodSet.entrySet()) {
            if (!otherMethodSet.containsKey(myMethod.getKey()))
                return false;

            GoTypeFunction methodType = otherMethodSet.get(myMethod.getKey());

            if ( ! myMethod.getValue().isIdentical(methodType))
                return false;
        }

        return true;
    }

    private GoPackage getPackage() {GoPackages packages = GoPackages.getInstance(this.getPsiType().getProject());
        GoFile contextFile = (GoFile) this.getPsiType().getContainingFile();
        return packages.getPackage(contextFile.getPackageImportPath());
    }

    protected Map<String, GoTypeFunction> getMethodSetTypes() {
        GoFunctionDeclaration functions[] = getPsiType().getMethodSet();

        Map<String, GoTypeFunction> methodsMap = new HashMap<String, GoTypeFunction>();
        for (GoFunctionDeclaration function : functions) {
            GoType methodType = types().fromPsiType(function);
            if (methodType instanceof GoTypeFunction)
                methodsMap.put(function.getName(), (GoTypeFunction) methodType);
        }

        return methodsMap;
    }

    @Override
    public <T> T accept(TypeVisitor<T> visitor) {
        return visitor.visitInterface(this);
    }

    @Override
    public String toString() {
        return "interface{ /* ... */ }";
    }
}

