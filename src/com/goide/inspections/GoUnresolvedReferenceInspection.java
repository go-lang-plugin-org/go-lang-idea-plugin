package com.goide.inspections;

import com.goide.psi.*;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReference;
import org.jetbrains.annotations.NotNull;

import static com.intellij.codeInspection.ProblemHighlightType.GENERIC_ERROR_OR_WARNING;
import static com.intellij.codeInspection.ProblemHighlightType.LIKE_UNKNOWN_SYMBOL;

public class GoUnresolvedReferenceInspection extends GoInspectionBase {
  @Override
  protected void checkFile(PsiFile file, final ProblemsHolder problemsHolder) {
    if (!(file instanceof GoFile)) return;
    file.accept(new GoRecursiveVisitor() {
      @Override
      public void visitReferenceExpression(@NotNull GoReferenceExpression o) {
        super.visitReferenceExpression(o);
        PsiReference reference = o.getReference();
        GoReferenceExpression qualifier = o.getQualifier();
        PsiReference qualifierRef = qualifier != null ? qualifier.getReference() : null;
        PsiElement qualifierResolve = qualifierRef != null ? qualifierRef.resolve() : null;
        if (qualifier != null && qualifierResolve == null) return;
        if (reference == null || reference.resolve() == null) {
          PsiElement id = o.getIdentifier();
          String name = id.getText();
          problemsHolder.registerProblem(id, "Unresolved reference " + "'" + name + "'", LIKE_UNKNOWN_SYMBOL);
        }
      }

      @Override
      public void visitImportString(@NotNull GoImportString o) {
        if (o.getTextLength() < 2) return;
        PsiReference[] references = o.getReferences();
        for (final PsiReference reference : references) {
          if (reference instanceof FileReference) {
            if (((FileReference)reference).multiResolve(false).length == 0) {
              ProblemHighlightType type = reference.getRangeInElement().isEmpty() ? GENERIC_ERROR_OR_WARNING : LIKE_UNKNOWN_SYMBOL;
              problemsHolder.registerProblem(reference, ProblemsHolder.unresolvedReferenceMessage(reference), type);
            }
          }
        }
      }

      @Override
      public void visitTypeReferenceExpression(@NotNull GoTypeReferenceExpression o) {
        super.visitTypeReferenceExpression(o);
        PsiReference reference = o.getReference();
        GoTypeReferenceExpression qualifier = o.getQualifier();
        PsiReference qualifierRef = qualifier != null ? qualifier.getReference() : null;
        PsiElement qualifierResolve = qualifierRef != null ? qualifierRef.resolve() : null;
        if (qualifier != null && qualifierResolve == null) return;
        if (reference == null || reference.resolve() == null) {
          PsiElement id = o.getIdentifier();
          String name = id.getText();
          problemsHolder.registerProblem(id, "Unresolved type " + "'" + name + "'", LIKE_UNKNOWN_SYMBOL);
        }
      }
    });
  }
}
