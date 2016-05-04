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

package com.goide.editor;

import com.goide.GoCodeInsightFixtureTestCase;
import com.intellij.testFramework.fixtures.CodeInsightTestUtil;
import org.jetbrains.annotations.NotNull;

public class GoSelectWordTest extends GoCodeInsightFixtureTestCase {
  public void testImportString()            { doTest(); }
  public void testMultipleImportString()    { doTest(); }
  public void testStringLiteral()           { doTest(); }
  public void testBlock()                   { doTest(); }
  public void testArguments()               { doTest(); }
  public void testParameters()              { doTest(); }
  public void testResultParameters()        { doTest(); }
  public void testFunctionName()            { doTest(); }

  private void doTest() {
    CodeInsightTestUtil.doWordSelectionTestOnDirectory(myFixture, getTestName(true), "go");
  }

  @NotNull
  @Override
  protected String getBasePath() {
    return "selectWord";
  }
}
