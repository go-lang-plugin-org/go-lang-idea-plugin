package ro.redeul.google.go.template.macro;

import com.intellij.codeInsight.template.*;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameter;
import ro.redeul.google.go.template.GoTemplateContextType;
import ro.redeul.google.go.template.TemplateBundle;

import static ro.redeul.google.go.template.macro.MacroUtil.findElementOfType;
import static ro.redeul.google.go.template.macro.MacroUtil.readFirstParamValue;

public class ParameterFormatMacro extends Macro {
    @Override
    public String getName() {
        return "parameterFormat";
    }

    @Override
    public String getPresentableName() {
        return TemplateBundle.message("macro.parameterFormat");
    }

    @Override
    public boolean isAcceptableInContext(TemplateContextType context) {
        return context instanceof GoTemplateContextType.Function;
    }

    @Override
    public Result calculateResult(@NotNull Expression[] params, ExpressionContext context) {
        String separator = readFirstParamValue(context, params, "%#v");
        GoFunctionDeclaration fd = findElementOfType(context, GoFunctionDeclaration.class);
        if (fd == null) {
            return new TextResult("");
        }

        StringBuilder sb = new StringBuilder();
        for (GoFunctionParameter fp : fd.getParameters()) {
            for (GoLiteralIdentifier id : fp.getIdentifiers()) {
                if (id != null && !id.isBlank()) {
                    String name = id.getName();
                    sb.append(name).append(" = [").append(separator).append("], ");
                }
            }
        }

        if (sb.length() > 0) {
            sb.delete(sb.length() - 2, sb.length());
        }
        return new TextResult(sb.toString());
    }
}
