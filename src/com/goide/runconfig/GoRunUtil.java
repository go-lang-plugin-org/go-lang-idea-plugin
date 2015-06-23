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

package com.goide.runconfig;

import com.goide.GoConstants;
import com.goide.GoFileType;
import com.goide.psi.GoFile;
import com.goide.psi.GoPackageClause;
import com.goide.stubs.index.GoPackagesIndex;
import com.goide.util.GoUtil;
import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;

public class GoRunUtil {
  private static final Pattern GO_CHECK_IMPORT_PATH = Pattern.compile("gopkg\\.in/check\\.v\\d+");
  private static final Pattern GO_CHECK_GITHUB_IMPORT_PATH = Pattern.compile("github\\.com/go-check/check\\.v\\d+");

  private GoRunUtil() {

  }

  @Contract("null -> false")
  public static boolean isPackageContext(@Nullable PsiElement contextElement) {
    return PsiTreeUtil.getNonStrictParentOfType(contextElement, GoPackageClause.class) != null;
  }

  @Nullable
  public static PsiFile findMainFileInDirectory(@NotNull VirtualFile packageDirectory, @NotNull Project project) {
    for (VirtualFile file : packageDirectory.getChildren()) {
      if (file == null) {
        continue;
      }
      PsiFile psiFile = PsiManager.getInstance(project).findFile(file);
      if (isMainGoFile(psiFile)) {
        return psiFile;
      }
    }
    return null;
  }

  @Nullable
  public static PsiElement getContextElement(@Nullable ConfigurationContext context) {
    if (context == null) {
      return null;
    }
    PsiElement psiElement = context.getPsiLocation();
    if (psiElement == null || !psiElement.isValid()) {
      return null;
    }
    return psiElement;
  }

  public static boolean hasGoCheckSupport(@Nullable Module module) {
    if (module == null) return false;
    for (GoFile file : StubIndex.getElements(GoPackagesIndex.KEY, "check", module.getProject(), GoUtil.moduleScope(module), GoFile.class)) {
      String importPath = file.getImportPath();
      if (importPath != null && (GO_CHECK_IMPORT_PATH.matcher(importPath).matches() ||
                                 GO_CHECK_GITHUB_IMPORT_PATH.matcher(importPath).matches())) {
        return true;
      }
    }
    return false;
  }

  public static void installGoWithMainFileChooser(final Project project, @NotNull TextFieldWithBrowseButton fileField) {
    installFileChooser(project, fileField, false, new Condition<VirtualFile>() {
      @Override
      public boolean value(VirtualFile file) {
        if (file.getFileType() != GoFileType.INSTANCE) {
          return false;
        }
        final PsiFile psiFile = PsiManager.getInstance(project).findFile(file);
        return isMainGoFile(psiFile);
      }
    });
  }

  public static boolean isMainGoFile(@Nullable PsiFile psiFile) {
    if (psiFile != null && psiFile instanceof GoFile) {
      return GoConstants.MAIN.equals(((GoFile)psiFile).getPackageName()) && ((GoFile)psiFile).hasMainFunction();
    }
    return false;
  }

  public static void installFileChooser(@NotNull Project project,
                                        @NotNull TextFieldWithBrowseButton field,
                                        boolean directory) {
    installFileChooser(project, field, directory, null);
  }

  public static void installFileChooser(@NotNull Project project,
                                        @NotNull TextFieldWithBrowseButton field,
                                        boolean directory,
                                        @Nullable Condition<VirtualFile> fileFilter) {
    FileChooserDescriptor chooseDirectoryDescriptor = directory
                                                      ? FileChooserDescriptorFactory.createSingleFolderDescriptor()
                                                      : FileChooserDescriptorFactory.createSingleLocalFileDescriptor();
    chooseDirectoryDescriptor.setRoots(project.getBaseDir());
    chooseDirectoryDescriptor.setShowFileSystemRoots(false);
    chooseDirectoryDescriptor.withFileFilter(fileFilter);
    field.addBrowseFolderListener(new TextBrowseFolderListener(chooseDirectoryDescriptor));
  }
}
