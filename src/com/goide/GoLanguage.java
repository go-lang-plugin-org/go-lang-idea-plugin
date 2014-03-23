package com.goide;

import com.intellij.lang.Language;
import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.Nullable;

public class GoLanguage extends Language {
  public static final Language INSTANCE = new GoLanguage();

  protected GoLanguage() {
    super("go", "text/go", "text/x-go", "application/x-go");
  }

  @Override
  public String getDisplayName() {
    return "Go";
  }

  @Nullable
  @Override
  public LanguageFileType getAssociatedFileType() {
    return GoFileType.INSTANCE;
  }
}
