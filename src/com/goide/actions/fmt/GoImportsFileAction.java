/*
 * Copyright 2013-2014 Sergey Ignatov, Alexander Zolotov
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

package com.goide.actions.fmt;

import com.goide.GoEnvironmentUtil;
import com.goide.sdk.GoSdkService;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.configurations.PathEnvironmentVariableUtil;
import com.intellij.execution.process.KillableColoredProcessHandler;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class GoImportsFileAction extends GoExternalToolsAction {

  @NotNull
  @Override
  protected String getWarningTitle() {
    return "Optimizing with goimports";
  }

  @NotNull
  @Override
  protected String getErrorTitle(@NotNull String fileName) {
    return fileName + " optimizing imports with goimports failed. You maybe need to install goimports.";
  }
  
  
  public boolean doSomething(@NotNull PsiFile file,
                             @NotNull Project project,
                             @Nullable final VirtualFile virtualFile,
                             @NotNull String groupId) throws ExecutionException {
    if (virtualFile == null || !virtualFile.isInLocalFileSystem()) return true;
    Document document = PsiDocumentManager.getInstance(project).getDocument(file);
    assert document != null;
    String filePath = virtualFile.getCanonicalPath();
    assert filePath != null;

    GeneralCommandLine commandLine = new GeneralCommandLine();
    String sdkHome = GoSdkService.getInstance(project).getSdkHomePath(ModuleUtilCore.findModuleForPsiElement(file));
    if (StringUtil.isEmpty(sdkHome)) {
      warning(project, groupId, "Project sdk is not valid");
      return true;
    }

    File executable = PathEnvironmentVariableUtil.findInPath(GoEnvironmentUtil.getBinaryFileNameForPath("goimports"));

    if (executable == null) {
      warning(project, groupId, "Looks like you haven't any goimports executable in your PATH");
      return true;
    }

    commandLine.setExePath(executable.getAbsolutePath());
    commandLine.addParameters("-w", filePath);

    FileDocumentManager.getInstance().saveDocument(document);

    String commandLineString = commandLine.getCommandLineString();
    OSProcessHandler handler = new KillableColoredProcessHandler(commandLine.createProcess(), commandLineString);
    handler.addProcessListener(new ProcessAdapter() {
      @Override
      public void processTerminated(ProcessEvent event) {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
          @Override
          public void run() {
            ApplicationManager.getApplication().runWriteAction(new Runnable() {
              @Override
              public void run() {
                virtualFile.refresh(false, false);
              }
            });
          }
        });
      }
    });
    handler.startNotify();
    return false;
  }

  
}
