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

package com.goide.highlighting;

import com.goide.psi.*;
import com.goide.psi.impl.GoPsiImplUtil;
import com.goide.quickfix.GoReplaceWithReturnStatementQuickFix;
import com.google.common.collect.Sets;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

public class GoAnnotator implements Annotator {
  private static final Set<String> INT_TYPE_NAMES = Sets.newHashSet(
    "int", "int8", "int16", "int32", "int64", "uint", "uint8", "uint16", "uint32", "uint64", "uintptr"
  ); // todo: unify with DlvApi.Variable.Kind

  @Override
  public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
    if (!(element instanceof GoCompositeElement) || !element.isValid()) return;

    if (element instanceof GoContinueStatement) {
      if (!(PsiTreeUtil.getParentOfType(element, GoForStatement.class, GoFunctionLit.class) instanceof GoForStatement)) {
        Annotation annotation = holder.createErrorAnnotation(element, "Continue statement not inside a for loop");
        annotation.registerFix(new GoReplaceWithReturnStatementQuickFix(element));
      }
    }
    else if (element instanceof GoBreakStatement) {
      if (GoPsiImplUtil.getBreakStatementOwner(element) == null) {
        Annotation annotation = holder.createErrorAnnotation(element, "Break statement not inside a for loop, select or switch");
        annotation.registerFix(new GoReplaceWithReturnStatementQuickFix(element));
      }
    }
    else if (element instanceof GoReferenceExpression) {
      GoReferenceExpression reference = (GoReferenceExpression)element;
      PsiElement resolvedReference = reference.getReference().resolve();
      if (resolvedReference instanceof PsiDirectory || resolvedReference instanceof GoImportSpec) {
        // It's a package reference. It should either be inside a package clause or part of a larger reference expression.
        if (!(element.getParent() instanceof GoReferenceExpression) &&
            PsiTreeUtil.getParentOfType(reference, GoPackageClause.class) == null) {
          holder.createErrorAnnotation(element, "Use of package " + element.getText() + " without selector");
        }
      }
      if (resolvedReference instanceof GoTypeSpec && isIllegalUseOfTypeAsExpression(reference)) {
        holder.createErrorAnnotation(element, "Type " + element.getText() + " is not an expression");
      }
      if (resolvedReference instanceof GoConstDefinition &&
          resolvedReference.getParent() instanceof GoConstSpec &&
          PsiTreeUtil.getParentOfType(element, GoConstDeclaration.class) != null) {
        checkSelfReference((GoReferenceExpression)element, resolvedReference, holder);
      }
      if (resolvedReference instanceof GoVarDefinition &&
          resolvedReference.getParent() instanceof GoVarSpec &&
          PsiTreeUtil.getParentOfType(element, GoVarDeclaration.class) != null) {
        checkSelfReference((GoReferenceExpression)element, resolvedReference, holder);
      }
    }
    else if (element instanceof GoLiteralTypeExpr) {
      if (isIllegalUseOfTypeAsExpression(element)) {
        holder.createErrorAnnotation(element, "Type " + element.getText() + " is not an expression");
      }
    }
    else if (element instanceof GoCompositeLit) {
      GoCompositeLit literal = (GoCompositeLit)element;
      if (literal.getType() instanceof GoMapType) {
        for (GoElement literalElement : literal.getLiteralValue().getElementList()) {
          if (literalElement.getKey() == null) {
            holder.createErrorAnnotation(literalElement, "Missing key in map literal");
          }
        }
      }
    }
    else if (element instanceof GoTypeAssertionExpr) {
      GoType type = ((GoTypeAssertionExpr)element).getExpression().getGoType(null);
      if (type != null) {
        GoType baseType = GoPsiImplUtil.findBaseTypeFromRef(type.getTypeReferenceExpression());
        if (baseType instanceof GoSpecType && !(((GoSpecType)baseType).getType() instanceof GoInterfaceType)) {
          String message =
            String.format("Invalid type assertion: %s, (non-interface type %s on left)", element.getText(), type.getText());
          holder.createErrorAnnotation(((GoTypeAssertionExpr)element).getExpression(), message);
        }
      }
    }
    else if (element instanceof GoBuiltinCallExpr) {
      GoBuiltinCallExpr call = (GoBuiltinCallExpr)element;
      if ("make".equals(call.getReferenceExpression().getText())) {
        checkMakeCall(call, holder);
      }
    }
    else if (element instanceof GoFunctionLit) {
      checkLiteralEvaluatedButNotUsed(holder, element, "Function");
    }
    else if (element instanceof GoStringLiteral) {
      checkLiteralEvaluatedButNotUsed(holder, element, "String");
    }
    else if (element instanceof GoLiteral) {
      String message = ((GoLiteral)element).getChar() != null ? "Rune" : "Number";
      checkLiteralEvaluatedButNotUsed(holder, element, message);
    }
  }

  private static void checkLiteralEvaluatedButNotUsed(@NotNull AnnotationHolder holder, @NotNull PsiElement element, String message) {
    PsiElement parent = element.getParent();
    if (element instanceof GoFunctionLit) {
      if (parent instanceof GoCallExpr) return;
    }
    else if (element instanceof GoStringLiteral ||
             element instanceof GoLiteral) {
      if (parent instanceof GoIndexOrSliceExpr ||
          parent instanceof GoConditionalExpr) {
        return;
      }
    }

    parent = PsiTreeUtil.getParentOfType(element, GoLeftHandExprList.class, GoArgumentList.class);
    if (parent == null) return;
    if (parent instanceof GoArgumentList) return;
    if (parent instanceof GoLeftHandExprList &&
        ((GoLeftHandExprList)parent).getExpressionList().size() != 1) {
      return;
    }

    if (element instanceof GoFunctionLit) {
      element = ((GoFunctionLit)element).getFunc();
    }
    else if (parent.getParent().getParent() instanceof GoSwitchStatement) {
      return;
    }

    holder.createErrorAnnotation(element, message + " literal evaluated but not used");
  }

  private static void checkMakeCall(@NotNull GoBuiltinCallExpr call, @NotNull AnnotationHolder holder) {
    GoBuiltinArgs args = call.getBuiltinArgs();
    if (args == null) {
      holder.createErrorAnnotation(call, "Missing argument to make");
      return;
    }
    GoType type = args.getType();
    if (type == null) {
      GoExpression first = ContainerUtil.getFirstItem(args.getExpressionList());
      if (first != null) {
        holder.createErrorAnnotation(call, first.getText() + " is not a type");
      }
    }
    else {
      // We have a type, is it valid?
      GoType baseType = getBaseType(type);
      if (canMakeType(baseType)) {
        // We have a type and we can make the type, are the parameters to make valid?
        checkMakeArgs(call, baseType, args.getExpressionList(), holder);
      }
      else {
        holder.createErrorAnnotation(type, "Cannot make " + type.getText());
      }
    }
  }

  private static boolean canMakeType(@Nullable GoType type) {
    if (type instanceof GoArrayOrSliceType) {
      // Only slices (no size expression) can be make()'d.
      return ((GoArrayOrSliceType)type).getExpression() == null;
    }
    return type instanceof GoChannelType || type instanceof GoMapType;
  }

  private static void checkMakeArgs(@NotNull GoBuiltinCallExpr call,
                                    @Nullable GoType baseType,
                                    @NotNull List<GoExpression> list,
                                    @NotNull AnnotationHolder holder) {
    if (baseType instanceof GoArrayOrSliceType) {
      if (list.isEmpty()) {
        holder.createErrorAnnotation(call, "Missing len argument to make");
        return;
      }
      else if (list.size() > 2) {
        holder.createErrorAnnotation(call, "Too many arguments to make");
        return;
      }
    }

    if (baseType instanceof GoChannelType || baseType instanceof GoMapType) {
      if (list.size() > 1) {
        holder.createErrorAnnotation(call, "Too many arguments to make");
        return;
      }
    }

    for (int i = 0; i < list.size(); i++) {
      GoExpression expression = list.get(i);
      GoType type = expression.getGoType(null); // todo: context
      if (type != null) {
        GoType expressionBaseType = getBaseType(type);
        if (!isIntegerType(expressionBaseType)) {
          String argName = i == 0 ? "size" : "capacity";
          holder.createErrorAnnotation(expression, "Non-integer " + argName + " argument to make");
        }
      }
    }
  }

  @Nullable
  private static GoType getBaseType(@NotNull GoType type) {
    if (type.getTypeReferenceExpression() != null) {
      type = GoPsiImplUtil.findBaseTypeFromRef(type.getTypeReferenceExpression());
    }
    if (type instanceof GoSpecType) {
      type = GoPsiImplUtil.findBaseSpecType(type);
    }
    return type instanceof GoSpecType ? ((GoSpecType)type).getType() : type;
  }

  private static boolean isIntegerType(@Nullable GoType type) {
    if (type == null) return false;
    GoTypeReferenceExpression ref = type.getTypeReferenceExpression();
    if (ref == null) return false;
    return INT_TYPE_NAMES.contains(ref.getText()) && GoPsiImplUtil.builtin(ref.getReference().resolve());
  }

  private static void checkSelfReference(@NotNull GoReferenceExpression o, PsiElement definition, AnnotationHolder holder) {
    GoExpression value = null;
    if (definition instanceof GoVarDefinition) {
      value = ((GoVarDefinition)definition).getValue();
    }
    else if (definition instanceof GoConstDefinition) {
      value = ((GoConstDefinition)definition).getValue();
    }

    if (value != null &&
        value.equals(PsiTreeUtil.getTopmostParentOfType(o, GoExpression.class))) {
      holder.createErrorAnnotation(o, "Cyclic definition detected");
    }
  }

  /**
   * Returns {@code true} if the given element is in an invalid location for a type literal or type reference.
   */
  private static boolean isIllegalUseOfTypeAsExpression(@NotNull PsiElement e) {
    PsiElement parent = PsiTreeUtil.skipParentsOfType(e, GoParenthesesExpr.class, GoUnaryExpr.class);
    // Part of a selector such as T.method
    if (parent instanceof GoReferenceExpression || parent instanceof GoSelectorExpr) return false;
    // A situation like T("foo").
    return !(parent instanceof GoCallExpr);
  }
}
