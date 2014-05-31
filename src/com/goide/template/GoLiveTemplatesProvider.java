package com.goide.template;

import com.intellij.codeInsight.template.impl.DefaultLiveTemplatesProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GoLiveTemplatesProvider implements DefaultLiveTemplatesProvider {
  @NotNull
  @Override
  public String[] getDefaultLiveTemplateFiles() {
    return new String[]{"/liveTemplates/go"};
  }

  @Nullable
  @Override
  public String[] getHiddenLiveTemplateFiles() {
    return new String[]{"/liveTemplates/hidden"};
  }
}
