/*
 * Copyright 2013-2016 Sergey Ignatov, Alexander Zolotov, Florin Patan
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
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;

public class GoTestFinder implements TestFinder {
  private static final String EXTENSION = "." + GoFileType.INSTANCE.getDefaultExtension();

  public static boolean isTestFile(@Nullable PsiFile file) {
    return file instanceof GoFile && file.getName().endsWith(GoConstants.TEST_SUFFIX_WITH_EXTENSION);
  }

  public static boolean isTestFile(@NotNull VirtualFile file) {
    return file.getName().endsWith(GoConstants.TEST_SUFFIX_WITH_EXTENSION);
  }

  public static boolean isTestOrExampleFunction(@NotNull GoFunctionOrMethodDeclaration function) {
    GoTestFunctionType type = GoTestFunctionType.fromName(function.getName());
    return type == GoTestFunctionType.EXAMPLE || type == GoTestFunctionType.TEST;
  }

  public static boolean isBenchmarkFunction(@NotNull GoFunctionOrMethodDeclaration function) {
    GoTestFunctionType type = GoTestFunctionType.fromName(function.getName());
    return type == GoTestFunctionType.BENCHMARK;
  }

  public static boolean isTestFileWithTestPackage(@Nullable PsiFile file) {
    return getTestTargetPackage(file) != null;
  }

  @Nullable
  public static String getTestTargetPackage(@Nullable PsiFile file) {
    if (isTestFile(file)) {
      String packageName = ((GoFile)file).getPackageName();
      if (packageName != null && packageName.endsWith(GoConstants.TEST_SUFFIX)) {
        return StringUtil.nullize(StringUtil.trimEnd(packageName, GoConstants.TEST_SUFFIX));
      }
    }
    return null;
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
        return ContainerUtil.newSmartList(testFile);
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
        return ContainerUtil.newSmartList(sourceFile);
      }
    }
    return Collections.emptyList();
  }

  @Override
  public boolean isTest(@NotNull PsiElement element) {
    return isTestFile(InjectedLanguageUtil.getTopLevelFile(element));
  }
}
