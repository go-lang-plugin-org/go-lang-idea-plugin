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

package com.goide;

import com.goide.psi.GoStatement;
import com.goide.sdk.GoSdkService;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFileFilter;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.PsiManagerImpl;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.exceptionCases.AssertionErrorCase;
import com.intellij.usageView.UsageInfo;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

import static com.intellij.util.containers.ContainerUtil.newArrayList;

@SdkAware
public class GoFindUsageTest extends GoCodeInsightFixtureTestCase {
  private static final String USAGE = "/*usage*/";

  private void doTest(@NotNull String text) {
    List<Integer> offsets = allOccurrences(StringUtil.replace(text, "<caret>", ""), USAGE);
    String replace = StringUtil.replace(text, USAGE, "");
    myFixture.configureByText("a.go", replace);
    PsiElement atCaret = myFixture.getElementAtCaret();
    List<Integer> actual = ContainerUtil.map(myFixture.findUsages(atCaret), UsageInfo::getNavigationOffset);
    assertSameElements(actual, offsets);
  }

  @NotNull
  private static List<Integer> allOccurrences(@NotNull String text, @NotNull String what) {
    List<Integer> list = newArrayList();
    int index = text.indexOf(what);
    while (index >= 0) {
      list.add(index - list.size() * what.length());
      index = text.indexOf(what, index + 1);
    }
    return list;
  }

  public void testBuiltinHighlighting() {
    myFixture.configureByText("a.go", "package main; func bar() i<caret>nt {}");
    assertSize(1, myFixture.findUsages(myFixture.getElementAtCaret()));
  }

  // #2301
  public void testCheckImportInWholePackage() {
    myFixture.addFileToProject("bar/bar1.go", "package bar; func Bar() { b := bar{}; b.f.Method() }");
    myFixture.addFileToProject("bar/bar.go", "package bar; import \"foo\"; type bar struct { f *foo.Foo }");
    PsiFile file = myFixture.addFileToProject("foo/foo.go", "package foo; type Foo struct{}; func (*Foo) M<caret>ethod() {}");
    myFixture.configureFromExistingVirtualFile(file.getVirtualFile());
    Collection<UsageInfo> usages = myFixture.findUsages(myFixture.getElementAtCaret());
    assertSize(1, usages);
    UsageInfo first = usages.iterator().next();
    PsiElement e = first.getElement();
    GoStatement statement = PsiTreeUtil.getParentOfType(e, GoStatement.class);
    assertNotNull(statement);
    assertEquals("b.f.Method()", statement.getText());
  }

  public void testCheckImportInWholePackageWithRelativeImports() {
    myFixture.addFileToProject("bar/bar1.go", "package bar; func Bar() { b := bar{}; b.f.Method() }");
    myFixture.addFileToProject("bar/bar.go", "package bar; import \"..\"; type bar struct { f *foo.Foo }");
    myFixture.configureByText("foo.go", "package foo; type Foo struct{}; func (*Foo) M<caret>ethod() {}");
    assertSize(1, myFixture.findUsages(myFixture.getElementAtCaret()));
  }

  public void testOverride() {
    doTest("package p\n" +
           "func test2() {\n" +
           "    y := 1\n" +
           "    {\n" +
           "        <caret>y, _ := 10, 1\n" +
           "        fmt.Println(/*usage*/y)\n" +
           "    }\n" +
           "}\n");
  }

  public void testOverride1() {
    doTest("package p\n" +
           "func test2() {\n" +
           "    <caret>y := 1\n" +
           "    /*usage*/y, _ := 10, 1\n" +
           "    /*usage*/y, x := 10, 1\n" +
           "    fmt.Println(/*usage*/y)\n" +
           "}\n");
  }

  public void testOverride2() {
    doTest("package p\n" +
           "func test2() {\n" +
           "    <caret>y := 1\n" +
           "    {\n" +
           "        y, _ := 10, 1\n" +
           "        fmt.Println(y)\n" +
           "    }\n" +
           "}\n");
  }

