/*
 * Copyright 2013-2014 Sergey Ignatov, Alexander Zolotov
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
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GoInspectionUtil {
  public static final int UNKNOWN_COUNT = -1;
  public static final int VARIADIC_COUNT = -2;

  public static int getExpressionResultCount(GoExpression call) {
    if (call instanceof GoLiteral
        || call instanceof GoBinaryExpr
        || (call instanceof GoUnaryExpr && ((GoUnaryExpr)call).getSendChannel() == null)
        || call instanceof GoParenthesesExpr
        || call instanceof GoSelectorExpr
      ) {
      return 1;
    }
    else if (call instanceof GoTypeAssertionExpr) {
      return getTypeAssertionResultCount((GoTypeAssertionExpr)call);
    }
    else if (call instanceof GoCallExpr) {
      return getFunctionResultCount((GoCallExpr)call);
    }
    return UNKNOWN_COUNT;
  }

  private static int getTypeAssertionResultCount(@NotNull GoTypeAssertionExpr expression) { // todo: ???
    PsiElement parent = expression.getParent();
    if (parent instanceof GoAssignmentStatement) {
      // TODO: get expressions and identifiers of assign statement
      return UNKNOWN_COUNT;
    }

    if (!(parent instanceof GoVarSpec)) {
      return 1;
    }

    List<GoVarDefinition> identifiers = ((GoVarSpec)parent).getVarDefinitionList();
    List<GoExpression> expressions = ((GoVarSpec)parent).getExpressionList();
    // if the type assertion is the only expression, and there are two variables.
    // The result of the type assertion is a pair of values with types (T, bool)
    if (identifiers.size() == 2 && expressions.size() == 1) {
      return 2;
    }

    return 1;
  }

  private static int getFunctionResultCount(@NotNull GoCallExpr call) {
    GoFunctionOrMethodDeclaration declaration = resolveFunctionCall(call);
    return declaration == null ? UNKNOWN_COUNT : getFunctionResultCount(declaration);
  }

  @Nullable
  private static GoFunctionOrMethodDeclaration resolveFunctionCall(@NotNull GoCallExpr call) {
    PsiReference reference = call.getExpression().getReference();
    PsiElement function = reference != null ? reference.resolve() : null;
    return function instanceof GoFunctionOrMethodDeclaration ? (GoFunctionOrMethodDeclaration)function : null;
  }

  public static int getFunctionResultCount(@NotNull GoFunctionOrMethodDeclaration function) {
    int count = 0;
    GoSignature signature = function.getSignature();
    GoResult result = signature != null ? signature.getResult() : null;
    GoParameters parameters = result != null ? result.getParameters() : null;
    if (parameters != null) {
      for (GoParameterDeclaration p : parameters.getParameterDeclarationList()) {
        count += Math.max(p.getParamDefinitionList().size(), 1);
      }
      return count;
    }
    else if (result != null) {
      GoType type = result.getType();
      if (type instanceof GoTypeList) return ((GoTypeList)type).getTypeList().size();
      if (type != null) return 1;
    }
    return count;
  }

  public static int getFunctionParameterCount(@NotNull GoCallExpr call) {
    GoFunctionOrMethodDeclaration function = resolveFunctionCall(call);
    if (function == null) {
      return UNKNOWN_COUNT;
    }

    int count = 0;

    GoSignature signature = function.getSignature();
    GoParameters parameters = signature != null ? signature.getParameters() : null;
    List<GoParameterDeclaration> list = parameters != null ? 
                                        parameters.getParameterDeclarationList() : 
                                        ContainerUtil.<GoParameterDeclaration>emptyList();
    for (GoParameterDeclaration p : list) {
      if (p.getTripleDot() != null) return VARIADIC_COUNT;
      count += Math.max(p.getParamDefinitionList().size(), 1);
    }
    return count;
  }

  public static void checkExpressionShouldReturnOneResult(@NotNull List<GoExpression> expressions, @NotNull ProblemsHolder result) {
    for (GoExpression expr : expressions) {
      int count = getExpressionResultCount(expr);
      if (count != UNKNOWN_COUNT && count != 1) {
        String text = expr.getText();
        if (expr instanceof GoCallExpr) {
          text = ((GoCallExpr)expr).getExpression().getText();
        }

        String msg = "Multiple-value " + text + "() in single-value context";
        result.registerProblem(expr, msg, ProblemHighlightType.GENERIC_ERROR);
      }
    }
  }
}
