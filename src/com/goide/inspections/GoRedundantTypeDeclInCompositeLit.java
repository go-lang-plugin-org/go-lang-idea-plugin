/*
 * Copyright 2013-2016 Sergey Ignatov, Alexander Zolotov, Florin Patan
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

package com.goide.inspections;

import com.goide.psi.*;
import com.goide.quickfix.GoDeleteRangeQuickFix;
import com.intellij.codeInspection.CleanupLocalInspectionTool;
import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GoRedundantTypeDeclInCompositeLit extends GoInspectionBase implements CleanupLocalInspectionTool {
  public final static String DELETE_REDUNDANT_TYPE_DECLARATION_QUICK_FIX_NAME = "Delete redundant type declaration";

  @NotNull
  @Override
  protected GoVisitor buildGoVisitor(@NotNull final ProblemsHolder holder, @NotNull LocalInspectionToolSession session) {
    return new GoVisitor() {
      @Override
      public void visitCompositeLit(@NotNull GoCompositeLit o) {
        GoLiteralValue literalValue = o.getLiteralValue();
        if (literalValue == null) return;

        GoType expectedType = getExpectedType(o);

        // TODO o.getType() instanceof GoStruct (struct or T[][])

        if (expectedType != null) {
          for (GoElement element : literalValue.getElementList()) {
            GoValue elementValue = element.getValue();
            if (elementValue != null) {
              GoExpression expr = elementValue.getExpression();
              if (expectedType instanceof GoPointerType && expr instanceof GoUnaryExpr) {
                GoUnaryExpr unaryExpr = (GoUnaryExpr)expr;
                PsiElement bitAnd = unaryExpr.getBitAnd();
                if (bitAnd != null && unaryExpr.getExpression() instanceof GoCompositeLit) {
                  GoCompositeLit compositeLit = (GoCompositeLit)unaryExpr.getExpression();
                  if (isTypeReferencesEquals(((GoPointerType)expectedType).getType(), compositeLit)) {
                    registerRedundantTypeDeclarationProblem(holder, bitAnd, compositeLit.getTypeReferenceExpression());
                  }
                }
              }
              else if (expr instanceof GoCompositeLit && isTypeReferencesEquals(expectedType, (GoCompositeLit)expr)) {
                registerRedundantTypeDeclarationProblem(holder, ((GoCompositeLit)expr).getTypeReferenceExpression(),
                                                        ((GoCompositeLit)expr).getTypeReferenceExpression());
              }
            }
          }
        }
      }
    };
  }

  @Nullable
  private static GoType getExpectedType(@NotNull GoCompositeLit o) {
    if (o.getType() instanceof GoArrayOrSliceType && ((GoArrayOrSliceType)o.getType()).getType() != null) {
      return ((GoArrayOrSliceType)o.getType()).getType();
    }
    if (o.getType() instanceof GoMapType && ((GoMapType)o.getType()).getValueType() != null) {
      return ((GoMapType)o.getType()).getValueType();
    }
    return null;
  }

  // TODO o to concrete type
  private static void registerRedundantTypeDeclarationProblem(@NotNull final ProblemsHolder holder,
                                                              @Nullable PsiElement start,
                                                              @Nullable GoTypeReferenceExpression end) {
    if (start != null && end != null) {
      GoDeleteRangeQuickFix fix = new GoDeleteRangeQuickFix(start, end, DELETE_REDUNDANT_TYPE_DECLARATION_QUICK_FIX_NAME);
      holder.registerProblem(holder.getManager().createProblemDescriptor(start, end, "Redundant type declaration",
                                                                         ProblemHighlightType.LIKE_UNUSED_SYMBOL, holder.isOnTheFly(),
                                                                         fix));
    }
  }

  private static boolean isTypeReferencesEquals(@Nullable GoType pattern, @NotNull GoCompositeLit value) {
    if (pattern == null || !pattern.isValid() || !value.isValid()) {
      return false;
    }

    if (pattern.getTypeReferenceExpression() == null || value.getTypeReferenceExpression() == null) {
      return false;
    }

    if (pattern.getTypeReferenceExpression().resolve() != value.getTypeReferenceExpression().resolve()) {
      return false;
    }
    //TODO Complex type comparison
    return true;
  }
}