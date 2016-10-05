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

import com.goide.SdkAware;
import com.goide.inspections.GoStringAndByteTypeMismatchInspection;
import org.jetbrains.annotations.NotNull;

@SdkAware
public class GoConvertStringToByteQuickFixTest extends GoQuickFixTestBase {
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    myFixture.enableInspections(GoStringAndByteTypeMismatchInspection.class);
  }

  @NotNull
  @Override
  protected String getBasePath() {
    return "quickfixes/string-index-is-byte";
  }

  private void doTest()                               { doTest(GoConvertStringToByteQuickFix.NAME); }
  private void doTestNoFix(boolean checkHighlighting) { doTestNoFix(GoConvertStringToByteQuickFix.NAME, checkHighlighting); }

  public void testEqualsCondition()                   { doTest(); }
  public void testNotEqualsCondition()                { doTest(); }
  public void testGreaterCondition()                  { doTest(); }
  public void testGreaterOrEqualsCondition()          { doTest(); }
  public void testLessCondition()                     { doTest(); }
  public void testLessOrEqualsCondition()             { doTest(); }
  public void testReverse()                           { doTest(); }
  public void testLiterals()                          { doTest(); }
  public void testLongLiteral()                       { doTest(); }
  public void testSliceFromLeft()                     { doTestNoFix(false); }
  public void testSliceFromRight()                    { doTestNoFix(false); }
  public void testSliceUnbound()                      { doTestNoFix(false); }
  public void testMoreThanOneCharInString()           { doTestNoFix(true); }
}