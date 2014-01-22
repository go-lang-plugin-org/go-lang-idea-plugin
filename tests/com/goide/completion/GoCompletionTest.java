package com.goide.completion;

import com.goide.GoCodeInsightFixtureTestCase;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.testFramework.TreePrintCondition;
import com.intellij.testFramework.UsefulTestCase;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    doTestInclude("package foo; func main(i, j int){<caret>}", "i", "j");
  }

  public void testConsts() {
    doTestInclude("package foo; const i, j; func main(){<caret>}", "i", "j");
  }

  public void testNoCompletionInsideStrings() {
    doTestEquals("package foo; func main(){\"<caret>\"}");
  }

  public void testStructTypes() throws Exception {
    doTestEquals("package foo; type AA struct {N AA}; func foo(a AA) {a.<caret>}", "N");
  }

  public void testStructTypes2() throws Exception {
    doTestEquals("package foo; type AA struct {N AA}; func foo(a *AA) {a.<caret>}", "N");
  }

  public void testImports() throws Exception {
    doCheckResult("package foo; import imp \"\"; func foo(a im<caret>) {}", "package foo; import imp \"\"; func foo(a imp.) {}");
  }

  public void testKeywords() {
    myFixture.testCompletionVariants(getTestName(true) + ".go", "const", "continue");
  }

  public void testMethods() throws Exception {
    doTestEquals("package foo; type T interface{}; func (t T) f() {t.<caret>}", "f");
  }

  public void testPreventStackOverflow() throws Exception {
    doTestEquals("package foo; type E struct {*E}; func (e E) foo() {}; func main() {e := E{}; e.<caret>}", "foo", "E");
  }

  public void testNestedStructures() throws Exception {
    doTestEquals("package foo; type E struct {}; type B struct {E}; func (e E) foo() {}; func (b B) bar(){}" +
                 "func main() {b := B{}; b.<caret>}", "foo", "bar", "E");
  }

  public void testNestedStructures2() throws Exception {
    doTestEquals("package foo; type E struct {}; type B struct {E}; func (e E) foo() {}; " +
                 "func main() {b := B{}; b.E.<caret>}", "foo");
  }

  public void testNestedStructures3() throws Exception {
    doTestEquals("package foo; type E struct {}; type B struct {E}; func (e E) foo() {}; " +
                 "func main() {B{}.E.<caret>}", "foo");
  }

  public void testInterfaceTypes() throws Exception {
    doTestInclude("package foo; type E interface {}; type B interface {<caret>}", "E");
  }

  public void testInterfaceTypesNoStruct() throws Exception {
    doTestExclude("package foo; type E struct {}; type B interface {<caret>}", "E");
  }

  public void testNoDuplicates() throws Exception {
    doTestInclude("package foo; type a struct {<caret>", "a");
    List<String> stringList = myFixture.getLookupElementStrings();
    assertNotNull(stringList);
    assertSize(1, ContainerUtil.filter(stringList, new TreePrintCondition.Include("a")));
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

  protected void doTestEquals(String txt, String... variants) {
    doTestVariants(txt, CompletionType.BASIC, 1, CheckType.EQUALS, variants);
  }

  protected void doCheckResult(@NotNull String before, @NotNull String after) { doCheckResult(before, after, null); }

  protected void doCheckResult(@NotNull String before, @NotNull String after, @Nullable Character c) {
    myFixture.configureByText("a.go", before);
    myFixture.completeBasic();
    if (c != null) myFixture.type(c);
    myFixture.checkResult(after);
  }
}
