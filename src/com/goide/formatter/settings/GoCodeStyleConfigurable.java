package com.goide.formatter.settings;

import com.goide.GoLanguage;
import com.intellij.application.options.CodeStyleAbstractConfigurable;
import com.intellij.application.options.CodeStyleAbstractPanel;
import com.intellij.application.options.TabbedLanguageCodeStylePanel;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import org.jetbrains.annotations.NotNull;

public class GoCodeStyleConfigurable extends CodeStyleAbstractConfigurable {
  public GoCodeStyleConfigurable(@NotNull CodeStyleSettings settings, CodeStyleSettings cloneSettings) {
    super(settings, cloneSettings, "Go");
  }

  @NotNull
  @Override
  protected CodeStyleAbstractPanel createPanel(CodeStyleSettings settings) {
    return new GoCodeStyleMainPanel(getCurrentSettings(), settings);
  }

  @Override
  public String getHelpTopic() {
    return null;
  }

  private static class GoCodeStyleMainPanel extends TabbedLanguageCodeStylePanel {
    private GoCodeStyleMainPanel(CodeStyleSettings currentSettings, CodeStyleSettings settings) {
      super(GoLanguage.INSTANCE, currentSettings, settings);
    }

    @Override
    protected void addSpacesTab(CodeStyleSettings settings) {
    }

    @Override
    protected void addBlankLinesTab(CodeStyleSettings settings) {
    }

    @Override
    protected void addWrappingAndBracesTab(CodeStyleSettings settings) {
    }
  }
}