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

package com.goide.psi;

import com.goide.GoCodeInsightFixtureTestCase;
import com.intellij.codeInsight.highlighting.ReadWriteAccessDetector;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

public class GoReadWriteAccessTest extends GoCodeInsightFixtureTestCase {
  public void testAssignment() {
    doTest("fo<caret>o = 1", ReadWriteAccessDetector.Access.Write);
  }

  public void testSimpleStatement() {
    doTest("fo<caret>o", ReadWriteAccessDetector.Access.Read);
  }

  public void testIncDec() {
    doTest("fo<caret>o++", ReadWriteAccessDetector.Access.Write);
  }

  public void testPlusAssign() {
    doTest("fo<caret>o += 1", ReadWriteAccessDetector.Access.ReadWrite);
  }

  public void testParenthesisExpression() {
    doTest("(fo<caret>o) = 2", ReadWriteAccessDetector.Access.Write);
  }

  public void testPointer() {
    doTest("(*fo<caret>o) = 2", ReadWriteAccessDetector.Access.Write);
  }

  public void testLeftPartOfSelectorExpressionInAssignment() {
    doTest("foo.b<caret>ar.baz = 1", ReadWriteAccessDetector.Access.Read);
  }

  public void testLeftPartOfSelectorExpressionInAssignment_1() {
    doTest("f<caret>oo.bar.baz = 1", ReadWriteAccessDetector.Access.Read);
  }

  public void testRightPartOfSelectorExpressionInAssignment() {
    doTest("foo.bar.ba<caret>z = 1", ReadWriteAccessDetector.Access.Write);
  }
  
  public void testRangeLeftExpression() {
    doTest("for fo<caret>o = range bar {\n}", ReadWriteAccessDetector.Access.Write);
  }
  
  public void testRangeRightExpression() {
    doTest("for foo = range ba<caret>r {\n}", ReadWriteAccessDetector.Access.Read);
  }
  
  public void testRangeRightExpression_1() {
    doTest("for foo := range ba<caret>r {\n}", ReadWriteAccessDetector.Access.Read);
  }
  
  public void testSendRead() {
    doTest("a := <- cha<caret>nnel", ReadWriteAccessDetector.Access.Read);
  }
  
  public void testSendWrite() {
    doTest("chan<caret>nel <- a", ReadWriteAccessDetector.Access.Write);
  }
  
  public void testCallExpression() {
    doTest("fmt.Print<caret>ln(a)", ReadWriteAccessDetector.Access.Read);
  }

  private void doTest(@NotNull String expressionText, @NotNull ReadWriteAccessDetector.Access expectedAccess) {
    myFixture.configureByText("a.go", "package main; func _() {\n" + expressionText + "\n}");
    PsiElement element = myFixture.getFile().findElementAt(myFixture.getCaretOffset());
    GoReferenceExpression expression = PsiTreeUtil.getNonStrictParentOfType(element, GoReferenceExpression.class);
    assertNotNull(expression);
    assertEquals(expectedAccess, expression.getReadWriteAccess());
  }
}
