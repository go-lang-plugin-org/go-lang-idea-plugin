package com.goide.inspections.unresolved;

import com.goide.inspections.GoInspectionBase;
import com.goide.psi.*;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.Query;
import org.jetbrains.annotations.NotNull;

public class GoUnusedVariableInspection extends GoInspectionBase {
  @Override
  protected void checkFile(PsiFile file, @NotNull final ProblemsHolder problemsHolder) {
    if (!(file instanceof GoFile)) return;
    file.accept(new GoRecursiveVisitor() {
      @Override
      public void visitVarDefinition(@NotNull GoVarDefinition o) {
        if ("_".equals(o.getIdentifier().getText())) return;
        if (PsiTreeUtil.getParentOfType(o, GoShortVarDeclaration.class) == null) return;
        PsiReference reference = o.getReference();
        PsiElement resolve = reference != null ? reference.resolve() : null;
        if (resolve != null) return;
        Query<PsiReference> search = ReferencesSearch.search(o, o.getUseScope());
        if (search.findFirst() == null) {
          problemsHolder.registerProblem(o, "Unused variable " + "'" + o.getText() + "'", ProblemHighlightType.GENERIC_ERROR_OR_WARNING);
        }
      }
    });
  }
}
