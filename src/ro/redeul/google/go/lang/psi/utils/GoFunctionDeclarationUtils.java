package ro.redeul.google.go.lang.psi.utils;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.lexer.GoTokenTypes;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.expressions.GoPrimaryExpression;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.expressions.primary.GoBuiltinCallExpression;
import ro.redeul.google.go.lang.psi.expressions.primary.GoCallOrConvExpression;
import ro.redeul.google.go.lang.psi.expressions.primary.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.expressions.primary.GoSelectorExpression;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameter;
import ro.redeul.google.go.util.GoUtil;

import java.util.ArrayList;
import java.util.List;

import static ro.redeul.google.go.lang.psi.utils.GoIdentifierUtils.getFunctionDeclaration;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.*;

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
