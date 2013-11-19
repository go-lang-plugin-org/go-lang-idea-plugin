package ro.redeul.google.go.lang.psi.types.interfaces;

import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class GoTypeInterfaceMethodSet {

    private final Set<GoFunctionDeclaration> myFunctions =
        new HashSet<>();

    public void add(GoFunctionDeclaration functionDeclaration) {
        myFunctions.add(functionDeclaration);
    }


    public void merge(GoTypeInterfaceMethodSet methodSet) {
        myFunctions.addAll(methodSet.myFunctions);
    }

    public Collection<GoFunctionDeclaration> getMethods() {
        return myFunctions;
    }
}
