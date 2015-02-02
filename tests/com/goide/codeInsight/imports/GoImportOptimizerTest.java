/*
 * Copyright 2013-2014 Sergey Ignatov, Alexander Zolotov
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

package com.goide.codeInsight.imports;

import com.goide.GoCodeInsightFixtureTestCase;
import com.intellij.codeInsight.actions.OptimizeImportsAction;
import com.intellij.ide.DataManager;
import com.intellij.testFramework.LightProjectDescriptor;

public class GoImportOptimizerTest extends GoCodeInsightFixtureTestCase {

  public void testUnusedImports() { doTest(); }
  public void testUnusedImportsWithSemicolon() { doTest(); }
  public void testUnusedImplicitImports() { doTest(); }
  public void testUsedImplicitImports() { doTest(); }
  public void testUsedDuplicatedImports() { doTest(); } 
  public void testDuplicatedImportsWithSameString() { doTest(); } 
  public void testDuplicatedImportsWithDifferentString() { doTest(); } 
  public void testUnusedDuplicatedImports() { doTest(); }
  public void testImportWithSameIdentifier() { doTest(); }
  public void testImportPackageWithMainFiles() { doTest(); }
  public void testImportDirectoryWithoutPackages() {
    doTest(); 
  }
  public void testUnusedImportsWithBacktick() {
    doTest(); 
  }

  @Override
  protected boolean isWriteActionRequired() {
    return false;
  }

  private void doTest() {
    myFixture.configureByFile(getTestName(true) + ".go");
    myFixture.doHighlighting();
    OptimizeImportsAction.actionPerformedImpl(DataManager.getInstance().getDataContext(myFixture.getEditor().getContentComponent()));
    myFixture.checkResultByFile(getTestName(true) + "_after.go");
  }
  
  @Override
  public void setUp() throws Exception {
    super.setUp();
    setUpProjectSdk();
  }

  @Override
  protected LightProjectDescriptor getProjectDescriptor() {
      return createMockProjectDescriptor();
    }
  

  @Override
  protected String getBasePath() {
    return "imports/optimize";
  }
}
