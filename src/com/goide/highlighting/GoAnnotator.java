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
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

public class GoAnnotator implements Annotator {
  private static final Set<String> intTypeNames = Sets.newHashSet(
    "int",
    "int8",
    "int16",
    "int32",
    "int64",
    "uint",
    "uint8",
    "uint16",
    "uint32",
    "uintptr"
  );

  @Override
  public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
    if (!(element instanceof GoCompositeElement) || !element.isValid()) {
      return;
    }

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
  }

  private static void checkMakeCall(GoBuiltinCallExpr call, AnnotationHolder holder) {
    if (call.getBuiltinArgs() == null) {
      holder.createErrorAnnotation(call, "Missing argument to make");
    }
    else {
      GoBuiltinArgs args = call.getBuiltinArgs();
      if (args.getType() == null) {
        if (!args.getExpressionList().isEmpty()) {
          holder.createErrorAnnotation(call, args.getExpressionList().get(0).getText() + " is not a type");
        }
      }
      else {
        // We have a type, is it valid?
        GoType baseType = getBaseType(args.getType());
        if (!canMakeType(baseType)) {
          holder.createErrorAnnotation(args.getType(), "Cannot make " + args.getType().getText());
        }
        else {
          // We have a type and we can make the type, are the parameters to make valid?
          checkMakeArgs(call, baseType, args.getExpressionList(), holder);
        }
      }
    }
  }

  private static boolean canMakeType(GoType type) {
    if (type instanceof GoArrayOrSliceType) {
      // Only slices (no size expression) can be make()'d.
      return ((GoArrayOrSliceType)type).getExpression() == null;
    }

    if (type instanceof GoChannelType || type instanceof GoMapType) {
      return true;
    }

    return false;
  }

  private static void checkMakeArgs(GoBuiltinCallExpr call,
                                    GoType baseType,
                                    List<GoExpression> list,
                                    AnnotationHolder holder) {
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
      GoType expressionType = expression.getGoType(null);
      if (expressionType != null) {
        GoType expressionBaseType = getBaseType(expressionType);
        if (!isIntegerType(expressionBaseType)) {
          String argName = i == 0 ? "size" : "capacity";
          holder.createErrorAnnotation(expression, "Non-integer " + argName + " argument to make");
        }
      }
    }
  }

  private static GoType getBaseType(GoType type) {
    if (type.getTypeReferenceExpression() != null) {
      type = GoPsiImplUtil.findBaseTypeFromRef(type.getTypeReferenceExpression());
    }

    if (type instanceof GoSpecType) {
      type = ((GoSpecType)type).getType();
    }

    return type;
  }

  private static boolean isIntegerType(GoType type) {
    if (type.getTypeReferenceExpression() == null) {
      return false;
    }
    return intTypeNames.contains(type.getTypeReferenceExpression().getText());
  }

  /**
   * Returns {@code true} if the given element is in an invalid location for a type literal or type reference.
   */
  private static boolean isIllegalUseOfTypeAsExpression(@NotNull PsiElement element) {
    PsiElement parent = PsiTreeUtil.skipParentsOfType(element, GoParenthesesExpr.class, GoUnaryExpr.class);

    if (parent instanceof GoReferenceExpression || parent instanceof GoSelectorExpr) {
      // Part of a selector such as T.method
      return false;
    }

    if (parent instanceof GoCallExpr) {
      // A situation like T("foo").
      return false;
    }

    return true;
  }
}
