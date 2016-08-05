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

package com.goide.quickfix;

import com.goide.GoParametrizedTestBase;
import com.goide.SdkAware;
import com.goide.inspections.GoBoolExpressionsInspection;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
@SdkAware
public class GoBoolExpressionsInspectionTest extends GoParametrizedTestBase {
  private final String expr;
  private final String vars;
  private final String after;

  @Parameterized.Parameters(name = "{1}")
  public static Collection<Object[]> data() {
    return Arrays.asList(new Object[][]{
      {"var b bool", "b || true", "true"},
      {"var b bool", "b || false", "b"},
      {"var b bool", "b && true", "b"},
      {"var b bool", "b && false", "false"},
      {"var b bool", "b && b", "b"},
      {"var b bool", "b || b", "b"},
      {"var b,c,d bool", "b || c && b", "b || c && b"},
      {"var b,c,d bool", "b || c || !b", "true"},
      {"var b,c,d bool", "!b || !(!b)", "true"},
      {"var b,c,d bool", "!b || d || c", "!b || d || c"},
      {"var b,c,d bool", "!b && b", "false"},
      {"var b,c,d bool", "(b && c) || (c && b)", "c && b"},
      {"var b,c,d bool", "(b == c || c == b) || (b == c)", "b == c"},

      {"var c1, c2 = 1, 2; var a, b, c int", "b == c1 || b == c2", "b == c1 || b == c2"},
      {"var c1, c2 = 1, 2; var a, b, c int", "b != c1 && b != c2", "b != c1 && b != c2"},
    });
  }

  @Before
  public void enableInspections() {
    myFixture.enableInspections(GoBoolExpressionsInspection.class);
  }

  public GoBoolExpressionsInspectionTest(String vars, String expr, String after) {
    this.expr = expr;
    this.vars = vars;
    this.after = after;
  }

  @Override
  public void doTest() {
    myFixture.configureByText("a.go", "package main\n func main(){\n" + vars + "\nvar a = " + expr + "<caret>" + "\n}");
    if (!expr.equals(after)) {
      applySingleQuickFix(GoSimplifyBoolExprQuickFix.QUICK_FIX_NAME);
      myFixture.checkResult("package main\n func main(){\n" + vars + "\nvar a = " + after + "<caret>" + "\n}");
    }
    else {
      assertEmpty(myFixture.filterAvailableIntentions(GoSimplifyBoolExprQuickFix.QUICK_FIX_NAME));
    }
  }
}




