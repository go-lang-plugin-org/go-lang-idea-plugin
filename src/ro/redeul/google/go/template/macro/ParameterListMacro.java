package ro.redeul.google.go.template.macro;

import com.intellij.codeInsight.template.Expression;
import com.intellij.codeInsight.template.ExpressionContext;
import com.intellij.codeInsight.template.Macro;
import com.intellij.codeInsight.template.Result;
import com.intellij.codeInsight.template.TemplateContextType;
import com.intellij.codeInsight.template.TextResult;
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
                    String name = id.getName();
                    if (name != null) {
                        sb.append(separator).append(name);
                    }
                }
            }
        }

        return new TextResult(sb.toString());
    }
}
