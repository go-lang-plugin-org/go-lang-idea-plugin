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

package com.goide.completion;

import com.goide.GoFileType;
import com.goide.project.GoExcludedPathsSettings;
import com.goide.psi.GoImportString;
import com.goide.sdk.GoSdkUtil;
import com.goide.util.GoUtil;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GoImportPathsCompletionProvider extends CompletionProvider<CompletionParameters> {
  @Override
  protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
    final GoImportString importString = PsiTreeUtil.getParentOfType(parameters.getPosition(), GoImportString.class);
    if (importString == null) return;
    String path = importString.getPath();
    if (path.startsWith("./") || path.startsWith("../")) return;

    TextRange pathRange = importString.getPathTextRange().shiftRight(importString.getTextRange().getStartOffset());
    String newPrefix = parameters.getEditor().getDocument().getText(TextRange.create(pathRange.getStartOffset(), parameters.getOffset()));
    result = result.withPrefixMatcher(result.getPrefixMatcher().cloneWithPrefix(newPrefix));

    addCompletions(result, ModuleUtilCore.findModuleForPsiElement(parameters.getPosition()), parameters.getOriginalFile(), true);
  }

  public static void addCompletions(@NotNull CompletionResultSet result,
                                    @Nullable Module module,
                                    @Nullable PsiElement context,
                                    boolean withLibraries) {
    if (module != null) {
      Project project = module.getProject();
      String contextImportPath = GoCompletionUtil.getContextImportPath(context);
      GoExcludedPathsSettings excludedSettings = GoExcludedPathsSettings.getInstance(project);
      GlobalSearchScope scope = withLibraries ? GoUtil.moduleScope(module) : GoUtil.moduleScopeWithoutLibraries(module);
      for (VirtualFile file : FileTypeIndex.getFiles(GoFileType.INSTANCE, scope)) {
        VirtualFile parent = file.getParent();
        if (parent == null) continue;
        String importPath = GoSdkUtil.getPathRelativeToSdkAndLibraries(parent, project, module);
        if (!StringUtil.isEmpty(importPath) && !importPath.equals(contextImportPath) && !excludedSettings.isExcluded(importPath)) {
          result.addElement(GoCompletionUtil.createPackageLookupElement(importPath, contextImportPath, false));
        }
      }
    }
  }
}
