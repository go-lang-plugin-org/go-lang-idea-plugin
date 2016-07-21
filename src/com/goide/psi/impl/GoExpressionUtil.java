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
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.Trinity;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GoExpressionUtil {

  public static boolean identical(@Nullable GoExpression left, @Nullable GoExpression right) {
    if (left == right) return true;
    if (left == null || right == null) return false;
    GoExpression l = unwrap(left);
    GoExpression r = unwrap(right);
    if (l == null || r == null) return false;

    if (!l.getClass().equals(r.getClass())) return false;
    if (l instanceof GoBinaryExpr) {
      GoBinaryExpr lBin = (GoBinaryExpr)l;
      GoBinaryExpr rBin = (GoBinaryExpr)r;
      return isOperatorEquals(lBin.getOperator(), rBin.getOperator()) && isChildrenExprEquals(lBin, rBin);
    }
    if (l instanceof GoUnaryExpr) {
      GoUnaryExpr lUnary = (GoUnaryExpr)l;
      GoUnaryExpr rUnary = (GoUnaryExpr)r;
      return isOperatorEquals(lUnary.getOperator(), rUnary.getOperator()) && identical(lUnary.getExpression(), rUnary.getExpression());
    }
    if (l instanceof GoReferenceExpression) {
      PsiElement resolve = ((GoReferenceExpression)l).resolve();
      return resolve != null && resolve.isEquivalentTo(((GoReferenceExpression)r).resolve());
    }
    if (l instanceof GoIndexOrSliceExpr) {
      GoIndexOrSliceExpr lSlice = (GoIndexOrSliceExpr)l;
      GoIndexOrSliceExpr rSlice = (GoIndexOrSliceExpr)r;
      return identical(lSlice.getExpression(), rSlice.getExpression()) && isIndicesIdentical(lSlice.getIndices(), rSlice.getIndices());
    }
    if (l instanceof GoStringLiteral) {
      return Comparing.equal(((GoStringLiteral)l).getDecodedText(), ((GoStringLiteral)r).getDecodedText());
    }
    if (l instanceof GoLiteralTypeExpr) {
      GoLiteralTypeExpr lLit = (GoLiteralTypeExpr)l;
      GoLiteralTypeExpr rLit = (GoLiteralTypeExpr)r;
      GoTypeReferenceExpression lExpr = lLit.getTypeReferenceExpression();
      GoTypeReferenceExpression rExpr = rLit.getTypeReferenceExpression();
      PsiElement lResolve = lExpr != null ? lExpr.resolve() : null;
      return lResolve != null && rExpr != null && lResolve.equals(rExpr.resolve());
      //todo: add || GoTypeUtil.identical(lLit.getType(), rLit.getType)
    }
    if (l instanceof GoLiteral) {
      return l.textMatches(r);
    }
    if (l instanceof GoBuiltinCallExpr || l instanceof GoCallExpr) {
      return false;
    }
    if (l instanceof GoCompositeLit) {
      GoCompositeLit lLit = (GoCompositeLit)l;
      GoCompositeLit rLit = (GoCompositeLit)r;
      return identical(lLit.getLiteralValue(), rLit.getLiteralValue());
      // todo: add && GoTypeUtil.identical
    }
    if (l instanceof GoTypeAssertionExpr) {
      GoTypeAssertionExpr lAssertion = (GoTypeAssertionExpr)l;
      GoTypeAssertionExpr rAssertion = (GoTypeAssertionExpr)r;
      return identical(lAssertion.getExpression(), rAssertion.getExpression());
      //todo: add && GoTypeUtil.identical(lAssertion.getType(), rAssertion.getType())
    }

    String lText = l.getText();
    return lText != null && lText.equals(r.getText());
  }

  private static boolean isIndicesIdentical(@NotNull Trinity<GoExpression, GoExpression, GoExpression> l,
                                            @NotNull Trinity<GoExpression, GoExpression, GoExpression> r) {
    return identical(l.first, r.first) && identical(l.second, r.second) && identical(l.third, r.third);
  }

  private static boolean identical(@Nullable GoLiteralValue l, @Nullable GoLiteralValue r) {
    if (l == null || r == null) return false;
    return l.textMatches(r); //todo: fill
  }

  private static boolean isOperatorEquals(@Nullable PsiElement l, @Nullable PsiElement r) {
    if (l == null || r == null) return false;
    ASTNode lNode = l.getNode();
    ASTNode rNode = r.getNode();
    return lNode instanceof LeafElement && lNode.getElementType().equals(rNode.getElementType());
  }

  private static boolean isOrderImportant(@NotNull GoBinaryExpr o) {
    if (o instanceof GoConversionExpr || o instanceof GoSelectorExpr) return true;
    if (o instanceof GoMulExpr) {
      GoMulExpr m = (GoMulExpr)o;
      return m.getRemainder() != null || m.getQuotient() != null || m.getShiftLeft() != null || m.getShiftRight() != null;
    }
    if (o instanceof GoConditionalExpr) {
      GoConditionalExpr c = (GoConditionalExpr)o;
      return c.getEq() == null && c.getNotEq() == null;
    }
    return false;
  }

  private static boolean isChildrenExprEquals(@NotNull GoBinaryExpr left, @NotNull GoBinaryExpr right) {
    GoExpression l1 = left.getLeft();
    GoExpression l2 = left.getRight();
    GoExpression r1 = right.getLeft();
    GoExpression r2 = right.getRight();

    boolean order = isOrderImportant(left);

    return identical(l1, r1) && identical(l2, r2) || !order && identical(l1, r2) && identical(l2, r1);
  }

  @Nullable
  private static GoExpression unwrap(@Nullable GoExpression o) {
    if (o instanceof GoParenthesesExpr) return unwrap(((GoParenthesesExpr)o).getExpression());
    if (o instanceof GoUnaryExpr && ((GoUnaryExpr)o).getPlus() != null) return unwrap(((GoUnaryExpr)o).getExpression());
    return o;
  }
}
