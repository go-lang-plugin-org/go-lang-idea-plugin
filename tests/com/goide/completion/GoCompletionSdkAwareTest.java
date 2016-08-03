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

package com.goide.completion;

import com.goide.SdkAware;
import com.goide.sdk.GoSdkService;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.lookup.Lookup;
import com.intellij.psi.PsiFile;

import java.io.IOException;
import java.util.List;

@SdkAware
public class GoCompletionSdkAwareTest extends GoCompletionTestBase {
  public void testFormatter() {
    doTestInclude("package main; import . \"fmt\"; type alias <caret>", "Formatter");
  }

  public void testDoNotCompleteBuiltinFunctions() {
    doCheckResult("package main; var a = fals<caret>", "package main; var a = false<caret>");
  }

  public void testCamelHumpTypeCompletion() {
    doCheckResult("package main;\n" +
                  "func test(){io.reWSC<caret>}",
                  "package main;\n\n" +
                  "import \"idea_io\"\n\n" +
                  "func test(){idea_io.ReadWriteSeekerCustom()}", Lookup.NORMAL_SELECT_CHAR);
  }


  public void testCamelHumpFunctionCompletion() {
    doCheckResult("package main;\n" +
                  "func test(){io.reALC<caret>}",
                  "package main;\n\n" +
                  "import \"idea_io\"\n\n" +
                  "func test(){idea_io.ReadAtLeastCustom()}", Lookup.NORMAL_SELECT_CHAR);
  }

  public void testTypeAutoImport() {
    doCheckResult("package main; \n" +
                  "func test(){Templat<caret>}",
                  "package main;\n\n" +
                  "import \"text/template\"\n\n" +
                  "func test(){template.Template{<caret>}}", "template.Template");
  }

  public void testTypeAutoImportOnQualifiedName() {
    doCheckResult("package main; \n" +
                  "func test(){template.Templat<caret>}",
                  "package main;\n\n" +
                  "import \"text/template\"\n\n" +
                  "func test(){template.Template{<caret>}}");
  }

  public void testFunctionAutoImport() {
    doCheckResult("package main; \n" +
                  "func test(){Fprintl<caret>}",
                  "package main;\n\n" +
                  "import \"fmt\"\n\n" +
                  "func test(){fmt.Fprintln(<caret>)}", "fmt.Fprintln");
  }

  public void testVariableAutoImport() {
    doCheckResult("package main; \n" +
                  "func test(){ErrNotSuppor<caret>}",
                  "package main;\n\n" +
                  "import \"net/http\"\n\n" +
                  "func test(){http.ErrNotSupported}", "http.ErrNotSupported");
  }

  public void testConstantAutoImport() {
    doCheckResult("package main; \n" +
                  "func test(){O_RDO<caret>}", "package main;\n\n" +
                                               "import \"os\"\n\n" +
                                               "func test(){os.O_RDONLY}");
  }

  public void testDuplicateAutoImport() {
    doCheckResult("package main; \n" +
                  "func test(){Fprintl<caret>}",
                  "package main;\n\n" +
                  "import \"fmt\"\n\n" +
                  "func test(){fmt.Fprintln(<caret>)}", "fmt.Fprintln");
    myFixture.type(");Fprintl");
    myFixture.completeBasic();
    selectLookupItem("fmt.Fprintln");
    myFixture.checkResult("package main;\n\n" +
                          "import \"fmt\"\n\n" +
                          "func test(){fmt.Fprintln();fmt.Fprintln()}");
  }

  public void testForceAutoImportBlankImports() {
    doCheckResult("package main; \n" +
                  "import _ \"fmt\"\n" +
                  "func test(){Fprintl<caret>}",
                  "package main;\n\n" +
                  "import (\n" +
                  "\t_ \"fmt\"\n" +
                  "\t\"fmt\"\n" +
                  ")\n" +
                  "func test(){fmt.Fprintln()}", "fmt.Fprintln");
  }

  public void testAutoImportWithAlias() {
    doCheckResult("package main; \n" +
                  "import alias `fmt`\n" +
                  "func test(){Fprintl<caret>}",
                  "package main; \n" +
                  "import alias `fmt`\n" +
                  "func test(){alias.Fprintln()}",
                  "alias.Fprintln");
  }

