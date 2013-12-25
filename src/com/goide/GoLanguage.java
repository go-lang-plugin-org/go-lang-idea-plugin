package com.goide;

import com.intellij.lang.Language;
import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.Nullable;

public class GoLanguage extends Language {
  public static final Language INSTANCE = new GoLanguage();

  protected GoLanguage() {
    super("go", "application/go");
  }

  @Nullable
  @Override
  public LanguageFileType getAssociatedFileType() {
    return GoFileType.INSTANCE;
  }
}
