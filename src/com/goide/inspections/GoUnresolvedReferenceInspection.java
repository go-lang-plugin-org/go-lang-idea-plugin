package com.goide.inspections;

import com.goide.GoTypes;
import com.goide.inspections.unresolved.GoIntroduceGlobalConstantFix;
import com.goide.inspections.unresolved.GoIntroduceGlobalVariableFix;
import com.goide.inspections.unresolved.GoIntroduceLocalVariableFix;
import com.goide.inspections.unresolved.GoIntroduceTypeFix;
import com.goide.psi.*;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.formatter.FormatterUtil;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
        if (reference.resolve() == null) {
          PsiElement id = o.getIdentifier();
          String name = id.getText();
          LocalQuickFix[] fixes = !isProhibited(o, qualifier) ? 
                                  new LocalQuickFix[]{
                                    new GoIntroduceLocalVariableFix(id, name),
                                    new GoIntroduceGlobalVariableFix(id, name),
                                    new GoIntroduceGlobalConstantFix(id, name),
                                  } :
                                  new LocalQuickFix[]{};
          problemsHolder.registerProblem(id, "Unresolved reference " + "'" + name + "'", LIKE_UNKNOWN_SYMBOL, fixes);
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
          LocalQuickFix[] fixes = isProhibited ? new LocalQuickFix[]{} : new LocalQuickFix[]{new GoIntroduceTypeFix(id, name)};
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