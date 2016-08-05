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

package com.goide.util;

import com.goide.GoParametrizedTestBase;
import com.goide.SdkAware;
import com.goide.psi.GoCallExpr;
import com.goide.psi.GoExpression;
import com.goide.psi.impl.GoExpressionUtil;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@RunWith(Parameterized.class)
@SdkAware
public class GoExpressionUtilTest extends GoParametrizedTestBase {
  private final String vars;
  private final String left;
  private final String right;
  private final boolean ok;


  @Parameterized.Parameters(name = "{1} == {2}; {0}")
  public static Collection<Object[]> data() {
    return Arrays.asList(new Object[][]{
      {"var a = 1", "a", "a", true},
      //{"", "1.00", "1.0", false},
      {"var a, b = 1, 2", "a + b", "a    + b", true},
      {"var a, b = 1, 2", "a + b", "(a    + b)", true},
      {"var a, b = 1, 2", "+a", "a", true},
      {"var a, b = 1, 2", "a + b", "(+((+(a    + b))))", true},
      {"var a, b []int", "len(a)", "len(a)", false},
      {"var a, b = 1, 2", "a + b", "a - b", false},
      {"var a, b = 1, 2", "a + b", "a >> b", false},
      {"var a, b = 1, 2", "a + b", " -(a + b)", false},
      {"var a, b = 1, 2", "1", " 23", false},
      {"var a, b = 1, 2", "1", " 1", true},
      {"var a, b = 1, 2", "105 - 3", " 105 - (+3)", true},
      {"var a, b = 1, 2", "\"hello\"", " `hello`", true},
      {"var a, b []int", "a[1]", "a[2]", false},
      {"var a, b []int", "a[1 : 2]", "a[1 : 2]", true},
      {"type T struct{name string}", "&T{name : \"name\"}", "&T{name : \"name\"}", true},
      {"type T struct{name string}", "&T{name : \"name\"}", "&T{name : \"\"}", false},
      {"func f() int {return 0}", "f()", "f()", false},
      {"var i interface{}; type T int", "i.(T)", "i.(T)", true},
      {"var i interface{}; type T int", "(a).(T)", "i.(T)", false},
    });
  }

  @SuppressWarnings("JUnitTestCaseWithNonTrivialConstructors")
  public GoExpressionUtilTest(@NotNull String vars, @NotNull String left, @NotNull String right, boolean ok) {
    this.vars = vars;
    this.left = left;
    this.right = right;
    this.ok = ok;
  }

  @Override
  protected void doTest() {
    PsiFile file = myFixture.configureByText("a.go", "package main\n func foo(i interface{}, j interface{}){}\n" + vars +
                                                     "\n func _(){\n fo<caret>o(" + left + ", " + right + ")\n}");
    myFixture.checkHighlighting();
    GoCallExpr call = PsiTreeUtil.getParentOfType(file.findElementAt(myFixture.getCaretOffset()), GoCallExpr.class);
    assert call != null;
    List<GoExpression> expressions = call.getArgumentList().getExpressionList();
    assertTrue(left + " should " + (ok ? "" : "not ") + "be identical " + right,
               ok == GoExpressionUtil.identical(expressions.get(0), expressions.get(1)));
  }
}
