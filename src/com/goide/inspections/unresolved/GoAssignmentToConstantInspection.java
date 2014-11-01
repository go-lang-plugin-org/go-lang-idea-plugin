package com.goide.inspections.unresolved;

import com.goide.inspections.GoInspectionBase;
import com.goide.psi.*;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.intellij.codeInspection.ProblemHighlightType.GENERIC_ERROR_OR_WARNING;

public class GoAssignmentToConstantInspection extends GoInspectionBase {
  @Override
  protected void checkFile(@NotNull GoFile file, @NotNull final ProblemsHolder problemsHolder) {
    file.accept(new GoRecursiveVisitor() {
      @Override
      public void visitAssignmentStatement(@NotNull GoAssignmentStatement o) {
        int offset = o.getAssignOp().getTextOffset();
        List<GoExpression> list = o.getExpressionList();
        for (GoExpression expression : list) {
          if (expression.getTextOffset() < offset) checkExpression(expression);
        }
        super.visitAssignmentStatement(o);
      }

      private void checkExpression(GoExpression expression) {
        if (expression instanceof GoReferenceExpression) {
          PsiElement resolve = ((GoReferenceExpression)expression).getReference().resolve();
          if (resolve instanceof GoConstDefinition) {
            String name = ((GoReferenceExpression)expression).getIdentifier().getText();
            problemsHolder.registerProblem(expression, "Cannot assign to constant '" + name + "'", GENERIC_ERROR_OR_WARNING);
          }
        }
      }
    });
  }
}