  public void testFunctionParam() {
    doTest("package p\n" +
           "func aaa(<caret>a int) {\n" +
           "    /*usage*/a\n" +
           "    var a int\n" +
           "    a := 1\n" +
           "}\n");
  }

  public void testDeclaredInForRange() {
    doTest("package main\n" +
           "const key = iota\n" +
           "func main() {\n" +
           "    key := 1\n" +
           "    for <caret>key, val := range m {\n" +
           "        y := /*usage*/key\n" +
           "    }\n" +
           "}");
  }

  public void testMethodWithTransitiveImport() {
    myFixture.addFileToProject("a.go", "package main; import `middle`; func main() { fmt.Println(middle.A.Method()) }");
    myFixture.addFileToProject("middle/middle.go", "package middle; import `declaration`; var A *declaration.D = nil");
    PsiFile file =
      myFixture.addFileToProject("declaration/declaration.go", "package declaration; type D struct {}; func (D) Met<caret>hod() {}");
    myFixture.configureFromExistingVirtualFile(file.getVirtualFile());
    Collection<UsageInfo> usages = myFixture.findUsages(myFixture.getElementAtCaret());
    assertEquals(1, usages.size());
    //noinspection ConstantConditions
    assertEquals("a.go", usages.iterator().next().getFile().getName());
  }

  public void testDoNoLoadUnreachableVendorDirectory() {
    myFixture.addFileToProject("a.go", "package a; import `foo/vendor/foo`; func _() { println(CONST_NAME) }");
    PsiFile declarationFile = myFixture.addFileToProject("foo/vendor/foo/foo.go", "package foo; const CON<caret>ST_NAME = 1;");
    myFixture.configureFromExistingVirtualFile(declarationFile.getVirtualFile());
    failOnFileLoading();
    myFixture.findUsages(myFixture.getElementAtCaret());
  }

  public void testDoNoLoadUnreachableInternalDirectory() {
    GoSdkService.setTestingSdkVersion("1.5", getTestRootDisposable());
    myFixture.addFileToProject("a.go", "package a; import `foo/internal/foo`; func _() { println(CONST_NAME) }");
    PsiFile declarationFile = myFixture.addFileToProject("foo/internal/foo/foo.go", "package foo; const CON<caret>ST_NAME = 1;");
    myFixture.configureFromExistingVirtualFile(declarationFile.getVirtualFile());
    failOnFileLoading();
    myFixture.findUsages(myFixture.getElementAtCaret());
  }

  public void testDoNoLoadNotImportedDirectory() {
    myFixture.addFileToProject("bar/bar.go", "package bar; func _() { println(CONST_NAME) }");
    PsiFile declarationFile = myFixture.addFileToProject("foo/foo.go", "package foo; const CON<caret>ST_NAME = 1;");
    myFixture.configureFromExistingVirtualFile(declarationFile.getVirtualFile());
    failOnFileLoading();
    myFixture.findUsages(myFixture.getElementAtCaret());
  }

  public void testDoNoLoadTestDirectoryFromTestFileWithDifferentPackage() {
    myFixture.addFileToProject("bar/bar_test.go", "package bar; import `foo`; func _() { println(CONST_NAME) }");
    PsiFile declarationFile = myFixture.addFileToProject("foo/foo_test.go", "package foo; const CON<caret>ST_NAME = 1;");
    myFixture.configureFromExistingVirtualFile(declarationFile.getVirtualFile());
    failOnFileLoading();
    myFixture.findUsages(myFixture.getElementAtCaret());
  }

  public void testDoNoLoadTestDirectoryFromProductionFile() {
    myFixture.addFileToProject("bar/bar.go", "package bar; import `foo`; func _() { println(CONST_NAME) }");
    PsiFile declarationFile = myFixture.addFileToProject("foo/foo_test.go", "package foo; const CON<caret>ST_NAME = 1;");
    myFixture.configureFromExistingVirtualFile(declarationFile.getVirtualFile());
    failOnFileLoading();
    myFixture.findUsages(myFixture.getElementAtCaret());
  }

