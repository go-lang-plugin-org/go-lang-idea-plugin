package ro.redeul.google.go.editor;

import com.intellij.codeInsight.template.CustomLiveTemplate;
import com.intellij.codeInsight.template.CustomTemplateCallback;
import com.intellij.codeInsight.template.LiveTemplateBuilder;
import com.intellij.codeInsight.template.impl.TemplateImpl;
import com.intellij.codeInsight.template.impl.TemplateSettings;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Aug 20, 2010
 * Time: 9:21:24 PM
 */
public class GoCustomLiveTemplate implements CustomLiveTemplate {

    public String computeTemplateKey(@NotNull CustomTemplateCallback callback) {
        return "f";
    }

    public boolean isApplicable(PsiFile file, int offset, boolean wrapping) {
        return false;
    }

    public boolean supportsWrapping() {
        return true;
    }

    public void expand(String key, @NotNull CustomTemplateCallback callback) {
        LiveTemplateBuilder builder = new LiveTemplateBuilder();

        builder.insertText(0, "unc () {\n\t\n}", true);

        builder.insertVariableSegment(5, "name()");
        builder.insertVariableSegment(10, TemplateImpl.END);

        callback.startTemplate(builder.buildTemplate(), null, null);
    }

    public void wrap(String selection, @NotNull CustomTemplateCallback callback) {
    }

    @NotNull
    public String getTitle() {
        return "custom";
    }

    public char getShortcut() {
        return TemplateSettings.TAB_CHAR;
    }
}
