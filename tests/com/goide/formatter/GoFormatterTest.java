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

package com.goide.formatter;

import com.goide.GoCodeInsightFixtureTestCase;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.codeStyle.CodeStyleManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GoFormatterTest extends GoCodeInsightFixtureTestCase {
  @NotNull
  @Override
  protected String getBasePath() {
    return "formatting";
  }

  public void testSimple()                          { doTest(); }
  public void testCaseE()                           { doTest('e'); }
  public void testCaseEnter()                       { doTestEnter(); }
  public void testCase2Enter()                      { doTestEnter(); }
  public void testSwitchEnter()                     { doTestEnter(); }
  public void testTypeEnter()                       { doTestEnter(); }
  public void testSpacesInArithmeticExpressions()   { doTest(); }
  public void testConstDeclaration()                { doTest(); }
  public void testVarDeclaration()                  { doTest(); }
  public void testBreakLines()                      { doTest(); }
  public void testCommentIndentation()              { doTest(); }
  public void testElseStatement()                   { doTest(); }
  public void testEmptyStatementInForClause()       { doTest(); }
  public void testExpressionsContinuationIndent()   { doTest(); }

  private void doTest() { doTest(null); }

  private void doTestEnter() { doTest('\n'); }

  private void doTest(@Nullable Character c) {
    String testName = getTestName(true);
    myFixture.configureByFile(testName + ".go");
    String after = doTest(c, testName);
    myFixture.checkResultByFile(after);
  }

  private String doTest(@Nullable Character c, String testName) {
    if (c == null) {
      WriteCommandAction.runWriteCommandAction(myFixture.getProject(), () -> {
        CodeStyleManager.getInstance(getProject()).reformat(myFixture.getFile());
      });
    }
    else {
      myFixture.type(c);
    }
    return String.format("%s-after.go", testName);
  }
}
