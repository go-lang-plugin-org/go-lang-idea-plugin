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

import com.goide.quickfix.GoQuickFixTestBase;
import org.jetbrains.annotations.NotNull;

public class GoRedundantBlankArgInRangeInspectionTest extends GoQuickFixTestBase {

  @Override
  public void setUp() throws Exception {
    super.setUp();
    myFixture.enableInspections(GoRedundantBlankArgInRangeInspection.class);
  }

  // Tests ranges
  public void testRangeSimplifyWithOneBlankArg() {
    doTest(GoRedundantBlankArgInRangeInspection.DELETE_BLANK_ARGUMENT_QUICK_FIX_NAME, true);
  }

  public void testRangeSimplifyWithOneArg() {
    doTestNoFix(GoRedundantBlankArgInRangeInspection.DELETE_BLANK_ARGUMENT_QUICK_FIX_NAME, true);
  }

  public void testRangeSimplifyWithTwoBlankArgs() {
    doTest(GoRedundantBlankArgInRangeInspection.DELETE_BLANK_ARGUMENT_QUICK_FIX_NAME, true);
  }

  public void testRangeSimplifyWithTwoArgs() {
    doTestNoFix(GoRedundantBlankArgInRangeInspection.DELETE_BLANK_ARGUMENT_QUICK_FIX_NAME, true);
  }

  public void testRangeSimplifyWithOneBlankAndOneNotBlankArgs() {
    doTestNoFix(GoRedundantBlankArgInRangeInspection.DELETE_BLANK_ARGUMENT_QUICK_FIX_NAME, true);
  }

  public void testRangeSimplifyWithOneNotBlankAndOneBlankArgs() {
    doTest(GoRedundantBlankArgInRangeInspection.DELETE_BLANK_ARGUMENT_QUICK_FIX_NAME, true);
  }

  public void testRangeSimplifyWithVarAssign() {
    doTest(GoRedundantBlankArgInRangeInspection.DELETE_BLANK_ARGUMENT_QUICK_FIX_NAME, true);
  }

  @NotNull
  @Override
  protected String getBasePath() {
    return "inspections/go-simplify";
  }
}
