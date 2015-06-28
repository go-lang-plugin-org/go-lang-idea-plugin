/*
 * Copyright 2013-2015 Sergey Ignatov, Alexander Zolotov, Mihai Toader, Florin Patan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.goide.runconfig.testing;

import com.goide.GoConstants;
import com.goide.GoFileType;
import com.goide.psi.GoFile;
import com.goide.psi.GoFunctionOrMethodDeclaration;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
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
  private static final String EXTENSION = "." + GoFileType.INSTANCE.getDefaultExtension();

  public static boolean isTestFile(@Nullable PsiFile file) {
    return file != null && file instanceof GoFile && file.getName().endsWith(GoConstants.TEST_SUFFIX_WITH_EXTENSION);
  }

  public static boolean isTestFile(@Nullable VirtualFile file) {
    return file != null && file.getFileType() == GoFileType.INSTANCE && file.getNameWithoutExtension().endsWith(GoConstants.TEST_SUFFIX);
  }

  @Nullable
  public static String getTestFunctionName(@NotNull GoFunctionOrMethodDeclaration function) {
    return isTestFunctionName(function.getName()) ? StringUtil.notNullize(function.getName()) : null;
  }

  public static boolean isTestFunctionName(@Nullable String functionName) {
    return checkPrefix(functionName, "Test");
  }

  public static boolean isExampleFunctionName(@Nullable String functionName) {
    return checkPrefix(functionName, "Example");
  }

  public static boolean isBenchmarkFunctionName(@Nullable String functionName) {
    return checkPrefix(functionName, "Benchmark");
  }

  private static boolean checkPrefix(@Nullable String name, @NotNull String prefix) {
    // https://github.com/golang/go/blob/master/src/cmd/go/test.go#L1161 – isTest()
    if (name == null || !name.startsWith(prefix)) return false;
    if (prefix.length() == name.length()) return true;
    final char c = name.charAt(prefix.length());
    return !Character.isLetter(c) || !Character.isLowerCase(c);
  }

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
      PsiFile testFile = directory.findFile(FileUtil.getNameWithoutExtension(file.getName()) + GoConstants.TEST_SUFFIX_WITH_EXTENSION);
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
      PsiFile sourceFile = directory.findFile(StringUtil.trimEnd(testFile.getName(), GoConstants.TEST_SUFFIX_WITH_EXTENSION) + EXTENSION);
      if (sourceFile != null) {
        return new SmartList<PsiElement>(sourceFile);
      }
    }
    return Collections.emptyList();
  }

  @Override
  public boolean isTest(@NotNull PsiElement element) {
    return isTestFile(InjectedLanguageUtil.getTopLevelFile(element));
  }
}
