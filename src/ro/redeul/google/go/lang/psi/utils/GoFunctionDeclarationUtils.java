package ro.redeul.google.go.lang.psi.utils;

import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameter;

import java.util.ArrayList;
import java.util.List;

public class GoFunctionDeclarationUtils {
    public static boolean IsResultNamed(GoFunctionDeclaration declaration){
        for (GoFunctionParameter resParam : declaration.getResults()) {
            if (resParam.getIdentifiers().length > 0){
                return true;
            }
        }
        return false;
    }

    public static int GetResultCount(GoFunctionDeclaration declaration){
        int returnCount = 0;
        for (GoFunctionParameter resParam : declaration.getResults()) {
            returnCount += Math.max(resParam.getIdentifiers().length, 1);
        }
        return returnCount;
    }

    public static boolean hasResult(GoFunctionDeclaration function) {
        return function.getResults().length > 0;
    }

    public static boolean hasBody(GoFunctionDeclaration function) {
        return function.getBlock() != null;
    }

    public static List<String> getResultParameterNames(GoFunctionDeclaration function){
        return getParameterNames(function.getResults());
    }
    private static List<String> getParameterNames(GoFunctionParameter[] parameters) {
        List<String> parameterNames = new ArrayList<String>();
        for (GoFunctionParameter fp : parameters) {
            for (GoLiteralIdentifier id : fp.getIdentifiers()) {
                if (!id.isBlank()) {
                    parameterNames.add(id.getText());
                }
            }
        }
        return parameterNames;
    }
}
