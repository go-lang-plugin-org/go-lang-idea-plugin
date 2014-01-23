package com.goide.codeInsight.imports;

import com.goide.GoCodeInsightFixtureTestCase;
import com.intellij.codeInsight.actions.OptimizeImportsAction;
import com.intellij.ide.DataManager;

public class GoImportOptimizerTest extends GoCodeInsightFixtureTestCase {

  public void testUnusedImports() { doTest(); } 
  public void testUsedDuplicatedImports() { doTest(); } 
  public void testDuplicatedImportsWithSameString() { doTest(); } 
  public void testDuplicatedImportsWithDifferentString() { doTest(); } 
  public void testUnusedDuplicatedImports() { doTest(); } 
  public void _testUnresolvedImports() { doTest(); } //todo 
  
  private void doTest() {
    myFixture.configureByFile(getTestName(true) + ".go");
    OptimizeImportsAction.actionPerformedImpl(DataManager.getInstance().getDataContext(myFixture.getEditor().getContentComponent()));
    myFixture.checkResultByFile(getTestName(true) + "_after.go");
  }

  @Override
  protected String getBasePath() {
    return "imports/optimize";
  }
}
