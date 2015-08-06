/*
 * Copyright 2013-2015 Sergey Ignatov, Alexander Zolotov, Mihai Toader, Florin Patan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.goide.inspections.unresolved;

import com.goide.GoConstants;
import com.goide.GoTypes;
import com.goide.codeInsight.imports.GoImportPackageQuickFix;
import com.goide.inspections.GoInspectionBase;
import com.goide.psi.*;
import com.goide.psi.impl.GoReference;
import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.formatter.FormatterUtil;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReference;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.intellij.codeInspection.ProblemHighlightType.GENERIC_ERROR_OR_WARNING;
import static com.intellij.codeInspection.ProblemHighlightType.LIKE_UNKNOWN_SYMBOL;

public class GoUnresolvedReferenceInspection extends GoInspectionBase {
  @NotNull
  @Override
  protected GoVisitor buildGoVisitor(@NotNull final ProblemsHolder holder,
                                     @SuppressWarnings({"UnusedParameters", "For future"}) @NotNull LocalInspectionToolSession session) {
    return new GoVisitor() {
      @Override
      public void visitFieldName(@NotNull GoFieldName o) {
        super.visitFieldName(o);
        PsiElement resolve = o.getReference().resolve();
        if (resolve == null) {
          PsiElement id = o.getIdentifier();
          holder.registerProblem(id, "unknown field '" + id.getText() + "' in struct literal", LIKE_UNKNOWN_SYMBOL);
        }
      }

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
          holder.registerProblem(id, "Ambiguous reference " + "'" + name + "'", GENERIC_ERROR_OR_WARNING);
        }
        else if (reference.resolve() == null) {
          LocalQuickFix[] fixes = LocalQuickFix.EMPTY_ARRAY;
          if (isProhibited(o, qualifier)) {
            fixes = createImportPackageFixes(o, reference);
          }
          else if (holder.isOnTheFly()) {
            fixes = new LocalQuickFix[]{
              new GoIntroduceLocalVariableFix(id, name),
              new GoIntroduceGlobalVariableFix(id, name),
              new GoIntroduceGlobalConstantFix(id, name),
            };
          }
          holder.registerProblem(id, "Unresolved reference " + "'" + name + "'", LIKE_UNKNOWN_SYMBOL, fixes);
        }
      }

      @Override
      public void visitImportString(@NotNull GoImportString o) {
        if (o.getTextLength() < 2) return;
        if (GoConstants.C_PATH.equals(o.getPath())) return;
        PsiReference[] references = o.getReferences();
        for (final PsiReference reference : references) {
          if (reference instanceof FileReference) {
            ResolveResult[] resolveResults = ((FileReference)reference).multiResolve(false);
            if (resolveResults.length == 0) {
              ProblemHighlightType type = reference.getRangeInElement().isEmpty() ? GENERIC_ERROR_OR_WARNING : LIKE_UNKNOWN_SYMBOL;
              holder.registerProblem(reference, ProblemsHolder.unresolvedReferenceMessage(reference), type);
            }
            else if (resolveResults.length > 1 && ((FileReference)reference).isLast()) {
              holder.registerProblem(reference, "Resolved to several targets", GENERIC_ERROR_OR_WARNING);
            }
          }
        }
      }

      @Override
      public void visitLabelRef(@NotNull GoLabelRef o) {
        PsiReference reference = o.getReference();
        String name = o.getText();
        if (reference.resolve() == null) {
          holder.registerProblem(o, "Unresolved label " + "'" + name + "'", LIKE_UNKNOWN_SYMBOL);
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
          LocalQuickFix[] fixes = LocalQuickFix.EMPTY_ARRAY;
          if (isProhibited) {
            fixes = createImportPackageFixes(o, reference);
          }
          else if (holder.isOnTheFly()) {
            fixes = new LocalQuickFix[]{new GoIntroduceTypeFix(id, name)};
          }
          holder.registerProblem(id, "Unresolved type " + "'" + name + "'", LIKE_UNKNOWN_SYMBOL, fixes);
        }
      }
    };
  }

  @NotNull
  private static LocalQuickFix[] createImportPackageFixes(@NotNull PsiElement target, @NotNull PsiReference reference) {
    GoImportPackageQuickFix importFix = new GoImportPackageQuickFix(reference);
    return importFix.isAvailable(target.getProject(), target.getContainingFile(), target, target)
           ? new LocalQuickFix[]{importFix}
           : LocalQuickFix.EMPTY_ARRAY;
  }

  private static boolean isProhibited(@NotNull GoCompositeElement o, @Nullable GoCompositeElement qualifier) {
    if (PsiTreeUtil.getPrevSiblingOfType(o, GoReceiverType.class) != null) return true;
    ASTNode next = FormatterUtil.getNextNonWhitespaceSibling(o.getNode());
    boolean isDot = next != null && next.getElementType() == GoTypes.DOT;
    return isDot || qualifier != null;
  }
}