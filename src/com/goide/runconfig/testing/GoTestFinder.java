package com.goide.runconfig.testing;

import com.goide.GoFileType;
import com.goide.psi.GoFile;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.tree.injected.InjectedLanguageUtil;
import com.intellij.testIntegration.TestFinder;
import com.intellij.util.SmartList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;

public class GoTestFinder implements TestFinder {

  private static final String TEST_SUFFIX = "_test.go";
  private static final String EXTENSION = "." + GoFileType.INSTANCE.getDefaultExtension();

  @Nullable
  @Override
  public PsiElement findSourceElement(@NotNull PsiElement from) {
    return InjectedLanguageUtil.getTopLevelFile(from);
  }

  @NotNull
  @Override
  public Collection<PsiElement> findTestsForClass(@NotNull PsiElement element) {
    PsiFile file = InjectedLanguageUtil.getTopLevelFile(element);
    if (file instanceof GoFile) {
      PsiDirectory directory = file.getContainingDirectory();
      PsiFile testFile = directory.findFile(FileUtil.getNameWithoutExtension(file.getName()) + TEST_SUFFIX);
      if (testFile != null) {
        return new SmartList<PsiElement>(testFile);
      }
    }
    return Collections.emptyList();
  }


  @NotNull
  @Override
  public Collection<PsiElement> findClassesForTest(@NotNull PsiElement element) {
    PsiFile testFile = InjectedLanguageUtil.getTopLevelFile(element);
    if (testFile instanceof GoFile) {
      PsiDirectory directory = testFile.getContainingDirectory();
      PsiFile sourceFile = directory.findFile(StringUtil.trimEnd(testFile.getName(), TEST_SUFFIX) + EXTENSION);
      if (sourceFile != null) {
        return new SmartList<PsiElement>(sourceFile);
      }
    }
    return Collections.emptyList();
  }

  @Override
  public boolean isTest(@NotNull PsiElement element) {
    PsiFile file = InjectedLanguageUtil.getTopLevelFile(element);
    return file instanceof GoFile && file.getName().endsWith(TEST_SUFFIX);
  }
}
