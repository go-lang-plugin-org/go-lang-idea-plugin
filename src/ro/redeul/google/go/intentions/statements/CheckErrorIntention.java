package ro.redeul.google.go.intentions.statements;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.editor.TemplateUtil;
import ro.redeul.google.go.intentions.Intention;
import ro.redeul.google.go.intentions.IntentionExecutionException;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.statements.GoExpressionStatement;
import ro.redeul.google.go.lang.psi.typing.GoType;
import ro.redeul.google.go.lang.psi.typing.GoTypePrimitive;
import ro.redeul.google.go.lang.psi.typing.GoTypes;

import java.util.ArrayList;
import java.util.List;

public class CheckErrorIntention extends Intention {

    @Override
    protected boolean satisfiedBy(PsiElement element) {

        GoExpressionStatement statement = getParentAs(element, GoExpressionStatement.class);
        if (statement == null) return false;

        GoExpr expr = statement.getExpression();
        if (expr == null) return false;

        for (GoType type : expr.getType()) {
            if (type != null && (isErrorType(type) || isErrorType(type.underlyingType())))
                return true;
        }

        return false;
    }

    @Override
    protected void processIntention(@NotNull PsiElement element, Editor editor) throws IntentionExecutionException {
        GoExpressionStatement statement = getParentAs(element, GoExpressionStatement.class);
        if (statement == null) return;

        TextRange textRange = statement.getTextRange();

        StringBuilder varListString = new StringBuilder();
        StringBuilder checkString = new StringBuilder();
        List<String> varNames = new ArrayList<String>();

        GoExpr expr = statement.getExpression();
        if (expr == null) return;

        GoType[] types = expr.getType();

        int errorVarIndex = -1;
        int varIndex = 0;
        boolean needsComma = false;
        for (GoType type : types) {
            if (type == null)
                continue;

            if (needsComma)
                varListString.append(", ");
            else
                needsComma = true;

            String templateVarName = String.format("$v%d$", varIndex++);
            varListString.append(templateVarName);
            if (isErrorType(type) || isErrorType(type.underlyingType())) {
                errorVarIndex++;
                String errVarName = findVarName(expr, errorVarIndex);
                if (errorVarIndex > 0) {
                    checkString.append(" || ");
                }

                varNames.add(errVarName);
                checkString.append(templateVarName).append(" != nil");
            } else {
                varNames.add("_");
            }
        }

        if (types.length <= 1)
            varNames.set(0, expr.getText());

        StringBuilder template = new StringBuilder();

        template.append("if ");
        if (types.length > 1) {
            template.append(varListString).append(":=").append(expr.getText()).append(';');
        } else {
            varNames.set(0, expr.getText());
        }

        String bodyTemplate = String.format("\n$v%d$$END$\n}", varIndex);
        varNames.add("//TODO: Handle error(s)");
        template
                .append(checkString)
                .append("{")
                .append(bodyTemplate);

        //varNames.add("panic(\"Unhandled error!\")");
        TemplateUtil.runTemplate(editor, textRange, varNames, TemplateUtil.createTemplate(template.toString()));
    }

    private String findVarName(GoExpr expr, int varIndex) {
        return String.format("err%s", varIndex == 0 ? "" : varIndex - 1);
    }

    private boolean isErrorType(GoType type) {
        return type != null &&
                type instanceof GoTypePrimitive &&
                ((GoTypePrimitive) type).getType() == GoTypes.Builtin.Error;
    }
}
