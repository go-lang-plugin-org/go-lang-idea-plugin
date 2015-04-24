package com.goide.completion;

import com.goide.GoFileType;
import com.goide.psi.GoImportString;
import com.goide.sdk.GoSdkUtil;
import com.goide.util.GoUtil;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
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

    addCompletions(result, ModuleUtilCore.findModuleForPsiElement(parameters.getPosition()), parameters.getOriginalFile());
  }

  public static void addCompletions(@NotNull CompletionResultSet result, @Nullable Module module, @Nullable PsiElement context) {
    if (module != null) {
      String contextImportPath = GoCompletionUtil.getContextImportPath(context);
      GlobalSearchScope scope = GoUtil.moduleScope(module);
      for (VirtualFile file : FileTypeIndex.getFiles(GoFileType.INSTANCE, scope)) {
        VirtualFile parent = file.getParent();
        if (parent == null) continue;
        String importPath = GoSdkUtil.getPathRelativeToSdkAndLibraries(parent, module.getProject(), module);
        if (StringUtil.isEmpty(importPath) || !GoUtil.importPathAllowed(importPath) || importPath.equals(contextImportPath)) continue;
        result.addElement(GoCompletionUtil.createPackageLookupElement(importPath, contextImportPath, false));
      }
    }
  }
}
