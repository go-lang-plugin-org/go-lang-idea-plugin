package com.goide.rename;

import com.goide.GoCodeInsightFixtureTestCase;

public class GoRenameTest extends GoCodeInsightFixtureTestCase {
  public void testAnonymousField() {
    doTest("package foo; type A struct {*A}; func foo(a A) {a.<caret>A}", "B",
           "package foo; type B struct {*B}; func foo(a B) {a.<caret>B}");
  }

  public void testType() {
    doTest("package foo; type A<caret> struct {*A}; func foo(a A) {a.A}", "B",
           "package foo; type B<caret> struct {*B}; func foo(a B) {a.B}");
  }
  
  public void testLabel() {
    doTest("package foo; func foo() {a:{}; goto <caret>a}", "b",
           "package foo; func foo() {b:{}; goto <caret>b}");
  }

  private void doTest(String before, String newName, String after) {
    myFixture.configureByText("foo.go", before);
    myFixture.renameElementAtCaret(newName);
    myFixture.checkResult(after);
  }
}
