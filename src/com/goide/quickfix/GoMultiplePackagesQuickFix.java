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

package com.goide.quickfix;

import com.goide.GoConstants;
import com.goide.psi.GoFile;
import com.goide.psi.GoPackageClause;
import com.goide.psi.impl.GoElementFactory;
import com.goide.psi.impl.GoPsiImplUtil;
import com.goide.runconfig.testing.GoTestFinder;
import com.intellij.codeInspection.LocalQuickFixAndIntentionActionOnPsiElement;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.ui.IdeBorderFactory;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

import java.util.Collection;

public class GoMultiplePackagesQuickFix extends LocalQuickFixAndIntentionActionOnPsiElement {
  private static String myTestingPackageName;
  private final Collection<String> myPackages;
  private final String myPackageName;
  private final boolean myIsOneTheFly;

  public GoMultiplePackagesQuickFix(@NotNull PsiElement element,
                                    @NotNull String packageName,
                                    @NotNull Collection<String> packages,
                                    boolean isOnTheFly) {
    super(element);
    myPackages = packages;
    myPackageName = packageName;
    myIsOneTheFly = isOnTheFly;
  }

  private static void renamePackagesInDirectory(@NotNull Project project,
                                                @NotNull PsiDirectory dir,
                                                @NotNull String newName) {
    WriteCommandAction.runWriteCommandAction(project, () -> {
      Module module = ModuleUtilCore.findModuleForPsiElement(dir);
      for (PsiFile file : dir.getFiles()) {
        if (file instanceof GoFile && GoPsiImplUtil.allowed(file, null, module)) {
          GoPackageClause packageClause = ((GoFile)file).getPackage();
          String oldName = ((GoFile)file).getPackageName();
          if (packageClause != null && oldName != null) {
            String fullName = GoTestFinder.isTestFile(file) && StringUtil.endsWith(oldName, GoConstants.TEST_SUFFIX)
                              ? newName + GoConstants.TEST_SUFFIX
                              : newName;
            packageClause.replace(GoElementFactory.createPackageClause(project, fullName));
          }
        }
      }
    });
  }

  @TestOnly
  public static void setTestingPackageName(@NotNull String packageName, @NotNull Disposable disposable) {
    myTestingPackageName = packageName;
    Disposer.register(disposable, () -> {
      //noinspection AssignmentToStaticFieldFromInstanceMethod
      myTestingPackageName = null;
    });
  }

  @Override
  public void invoke(@NotNull Project project,
                     @NotNull PsiFile file,
                     @Nullable("is null when called from inspection") Editor editor,
                     @NotNull PsiElement startElement,
                     @NotNull PsiElement endElement) {
    if (editor == null || myTestingPackageName != null) {
      renamePackagesInDirectory(project, file.getContainingDirectory(),
                                myTestingPackageName != null ? myTestingPackageName : myPackageName);
      return;
    }
    JBList list = new JBList(myPackages);
    list.installCellRenderer(o -> {
      JBLabel label = new JBLabel(o.toString());
      label.setBorder(IdeBorderFactory.createEmptyBorder(2, 4, 2, 4));
      return label;
    });

    JBPopupFactory.getInstance().createListPopupBuilder(list).setTitle("Choose package name").setItemChoosenCallback(() -> {
      String name = (String)list.getSelectedValue();
      if (name != null) {
        renamePackagesInDirectory(project, file.getContainingDirectory(), name);
      }
    }).createPopup().showInBestPositionFor(editor);
  }

  @NotNull
  @Override
  public String getText() {
    return "Rename packages" + (myIsOneTheFly ? "" : " to " + myPackageName);
  }

  @NotNull
  @Override
  public String getFamilyName() {
    return "Rename packages";
  }
}
