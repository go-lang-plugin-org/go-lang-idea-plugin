package com.goide.psi;

import com.goide.GoCodeInsightFixtureTestCase;

public class GoImportListTest extends GoCodeInsightFixtureTestCase {
  public void testAddImportToEmptyList()                                { doAddImportTest(); }
  public void testAddImportToListWithMultiSpecDeclaration()             { doAddImportTest(); }
  public void testAddImportToListWithSingleSpecDeclaration()            { doAddImportTest(); }
  public void testAddImportToListWithSingleSpecDeclarationWithParens()  { doAddImportTest(); }
  public void testAddImportToEmptyListBeforeFunction()                  { doAddImportTest(); }
  public void testAddImportBeforeFunction()                             { doAddImportTest(); }
  
  private void doAddImportTest() {
    myFixture.configureByFile(getTestName(true) + ".go");
    ((GoFile)myFixture.getFile()).addImport("package/path", null);
    myFixture.checkResultByFile(getTestName(true) + "_after.go");
  }

  @Override
  protected String getBasePath() {
    return "psi/importDeclaration";
  }
}