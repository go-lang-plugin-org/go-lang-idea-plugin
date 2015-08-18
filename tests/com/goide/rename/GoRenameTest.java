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

package com.goide.rename;

import com.goide.GoCodeInsightFixtureTestCase;
import com.intellij.refactoring.util.CommonRefactoringUtil;

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

  public void testAliasQualifier() {
    doTest("package foo; import a \"fmt\"; func c() { a<caret>.Println() }", "b",
           "package foo; import b \"fmt\"; func c() { b<caret>.Println() }");
  }

  public void testImportAlias() {
    doTest("package foo; import <caret>a \"fmt\"; func foo() { a.Println() }", "b",
           "package foo; import <caret>b \"fmt\"; func foo() { b.Println() }");
  }

  public void testDotImportAlias() {
    myFixture.configureByText("foo.go", "package foo; import <caret>. \"fmt\"");
    try {
      myFixture.renameElementAtCaret("bar");
      fail("Shouldn't be performed");
    }
    catch (CommonRefactoringUtil.RefactoringErrorHintException e) {
      assertEquals("This element cannot be renamed", e.getMessage());
    }
  }

  private void doTestDoNotRename(String text) {
    myFixture.configureByText("foo.go", text);
    myFixture.renameElementAtCaret("bar");
    myFixture.checkResult(text);
  }

  public void testNullAlias() {
    doTestDoNotRename("package foo; import <caret>\"fmt\"");
  }

  public void testPackageQualifier() {
    doTestDoNotRename("package foo; import \"fmt\" func foo() { <caret>fmt.Println() }");
  }

  private void doTest(String before, String newName, String after) {
    myFixture.configureByText("foo.go", before);
    myFixture.renameElementAtCaret(newName);
    myFixture.checkResult(after);
  }
}
