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
import org.jetbrains.annotations.NotNull;

public class GoFoldingBuilderTest extends GoCodeInsightFixtureTestCase {
  @NotNull
  @Override
  protected String getBasePath() {
    return "folding";
  }

  private void doTest() { myFixture.testFolding(getTestDataPath() + "/" + getTestName(true) + ".go"); }

  public void testSimple()                                        { doTest(); }
  public void testImportList()                                    { doTest(); }
  public void testImportListWithJustSingleImportKeyword()         { doTest(); }
  public void testImportListWithoutSpaceBetweenKeywordAndString() { doTest(); }
  public void testImportListWithoutSpaceBetweenKeywordAndParen()  { doTest(); }
  public void testEmptyImportList()                               { doTest(); }
  public void testImportListWithNewLineAfterKeyword()             { doTest(); }
  public void testImportListWithOnlyThreeSymbolsToFold()          { doTest(); }
  public void testVarDeclaration()                                { doTest(); }
  public void testTypeDeclaration()                               { doTest(); }
  public void testCompositeLiteral()                              { doTest(); }
}
