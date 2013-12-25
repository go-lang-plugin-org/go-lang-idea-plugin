package com.goide;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.util.IconLoader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class GoFileType extends LanguageFileType {
  public static final LanguageFileType INSTANCE = new GoFileType();
  private static final Icon ICON = IconLoader.findIcon("/icons/go.png");

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
    return ICON;
  }
}
