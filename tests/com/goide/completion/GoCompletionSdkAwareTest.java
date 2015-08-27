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

package com.goide.completion;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.lookup.Lookup;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.testFramework.fixtures.TempDirTestFixture;

import java.io.IOException;
import java.util.List;

public class GoCompletionSdkAwareTest extends GoCompletionSdkAwareTestBase {
  public void testFormatter() {
    doTestInclude("package main; import . \"fmt\"; type alias <caret>", "Formatter");
  }

  public void testCamelHumpTypeCompletion() {
    doCheckResult("package main;\n" +
                  "func test(){io.reWSC<caret>}",
                  "package main;\n" +
                  "import \"idea_io\"\n" +
                  "func test(){idea_io.ReadWriteSeekerCustom()}", Lookup.NORMAL_SELECT_CHAR);
  }


  public void testCamelHumpFunctionCompletion() {
    doCheckResult("package main;\n" +
                  "func test(){io.reALC<caret>}",
                  "package main;\n" +
                  "import \"idea_io\"\n" +
                  "func test(){idea_io.ReadAtLeastCustom()}", Lookup.NORMAL_SELECT_CHAR);
  }

  public void testTypeAutoImport() {
    doCheckResult("package main; \n" +
                  "func test(){Templat<caret>}",
                  "package main;\n" +
                  "import \"text/template\"\n" +
                  "func test(){template.Template{<caret>}}", "template.Template");
  }

  public void testTypeAutoImportOnQualifiedName() {
    doCheckResult("package main; \n" +
                  "func test(){template.Templat<caret>}",
                  "package main;\n" +
                  "import \"text/template\"\n" +
                  "func test(){template.Template{<caret>}}");
  }

  public void testFunctionAutoImport() {
    doCheckResult("package main; \n" +
                  "func test(){Fprintl<caret>}",
                  "package main;\n" +
                  "import \"fmt\"\n" +
                  "func test(){fmt.Fprintln(<caret>)}", "fmt.Fprintln");
  }

  public void testVariableAutoImport() {
    doCheckResult("package main; \n" +
                  "func test(){ErrNotSuppor<caret>}",
                  "package main;\n" +
                  "import \"net/http\"\n" +
                  "func test(){http.ErrNotSupported}", "http.ErrNotSupported");
  }

  public void testConstantAutoImport() {
    doCheckResult("package main; \n" +
                  "func test(){O_RDO<caret>}", "package main;\n" +
                                               "import \"os\"\n" +
                                               "func test(){os.O_RDONLY}");
  }

  public void testDuplicateAutoImport() {
    doCheckResult("package main; \n" +
                  "func test(){Fprintl<caret>}",
                  "package main;\n" +
                  "import \"fmt\"\n" +
                  "func test(){fmt.Fprintln(<caret>)}", "fmt.Fprintln");
    myFixture.type(");Fprintl");
    myFixture.completeBasic();
    selectLookupItem("fmt.Fprintln");
    myFixture.checkResult("package main;\n" +
                          "import \"fmt\"\n" +
                          "func test(){fmt.Fprintln();fmt.Fprintln()}");
  }

