/*
 * Copyright 2013-2015 Sergey Ignatov, Alexander Zolotov, Florin Patan
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
import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GoPlaceholderCountInspection extends GoInspectionBase {
  private static final Pattern PLACEHOLDER_PATTERN =
    Pattern.compile("(%#?((\\[\\d+\\]|\\*?)?\\.?)*(\\d*\\.\\d*)*\\s?(v|T|t|b|c|d|o|q|x|X|U|b|e|E|f|F|g|G|s|q|x|X|p))");

  private static final Pattern INDEXED_PLACEHOLDER_PATTERN = Pattern.compile("(?:\\[(\\d+)\\])");

  // This holds the name of the known formatting functions and position of the string to be formatted
  private static final Map<String, Integer> FORMATTING_FUNCTIONS = ContainerUtil.newHashMap(
    Pair.pair("printf", 0),
    Pair.pair("sprintf", 0),
    Pair.pair("errorf", 0),
    Pair.pair("fprintf", 1),
    Pair.pair("fscanf", 1),
    Pair.pair("scanf", 0),
    Pair.pair("sscanf", 1),
    Pair.pair("fatalf", 0),
    Pair.pair("panicf", 0));

  @NotNull
  @Override
  protected GoVisitor buildGoVisitor(@NotNull final ProblemsHolder holder, @NotNull LocalInspectionToolSession session) {
    return new GoVisitor() {
      @Override
      public void visitCallExpr(@NotNull GoCallExpr o) {
        PsiReference psiReference = o.getExpression().getReference();

        if (psiReference == null) return;

        PsiElement resolved = psiReference.resolve();
        if (!(resolved instanceof GoFunctionOrMethodDeclaration)) return;
        if (!isInCorrectPackage((GoFunctionOrMethodDeclaration)resolved)) return;

        String name = getName((GoFunctionOrMethodDeclaration)resolved);
        Integer placeholderPosition = FORMATTING_FUNCTIONS.get(name);
        List<GoExpression> arguments = o.getArgumentList().getExpressionList();
        if (arguments.size() <= placeholderPosition) return;

        GoExpression placeholder = arguments.get(placeholderPosition);
        if (!isStringPlaceholder(placeholder)) {
          String message = "Value used for formatting text does not appear to be a string";
          holder.registerProblem(placeholder, message, ProblemHighlightType.GENERIC_ERROR_OR_WARNING);
          return;
        }

        int parameterCount = arguments.size() - placeholderPosition - 1;
        int placeholdersCount = getPlaceholders(placeholder);
        if (placeholdersCount == -1 || placeholdersCount == parameterCount) return;

        String message = String.format("Got %d placeholder(s) for %d arguments(s)", placeholdersCount, parameterCount);
        holder.registerProblem(placeholder, message, ProblemHighlightType.GENERIC_ERROR_OR_WARNING);
      }
    };
  }

  private static boolean isInCorrectPackage(@NotNull GoFunctionOrMethodDeclaration element) {
    GoFile containingFile = element.getContainingFile();

    return FORMATTING_FUNCTIONS.containsKey(getName(element)) &&
           ("fmt".equals(containingFile.getImportPath()) ||
            "log".equals(containingFile.getImportPath()));
  }

  @Nullable
  private static String getName(@NotNull GoFunctionOrMethodDeclaration element) {
    String name = element.getName();
    if (name == null) return null;

    return name.toLowerCase(Locale.getDefault());
  }

  private static int getPlaceholders(@Nullable GoExpression argument) {
    if (argument == null) return -1;

    String placeholderText = resolve(argument);
    if (placeholderText == null) return -1;


    Matcher placeholders = PLACEHOLDER_PATTERN.matcher(placeholderText);
    Matcher countPlaceholders;

    int indexCount = 0;
    int maxIndexCount = 0;

    while (placeholders.find()) {
      indexCount++;

      countPlaceholders = INDEXED_PLACEHOLDER_PATTERN.matcher(placeholders.group());
      while (countPlaceholders.find()) {
        int index = StringUtil.parseInt(countPlaceholders.group().replaceAll("\\[|\\]", ""), -1);
        if (index > 0 && index != indexCount) {
          indexCount = index;
        }
        if (maxIndexCount < indexCount) {
          maxIndexCount = indexCount;
        }
      }
      if (maxIndexCount < indexCount) {
        maxIndexCount = indexCount;
      }
    }

    return maxIndexCount;
  }

  private static boolean isStringPlaceholder(GoExpression argument) {
    GoType goType = argument.getGoType(null);
    if (goType == null || goType.getTypeReferenceExpression() == null) return false;

    return goType.getTypeReferenceExpression().getText().equals("string");
  }

  @Nullable
  private static String resolve(GoExpression argument) {
    if (argument instanceof GoStringLiteral) return argument.getText();

    if (argument.getReference() == null) return null;

    PsiElement resolved = argument.getReference().resolve();
    if (resolved instanceof GoVarDefinition) {
      return getValue(((GoVarDefinition)resolved).getValue());
    }
    else if (resolved instanceof GoConstDefinition) {
      return getValue(((GoConstDefinition)resolved).getValue());
    }

    return null;
  }

  @Nullable
  private static String getValue(@Nullable GoExpression expression) {
    if (expression instanceof GoStringLiteral) {
      return expression.getText();
    }
    else if (expression instanceof GoAddExpr) {
      String sum = getValue((GoAddExpr)expression);
      return sum.isEmpty() ? null : sum;
    }

    return null;
  }

  @NotNull
  private static String getValue(@Nullable GoAddExpr expression) {
    if (expression == null) return "";
    String result = "";
    for (GoExpression expr : expression.getExpressionList()) {
      if (expr instanceof GoStringLiteral) {
        result += expr.getText();
      }
      else if (expr instanceof GoAddExpr) {
        result += getValue(expr);
      }
    }

    return result;
  }
}
