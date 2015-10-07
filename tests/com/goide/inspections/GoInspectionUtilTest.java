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

import com.goide.GoCodeInsightFixtureTestCase;
import com.goide.psi.GoBuiltinCallExpr;
import com.goide.psi.GoCallExpr;
import com.goide.psi.GoExpression;
import com.goide.psi.GoStringLiteral;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

public class GoInspectionUtilTest extends GoCodeInsightFixtureTestCase {

  private <T extends GoExpression> void doTest(@NotNull String text, @NotNull Class<T> aClass, int expected) {
    myFixture.configureByText("a.go", text);
    PsiElement element = myFixture.getFile().findElementAt(myFixture.getEditor().getCaretModel().getOffset());
    T callExpr = PsiTreeUtil.getParentOfType(element, aClass);
    assertEquals(expected, GoInspectionUtil.getExpressionResultCount(callExpr));
  }

  public void testBuiltinCallResultCount() {
    String text = "package a; func b() {\n b := <caret>make(int, 10); _ = b }";
    doTest(text, GoBuiltinCallExpr.class, 1);
  }

  public void testMultipleResultCount() {
    String text = "package a; func vals() (int, int) {\n return 42, 0}; func b() {\n <caret>vals()}";
    doTest(text, GoCallExpr.class, 2);
  }

  public void testLiteralResultCount() {
    String text = "package a; func b() {\n a := \"hello <caret>world\"}";
    doTest(text, GoStringLiteral.class, 1);
  }
}
