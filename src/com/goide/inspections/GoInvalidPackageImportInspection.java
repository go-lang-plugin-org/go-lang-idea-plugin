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

package com.goide.inspections;

import com.goide.GoConstants;
import com.goide.project.GoVendoringUtil;
import com.goide.psi.GoFile;
import com.goide.psi.GoImportSpec;
import com.goide.psi.impl.imports.GoImportReference;
import com.goide.quickfix.GoDeleteImportQuickFix;
import com.goide.quickfix.GoDisableVendoringInModuleQuickFix;
import com.goide.sdk.GoPackageUtil;
import com.goide.sdk.GoSdkUtil;
import com.intellij.codeInsight.intention.HighPriorityAction;
import com.intellij.codeInspection.LocalQuickFixBase;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Set;

public class GoInvalidPackageImportInspection extends GoInspectionBase {
  @Override
  protected void checkFile(@NotNull GoFile file, @NotNull ProblemsHolder problemsHolder) {
    Module module = ModuleUtilCore.findModuleForPsiElement(file);
    boolean vendoringEnabled = GoVendoringUtil.isVendoringEnabled(module);
    Set<VirtualFile> sourceRoots = GoSdkUtil.getSourcesPathsToLookup(file.getProject(), module, vendoringEnabled ? file : null);
    for (GoImportSpec importSpec : file.getImports()) {
      PsiDirectory resolve = importSpec.getImportString().resolve();
      if (resolve != null) {
        if (GoPackageUtil.isBuiltinPackage(resolve)) {
          problemsHolder.registerProblem(importSpec, "Cannot import 'builtin' package", new GoDeleteImportQuickFix());
        }
        Collection<String> packagesInDirectory = GoPackageUtil.getAllPackagesInDirectory(resolve, true);
        if (packagesInDirectory.isEmpty()) {
          problemsHolder.registerProblem(importSpec, "'" + resolve.getVirtualFile().getPath() + "' has no buildable Go source files",
                                         new GoDeleteImportQuickFix());
          continue;
        }
        if (packagesInDirectory.size() == 1 && packagesInDirectory.contains(GoConstants.MAIN)) {
          problemsHolder.registerProblem(importSpec, "'" + importSpec.getPath() + "' is a program, not an importable package",
                                         new GoDeleteImportQuickFix());
          continue;
        }
        if (packagesInDirectory.size() > 1) {
          problemsHolder.registerProblem(importSpec, "Found several packages [" + StringUtil.join(packagesInDirectory, ", ") + "] in '" +
                                                     resolve.getVirtualFile().getPath() + "'", new GoDeleteImportQuickFix());
          continue;
        }

        for (PsiReference reference : importSpec.getImportString().getReferences()) {
          if (reference instanceof GoImportReference) {
            if (vendoringEnabled && GoConstants.VENDOR.equals(reference.getCanonicalText())) {
              VirtualFile resolvedVirtualFile = resolve.getVirtualFile();
              if (GoSdkUtil.isUnreachableVendoredPackage(resolvedVirtualFile, file.getVirtualFile(), sourceRoots)) {
                problemsHolder.registerProblem(importSpec, "Use of vendored package is not allowed",
                                               new GoDeleteImportQuickFix(), GoDisableVendoringInModuleQuickFix.create(module));
              }
              else {
                String vendoredImportPath = GoSdkUtil.getVendoringAwareImportPath(resolve, file);
                if (vendoredImportPath != null) {
                  problemsHolder.registerProblem(importSpec, "Must be imported as '" + vendoredImportPath + "'",
                                                 new GoReplaceImportPath(vendoredImportPath),
                                                 new GoDeleteImportQuickFix(), GoDisableVendoringInModuleQuickFix.create(module));
                }
              }
              break;
            }
          }
        }
      }
    }
  }

  private static class GoReplaceImportPath extends LocalQuickFixBase implements HighPriorityAction {
    @NotNull private final String myNewImportPath;

    protected GoReplaceImportPath(@NotNull String newImportPath) {
      super("Replace with '" + newImportPath + "'");
      myNewImportPath = newImportPath;
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
      PsiElement element = descriptor.getPsiElement();
      PsiFile file = element != null ? element.getContainingFile() : null;
      if (!(element instanceof GoImportSpec) || !(file instanceof GoFile)) return;
      ElementManipulators.handleContentChange(((GoImportSpec)element).getImportString(), myNewImportPath);
    }
  }
}
