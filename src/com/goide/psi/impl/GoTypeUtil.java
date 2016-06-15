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

package com.goide.psi.impl;

import com.goide.psi.*;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.Function;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class GoTypeUtil {
  /**
   * https://golang.org/ref/spec#For_statements
   * The expression on the right in the "range" clause is called the range expression,
   * which may be an array, pointer to an array, slice, string, map, or channel permitting receive operations.
   */
  public static boolean isIterable(@Nullable GoType type) {
    type = type != null ? type.getUnderlyingType() : null;
    return type instanceof GoArrayOrSliceType ||
           type instanceof GoPointerType && isArray(((GoPointerType)type).getType()) ||
           type instanceof GoMapType ||
           type instanceof GoChannelType ||
           isString(type);
  }

  private static boolean isArray(@Nullable GoType type) {
    type = type != null ? type.getUnderlyingType() : null;
    return type instanceof GoArrayOrSliceType && ((GoArrayOrSliceType)type).getExpression() != null;
  }

  public static boolean isString(@Nullable GoType type) {
    GoType underlyingType = type != null ? type.getUnderlyingType() : null;
    return underlyingType != null && underlyingType.textMatches("string") && GoPsiImplUtil.builtin(underlyingType);
  }

  @NotNull
  public static List<GoType> getExpectedTypes(@NotNull GoExpression expression) {
    PsiElement parent = expression.getParent();
    if (parent == null) return Collections.emptyList();
    List<GoType> result = ContainerUtil.newSmartList();
    if (parent instanceof GoAssignmentStatement) {
      return getExpectedTypesFromAssignmentStatement(expression, (GoAssignmentStatement)parent);
    }
    if (parent instanceof GoRangeClause) {
      result.add(getGoType(null, parent));
      return result;
    }
    if (parent instanceof GoVarSpec) {
      return getExpectedTypesFromVarSpec(expression, (GoVarSpec)parent);
    }
    if (parent instanceof GoArgumentList) {
      return getExpectedTypesFromArgumentList(expression, (GoArgumentList)parent);
    }
    if (parent instanceof GoUnaryExpr) {
      GoUnaryExpr unaryExpr = (GoUnaryExpr)parent;
      if (unaryExpr.getSendChannel() != null) {
        GoType type = ContainerUtil.getFirstItem(getExpectedTypes(unaryExpr));
        result.add(GoElementFactory.createType(parent.getProject(), "chan " + getInterfaceIfNull(type, parent).getText()));
      }
      else {
        result.add(getInterfaceIfNull(null, parent));
      }
      return result;
    }
    if (parent instanceof GoSendStatement || parent instanceof GoLeftHandExprList && parent.getParent() instanceof GoSendStatement) {
      return getExpectedTypesFromGoSendStatement(expression, parent);
    }
    if (parent instanceof GoExprCaseClause) {
      return getExpectedTypesFromExprCaseClause((GoExprCaseClause)parent);
    }
    return result;
  }

  @NotNull
  private static List<GoType> getExpectedTypesFromExprCaseClause(@NotNull GoExprCaseClause parent) {
    List<GoType> result = ContainerUtil.newSmartList();
    GoExprSwitchStatement switchStatement = PsiTreeUtil.getParentOfType(parent, GoExprSwitchStatement.class);
    assert switchStatement != null;

    GoType type;
    GoExpression switchExpr = switchStatement.getExpression();
    if (switchExpr != null) {
      type = getGoType(switchExpr, parent);
    }
    else {
      GoStatement statement = switchStatement.getStatement();
      if (statement == null) {
        type = GoElementFactory.createType(parent.getProject(), "bool");
      }
      else {
        GoLeftHandExprList leftHandExprList = ((GoSimpleStatement)statement).getLeftHandExprList();
        GoExpression expr = leftHandExprList != null ? ContainerUtil.getFirstItem(leftHandExprList.getExpressionList()) : null;
        type = getGoType(expr, parent);
      }
    }
    result.add(type);
    return result;
  }

  @NotNull
  private static List<GoType> getExpectedTypesFromGoSendStatement(@NotNull GoExpression expression, @NotNull PsiElement parent) {
    List<GoType> result = ContainerUtil.newSmartList();
    GoSendStatement sendStatement = (GoSendStatement)(parent instanceof GoSendStatement ? parent : parent.getParent());
    GoLeftHandExprList leftHandExprList = sendStatement.getLeftHandExprList();
    GoExpression channel =
      ContainerUtil.getFirstItem(leftHandExprList != null ? leftHandExprList.getExpressionList() : sendStatement.getExpressionList());
    GoExpression sendExpr = sendStatement.getSendExpression();
    assert channel != null;
    if (expression.isEquivalentTo(sendExpr)) {
      GoType chanType = channel.getGoType(null);
      if (chanType instanceof GoChannelType) {
        result.add(getInterfaceIfNull(((GoChannelType)chanType).getType(), parent));
        return result;
      }
    }
    if (expression.isEquivalentTo(channel)) {
      GoType type = sendExpr != null ? sendExpr.getGoType(null) : null;
      result.add(GoElementFactory.createType(parent.getProject(), "chan " + getInterfaceIfNull(type, parent).getText()));
      return result;
    }
    result.add(getInterfaceIfNull(null, parent));
    return result;
  }

  @NotNull
  private static List<GoType> getExpectedTypesFromArgumentList(@NotNull GoExpression expression, @NotNull GoArgumentList argumentList) {
    List<GoType> result = ContainerUtil.newSmartList();
    PsiElement parentOfParent = argumentList.getParent();
    assert parentOfParent instanceof GoCallExpr;
    PsiReference reference = ((GoCallExpr)parentOfParent).getExpression().getReference();
    if (reference != null) {
      PsiElement resolve = reference.resolve();
      if (resolve instanceof GoFunctionOrMethodDeclaration) {
        GoSignature signature = ((GoFunctionOrMethodDeclaration)resolve).getSignature();
        if (signature != null) {
          List<GoExpression> exprList = argumentList.getExpressionList();
          List<GoParameterDeclaration> paramsList = signature.getParameters().getParameterDeclarationList();
          if (exprList.size() == 1) {
            List<GoType> typeList = ContainerUtil.newSmartList();
            for (GoParameterDeclaration parameterDecl : paramsList) {
              for (GoParamDefinition parameter : parameterDecl.getParamDefinitionList()) {
                typeList.add(getGoType(parameter, argumentList));
              }
              if (parameterDecl.getParamDefinitionList().isEmpty()) {
                typeList.add(getInterfaceIfNull(parameterDecl.getType(), argumentList));
              }
            }
            result.add(createGoTypeListOrGoType(typeList, argumentList));
            if (paramsList.size() > 1) {
              assert paramsList.get(0) != null;
              result.add(getInterfaceIfNull(paramsList.get(0).getType(), argumentList));
            }
            return result;
          }
          else {
            int position = exprList.indexOf(expression);
            if (position >= 0) {
              int i = 0;
              for (GoParameterDeclaration parameterDecl : paramsList) {
                int paramDeclSize = parameterDecl.getParamDefinitionList().isEmpty() ? 1 : parameterDecl.getParamDefinitionList().size();
                if (i + paramDeclSize > position) {
                  result.add(getInterfaceIfNull(parameterDecl.getType(), argumentList));
                  return result;
                }
                i+= paramDeclSize;
              }
            }
            result.add(getInterfaceIfNull(null, argumentList));
            return result;
          }
        }
      }
    }
    result.add(getInterfaceIfNull(null, argumentList));
    return result;
  }

  @NotNull
  private static List<GoType> getExpectedTypesFromVarSpec(@NotNull GoExpression expression, @NotNull GoVarSpec varSpec) {
    List<GoType> result = ContainerUtil.newSmartList();
    GoType type = getInterfaceIfNull(varSpec.getType(), varSpec);
    if (varSpec instanceof GoRecvStatement) {
      GoRecvStatement recvStatement = (GoRecvStatement)varSpec;
      type =
        recvStatement.getAssign() != null ? getGoType(ContainerUtil.getFirstItem(recvStatement.getLeftExpressionsList()), varSpec) : type;
    }
    if (varSpec.getRightExpressionsList().size() == 1) {
      List<GoType> typeList = ContainerUtil.newSmartList();
      int defListSize = varSpec.getVarDefinitionList().size();
      for (int i = 0; i < defListSize; i++) {
        typeList.add(type);
      }
      if (varSpec instanceof GoRecvStatement) {
        for (GoExpression expr : ((GoRecvStatement)varSpec).getLeftExpressionsList()) {
          typeList.add(getGoType(expr, varSpec));
        }
      }
      result.add(createGoTypeListOrGoType(typeList, expression));
      if (defListSize > 1) {
        result.add(getInterfaceIfNull(type, varSpec));
      }
      return result;
    }
    result.add(type);
    return result;
  }

  @NotNull
  private static List<GoType> getExpectedTypesFromAssignmentStatement(@NotNull GoExpression expression,
                                                                      @NotNull GoAssignmentStatement assignment) {
    List<GoType> result = ContainerUtil.newSmartList();
    List<GoExpression> leftExpressions = assignment.getLeftHandExprList().getExpressionList();
    if (assignment.getExpressionList().size() == 1) {
      List<GoType> typeList = ContainerUtil.newSmartList();
      for (GoExpression expr : leftExpressions) {
        GoType type = expr.getGoType(null);
        typeList.add(type);
      }
      result.add(createGoTypeListOrGoType(typeList, expression));
      if (leftExpressions.size() > 1) {
        result.add(getGoType(leftExpressions.get(0), assignment));
      }
      return result;
    }
    int position = assignment.getExpressionList().indexOf(expression);
    result.add(leftExpressions.size() > position
               ? assignment.getLeftHandExprList().getExpressionList().get(position).getGoType(null)
               : getInterfaceIfNull(null, assignment));
    return result;
  }

  @NotNull
  private static GoType createGoTypeListOrGoType(@NotNull List<GoType> types, @NotNull PsiElement context) {
    if (types.size() < 2) {
      return getInterfaceIfNull(ContainerUtil.getFirstItem(types), context);
    }
    return GoElementFactory.createTypeList(context.getProject(), StringUtil.join(types, new Function<GoType, String>() {
      @Override
      public String fun(GoType type) {
        return type == null ? "interface{}" : type.getText();
      }
    }, ", "));
  }

  @NotNull
  private static GoType getInterfaceIfNull(@Nullable GoType type, @NotNull PsiElement context) {
    return type == null ? GoElementFactory.createType(context.getProject(), "interface{}") : type;
  }

  @NotNull
  private static GoType getGoType(@Nullable GoTypeOwner element, @NotNull PsiElement context) {
    return getInterfaceIfNull(element == null ? null : element.getGoType(null), context);
  }
}
