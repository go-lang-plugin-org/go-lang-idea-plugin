package ro.redeul.google.go.editor;

import com.intellij.codeInsight.template.TemplateManager;
import com.intellij.codeInsight.template.impl.TemplateImpl;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.TextRange;

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
}
