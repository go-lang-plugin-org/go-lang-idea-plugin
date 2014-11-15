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

public class ParameterListMacro extends Macro {
    @Override
    public String getName() {
        return "parameterList";
    }

    @Override
    public String getPresentableName() {
        return TemplateBundle.message("macro.parameterList");
    }

    @Override
    public boolean isAcceptableInContext(TemplateContextType context) {
        return context instanceof GoTemplateContextType.Function;
    }

    @Override
    public Result calculateResult(@NotNull Expression[] params, ExpressionContext context) {
        String separator = readFirstParamValue(context, params, ", ");
        GoFunctionDeclaration fd = findElementOfType(context, GoFunctionDeclaration.class);
        if (fd == null) {
            return new TextResult("");
        }

        StringBuilder sb = new StringBuilder();
        for (GoFunctionParameter fp : fd.getParameters()) {
            for (GoLiteralIdentifier id : fp.getIdentifiers()) {
                if (id != null && !id.isBlank()) {
                    sb.append(separator).append(id.getName());
                }
            }
        }

        return new TextResult(sb.toString());
    }
}
