package com.goide.inspections.unresolved;

import com.goide.inspections.GoInspectionBase;
import com.goide.psi.GoFile;
import com.goide.psi.GoFunctionDeclaration;
import com.goide.psi.GoRecursiveVisitor;
import com.goide.runconfig.testing.GoTestFinder;
import com.intellij.codeInspection.LocalQuickFixBase;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
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
        String name = o.getName();
        if ("main".equals(((GoFile)file).getPackageName()) && "main".equals(name)) return;
        if (GoTestFinder.isTestFile(file) && name != null && (name.startsWith("Test") || name.startsWith("Benchmark"))) return;
        Query<PsiReference> search = ReferencesSearch.search(o, o.getUseScope());
        if (search.findFirst() == null) {
          PsiElement id = o.getIdentifier();
          TextRange range = TextRange.from(id.getStartOffsetInParent(), id.getTextLength());
          problemsHolder.registerProblem(o, "Unused function " + "'" + name + "'", ProblemHighlightType.LIKE_UNUSED_SYMBOL, range,
            new LocalQuickFixBase("Delete function '" + name + "'") {
              @Override
              public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
                PsiElement element = descriptor.getPsiElement();
                if (element != null) {
                  element.delete();
                }
              }
            });
        }
      }
    });
  }
}
