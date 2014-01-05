package com.goide.completion;

import com.goide.GoCodeInsightFixtureTestCase;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.testFramework.UsefulTestCase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class GoCompletionTest extends GoCodeInsightFixtureTestCase {
  public void testLocalFunction() {
    doTestInclude("package foo; func foo() {}; func main() {<caret>}", "foo", "main");
  }

  public void testLocalType() {
    doTestInclude("package foo; type (T struct {}; T2 struct{}); func main(){var i <caret>}", "T", "T2");
  }

  public void testLocalVar() {
    doTestInclude("package foo; func main(){var i, j int; <caret>}", "i", "j");
  }

  public void testPackageLocalVar() {
    doTestInclude("package foo; var i, j int; func main(){<caret>}", "i", "j");
  }

  public void testLocalVarExclude() {
    doTestExclude("package foo; func main(){{var i, j int;}; <caret>}", "i", "j");
  }

  public void testParams() {
    doTestInclude("package foo; func foo(p1, p2 int){<caret>}", "p1", "p2");
  }

  public void testKeywords() {
    myFixture.testCompletionVariants(getTestName(true) + ".go", "const", "continue");
  }

  @Override
  protected String getBasePath() {
    return "completion";
  }

  protected enum CheckType {EQUALS, INCLUDES, EXCLUDES}

  protected void doTestVariantsInner(CompletionType type, int count, CheckType checkType, String... variants) {
    myFixture.complete(type, count);
    List<String> stringList = myFixture.getLookupElementStrings();

    assertNotNull(
      "\nPossibly the single variant has been completed.\n" +
      "File after:\n" +
      myFixture.getFile().getText(),
      stringList);
    Collection<String> varList = new ArrayList<String>(Arrays.asList(variants));
    if (checkType == CheckType.EQUALS) {
      UsefulTestCase.assertSameElements(stringList, variants);
    }
    else if (checkType == CheckType.INCLUDES) {
      varList.removeAll(stringList);
      assertTrue("Missing variants: " + varList, varList.isEmpty());
    }
    else if (checkType == CheckType.EXCLUDES) {
      varList.retainAll(stringList);
      assertTrue("Unexpected variants: " + varList, varList.isEmpty());
    }
  }

  protected void doTestVariants(String txt, CompletionType type, int count, CheckType checkType, String... variants) {
    myFixture.configureByText("a.go", txt);
    doTestVariantsInner(type, count, checkType, variants);
  }

  protected void doTestInclude(String txt, String... variants) {
    doTestVariants(txt, CompletionType.BASIC, 1, CheckType.INCLUDES, variants);
  }

  protected void doTestExclude(String txt, String... variants) {
    doTestVariants(txt, CompletionType.BASIC, 1, CheckType.EXCLUDES, variants);
  }
}
