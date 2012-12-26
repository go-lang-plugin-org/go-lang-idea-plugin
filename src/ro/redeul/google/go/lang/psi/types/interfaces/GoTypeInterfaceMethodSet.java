package ro.redeul.google.go.lang.psi.types.interfaces;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;

public class GoTypeInterfaceMethodSet {

    private Set<GoFunctionDeclaration> myFunctions =
        new HashSet<GoFunctionDeclaration>();

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