  public void testForceAutoImportBlankImports() {
    doCheckResult("package main; \n" +
                  "import _ \"fmt\"\n" +
                  "func test(){Fprintl<caret>}",
                  "package main;\n" +
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
    myFixture.getTempDirFixture().createFile("imported/imported.go", "package imported\n" +
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
  }

  public void testDoNotRunAutoImportCompletionAfterDotAndSpace() {
    doTestCompletion();
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
                  "package main\n" +
                  "import \"fmt\"\n" +
                  "func main() {\n" +
                  "    fmt.Fprintln()\n" +
                  "}");
  }

  public void testDoNotCompleteTestFunctions() throws IOException {
    myFixture.getTempDirFixture().createFile("pack/pack_test.go", "package pack; func TestFoo() {}");
    myFixture.configureByText("my_test.go", "package a; func main() { _ = TestF<caret>");
    myFixture.completeBasic();
    myFixture.checkResult("package a; func main() { _ = TestF<caret>");
  }

  public void testDoNotCompleteBenchmarkFunctions() throws IOException {
    myFixture.getTempDirFixture().createFile("pack/pack_test.go", "package pack; func BenchmarkFoo() {}");
    myFixture.configureByText("my_test.go", "package a; func main() { _ = BenchmarkF<caret>");
    myFixture.completeBasic();
    myFixture.checkResult("package a; func main() { _ = BenchmarkF<caret>");
  }

  public void testDoNotCompleteExampleFunctions() throws IOException {
    myFixture.getTempDirFixture().createFile("pack/pack_test.go", "package pack; func ExampleFoo() {}");
    myFixture.configureByText("my_test.go", "package a; func main() { _ = ExampleF<caret>");
    myFixture.completeBasic();
    myFixture.checkResult("package a; func main() { _ = ExampleF<caret>");
  }

  public void testCompleteTestBenchmarkExamplesFromNonTestFiles() throws IOException {
    myFixture.getTempDirFixture().createFile("pack/pack.go", "package pack; func TestFoo() {} func BenchmarkFoo() {} func ExampleFoo() {}");
    myFixture.configureByText("my_test.go", "package a; func main() { _ = Foo<caret>");
    myFixture.completeBasic();
    assertContainsElements(myFixture.getLookupElementStrings(), "pack.TestFoo", "pack.BenchmarkFoo", "pack.ExampleFoo");
  }

  public void testDoNotAutoImportWithTheSameImportPath() throws IOException {
    myFixture.getTempDirFixture().createFile("pack1/file2.go", "package pack1; func MyFunctionFromSamePath() {}");
    myFixture.getTempDirFixture().createFile("pack2/file2.go", "package pack1; func MyFunctionFromOtherPath() {}");
    VirtualFile file = myFixture.getTempDirFixture().createFile("pack1/file1.go", "package pack1; func test() { pack1.MyFunc<caret> }");
    myFixture.configureFromExistingVirtualFile(file);
    myFixture.completeBasic();
    myFixture.checkResult("package pack1;\nimport \"pack2\" func test() { pack1.MyFunctionFromOtherPath() }");
  }
  
  public void testAutoImportOwnImportPathFromTest() throws IOException {
    myFixture.getTempDirFixture().createFile("pack/a.go", "package myPack; func Func() {}");
    VirtualFile testFile = myFixture.getTempDirFixture()
      .createFile("pack/a_test.go", "package myPack_test; func TestFunc() { myPack.Fun<caret> }");
    myFixture.configureFromExistingVirtualFile(testFile);
    myFixture.completeBasic();
    myFixture.checkResult("package myPack_test;\nimport \"pack\" func TestFunc() { myPack.Func() }");
  }

  public void testDoNotAutoImportDifferentPackageInSamePathFromTest() throws IOException {
    String text = "package foo_test; func TestFunc() { bar.Fun<caret> }";
    myFixture.getTempDirFixture().createFile("pack/a.go", "package bar; func Func() {}");
    myFixture.configureFromExistingVirtualFile(myFixture.getTempDirFixture().createFile("pack/a_test.go", text));
    myFixture.completeBasic();
    myFixture.checkResult(text);
  }
  
  public void testImportOwnPathFromTestFile() throws IOException {
    TempDirTestFixture dir = myFixture.getTempDirFixture();
    VirtualFile testFile = dir.createFile("fuzz/fuzy_test.go", "package fuzy_test; import \"<caret>\"");
    myFixture.configureFromExistingVirtualFile(testFile);
    myFixture.completeBasic();
    assertContainsElements(myFixture.getLookupElementStrings(), "fuzz");
  }

  public void testDoNotImportOwnPathFromNonTestPackage() throws IOException {
    TempDirTestFixture dir = myFixture.getTempDirFixture();
    VirtualFile testFile = dir.createFile("fuzz/fuzy_test.go", "package fuzy; import \"<caret>\"");
    myFixture.configureFromExistingVirtualFile(testFile);
    myFixture.completeBasic();
    List<String> strings = myFixture.getLookupElementStrings();
    assertTrue(strings != null && !strings.contains("fuzz"));
  }

}
