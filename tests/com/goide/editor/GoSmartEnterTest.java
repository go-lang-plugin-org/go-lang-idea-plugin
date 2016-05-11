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

package com.goide.editor;

import com.goide.GoCodeInsightFixtureTestCase;
import com.intellij.openapi.actionSystem.IdeActions;

public class GoSmartEnterTest extends GoCodeInsightFixtureTestCase {
  public void testGoStatement()                          { doTest(); }
  public void testDeferStatement()                       { doTest(); }
  public void testGoStatementOnNonFunctionType()         { doTest(); }
  public void testDeferStatementOnNonFunctionType()      { doTest(); }
  public void testFunction()                             { doTest(); }
  public void testFunctionLiteral()                      { doTest(); }
  public void testFunctionWithBlock()                    { doTest(); }
  public void testFunctionLiteralWithBlock()             { doTest(); }
  public void testIf()                                   { doTest(); }
  public void testIfWithBlock()                          { doTest(); }
  public void testFor()                                  { doTest(); }
  public void testForWithBlock()                         { doTest(); }

  private void doTest() {
    myFixture.configureByFile(getTestName(true) + ".go");
    myFixture.performEditorAction(IdeActions.ACTION_EDITOR_COMPLETE_STATEMENT);
    myFixture.checkResultByFile(getTestName(true) + "-after.go");
  }

  @Override
  protected String getBasePath() {
    return "smartEnter";
  }
}
