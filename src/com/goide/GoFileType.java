package com.goide;

import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class GoFileType extends LanguageFileType {
  public static final LanguageFileType INSTANCE = new GoFileType();

  protected GoFileType() {
    super(GoLanguage.INSTANCE);
  }

  @NotNull
  @Override
  public String getName() {
    return "Go";
  }

  @NotNull
  @Override
  public String getDescription() {
    return "Go files";
  }

  @NotNull
  @Override
  public String getDefaultExtension() {
    return "go";
  }

  @Nullable
  @Override
  public Icon getIcon() {
    return GoIcons.ICON;
  }
}
