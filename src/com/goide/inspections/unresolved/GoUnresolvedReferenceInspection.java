package com.goide.inspections.unresolved;

import com.goide.GoTypes;
import com.goide.codeInsight.imports.GoImportPackageQuickFix;
import com.goide.inspections.GoInspectionBase;
import com.goide.psi.*;
import com.goide.psi.impl.GoReference;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.formatter.FormatterUtil;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.intellij.codeInspection.ProblemHighlightType.GENERIC_ERROR_OR_WARNING;
import static com.intellij.codeInspection.ProblemHighlightType.LIKE_UNKNOWN_SYMBOL;

public class GoUnresolvedReferenceInspection extends GoInspectionBase {
  @Override
  protected void checkFile(@NotNull GoFile file, @NotNull final ProblemsHolder problemsHolder) {
    file.accept(new GoRecursiveVisitor() {
      @Override
      public void visitReferenceExpression(@NotNull GoReferenceExpression o) {
        super.visitReferenceExpression(o);
        GoReference reference = o.getReference();
        GoReferenceExpression qualifier = o.getQualifier();
        GoReference qualifierRef = qualifier != null ? qualifier.getReference() : null;
        PsiElement qualifierResolve = qualifierRef != null ? qualifierRef.resolve() : null;
        if (qualifier != null && qualifierResolve == null) return;
        ResolveResult[] results = reference.multiResolve(false);
        PsiElement id = o.getIdentifier();
        String name = id.getText();
        if (results.length > 1) {
          problemsHolder.registerProblem(id, "Ambiguous reference " + "'" + name + "'", GENERIC_ERROR_OR_WARNING);
        }
        else if (reference.resolve() == null) {
          LocalQuickFix[] fixes = !isProhibited(o, qualifier) ?
                                  new LocalQuickFix[]{
                                    new GoIntroduceLocalVariableFix(id, name),
                                    new GoIntroduceGlobalVariableFix(id, name),
                                    new GoIntroduceGlobalConstantFix(id, name),
                                  } :
                                  new LocalQuickFix[]{new GoImportPackageQuickFix(reference)};
          problemsHolder.registerProblem(id, "Unresolved reference " + "'" + name + "'", LIKE_UNKNOWN_SYMBOL, fixes);
        }
      }

      @Override
      public void visitImportString(@NotNull GoImportString o) {
        if (o.getTextLength() < 2) return;
        PsiReference[] references = o.getReferences();
        for (final PsiReference reference : references) {
          if (reference instanceof FileReference) {
            ResolveResult[] resolveResults = ((FileReference)reference).multiResolve(false);
            if (resolveResults.length == 0) {
              ProblemHighlightType type = reference.getRangeInElement().isEmpty() ? GENERIC_ERROR_OR_WARNING : LIKE_UNKNOWN_SYMBOL;
              problemsHolder.registerProblem(reference, ProblemsHolder.unresolvedReferenceMessage(reference), type);
            }
            else if (resolveResults.length > 1) {
              problemsHolder.registerProblem(reference, "Resolved to several targets", GENERIC_ERROR_OR_WARNING);
            } 
          }
        }
      }

      @Override
      public void visitLabelRef(@NotNull GoLabelRef o) {
        PsiReference reference = o.getReference();
        String name = o.getText();
        if (reference.resolve() == null) {
          problemsHolder.registerProblem(o, "Unresolved label " + "'" + name + "'", LIKE_UNKNOWN_SYMBOL);
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
        if (reference.resolve() == null) {
          PsiElement id = o.getIdentifier();
          String name = id.getText();
          boolean isProhibited = isProhibited(o, qualifier);
          LocalQuickFix[] fixes = isProhibited
                                  ? new LocalQuickFix[]{new GoImportPackageQuickFix(reference)}
                                  : new LocalQuickFix[]{new GoIntroduceTypeFix(id, name)};
          problemsHolder.registerProblem(id, "Unresolved type " + "'" + name + "'", LIKE_UNKNOWN_SYMBOL, fixes);
        }
      }
    });
  }

  private static boolean isProhibited(@NotNull GoCompositeElement o, @Nullable GoCompositeElement qualifier) {
    ASTNode next = FormatterUtil.getNextNonWhitespaceSibling(o.getNode());
    boolean isDot = next != null && next.getElementType() == GoTypes.DOT;
    return isDot || qualifier != null;
  }
}