package ro.redeul.google.go.ide;

import com.intellij.application.options.CodeStyleAbstractConfigurable;
import com.intellij.application.options.CodeStyleAbstractPanel;
import com.intellij.application.options.TabbedLanguageCodeStylePanel;
import com.intellij.openapi.options.Configurable;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CodeStyleSettingsProvider;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.GoLanguage;

public class GoCodeStyleSettingsProvider extends CodeStyleSettingsProvider {
    @NotNull
    @Override
    public Configurable createSettingsPage(CodeStyleSettings settings, CodeStyleSettings originalSettings) {
        return new CodeStyleAbstractConfigurable(settings, originalSettings, "Go") {
            @Override
            protected CodeStyleAbstractPanel createPanel(CodeStyleSettings settings) {
                return new TabbedLanguageCodeStylePanel(GoLanguage.INSTANCE, getCurrentSettings(), settings) {
                    @Override
                    protected void initTabs(CodeStyleSettings settings) {
                        addIndentOptionsTab(settings);
                    }
                };
            }

            @Override
            public String getHelpTopic() {
                return "reference.settingsdialog.codestyle.groovy";
            }
        };
    }

    @Override
    public String getConfigurableDisplayName() {
        return "Go Lang";
    }
}
