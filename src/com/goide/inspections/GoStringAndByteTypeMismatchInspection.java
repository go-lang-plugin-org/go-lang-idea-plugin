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

import com.goide.GoTypes;
import com.goide.psi.*;
import com.goide.psi.impl.GoPsiImplUtil;
import com.goide.psi.impl.GoTypeUtil;
import com.goide.quickfix.GoConvertStringToByteQuickFix;
import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.util.Trinity;
import com.intellij.psi.tree.TokenSet;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class GoStringAndByteTypeMismatchInspection extends GoInspectionBase {
  private static final String TEXT_HINT = "Mismatched types: byte and string";
  private static final GoConvertStringToByteQuickFix STRING_INDEX_IS_BYTE_QUICK_FIX = new GoConvertStringToByteQuickFix();

  @NotNull
  @Override
  protected GoVisitor buildGoVisitor(@NotNull ProblemsHolder holder, @NotNull LocalInspectionToolSession session) {
    return new GoVisitor() {

      @Override
      public void visitConditionalExpr(@NotNull GoConditionalExpr o) {
        GoExpression left = o.getLeft();
        GoExpression right = o.getRight();

        GoIndexOrSliceExpr indexExpr = ContainerUtil.findInstance(Arrays.asList(left, right), GoIndexOrSliceExpr.class);
        GoStringLiteral stringLiteral = ContainerUtil.findInstance(Arrays.asList(left, right), GoStringLiteral.class);

        if (indexExpr == null || stringLiteral == null) {
          return;
        }

        if (isStringIndexExpression(indexExpr)) {
          LocalQuickFix[] fixes = GoPsiImplUtil.isSingleCharLiteral(stringLiteral) ? new LocalQuickFix[]{STRING_INDEX_IS_BYTE_QUICK_FIX}
                                                                                   : LocalQuickFix.EMPTY_ARRAY;
          holder.registerProblem(o, TEXT_HINT, ProblemHighlightType.GENERIC_ERROR, fixes);
        }
      }
    };
  }

  private static boolean isStringIndexExpression(@NotNull GoIndexOrSliceExpr expr) {
    GoExpression expression = expr.getExpression();
    if (expression == null || !GoTypeUtil.isString(expression.getGoType(null))) {
      return false;
    }

    Trinity<GoExpression, GoExpression, GoExpression> indices = expr.getIndices();
    return indices.getSecond() == null
           && indices.getThird() == null
           && expr.getNode().getChildren(TokenSet.create(GoTypes.COLON)).length == 0;
  }
}