  public void testAutoImportWithDotAlias() {
    doCheckResult("package main; \n" +
                  "import . `fmt`\n" +
                  "func test(){Fprintl<caret>}",
                  "package main; \n" +
                  "import . `fmt`\n" +
                  "func test(){Fprintln()}");
  }

  public void testUseImportPathInsteadOfPackageNameForAutoImport() {
    doCheckResult("package main\n" +
                  "\n" +
                  "import (\n" +
                  "    \"fmt\"\n" +
                  ")\n" +
                  "\n" +
                  "func main() {\n" +
                  "    fmt.Printf(FunctionInPackageThatDoesNotMatchDirectory<caret>);\n" +
                  "}",
                  "package main\n" +
                  "\n" +
                  "import (\n" +
                  "    \"fmt\"\n" +
                  "\t\"dirName\"\n" +
                  ")\n" +
                  "\n" +
                  "func main() {\n" +
                  "    fmt.Printf(otherPackage.FunctionInPackageThatDoesNotMatchDirectoryName());\n" +
                  "}", "otherPackage.FunctionInPackageThatDoesNotMatchDirectoryName");
  }

  public void testUsePackageNameInsteadOfImportPathIfPackageIsImported() {
    doCheckResult("package main\n" +
                  "\n" +
                  "import (\n" +
                  "    \"fmt\"\n" +
                  "    \"dirName\"\n" +
                  ")\n" +
                  "\n" +
                  "func main() {\n" +
                  "    fmt.Printf(FunctionInPackageThatDoesNotMatchDirectory<caret>);\n" +
                  "}",
                  "package main\n" +
                  "\n" +
                  "import (\n" +
                  "    \"fmt\"\n" +
                  "    \"dirName\"\n" +
                  ")\n" +
                  "\n" +
                  "func main() {\n" +
                  "    fmt.Printf(otherPackage.FunctionInPackageThatDoesNotMatchDirectoryName());\n" +
                  "}", "otherPackage.FunctionInPackageThatDoesNotMatchDirectoryName");
  }

  public void testDoNotImportLocallyImportedPackage() throws IOException {
    myFixture.addFileToProject("imported/imported.go", "package imported\n" +
                                                       "func LocallyImported() {}");
    doCheckResult("package main; \n" +
                  "import `./imported`\n" +
                  "func test(){LocallyImport<caret>}", "package main; \n" +
                                                       "import `./imported`\n" +
                                                       "func test(){imported.LocallyImported()}",
                  "imported.LocallyImported");
  }

  public void testImportedFunctionsPriority() {
    myFixture.configureByText("a.go", "package main; \n" +
                                      "import `io`\n" +
                                      "func test(){ReadA<caret>}");
    failOnFileLoading();
    myFixture.completeBasic();
    myFixture.assertPreferredCompletionItems(0, "io.ReadAtLeast", "io.ReaderAt", "idea_io.ReadAtLeastCustom", "idea_io.ReaderAtCustom");
  }

  public void testImportedTypesPriority() {
    myFixture.configureByText("a.go", "package main; \n" +
                                      "import `io`\n" +
                                      "func test(ReadWriteSeeke<caret>){}");
    failOnFileLoading();
    myFixture.completeBasic();
    myFixture.assertPreferredCompletionItems(0, "io.ReadWriteSeeker", "idea_io.ReadWriteSeekerCustom");
  }

  public void testDoNothingInsideSelector() {
    doTestVariants(
      "package main\n" +
      "import \"fmt\"\n" +
      "func test(){fmt.Sprintln().<caret>}", CompletionType.BASIC, 1, CheckType.EQUALS
    );
  }

  public void testDoNotRunAutoImportCompletionAfterDot() {
    doTestCompletion();
  }

  public void testDoNotRunAutoImportFunctionCompletionAfterUnaryAmpersand() {
    doCheckResult("package main;\n" +
                  "func test(){println(&io.reALC<caret>)}",
                  "package main;\n" +
                  "func test(){println(&io.reALC\n" +
                  "\t<caret>)}", Lookup.NORMAL_SELECT_CHAR);
    assertNull(myFixture.getLookup());
  }

