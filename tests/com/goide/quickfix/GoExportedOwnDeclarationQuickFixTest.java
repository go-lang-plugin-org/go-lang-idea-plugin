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

package com.goide.quickfix;

import com.goide.inspections.GoExportedOwnDeclarationInspection;
import org.jetbrains.annotations.NotNull;

public class GoExportedOwnDeclarationQuickFixTest extends GoQuickFixTestBase {
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    myFixture.enableInspections(GoExportedOwnDeclarationInspection.class);
  }

  @NotNull
  @Override
  protected String getBasePath() {
    return "quickfixes/exported-own-declaration";
  }

  public void testSingleLineMultipleExportedAndPrivateConsts() { doTest(GoExportedOwnDeclarationInspection.QUICK_FIX_NAME); }
  public void testSingleLineMultipleConsts()                   { doTest(GoExportedOwnDeclarationInspection.QUICK_FIX_NAME); }
  public void testSingleLineMultipleVars()                     { doTest(GoExportedOwnDeclarationInspection.QUICK_FIX_NAME); }
  public void testMultiLineMultipleConstsWithType()            { doTest(GoExportedOwnDeclarationInspection.QUICK_FIX_NAME); }
  public void testMultiLineMultipleConstsWithoutType()         { doTest(GoExportedOwnDeclarationInspection.QUICK_FIX_NAME); }
  public void testMultiLineMultipleVarsOnlyType()              { doTest(GoExportedOwnDeclarationInspection.QUICK_FIX_NAME); }
  public void testMultiLineMultipleVarsWithTypeAndValues()     { doTest(GoExportedOwnDeclarationInspection.QUICK_FIX_NAME); }
  public void testMultiLineMultipleVarsOnlyValues()            { doTest(GoExportedOwnDeclarationInspection.QUICK_FIX_NAME); }
}
