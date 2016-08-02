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

package com.goide.inspections;

import com.goide.SdkAware;
import com.goide.quickfix.GoQuickFixTestBase;
import org.jetbrains.annotations.NotNull;

@SdkAware
public class GoTestSignaturesInspectionTest extends GoQuickFixTestBase {
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    myFixture.enableInspections(GoTestSignaturesInspection.class);
  }

  @NotNull
  @Override
  protected String getBasePath() {
    return "inspections/test-signatures";
  }

  private void doTest() {
    String testName = getTestName(true);
    myFixture.configureByFile(testName + "_test.go");
    myFixture.checkHighlighting();
    applySingleQuickFix("Fix signature");
    myFixture.checkResultByFile(testName + "_test-after.go");
  }

  public void testExampleNonEmptySignature()  { doTest(); }
  public void testTestNoTestingImport()       { doTest(); }
  public void testTestWrongTestingAlias()     { doTest(); }
  public void testTestLocalTestingImport()    { doTest(); }
  public void testTestMain()                  { doTest(); }
  public void testBenchmark()                 { doTest(); }
  public void testExampleWithReturnValue()    { doTest(); }
  public void testTestWithReturnValue()       { doTest(); }
}
