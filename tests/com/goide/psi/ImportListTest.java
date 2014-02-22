package com.goide.psi;

import com.goide.GoCodeInsightFixtureTestCase;

public class ImportListTest extends GoCodeInsightFixtureTestCase {
  public void testAddImportToEmptyList() {
    doAddImportTest();
  }

  public void testAddImportToListWithMultiSpecDeclaration() {
    doAddImportTest();
  }

  public void testAddImportToListWithSingleSpecDeclaration() {
    doAddImportTest();
  }

  public void testAddImportToListWithSingleSpecDeclarationWithParens() {
    doAddImportTest();
  }

  public void testAddImportToEmptyListBeforeFunction() {
    doAddImportTest();
  }

  private void doAddImportTest() {
    myFixture.configureByFile(getTestName(true) + ".go");
    //noinspection ConstantConditions
    ((GoFile)myFixture.getFile()).getImportList().addImport("package/path", null);
    myFixture.checkResultByFile(getTestName(true) + "_after.go");
  }

  @Override
  protected String getBasePath() {
    return "psi/importDeclaration";
  }
}
