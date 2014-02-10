package com.goide.inspections.unresolved;

import com.goide.inspections.GoInspectionBase;
import com.goide.psi.GoFile;
import com.goide.psi.GoFunctionDeclaration;
import com.goide.psi.GoRecursiveVisitor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.util.Query;
import org.jetbrains.annotations.NotNull;

public class GoUnusedFunctionInspection extends GoInspectionBase {
  @Override
  protected void checkFile(final PsiFile file, final ProblemsHolder problemsHolder) {
    if (!(file instanceof GoFile)) return;
    file.accept(new GoRecursiveVisitor() {
      @Override
      public void visitFunctionDeclaration(@NotNull GoFunctionDeclaration o) {
        if ("main".equals(((GoFile)file).getPackageName()) && "main".equals(o.getName())) return;
        Query<PsiReference> search = ReferencesSearch.search(o, o.getUseScope());
        if (search.findFirst() == null) {
          problemsHolder.registerProblem(o.getIdentifier(), "Unused function " + "'" + o.getName() + "'", ProblemHighlightType.LIKE_UNUSED_SYMBOL);
        }
      }
    });
  }
}
