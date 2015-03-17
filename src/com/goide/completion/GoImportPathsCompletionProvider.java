package com.goide.completion;

import com.goide.psi.GoFile;
import com.goide.psi.GoImportString;
import com.goide.stubs.index.GoPackagesIndex;
import com.goide.util.GoUtil;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import com.intellij.util.Processor;
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

    addCompletions(result, ModuleUtilCore.findModuleForPsiElement(parameters.getPosition()));
  }

  public static void addCompletions(@NotNull final CompletionResultSet result, @Nullable Module module) {
    if (module != null) {
      final GlobalSearchScope scope = GoUtil.moduleScope(module);
      final Project project = module.getProject();
      for (String packageName : GoPackagesIndex.getAllPackages(project)) {
        StubIndex.getInstance()
          .processElements(GoPackagesIndex.KEY, packageName, project, scope, GoFile.class, new Processor<GoFile>() {
            @Override
            public boolean process(@NotNull GoFile file) {
              String fullPackageName = file.getImportPath();
              if (fullPackageName != null) {
                result.addElement(GoCompletionUtil.createPackageLookupElement(fullPackageName, false));
              }
              return true;
            }
          });
      }
    }
  }
}
