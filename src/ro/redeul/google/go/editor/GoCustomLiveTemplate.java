package ro.redeul.google.go.editor;

import com.intellij.codeInsight.template.CustomLiveTemplate;
import com.intellij.codeInsight.template.CustomTemplateCallback;
import com.intellij.codeInsight.template.LiveTemplateBuilder;
import com.intellij.codeInsight.template.TemplateInvokationListener;
import com.intellij.codeInsight.template.impl.TemplateImpl;
import com.intellij.codeInsight.template.impl.TemplateSettings;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Aug 20, 2010
 * Time: 9:21:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class GoCustomLiveTemplate implements CustomLiveTemplate {

    public void wrap(String selection, @NotNull CustomTemplateCallback callback, @Nullable TemplateInvokationListener listener) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

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
        int a = 10;
    }

    @NotNull
    public String getTitle() {
        return "custom";
    }

    public char getShortcut() {
        return TemplateSettings.TAB_CHAR;
    }
}