  public void testDoNotRunAutoImportCompletionAfterDotAndSpace() {
    doTestCompletion();
    assertNull(myFixture.getLookup());
  }

  public void testImports() {
    doTestInclude("package main; import \"<caret>", "fmt", "io");
  }

  public void testCaseInsensitiveTypeConversion() {
    doCheckResult("package main; import \"fmt\"; func test(){fmt.form<caret>}",
                  "package main; import \"fmt\"; func test(){fmt.Formatter(<caret>)}");
  }

  public void testCaseInsensitiveFunction() {
    doCheckResult("package main; import \"fmt\"; func test(){fmt.err<caret>}",
                  "package main; import \"fmt\"; func test(){fmt.Errorf(<caret>)}");
  }

  public void testCaseInsensitiveType() {
    doCheckResult("package main; import \"fmt\"; func test(fmt.form<caret>}",
                  "package main; import \"fmt\"; func test(fmt.Formatter<caret>}");
  }

  public void testPrintlnBuiltin() {
    doTestInclude("package main; func test(){pr<caret>}", "print", "println");
  }

  public void testNothingUnrelatedInsideSelector() {
    doTestEquals("package foo; type E struct {}; type B struct {E}; func (e E) foo() {}; func boo() {}" +
                 "func main() {B{}.E..<caret>foo()}", "foo");
  }

  public void testCompleteWithUnfinishedPackage() {
    doCheckResult("package main\n" +
                  "func main() {\n" +
                  "    f.fprintl<caret>\n" +
                  "}",
                  "package main\n\n" +
                  "import \"fmt\"\n\n" +
                  "func main() {\n" +
                  "    fmt.Fprintln()\n" +
                  "}");
  }

  public void testDoNotCompleteTestFunctions() throws IOException {
    myFixture.addFileToProject("pack/pack_test.go", "package pack; func TestFoo() {}");
    myFixture.configureByText("my_test.go", "package a; func main() { _ = TestF<caret>");
    myFixture.completeBasic();
    assertNull(myFixture.getLookup());
    myFixture.checkResult("package a; func main() { _ = TestF<caret>");
  }

  public void testDoNotCompleteBenchmarkFunctions() throws IOException {
    myFixture.addFileToProject("pack/pack_test.go", "package pack; func BenchmarkFoo() {}");
    myFixture.configureByText("my_test.go", "package a; func main() { _ = BenchmarkF<caret>");
    myFixture.completeBasic();
    assertNull(myFixture.getLookup());
    myFixture.checkResult("package a; func main() { _ = BenchmarkF<caret>");
  }

  public void testDoNotCompleteExampleFunctions() throws IOException {
    myFixture.addFileToProject("pack/pack_test.go", "package pack; func ExampleFoo() {}");
    myFixture.configureByText("my_test.go", "package a; func main() { _ = ExampleF<caret>");
    assertNull(myFixture.getLookup());
    myFixture.completeBasic();
  }

  public void testCompleteTestBenchmarkExamplesFromNonTestFiles() throws IOException {
    myFixture.addFileToProject("pack/pack.go", "package pack; func TestFoo() {} func BenchmarkFoo() {} func ExampleFoo() {}");
    myFixture.configureByText("my_test.go", "package a; func main() { _ = Foo<caret>");
    myFixture.completeBasic();
    //noinspection ConstantConditions
    assertContainsElements(myFixture.getLookupElementStrings(), "pack.TestFoo", "pack.BenchmarkFoo", "pack.ExampleFoo");
  }

  public void testDoNotAutoImportWithTheSameImportPath() throws IOException {
    myFixture.addFileToProject("pack1/file2.go", "package pack1; func MyFunctionFromSamePath() {}");
    myFixture.addFileToProject("pack2/file2.go", "package pack1; func MyFunctionFromOtherPath() {}");
    PsiFile file = myFixture.addFileToProject("pack1/file1.go", "package pack1; func test() { pack1.MyFunc<caret> }");
    myFixture.configureFromExistingVirtualFile(file.getVirtualFile());
    myFixture.completeBasic();
    myFixture.checkResult("package pack1;\n\nimport \"pack2\"\n\nfunc test() { pack1.MyFunctionFromOtherPath() }");
  }

