package com.goide.inspections.unresolved;

import com.goide.inspections.GoInspectionBase;
import com.goide.psi.GoFile;
import com.goide.psi.GoRecursiveVisitor;
import com.goide.psi.GoShortVarDeclaration;
import com.goide.psi.GoVarDefinition;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.Query;
import org.jetbrains.annotations.NotNull;

public class GoUnusedVariableInspection extends GoInspectionBase {
  @Override
  protected void checkFile(@NotNull GoFile file, @NotNull final ProblemsHolder problemsHolder) {
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
