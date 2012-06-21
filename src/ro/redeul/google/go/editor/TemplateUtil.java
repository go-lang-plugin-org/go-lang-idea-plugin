package ro.redeul.google.go.editor;

import com.intellij.codeInsight.template.TemplateManager;
import com.intellij.codeInsight.template.impl.TemplateImpl;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.TextRange;

import java.util.List;

public class TemplateUtil {
    /**
     * In the specified range of editor, replace all variables with defaultValue,
     * and let user change the value.
     * @param editor
     * @param range
     * @param variable
     * @param defaultValue
     */
    public static void runTemplate(Editor editor, TextRange range, String variable, String defaultValue) {
        String text = editor.getDocument().getText(range);
        editor.getDocument().deleteString(range.getStartOffset(), range.getEndOffset());

        if (!defaultValue.matches("\".*\"")) {
            defaultValue = '"' + defaultValue + '"';
        }

        TemplateImpl template = new TemplateImpl("", text, "");
        template.setToIndent(false);
        template.addVariable(variable, defaultValue, defaultValue, true);
        TemplateManager.getInstance(editor.getProject()).startTemplate(editor, "", template);
    }

    /**
     * Generate template expression to be used in TemplateImpl.
     * For example getTemplateVariableExpression(5, ", ") will return string:
     *   "$v0$, $v1$, $v2$, $v3$, $v4$"
     *
     * @param variableCount how many variables to be generated.
     * @param separator the separator between variables
     * @return the variable expression
     */
    public static String getTemplateVariableExpression(int variableCount, String separator) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < variableCount; i++) {
            if (sb.length() > 0) {
                sb.append(separator);
            }
            sb.append("$v").append(i).append('$');
        }
        return sb.toString();
    }

    /**
     * Set template variable values
     * @param template The template
     * @param values These values will be set to variable v0, v1, ... , vn. "n" is "values.size() - 1"
     */
    public static void setTemplateVariableValues(TemplateImpl template, List<String> values) {
        for (int i = 0; i < values.size(); i++) {
            String value = values.get(i);
            if (!value.startsWith("\"") || !value.endsWith("\"")) {
                value = String.format("\"%s\"", value);
            }
            template.addVariable("v" + i, value, value, true);
        }
    }
}
