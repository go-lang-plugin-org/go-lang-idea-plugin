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

package com.goide.inspections;

import com.goide.GoConstants;
import com.goide.psi.GoFile;
import com.goide.psi.GoPackageClause;
import com.goide.psi.impl.GoElementFactory;
import com.goide.runconfig.testing.GoTestFinder;
import com.goide.util.GoUtil;
import com.intellij.codeInspection.LocalQuickFixAndIntentionActionOnPsiElement;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.ui.components.JBList;
import com.intellij.util.ArrayUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Collection;

public class GoMultiplePackagesQuickFix extends LocalQuickFixAndIntentionActionOnPsiElement {
  private Collection<String> myPackages;
  private String myPackageName;
  private boolean myIsOneTheFly;

  protected GoMultiplePackagesQuickFix(@NotNull PsiElement element, @NotNull String packageName, Collection<String> packages, boolean isOnTheFly) {
    super(element);
    myPackages = packages;
    myPackageName = packageName;
    myIsOneTheFly = isOnTheFly;
  }

  private static void renamePackagesInDirectory(@NotNull final Project project,
                                                @NotNull final PsiDirectory dir,
                                                @NotNull final String newName) {
    WriteCommandAction.runWriteCommandAction(project, new Runnable() {
      @Override
      public void run() {
        for (PsiFile file : dir.getFiles()) {
          if (file instanceof GoFile && !GoUtil.libraryDirectoryToIgnore(file.getName())) {
            GoPackageClause packageClause = ((GoFile)file).getPackage();
            String name = ((GoFile)file).getPackageName();
            if (packageClause != null && name != null && !name.equals(GoConstants.DOCUMENTATION)) {
              String fullName = GoTestFinder.isTestFile(file) ? newName + GoConstants.TEST_SUFFIX : newName;
              packageClause.replace(GoElementFactory.createPackageClause(project, fullName));
            }
          }
        }
      }
    });
  }

  @Override
  public void invoke(@NotNull final Project project,
                     @NotNull final PsiFile file,
                     @Nullable("is null when called from inspection") Editor editor,
                     @NotNull PsiElement startElement,
                     @NotNull PsiElement endElement) {
    if (ApplicationManager.getApplication().isUnitTestMode()) {
      renamePackagesInDirectory(project, file.getContainingDirectory(), ArrayUtil.toStringArray(myPackages)[0]);
      return;
    }
    if (editor == null) {
      renamePackagesInDirectory(project, file.getContainingDirectory(), myPackageName);
      return;
    }
    final JList list = new JBList(myPackages);
    JBPopup popup = JBPopupFactory.getInstance().createListPopupBuilder(list).setItemChoosenCallback(new Runnable() {
      @Override
      public void run() {
        String name = (String)list.getSelectedValue();
        if (name != null) {
          renamePackagesInDirectory(project, file.getContainingDirectory(), name);
        }
      }
    }).createPopup();
    popup.showInBestPositionFor(editor);
  }

  @NotNull
  @Override
  public String getText() {
    return "Rename packages" + (myIsOneTheFly ? "" : " to " + myPackageName);
  }

  @NotNull
  @Override
  public String getFamilyName() {
    return "Go";
  }
}
