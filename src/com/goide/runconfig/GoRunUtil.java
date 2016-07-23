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

package com.goide.runconfig;

import com.goide.GoConstants;
import com.goide.GoFileType;
import com.goide.psi.GoFile;
import com.goide.psi.GoPackageClause;
import com.goide.runconfig.testing.GoTestFinder;
import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.process.ProcessOutputTypes;
import com.intellij.ide.scratch.ScratchFileType;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.FileIndexFacade;
import com.intellij.openapi.ui.ComponentWithBrowseButton;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextComponentAccessor;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileSystemItem;
import com.intellij.psi.PsiManager;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class GoRunUtil {
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

    FileIndexFacade indexFacade = FileIndexFacade.getInstance(psiElement.getProject());
    PsiFileSystemItem psiFile = psiElement instanceof PsiFileSystemItem ? (PsiFileSystemItem)psiElement : psiElement.getContainingFile();
    VirtualFile file = psiFile != null ? psiFile.getVirtualFile() : null;
    if (file != null && file.getFileType() != ScratchFileType.INSTANCE && 
        (!indexFacade.isInContent(file) || indexFacade.isExcludedFile(file))) {
      return null;
    }

    return psiElement;
  }

  public static void installGoWithMainFileChooser(Project project, @NotNull TextFieldWithBrowseButton fileField) {
    installFileChooser(project, fileField, false, false, file -> {
      if (file.getFileType() != GoFileType.INSTANCE) {
        return false;
      }
      return isMainGoFile(PsiManager.getInstance(project).findFile(file));
    });
  }

  @Contract("null -> false")
  public static boolean isMainGoFile(@Nullable PsiFile psiFile) {
    if (!GoTestFinder.isTestFile(psiFile) && psiFile instanceof GoFile) {
      return GoConstants.MAIN.equals(((GoFile)psiFile).getPackageName()) && ((GoFile)psiFile).hasMainFunction();
    }
    return false;
  }

  public static void installFileChooser(@NotNull Project project,
                                        @NotNull ComponentWithBrowseButton field,
                                        boolean directory) {
    installFileChooser(project, field, directory, false);
  }

  public static void installFileChooser(@NotNull Project project, @NotNull ComponentWithBrowseButton field, boolean directory,
                                        boolean showFileSystemRoots) {
    installFileChooser(project, field, directory, showFileSystemRoots, null);
  }

  public static void installFileChooser(@NotNull Project project,
                                        @NotNull ComponentWithBrowseButton field,
                                        boolean directory,
                                        boolean showFileSystemRoots,
                                        @Nullable Condition<VirtualFile> fileFilter) {
    FileChooserDescriptor chooseDirectoryDescriptor = directory
                                                      ? FileChooserDescriptorFactory.createSingleFolderDescriptor()
                                                      : FileChooserDescriptorFactory.createSingleLocalFileDescriptor();
    chooseDirectoryDescriptor.setRoots(project.getBaseDir());
    chooseDirectoryDescriptor.setShowFileSystemRoots(showFileSystemRoots);
    chooseDirectoryDescriptor.withFileFilter(fileFilter);
    if (field instanceof TextFieldWithBrowseButton) {
      ((TextFieldWithBrowseButton)field).addBrowseFolderListener(new TextBrowseFolderListener(chooseDirectoryDescriptor, project));
    }
    else {
      //noinspection unchecked
      field.addBrowseFolderListener(project, new ComponentWithBrowseButton.BrowseFolderActionListener(null, null, field, project,
                                                                                                      chooseDirectoryDescriptor,
                                                                                                      TextComponentAccessor.TEXT_FIELD_WITH_HISTORY_WHOLE_TEXT));
    }
  }

  public static void printGoEnvVariables(@NotNull GeneralCommandLine commandLine, @NotNull ProcessHandler handler) {
    Map<String, String> environment = commandLine.getEnvironment();
    handler.notifyTextAvailable("GOROOT=" + StringUtil.nullize(environment.get(GoConstants.GO_ROOT)) + '\n', ProcessOutputTypes.SYSTEM);
    handler.notifyTextAvailable("GOPATH=" + StringUtil.nullize(environment.get(GoConstants.GO_PATH)) + '\n', ProcessOutputTypes.SYSTEM);
  }
}
