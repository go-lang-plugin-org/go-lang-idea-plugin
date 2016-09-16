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

package com.goide.refactor;

import com.goide.GoCodeInsightFixtureTestCase;
import com.intellij.refactoring.RefactoringBundle;
import com.intellij.refactoring.util.CommonRefactoringUtil;
import org.jetbrains.annotations.NotNull;

public class GoIntroduceVariableTest extends GoCodeInsightFixtureTestCase {
  @NotNull
  @Override
  protected String getBasePath() {
    return "refactor/introduce-variable";
  }

  private void doTest() {
    doTest(true);
  }

  private void doTest(boolean replaceAll) {
    String testName = getTestName(true);
    myFixture.configureByFile(testName + ".go");
    GoIntroduceVariableBase.performAction(new GoIntroduceOperation(getProject(), myFixture.getEditor(), myFixture.getFile(), replaceAll));
    myFixture.checkResultByFile(testName + "-after.go");
  }

  private void doFailureTest(String msg) {
    try {
      doTest();
      fail("Shouldn't be performed");
    }
    catch (CommonRefactoringUtil.RefactoringErrorHintException e) {
      assertEquals("Cannot perform refactoring.\n" + msg, e.getMessage());
    }
  }

  public void testCaretAfterRightParenthesis()                      { doTest(); }
  public void testCaretOnRightParenthesis()                         { doTest(); }
  public void testCaretOnCallParenthesis()                          { doTest(); }
  public void testNameSuggestOnGetterFunction()                     { doTest(); }
  public void testNameSuggestOnDefaultName()                        { doTest(); }
  public void testNameSuggestOnParamName()                          { doTest(); }
  public void testNameSuggestOnType()                               { doTest(); }
  public void testNameSuggestOnArrayType()                          { doTest(); }
  public void testDoNotSuggestKeywordBasedOnType()                  { doTest(); }
  public void testDoNotSuggestKeywordBasedOnCallName()              { doTest(); }
  public void testDoNotSuggestNameEqualsToType()                    { doTest(); }
  public void testExtractFunctionLiteral()                          { doTest(); }
  
  public void testExtractSingleExpressionStatement_1()              { doTest(); }
  public void testExtractSingleExpressionStatement_2()              { doTest(); }
  public void testExtractSingleExpressionStatement_3()              { doTest(); }

  public void testCompositeLiteral()                                { doTest(); }
  public void testIndexedExpression()                               { doTest(); }
  public void testConversion()                                      { doTest(); }

  public void testVoidExpression()      { doFailureTest("Expression fmt.Println() returns multiple values."); }
  public void testVoidCallExpression()  { doFailureTest("Expression fmt.Println() returns multiple values."); }
  public void testWrongSelection()      { doFailureTest(RefactoringBundle.message("selected.block.should.represent.an.expression")); }
  public void testTopLevelExpression()  { doFailureTest(RefactoringBundle.message("refactoring.introduce.context.error"));}

  public void testReplaceAll()          { doTest(true); }
  public void testReplaceOnly()         { doTest(false);}

  public void testTwoOccurrences()      { doTest(true);}
}