  public void testLoadTestDirectoryFromTestFile() throws Throwable {
    myFixture.addFileToProject("foo/bar_test.go", "package foo_test; import `foo`; func _() { println(CONST_NAME) }");
    PsiFile declarationFile = myFixture.addFileToProject("foo/foo_test.go", "package foo; const CON<caret>ST_NAME = 1;");
    myFixture.configureFromExistingVirtualFile(declarationFile.getVirtualFile());
    failOnFileLoading();
    assertException(new AssertionErrorCase() {
      @Override
      public void tryClosure() {
        try {
          myFixture.findUsages(myFixture.getElementAtCaret());
        }
        catch (AssertionError e) {
          String message = e.getMessage();
          assertTrue(message.contains("Access to tree elements not allowed in tests"));
          assertTrue(message.contains("bar_test.go"));
          throw e;
        }
      }
    });
  }

  public void testLoadSdkUsagesForSdkDeclarations() throws Throwable {
    myFixture.configureByText("bar.go", "package foo; import `io`; type ReaderWrapper interface{io.Rea<caret>der}");
    failOnFileLoading();
    assertException(new AssertionErrorCase() {
      @Override
      public void tryClosure() {
        try {
          assertSize(5, myFixture.findUsages(myFixture.getElementAtCaret()));
        }
        catch (AssertionError e) {
          String message = e.getMessage();
          assertTrue(message.contains("Access to tree elements not allowed in tests"));
          assertTrue(message.contains("io.go"));
          throw e;
        }
      }
    });
  }

  public void testDoNotLoadSdkUsagesForProjectDeclarations() {
    myFixture.configureByText("foo.go", "package foo; func _() { Println() }");
    myFixture.configureByText("bar.go", "package foo; func Pri<caret>ntln() {}");
    failOnFileLoading();
    assertSize(1, myFixture.findUsages(myFixture.getElementAtCaret()));
  }

  public void testLoadImportedDirectory() throws Throwable {
    myFixture.addFileToProject("bar/bar.go", "package bar; import `foo`; func _() { println(CONST_NAME) }");
    PsiFile declarationFile = myFixture.addFileToProject("foo/foo.go", "package foo; const CON<caret>ST_NAME = 1;");
    myFixture.configureFromExistingVirtualFile(declarationFile.getVirtualFile());
    failOnFileLoading();
    assertException(new AssertionErrorCase() {
      @Override
      public void tryClosure() {
        try {
          assertSize(1, myFixture.findUsages(myFixture.getElementAtCaret()));
        }
        catch (AssertionError e) {
          String message = e.getMessage();
          assertTrue(message.contains("Access to tree elements not allowed in tests"));
          assertTrue(message.contains("bar.go"));
          throw e;
        }
      }
    });
  }

  public void testSdkTestDataDirectory() {
    myFixture.configureByText("a.go", "package a; import `doc/testdata`; func _() { println(pkg.ExportedConst<caret>ant) } ");
    PsiReference reference = myFixture.getFile().findReferenceAt(myFixture.getCaretOffset());
    PsiElement resolve = reference != null ? reference.resolve() : null;
    assertNotNull(resolve);
    assertEquals("pkg.go", resolve.getContainingFile().getName());
    assertEmpty(myFixture.findUsages(resolve));
  }

  private void failOnFileLoading() {
    ((PsiManagerImpl)myFixture.getPsiManager()).setAssertOnFileLoadingFilter(VirtualFileFilter.ALL, getTestRootDisposable());
  }

  private void doTestDoNotFind(@NotNull String text) {
    myFixture.configureByText("a.go", text);
    PsiElement atCaret = myFixture.getElementAtCaret();
    try {
      myFixture.findUsages(atCaret);
      fail("Shouldn't be performed");
    }
    catch (AssertionError e) {
      assertEquals("Cannot find handler for: IMPORT_SPEC", e.getMessage());
    }
  }

  public void testDot()              { doTestDoNotFind("package main; import <caret>. \"fmt\""); }

  public void testUnderscore()       { doTestDoNotFind("package main; import <caret>_ \"fmt\""); }

  public void testNearImportString() { doTestDoNotFind("package main; import <caret>\"fmt\""); }
}
