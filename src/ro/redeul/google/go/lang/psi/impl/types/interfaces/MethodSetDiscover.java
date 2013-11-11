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

    private final GoPsiTypeInterface myPsiType;
    private final Set<String> myIgnoredNames;
    private GoTypeInterfaceMethodSet myMethodSet;

    public MethodSetDiscover(GoPsiTypeInterface psiType) {
        this(psiType, new HashSet<String>());
    }

    private MethodSetDiscover(GoPsiTypeInterface psiType,
                              Set<String> ignoredNames) {
        this.myPsiType = psiType;
        this.myIgnoredNames = ignoredNames;
    }

    public GoTypeInterfaceMethodSet getMethodSet() {
        discover();
        return myMethodSet;
    }

    private void discover() {
        myIgnoredNames.add(myPsiType.getQualifiedName());
        myMethodSet = new GoTypeInterfaceMethodSet();

        for (GoPsiTypeName embeddedInterface : myPsiType.getTypeNames()) {
            GoTypeInterface typeInterface =
                GoTypes.resolveToInterface(embeddedInterface);

            if (typeInterface == null) {
                continue;
            }

            GoTypeInterfaceMethodSet methodSet =
                new MethodSetDiscover(
                    typeInterface.getPsiType(),
                    myIgnoredNames).getMethodSet();

            myMethodSet.merge(methodSet);
        }

        for (GoFunctionDeclaration functionDeclaration : myPsiType.getFunctionDeclarations()) {
            myMethodSet.add(functionDeclaration);
        }
    }
}
