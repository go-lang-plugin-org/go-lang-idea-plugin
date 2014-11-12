package ro.redeul.google.go.lang.psi.utils;

import com.intellij.util.Function;
import com.intellij.util.containers.ContainerUtil;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameter;
import ro.redeul.google.go.lang.psi.types.GoPsiType;
import ro.redeul.google.go.lang.psi.typing.GoType;
import ro.redeul.google.go.lang.psi.typing.GoTypes;

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

    public static int getResultCount(GoFunctionParameter []params) {
        int returnCount = 0;
        for (GoFunctionParameter resParam : params) {
            returnCount += Math.max(resParam.getIdentifiers().length, 1);
        }
        return returnCount;
    }
    public static int getResultCount(GoFunctionDeclaration declaration) {
        return getResultCount(declaration.getResults());
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

    public static GoType[] getParameterTypes(GoFunctionParameter[] params) {
        List<GoPsiType> types = new ArrayList<GoPsiType>();
        for (GoFunctionParameter result : params) {
            GoLiteralIdentifier identifiers[] = result.getIdentifiers();

            if (identifiers.length == 0 && result.getType() != null) {
                types.add(result.getType());
            } else {
                for (GoLiteralIdentifier identifier : identifiers) {
                    types.add(result.getType());
                }
            }
        }

        return ContainerUtil.map2Array(types, GoType.class, new Function<GoPsiType, GoType>() {
            @Override
            public GoType fun(GoPsiType type) { return GoTypes.fromPsi(type); }
        });
    }
}