  public void testAutoImportOwnImportPathFromTest() throws IOException {
    myFixture.addFileToProject("pack/a.go", "package myPack; func Func() {}");
    PsiFile testFile = myFixture.addFileToProject("pack/a_test.go", "package myPack_test; func TestFunc() { myPack.Fun<caret> }");
    myFixture.configureFromExistingVirtualFile(testFile.getVirtualFile());
    myFixture.completeBasic();
    myFixture.checkResult("package myPack_test;\n\nimport \"pack\"\n\nfunc TestFunc() { myPack.Func() }");
  }

  public void testDoNotAutoImportDifferentPackageInSamePathFromTest() throws IOException {
    String text = "package foo_test; func TestFunc() { bar.Fun<caret> }";
    myFixture.addFileToProject("pack/a.go", "package bar; func Func() {}");
    PsiFile file = myFixture.addFileToProject("pack/a_test.go", text);
    myFixture.configureFromExistingVirtualFile(file.getVirtualFile());
    myFixture.completeBasic();
    assertNull(myFixture.getLookup());
    myFixture.checkResult(text);
  }

  public void testImportOwnPathFromTestFile() throws IOException {
    PsiFile testFile = myFixture.addFileToProject("fuzz/fuzy_test.go", "package fuzy_test; import \"<caret>\"");
    myFixture.configureFromExistingVirtualFile(testFile.getVirtualFile());
    myFixture.completeBasic();
    //noinspection ConstantConditions
    assertContainsElements(myFixture.getLookupElementStrings(), "fuzz");
  }

  public void testDoNotImportOwnPathFromNonTestPackage() throws IOException {
    PsiFile testFile = myFixture.addFileToProject("fuzz/fuzy_test.go", "package fuzy; import \"<caret>\"");
    myFixture.configureFromExistingVirtualFile(testFile.getVirtualFile());
    myFixture.completeBasic();
    List<String> strings = myFixture.getLookupElementStrings();
    assertTrue(strings != null && !strings.contains("fuzz"));
  }

  public void testDoNotCompleteBuiltinImport() {
    doCheckResult("package a; import \"built<caret>\"", "package a; import \"built<caret>\"");
    assertNull(myFixture.getLookup());
  }

  public void testCompleteVendoredBuiltinImport() {
    myFixture.addFileToProject("vendor/builtin/builtin.go", "package builtin; func Hello() {}");
    doCheckResult("package a; import \"built<caret>\"", "package a; import \"builtin<caret>\"");
  }

  public void testAutoImportVendorPackage() {
    myFixture.addFileToProject("vendor/vendorPackage/foo.go", "package vendorPackage; func Bar() {}");
    doCheckResult("package src; func _() { ven.Ba<caret> }", "package src;\n" +
                                                             "\n" +
                                                             "import \"vendorPackage\"\n" +
                                                             "\n" +
                                                             "func _() { vendorPackage.Bar() }");
  }

  public void testAutoImportVendorPackageWithDisabledVendoring() {
    disableVendoring();
    myFixture.addFileToProject("vendor/vendorPackage/foo.go", "package vendorPackage; func Bar() {}");
    doCheckResult("package src; func _() { ven.Ba<caret> }", "package src;\n" +
                                                             "\n" +
                                                             "import \"vendor/vendorPackage\"\n" +
                                                             "\n" +
                                                             "func _() { vendorPackage.Bar() }");
  }

  public void testDoNotCompleteSymbolsFromUnreachableVendoredPackages() {
    myFixture.addFileToProject("vendor/foo/foo.go", "package foo; func VendoredFunction() {}");
    myFixture.addFileToProject("vendor/foo/vendor/bar/bar.go", "package bar; func VendoredFunction() {}");
    myFixture.configureByText("a.go", "package src; func _() { VendorF<caret> }");
    myFixture.completeBasic();
    //noinspection ConstantConditions
    assertSameElements(myFixture.getLookupElementStrings(), "foo.VendoredFunction");
  }

