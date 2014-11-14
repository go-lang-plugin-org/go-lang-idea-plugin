package ro.redeul.google.go.lang.psi.impl.types.interfaces;

import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeInterface;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeName;
import ro.redeul.google.go.lang.psi.types.interfaces.GoTypeInterfaceMethodSet;
import ro.redeul.google.go.lang.psi.typing.GoTypeInterface;
import ro.redeul.google.go.lang.psi.typing.GoTypes;

import java.util.HashSet;
import java.util.Set;

public class MethodSetDiscover {

    private final GoTypeInterface myTypeInterface;
    private final Set<String> myIgnoredNames;
    private GoTypeInterfaceMethodSet myMethodSet;

    public MethodSetDiscover(GoTypeInterface interfaceType) {
        this(interfaceType, new HashSet<String>());
    }

    private MethodSetDiscover(GoTypeInterface interfaceType, Set<String> ignoredNames) {
        this.myTypeInterface = interfaceType;
        this.myIgnoredNames = ignoredNames;
    }

    public GoTypeInterfaceMethodSet getMethodSet() {
        discover();
        return myMethodSet;
    }

    private void discover() {
        GoPsiTypeInterface psiType = myTypeInterface.getPsiType();

        myIgnoredNames.add(psiType.getName());
        myMethodSet = new GoTypeInterfaceMethodSet();

        for (GoPsiTypeName embeddedInterface : psiType.getTypeNames()) {

            GoTypeInterface typeInterface = GoTypes.fromPsi(embeddedInterface).underlyingType(GoTypeInterface.class);
            if (typeInterface == null)
                continue;

            GoTypeInterfaceMethodSet methodSet =
                    new MethodSetDiscover(typeInterface, myIgnoredNames).getMethodSet();

            myMethodSet.merge(methodSet);
        }

        for (GoFunctionDeclaration functionDeclaration : psiType.getFunctionDeclarations()) {
            myMethodSet.add(functionDeclaration);
        }
    }
}
