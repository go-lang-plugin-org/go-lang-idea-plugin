/*
 * Copyright 2013-2015 Sergey Ignatov, Alexander Zolotov, Mihai Toader, Florin Patan
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

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.refactoring.RefactoringBundle;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;
import org.jetbrains.annotations.NotNull;

public class GoIntroduceVariableTest extends LightPlatformCodeInsightFixtureTestCase {
  private static class IntroduceTest extends GoIntroduceVariableBase {
    static void invoke(@NotNull Project project, @NotNull Editor editor, @NotNull PsiFile file, boolean replaceAll) {
      performAction(new GoIntroduceOperation(project, editor, file, replaceAll));
    }
  }

  @Override
  protected String getTestDataPath() {
    return "testData/refactor/introduce-variable";
  }

  private void doTest() {
    doTest(true);
  }

  private void doTest(boolean replaceAll) {
    String testName = getTestName(true);
    myFixture.configureByFile(testName + ".go");
    IntroduceTest.invoke(myFixture.getProject(), myFixture.getEditor(), myFixture.getFile(), replaceAll);
    myFixture.checkResultByFile(testName + "-after.go");
  }

  private void doFailureTest(String msg) {
    try {
      doTest();
    }
    catch (RuntimeException e) {
      assertEquals("Cannot perform refactoring.\n" + msg, e.getMessage());
    }
  }

  public void testCaretAfterRightParenthesis()      { doTest(); }
  public void testCaretOnRightParenthesis()         { doTest(); }
  public void testCaretOnCallParenthesis()          { doTest(); }
  public void testNameSuggestOnGetterFunction()     { doTest(); }
  public void testNameSuggestOnDefinedImportAlias() { doTest(); }
  public void testNameSuggestOnDefaultName()        { doTest(); }
  public void testNameSuggestOnParamName()          { doTest(); }

  public void testMultipleValueResult() { doFailureTest("Expression fmt.Println() returns multiple values."); }
  public void testWrongSelection()      { doFailureTest(RefactoringBundle.message("selected.block.should.represent.an.expression")); }
  public void testTopLevelExpression()  { doFailureTest(RefactoringBundle.message("refactoring.introduce.context.error"));}

  public void testReplaceAll()  { doTest(true); }
  public void testReplaceOnly() { doTest(false);}
}
