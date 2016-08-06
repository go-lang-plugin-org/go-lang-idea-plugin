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

import com.intellij.codeInsight.lookup.Lookup;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementPresentation;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.TreePrintCondition;
import com.intellij.util.ArrayUtil;
import com.intellij.util.containers.ContainerUtil;

import java.util.List;

public class GoCompletionTest extends GoCompletionTestBase {
  public void testPackageWithoutAlias() {
    doTestInclude("package foo; import `fmt`; func main(){<caret>}", "fmt");
  }

  public void testLocalFunction() {
    doTestInclude("package foo; func foo() {}; func main() {<caret>}", "foo", "main");
  }

  public void testLocalType() {
    doTestInclude("package foo; type (T struct {}; T2 struct{}); func main(){var i <caret>}", "T", "T2");
  }

  public void testLocalFunctionInDifferentFiles() {
    myFixture.copyFileToProject(getTestName(true) + "_context.go", "importPath/context.go");
    myFixture.configureFromExistingVirtualFile(myFixture.copyFileToProject(getTestName(true) + ".go", "importPath/main.go"));
    myFixture.completeBasic();
    myFixture.checkResultByFile(getTestName(true) + "_after.go");
  }
  
  public void testImportVendoringPackages() {
    myFixture.addFileToProject("vendor/pack1/pack2/test.go", "package foo");
    doCheckResult("package foo; import `pa<caret>`", "package foo; import `pack1/pack2<caret>`");
  }

  public void testImportVendoringPackagesWithDisabledVendoring() {
    disableVendoring();
    myFixture.addFileToProject("vendor/pack1/pack2/test.go", "package foo");
    doCheckResult("package foo; import `pa<caret>`", "package foo; import `vendor/pack1/pack2<caret>`", Lookup.NORMAL_SELECT_CHAR);
  }

  public void testImportPackages() {
    myFixture.addFileToProject("package1/pack/test.go", "package foo");
    myFixture.addFileToProject("package2/pack/test.go", "package bar");
    myFixture.configureByText("test.go", "package foo; import `pack<caret>`");
    myFixture.completeBasic();
    List<String> lookupElementStrings = myFixture.getLookupElementStrings();
    assertNotNull(lookupElementStrings);
    assertSameElements(lookupElementStrings, "package1/pack", "package2/pack");
  }

  public void testImportPackagesWithoutClosingQuote() {
    myFixture.addFileToProject("package1/pack/test.go", "package foo");
    myFixture.addFileToProject("package2/pack/test.go", "package bar");
    myFixture.configureByText("test.go", "package foo; import `pack<caret>");
    myFixture.completeBasic();
    List<String> lookupElementStrings = myFixture.getLookupElementStrings();
    assertNotNull(lookupElementStrings);
    assertSameElements(lookupElementStrings, "package1/pack", "package2/pack");
  }

  public void testImportRelativePackages() {
    myFixture.addFileToProject("package1/pack/test.go", "package foo");
    myFixture.addFileToProject("package2/pack/test.go", "package bar");
    myFixture.configureByText("test.go", "package foo; import `./pack<caret>`");
    myFixture.completeBasic();
    List<String> lookupElementStrings = myFixture.getLookupElementStrings();
    assertNotNull(lookupElementStrings);
    assertSameElements(lookupElementStrings, "package1", "package2");
  }

  public void testDoNotCompleteFullPackagesForRelativeImports() {
    myFixture.addFileToProject("package1/pack/test.go", "package foo");
    myFixture.addFileToProject("package2/pack/test.go", "package bar");
    myFixture.configureByText("test.go", "package foo; import `./pack<caret>`");
    myFixture.completeBasic();
    List<String> lookupElementStrings = myFixture.getLookupElementStrings();
    assertNotNull(lookupElementStrings);
    assertSameElements(lookupElementStrings, "package1", "package2");
  }

  public void testDoNotCompleteOwnImportPath() {
    myFixture.addFileToProject("package/long/long/path/test.go", "package pack");
    PsiFile testFile = myFixture.addFileToProject("package/very/long/path/but/same/package/test.go",
                                                  "package pack; import `package/<caret>`");
    myFixture.configureFromExistingVirtualFile(testFile.getVirtualFile());
    myFixture.completeBasic();
    myFixture.checkResult("package pack; import `package/long/long/path`");
  }

