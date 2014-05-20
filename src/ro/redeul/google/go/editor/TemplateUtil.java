package ro.redeul.google.go.editor;

import com.intellij.codeInsight.template.TemplateManager;
import com.intellij.codeInsight.template.impl.TemplateImpl;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.RangeMarker;
import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TemplateUtil {
    public static TemplateImpl createTemplate(String text) {
        TemplateImpl template = new TemplateImpl("", text, "");
        template.setToReformat(true);
        return template;
    }

    /**
     * In the specified range of editor, replace all variables with defaultValue,
     * and let user change the value.
     *
     * @param editor       Editor
     * @param range        TextRange
     * @param variable     String
     * @param defaultValue String
     */
    public static void runTemplate(Editor editor, TextRange range, String variable, String defaultValue) {
        String text = editor.getDocument().getText(range);
        editor.getDocument().deleteString(range.getStartOffset(), range.getEndOffset());

        if (!defaultValue.matches("\".*\"")) {
            defaultValue = '"' + defaultValue + '"';
        }

        TemplateImpl template = createTemplate(text);
        template.addVariable(variable, defaultValue, defaultValue, true);
        TemplateManager.getInstance(editor.getProject()).startTemplate(editor, "", template);
    }

    /**
     * Generate template expression to be used in TemplateImpl.
     * For example getTemplateVariableExpression(5, ", ") will return string:
     * "$v0$, $v1$, $v2$, $v3$, $v4$"
     *
     * @param variableCount how many variables to be generated.
     * @param separator     the separator between variables
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
     *
     * @param template The template
     * @param values   These values will be set to variable v0, v1, ... , vn. "n" is "values.size() - 1"
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

    public static void runTemplate(Editor editor, TextRange textRange, List<String> stringList, TemplateImpl template) {
        final Document document = editor.getDocument();
        final RangeMarker range = document.createRangeMarker(textRange.getStartOffset(), textRange.getEndOffset());
        setTemplateVariableValues(template, stringList);
        WriteCommandAction writeCommandAction = new WriteCommandAction(editor.getProject()) {
            @Override
            protected void run(@NotNull Result result) throws Throwable {
                document.deleteString(range.getStartOffset(), range.getEndOffset());
            }
        };
        writeCommandAction.execute();
        TemplateManager.getInstance(editor.getProject()).startTemplate(editor, "", template);
    }

    public static void runTemplate(Editor editor, int insertPoint, List<String> stringList, TemplateImpl template) {
        setTemplateVariableValues(template, stringList);
        editor.getCaretModel().moveToOffset(insertPoint,true);
        TemplateManager.getInstance(editor.getProject()).startTemplate(editor, template);
    }
}