  public void testDoNotCompleteSymbolsFromShadowedPackages() {
    myFixture.addFileToProject("foo/foo.go", "package foo; func ShadowedFunction() {}");
    myFixture.addFileToProject("vendor/foo/foo.go", "package bar; func ShadowedFunction() {}");
    myFixture.configureByText("a.go", "package src; func _() { ShadowF<caret> }");
    myFixture.completeBasic();
    //noinspection ConstantConditions
    assertSameElements(myFixture.getLookupElementStrings(), "bar.ShadowedFunction");
  }

  public void testDoNotCompleteSymbolsFromShadowedVendoredPackages() {
    myFixture.addFileToProject("vendor/foo/foo.go", "package bar; func ShadowedFunction() {}");
    myFixture.addFileToProject("vendor/foo/vendor/foo/foo.go", "package bar; func ShadowedFunction() {}");
    PsiFile file = myFixture.addFileToProject("vendor/foo/main.go", "package foo; func _() { ShadowF<caret> }");
    myFixture.configureFromExistingVirtualFile(file.getVirtualFile());
    myFixture.completeBasic();
    //noinspection ConstantConditions
    assertSameElements(myFixture.getLookupElementStrings(), "bar.ShadowedFunction");
  }

  public void testDoNotCompletePackagesShadowedBySdkDirectories() {
    myFixture.addFileToProject("fmt/subdir/fmt.go", "package subdir; func Println() {}");
    myFixture.addFileToProject("fmt/fmt.go", "package shadowed; func Println() {}");
    myFixture.configureByText("a.go", "package src; import `fmt<caret>`");
    myFixture.completeBasic();
    //noinspection ConstantConditions
    assertSameElements(myFixture.getLookupElementStrings(), "fmt", "fmt/subdir");
  }

  public void testDoNotCompleteFunctionsFromPackagesShadowedBySdkDirectories() {
    myFixture.addFileToProject("fmt/subdir/fmt.go", "package subdir; func Println() {}");
    myFixture.addFileToProject("fmt/fmt.go", "package shadowed; func Println() {}");
    myFixture.configureByText("a.go", "package src; func _() { Printl<caret> }");
    myFixture.completeBasic();
    List<String> elementStrings = myFixture.getLookupElementStrings();
    assertNotNull(elementStrings);
    assertContainsElements(elementStrings, "subdir.Println");
    assertDoesntContain(elementStrings, "shadowed.Println");
  }
  
  public void testCompleteInternalPackageOn1_2_SDK() {
    myFixture.addFileToProject("internal/internal.go", "package internalPackage; func InternalFunction() {}");
    myFixture.addFileToProject("sub/internal/internal.go", "package subInternalPackage; func InternalFunction() {}");
    myFixture.configureByText("a.go", "package a; import `inte<caret>`");
    myFixture.completeBasic();
    List<String> elementStrings = myFixture.getLookupElementStrings();
    assertNotNull(elementStrings);
    assertContainsElements(elementStrings, "internal", "sub/internal", "net/internal");
  }

  public void testCompleteInternalPackageOn1_4_SDK() {
    GoSdkService.setTestingSdkVersion("1.4", getTestRootDisposable());
    myFixture.addFileToProject("internal/internal.go", "package internalPackage; func InternalFunction() {}");
    myFixture.addFileToProject("sub/internal/internal.go", "package subInternalPackage; func InternalFunction() {}");
    myFixture.configureByText("a.go", "package a; import `inte<caret>`");
    myFixture.completeBasic();
    List<String> elementStrings = myFixture.getLookupElementStrings();
    assertNotNull(elementStrings);
    assertContainsElements(elementStrings, "internal", "sub/internal");
    assertDoesntContain(elementStrings, "net/internal");
  }

  public void testCompleteInternalPackageOn1_5_SDK() {
    GoSdkService.setTestingSdkVersion("1.5", getTestRootDisposable());
    myFixture.addFileToProject("internal/internal.go", "package internalPackage; func InternalFunction() {}");
    myFixture.addFileToProject("sub/internal/internal.go", "package subInternalPackage; func InternalFunction() {}");
    doCheckResult("package a; import `inte<caret>`", "package a; import `internal`");
  }

  public void testIntArray()  { doTestInclude("package a; var x = []<caret>", "int"); }
  public void testMapIntInt() { doTestInclude("package a; var x = map[int]<caret>", "int"); }
  public void testStruct()    { doTestInclude("package a; var x = <caret>", "struct"); }
}