  public void testImportsPriority() {
    myFixture.addFileToProject("package/long/but/similar/path/test.go", "package pack");
    myFixture.addFileToProject("package/very/long/path/test.go", "package pack");
    myFixture.addFileToProject("package/middle/path/test.go", "package pack");
    myFixture.addFileToProject("package/short/test.go", "package pack");
    PsiFile testFile = myFixture.addFileToProject("package/long/but/similar/test.go", "package pack; import `package/<caret>`");
    myFixture.configureFromExistingVirtualFile(testFile.getVirtualFile());
    myFixture.completeBasic();
    myFixture.assertPreferredCompletionItems(0, "package/long/but/similar/path", "package/short", "package/middle/path",
                                             "package/very/long/path");
  }

  public void testDoNotHidePopupOnSlash() {
    myFixture.addFileToProject("package1/pack/test.go", "package foo");
    myFixture.addFileToProject("package2/pack/test.go", "package bar");
    myFixture.configureByText("test.go", "package foo; import `<caret>`");
    myFixture.completeBasic();
    myFixture.type("package1/\n");
    myFixture.checkResult("package foo; import `package1/pack<caret>`");
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

  public void testNoKeywordCompletionInsideTypeDeclarationList() {
    doTestEquals("package foo; type (\n\t<caret>\n)");
  }

  public void testNoKeywordCompletionInsideConstDeclarationList() {
    doTestEquals("package foo; const (\n\t<caret>\n)");
  }

  public void testNoKeywordCompletionInsideVarDeclarationList() {
    doTestEquals("package foo; var (\n\t<caret>\n)");
  }
  
  public void testKeywordCompletionInsideVarDeclarationListAfterEquals() {
    doTestInclude("package foo; var (\n\ta = <caret>\n)", "map", "func", "struct");
  }

  public void testNoCompletionInsideComments() {
    doTestEquals("package foo; func main(){/*<caret>*/}");
  }

  public void testStructTypes() {
    doTestEquals("package foo; type AA struct {N AA}; func foo(a AA) {a.<caret>}", "N");
  }

  public void testStructTypes2() {
    doTestEquals("package foo; type AA struct {N AA}; func foo(a *AA) {a.<caret>}", "N");
  }

  public void testStructKeyword() {
    doCheckResult("package main; func main() { d := struct { name str<caret> }; _ = d }",
                  "package main; func main() { d := struct { name struct{} }; _ = d }");
  }

  public void testStructKeyword2() {
    doTestInclude("package main; func main() { d := struct { name <caret> }; _ = d }", "struct");
  }

  public void testStructKeyword3() {
    doCheckResult("package main; func main() { d := str<caret>; _ = d }",
                  "package main; func main() { d := struct {\n" +
                  "\t<caret>\n" +
                  "}{}; _ = d }");
  }

  public void testImports() {
    doCheckResult("package foo; import imp \"\"; func foo(a im<caret>) {}", "package foo; import imp \"\"; func foo(a imp.) {}");
  }

  public void testImportsForRefs() {
    doCheckResult("package foo; import imp \"\"; func foo() {im<caret>.foo}", "package foo; import imp \"\"; func foo() {imp.<caret>foo}");
  }

  public void testCompleteFieldWithoutColon() {
    doCheckResult("package main;func main(){a:=struct{Demo int}{Demo:1};println(a.D<caret>)}",
                  "package main;func main(){a:=struct{Demo int}{Demo:1};println(a.Demo<caret>)}");
  }

  public void testCompleteFieldWithoutColonAfterKey() {
    doCheckResult("package main; type Test struct { ID string }; func t() { var n Test; _ = &Test{ID: n.I<caret>} }",
                  "package main; type Test struct { ID string }; func t() { var n Test; _ = &Test{ID: n.ID<caret>} }");
  }

  public void testTopLevelKeywords() {
    myFixture.testCompletionVariants(getTestName(true) + ".go", "const", "func", "import", "type", "var");
  }

  public void testTopLevelKeywords_2() {
    myFixture.testCompletionVariants(getTestName(true) + ".go", "const", "func", "type", "var");
  }

  public void testBlockKeywords() {
    myFixture.testCompletionVariants(getTestName(true) + ".go", "for", "const", "var", "return", "if", "switch", "go", "defer", "select",
                                     "fallthrough", "goto", "main", "struct", "map", "type");
  }

  public void testBlockKeywordsInsideOneLineFunction() {
    myFixture.testCompletionVariants(getTestName(true) + ".go", "for", "const", "var", "return", "if", "switch", "go", "defer", "select",
                                     "fallthrough", "goto", "main", "struct", "map", "type");
  }

  public void testBlockKeywordsInsideCaseStatement() {
    myFixture.testCompletionVariants(getTestName(true) + ".go", "break", "for", "const", "var", "return", "if", "switch", "go", "defer", "select",
                                     "fallthrough", "goto", "main", "struct", "map", "case", "default", "type");
  }

  public void testAddSpaceAfterKeyword() {
    doTestCompletion();
  }

  public void testTypeKeywords() {
    myFixture.testCompletionVariants(getTestName(true) + ".go", "struct", "interface", "chan", "map");
  }

  public void testExpressionKeywords() {
    myFixture.testCompletionVariants(getTestName(true) + ".go", "struct", "map", "main", "func");
  }

  public void testTypeKeywordsInsideParentheses() {
    myFixture.testCompletionVariants(getTestName(true) + ".go", "chan", "map", "interface", "struct");
  }

  public void testSelectKeywordInsertHandler() {
    doTestCompletion();
  }

  public void testTypeKeywordInsertBraces() {
    doTestCompletion();
  }

  public void testTypeKeywordDoNotInsertBraces() {
    doTestCompletion();
  }

  public void testInterfaceKeywordAsFunctionParameter() {
    doTestCompletion();
  }

  public void testStructKeywordAsFunctionParameter() {
    doTestCompletion();
  }

  public void testForStatementKeywords() {
    myFixture.testCompletionVariants(getTestName(true) + ".go", "bar", "break", "const", "continue", "defer", "for", "go", "if", "return",
                                     "fallthrough", "goto", "select", "switch", "var", "struct", "map", "type");
  }
  
  public void testForStatementKeywordsInsideFuncLit() {
    myFixture.testCompletionVariants(getTestName(true) + ".go", "bar", "const", "defer", "for", "go", "if", "return",
                                     "fallthrough", "goto", "select", "switch", "var", "struct", "map", "type");
  }

  public void testDoNotCompleteKeywordsInsideConstSpec() {
    myFixture.testCompletionVariants(getTestName(true) + ".go", "a");
  }

  public void testDoNotCompleteKeywordsInsideSelectorExpression() {
    myFixture.testCompletionVariants(getTestName(true) + ".go", "FuncA", "FuncB");
  }

  public void testRangeKeyword_1() {
    doTestCompletion();
  }

  public void testRangeKeyword_2() {
    doTestCompletion();
  }

  public void testRangeKeyword_3() {
    doTestCompletion();
  }

  public void testChanKeyword() {
    doTestCompletion();
  }

  public void testChanKeyword_2() {
    doTestCompletion();
  }

  public void testChanKeyword_3() {
    doTestCompletion();
  }

  public void testMapKeyword() {
    doTestCompletion();
  }

  public void testMapKeyword_2() {
    doTestCompletion();
  }

  public void testMapKeywordInsertHandler() {
    doTestCompletion();
  }

  public void testMapKeywordInsertHandlerDoNotInsertBrackets() {
    doTestCompletion();
  }

  public void testElseKeyword() {
    doTestCompletion();
  }

  public void testElseKeywordRegression() {
    doTestEmptyCompletion();
  }

  public void testIfKeywordAfterElse() {
    doTestCompletion();
  }

  public void testForStatementKeywordsDoNotInsertSpace() {
    doTestCompletion();
  }

  public void testFunctionInDefer() {
    doTestCompletion();
  }

  public void testFunctionAsFunctionArgument() {
    doTestCompletion();
  }

  public void testFunctionAsVariableValue() {
    doTestCompletion();
  }

  public void testFunctionInGo() {
    doTestCompletion();
  }

  public void testPackageKeyword() {
    doTestCompletion();
  }

  public void testPackageKeywordInEmptyFile() {
    doTestCompletion();
  }

  public void testExpressionCaseKeywordCompletion() {
    doTestCompletion();
  }

  public void testExpressionDefaultKeywordCompletion() {
    doTestCompletion();
  }

  public void testTypeCaseKeywordCompletion() {
    doTestCompletion();
  }

  public void testTypeDefaultKeywordCompletion() {
    doTestCompletion();
  }

  public void testMethods() {
    doTestEquals("package foo; type T interface{}; func (t T) f() {t.<caret>}", "f");
  }

  public void testPreventStackOverflow() {
    doTestEquals("package foo; type E struct {*E}; func (e E) foo() {}; func main() {e := E{}; e.<caret>}", "foo", "E");
  }

  public void testNestedStructures() {
    doTestEquals("package foo; type E struct {}; type B struct {E}; func (e E) foo() {}; func (b B) bar(){}" +
                 "func main() {b := B{}; b.<caret>}", "foo", "bar", "E");
  }

  public void testNestedStructures2() {
    doTestEquals("package foo; type E struct {}; type B struct {E}; func (e E) foo() {}; " +
                 "func main() {b := B{}; b.E.<caret>}", "foo");
  }

  public void testNestedStructures3() {
    doTestEquals("package foo; type E struct {}; type B struct {E}; func (e E) foo() {}; " +
                 "func main() {B{}.E.<caret>}", "foo");
  }

  public void testInterfaceTypes() {
    doTestInclude("package foo; type E interface {}; type B interface {<caret>}", "E");
  }

  public void testReceiverCompletion() {
    doTestInclude("package foo; type E interface {}; func (e <caret>", "E");
  }

  public void testInterfaceTypesNoStruct() {
    doTestExclude("package foo; type E struct {}; type B interface {<caret>}", "E");
  }

  public void testOnlyTypesInParameters() {
    doTestExclude("package foo; const a int = 1; var b = 2; func main(<caret>) {}", "a", "b", "main");
  }

  public void testInnerTypes() {
    doTestInclude("package foo; func foo() {type innerType struct {}; var i <caret>}", "innerType");
  }

  public void testChannelType() {
    doTestInclude("package foo; func foo() {type T struct {cn *T}; var i T; i.<caret>}", "cn");
  }

  public void testInterfaceType() {
    doTestInclude("package foo; func foo(i interface {Boo() int}) {i.<caret>}", "Boo");
  }

  public void testInterfaceType2() {
    doTestInclude("package foo; type I interface {Boo() int}; func foo(i I) {i.<caret>}", "Boo");
  }

  public void testLabel() {
    doTestInclude("package foo; func main() { goto <caret>; Label1: 1}", "Label1");
  }
  
  public void testNoKeywordsInReceiver() {
    doTestExclude("package foo; func (a <caret>) main() { }", "map", "chan", "struct", "interface");
  }

  public void testOnlyLocalTypesInReceiver() {
    doTestEquals("package foo; type ( t int; s int; ); func (c <caret>)", "s", "t");
  }

  public void testNestedBlocks() {
    doTestInclude("package main; func main() {def := 1; abc := 0; {<caret>}}", "abc", "def");
  }

  public void testNoMainAnymore() {
    doTestEquals("package foo; func ma<caret> { }");
  }

  public void testNoChanOrMap() {
    doTestEquals("package foo; func ma(f int.<caret>) { }");
  }

  public void testOnTopLevel() {
    doTestEquals("package foo; func ma() { }\n<caret>", "var", "const", "func", "type");
  }

  public void testPackageBeforeDot() {
    doCheckResult("package foo; import imp \"\"; func foo(a im<caret>.SomeType) {}",
                  "package foo; import imp \"\"; func foo(a imp.<caret>SomeType) {}");
  }

  public void testNoUnderscore() {
    String theSame = "package foo; func foo() {_ := 1; <caret>}";
    doCheckResult(theSame, theSame);
  }

  public void testVarFuncTypeZeroParam() {
    doCheckResult("package foo; var abc = func() {}; func main() {ab<caret>}",
                  "package foo; var abc = func() {}; func main() {abc()<caret>}");
  }

  public void testVarFuncTypeOneParam() {
    doCheckResult("package foo; var abc = func(o int) {}; func main() {ab<caret>}",
                  "package foo; var abc = func(o int) {}; func main() {abc(<caret>)}");
  }

  public void testCaseInsensitiveVariable() {
    doCheckResult("package main; func test(BBA int){b<caret>}",
                  "package main; func test(BBA int){BBA<caret>}");
  }

  public void testArrayType() {
    doCheckResult("package main; type custom []string; func main() { _ = custom<caret> }",
                  "package main; type custom []string; func main() { _ = custom{<caret>} }");
  }

  public void testMapType() {
    doCheckResult("package main; type custom map[string]string; func main() { _ = custom<caret> }",
                  "package main; type custom map[string]string; func main() { _ = custom{<caret>} }");
  }

  public void testRanges() {
    doTestInclude("package foo; func foo(a int) {for k := range <caret>}", "a");
  }

  public void testNoDuplicates() {
    doTestInclude("package foo; type a struct {<caret>", "a");
    List<String> stringList = myFixture.getLookupElementStrings();
    assertNotNull(stringList);
    assertSize(1, ContainerUtil.filter(stringList, new TreePrintCondition.Include("a")));
  }

  public void testTypeCastAsVar() {
    doTestInclude("package main\n" +
                  "var fooVar int = 1\n" +
                  "func main() {\n" +
                  "    for _, v := range (*<caret>) {\n" +
                  "    }\n" +
                  "}", "fooVar");
  }

  private static final String TYPE = "package main;\n" +
                                     "type WaitGroup struct {\n" +
                                     "    counter int32\n" +
                                     "    waiters int32\n" +
                                     "    sema    *uint32\n" +
                                     "}\n";

  public void testStructField() {
    myFixture.configureByText("a.go", TYPE + "func main() {WaitGroup{<caret>}};");
    myFixture.completeBasic();
    myFixture.assertPreferredCompletionItems(0, "counter", "sema", "waiters");
  }

  public void testAnonymousStructField() {
    myFixture.configureByText("a.go", TYPE + "var baz = []*WaitGroup{{<caret>}}");
    myFixture.completeBasic();
    myFixture.assertPreferredCompletionItems(0, "counter", "sema", "waiters");
  }

  public void testStructField2() {
    doTestInclude(TYPE + "func main() {WaitGroup{foo:bar, <caret>}};", "counter", "waiters", "sema");
  }

  public void testStructFieldReplace() {
    doCheckResult(TYPE + "func main() { WaitGroup{sem<caret>abc} }", TYPE + "func main() { WaitGroup{sema:<caret>} }",
                  Lookup.REPLACE_SELECT_CHAR);
  }

  public void testStructFieldFromOtherStruct() {
    doCheckResult("package main; type Example struct { ID string }; func main() { a := Example{ID: \"a\"}; _ = []string{a.<caret>} }",
                  "package main; type Example struct { ID string }; func main() { a := Example{ID: \"a\"}; _ = []string{a.ID<caret>} }",
                  Lookup.NORMAL_SELECT_CHAR);
  }

  public void testNoStructFieldAfterColon() {
    doTestExclude(TYPE + "func main() {WaitGroup{sema:<caret>}};", "counter", "waiters", "sema");
  }

  public void testStructConstructions() {
    doCheckResult("package main; func main() {WaitGr<caret>}; type WaitGroup struct {sema *uint32}",
                  "package main; func main() {WaitGroup{<caret>}}; type WaitGroup struct {sema *uint32}");
  }

  public void testIntConversion() {
    doCheckResult("package main; func main() {int<caret>}; type int int",
                  "package main; func main() {int(<caret>)}; type int int");
  }

  public void testPreventSOE() {
    doTestInclude("package rubex; const ( IGNORECASE = 1; EXTEND = (IGNORECASE << 1); MULTILINE = (EXTEND << 1)); func m() {<caret>}",
                  "EXTEND");
  }

  public void testPreventSOE2() {
    doTestInclude("package main; import \"fmt\"; var fmt = &fmt.<caret>");
  }

  @SuppressWarnings("ConstantConditions")
  public void testPackageNames() {
    myFixture.configureByText("test_test.go", "package myFromTest_test");
    myFixture.configureByText("test_file.go", "package myFromFile");
    myFixture.configureByText("test.go", "package m<caret>");
    myFixture.completeBasic();
    assertSameElements(myFixture.getLookupElementStrings(), "myFromTest", "myFromFile", "main");
  }

  @SuppressWarnings("ConstantConditions")
  public void testPackageNamesInTestFile() {
    myFixture.configureByText("foo.go", "package foo");
    myFixture.configureByText("foo_test.go", "package <caret>");
    myFixture.completeBasic();
    assertSameElements(myFixture.getLookupElementStrings(), "foo", "foo_test", "main");
  }

  public void testPointerSpecType() {
    myFixture.configureByText("foo.go", "package main; type a struct{};" +
                                        "func main() {q1, q2:=&a{};q<caret>}");
    myFixture.completeBasic();
    LookupElement first = ArrayUtil.getFirstElement(myFixture.getLookupElements());
    assertNotNull(first);
    LookupElementPresentation presentation = new LookupElementPresentation();
    first.renderElement(presentation);
    assertEquals("*main.a", presentation.getTypeText());
  }

  public void testPackageNamesInEmptyDirectory() {
    PsiFile file = myFixture.addFileToProject("my-directory-name/test.go", "package m<caret>");
    myFixture.configureFromExistingVirtualFile(file.getVirtualFile());
    myFixture.completeBasic();
    List<String> strings = myFixture.getLookupElementStrings();
    assertNotNull(strings);
    assertSameElements(strings, "my_directory_name", "main");
  }
  
  public void testCompleteFromTestsDefinedInCurrentPackage() {
    myFixture.addFileToProject("my_test.go", "package mytest; func MyFunction() {}");
    myFixture.configureByText("current_test.go", "package mytest; func TestSomething() { MyFunc<caret> }");
    myFixture.completeBasic();
    myFixture.checkResult("package mytest; func TestSomething() { MyFunction() }");
  }

  public void testFieldNamesInEmptyStructLiteral() {
    String source = "package test; " +
                    "type A struct { field_in_a string }; " +
                    "type B struct { A; field_in_b string }; " +
                    "func Test() B { a := A{}; s := \"\"; return B{<caret>}; }";
    doTestInclude(source, "a", "A", "field_in_b");
  }

  public void testFieldNamesInStructLiteralWithNameValueInitializers() {
    String source = "package test; " +
                    "type A struct { field_in_a string }; " +
                    "type B struct { A; field_in_b string }; " +
                    "func Test() B { a := A{}; s := \"\"; return B{ A: A{}, <caret>}; }";
    doTestEquals(source, "field_in_b");
  }

  public void testNoFieldNamesInStructLiteralWithValueInitializers() {
    String source = "package test; " +
                    "type A struct { field_in_a string }; " +
                    "type B struct { A; field_in_b string }; " +
                    "func Test() B { a := A{}; s := \"\"; return B{ A{}, <caret>}; }";
    myFixture.configureByText("test.go", source);
    myFixture.completeBasic();
    List<String> variants = myFixture.getLookupElementStrings();
    assertNotNull(variants);
    assertContainsElements(variants, "s");
    assertDoesntContain(variants, "field_in_b");
  }

  public void testStructFieldValueCompletion() {
    String source = "package test; " +
                    "type A struct { field1 string; field2 string }; " +
                    "func Test() A { s := \"\"; return A{field1: \"\", field2: <caret>}; }";
    doTestInclude(source, "s");
  }

  public void testDoNotCompleteVariableTwice() {
    doCheckResult("package t; func _() {\n err := 1; for { err := 1; return er<caret> } }", 
                  "package t; func _() {\n err := 1; for { err := 1; return err<caret> } }"); 
  }

  private void doTestEmptyCompletion() {
    myFixture.testCompletionVariants(getTestName(true) + ".go");
  }
}
