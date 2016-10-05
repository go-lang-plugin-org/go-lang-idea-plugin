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

package com.goide.inspections;

import com.goide.GoCodeInsightFixtureTestCase;
import com.goide.SdkAware;
import com.goide.codeInsight.imports.GoImportOptimizerTest;
import com.goide.inspections.unresolved.*;
import com.goide.project.GoModuleLibrariesService;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

@SdkAware
public class GoHighlightingTest extends GoCodeInsightFixtureTestCase {
  @Override
  public void setUp() throws Exception {
    super.setUp();
    myFixture.enableInspections(
      GoUnresolvedReferenceInspection.class,
      GoDuplicateFieldsOrMethodsInspection.class,
      GoUnusedImportInspection.class,
      GoUnusedVariableInspection.class,
      GoUnusedConstInspection.class,
      GoUnusedGlobalVariableInspection.class,
      GoUnusedFunctionInspection.class,
      GoUnusedExportedFunctionInspection.class,
      GoAssignmentToConstantInspection.class,
      GoDuplicateFunctionOrMethodInspection.class,
      GoDuplicateArgumentInspection.class,
      GoDuplicateReturnArgumentInspection.class,
      GoFunctionVariadicParameterInspection.class,
      GoVarDeclarationInspection.class,
      GoNoNewVariablesInspection.class,
      GoMissingReturnInspection.class,
      GoFunctionCallInspection.class,
      GoDeferGoInspection.class,
      GoReservedWordUsedAsNameInspection.class,
      GoImportUsedAsNameInspection.class,
      GoMultiplePackagesInspection.class,
      GoCgoInTestInspection.class,
      GoTestSignaturesInspection.class,
      GoAssignmentNilWithoutExplicitTypeInspection.class,
      GoRedeclareImportAsFunctionInspection.class,
      GoStructTagInspection.class,
      GoUsedAsValueInCondition.class,
      GoDeferInLoopInspection.class,
      GoCommentStartInspection.class,
      GoPlaceholderCountInspection.class,
      GoEmbeddedInterfacePointerInspection.class,
      GoUnderscoreUsedAsValueInspection.class,
      GoRangeIterationOnIllegalTypeInspection.class,
      GoUnusedParameterInspection.class,
      GoDirectAssignToStructFieldInMapInspection.class,
      GoInfiniteForInspection.class,
      GoAssignmentToReceiverInspection.class,
      GoInvalidStringOrCharInspection.class,
      GoMixedNamedUnnamedParametersInspection.class,
      GoAnonymousFieldDefinitionTypeInspection.class,
      GoStringAndByteTypeMismatchInspection.class
    );
  }

  private void doTest() {
    myFixture.testHighlighting(true, false, false, getTestName(true) + ".go");
  }

  @NotNull
  @Override
  protected String getBasePath() {
    return "highlighting";
  }

  @Override
  protected boolean isWriteActionRequired() {
    return false;
  }

  public void testSimple()                    { doTest(); }
  public void testLabels()                    { doTest(); }
  public void testStruct()                    { doTest(); }
  public void testBoxes()                     { doTest(); }
  public void testRanges()                    { doTest(); }
  public void testSelector()                  { doTest(); }
  public void testComposite()                 { doTest(); }
  public void testVars()                      { doTest(); }
  public void testRecv()                      { doTest(); }
  public void testPointers()                  { doTest(); }
  public void testSlices()                    { doTest(); }
  public void testShortVars()                 { doTest(); }
  public void testReturns()                   { doTest(); }
  public void testRequest()                   { doTest(); }
  public void testStop()                      { doTest(); }
  public void testVarBlocks()                 { doTest(); }
  public void testBlankImport()               { doTest(); }
  public void testVariadic()                  { doTest(); }
  public void testCheck()                     { doTest(); }
  public void testCheck_test()                { doTest(); }
  public void testFuncCall()                  { doTest(); }
  public void testBuiltinFuncCalls()          { doTest(); }
  public void testBackticks()                 { doTest(); }
  public void testConsts()                    { doTest(); }
  public void testFields()                    { doTest(); }
  public void testBlankFields()               { doTest(); }
  public void testFuncLiteral()               { doTest(); }
  public void testTypeLiterals()              { doTest(); }
  public void testFuncType()                  { doTest(); }
  public void testTemplates()                 { doTest(); }
  public void testInterfaces()                { doTest(); }
  public void testReceiverType()              { doTest(); }
  public void testForRange()                  { doTest(); }
  public void testMismatch()                  { doTest(); }
  public void testStubParams()                { doTest(); }
  public void testNil()                       { doTest(); }
  public void testAssignUsages()              { doTest(); }
  public void testMethodExpr()                { doTest(); }
  public void testVarToImport()               { doTest(); }
  public void testCgotest()                   { doTest(); }
  public void testRedeclaredImportAsFunction(){ doTest(); }
  public void testStructTags()                { doTest(); }
  public void testContinue()                  { doTest(); }
  public void testBreak()                     { doTest(); }
  public void testEqualinif()                 { doTest(); }
  public void testSpecTypes()                 { doTest(); }
  public void testFunctionTypes()             { doTest(); }
  public void testGithubIssue2099()           { doTest(); }
  public void testCyclicDefinition()          { doTest(); }
  public void testEmbeddedInterfacePointer()  { doTest(); }
  public void testPlaceholderCount()          { doTest(); }
  public void testPlaceholderCountVet()       { doTest(); }
  public void testTypeConversion()            { doTest(); }
  public void testInit()                      { doTest(); }
  public void testMainWithWrongSignature()    { doTest(); }
  public void testChan()                      { doTest(); }
  public void testIota()                      { doTest(); }
  public void testIota2()                     { doTest(); }
  public void testUnaryPointer()              { doTest(); }
  public void testUnaryMinus()                { doTest(); }
  public void testFileRead()                  { doTest(); }
  public void testLiteralValues()             { doTest(); }
  public void testUnderscoreUsedAsValue()     { doTest(); }
  public void testUnusedParameter()           { doTest(); }
  public void testUnusedParameter_test()      { doTest(); }
  public void testVoidFunctionUsedAsValue()   { doTest(); }
  public void testIndexedStringAssign()       { doTest(); }
  public void testStringSliceWithThirdIndex() { doTest(); }
  public void testSliceWithThirdIndex()       { doTest(); }
  public void testAssignToStructFieldInMap()  { doTest(); }
  public void testInfiniteFor()               { doTest(); }
  public void testGh2147()                    { doTest(); }
  public void testAssignmentToReceiver()      { doTest(); }
  public void testReservedWordUsedAsName()    { doTest(); }
  public void testImportUsedAsName()          { doTest(); }
  public void testMixedNamedUnnamedParameters() { doTest(); }
  public void testStringInStructSliceWithThirdIndex() { doTest(); }
  public void testAnonymousFieldDefinition()  { doTest(); }
  public void testStringIndexIsByte()         { doTest(); }

  public void testCodedImportString() {
    myFixture.addFileToProject("a/a.go", "package a\n const A = 3");
    PsiFile file = myFixture.addFileToProject("b/b.go", "package b\n import \"\\u0061\" \n" +
                                                        "const <warning descr=\"Unused constant 'my'\">my</warning> = a.A");
    myFixture.configureFromExistingVirtualFile(file.getVirtualFile());
    myFixture.checkHighlighting();
  }

  public void testDoNotResolveReceiverTypeToFunction() { 
    myFixture.addFileToProject("pack1/a.go", "package foo; func functionInCurrentPackage() {}");
    PsiFile file = myFixture.addFileToProject("pack1/b.go", 
                                              "package foo; func (<error descr=\"Unresolved type 'functionInCurrentPackage'\">functionInCurrentPackage</error>) method() {}");
    myFixture.configureFromExistingVirtualFile(file.getVirtualFile());
    myFixture.checkHighlighting();
  }

  public void testAvoidDuplicatedUnusedImportReports() {
    myFixture.addFileToProject("pack1/a.go", "package foo;");
    myFixture.addFileToProject("pack1/b.go", "package foo_test;");
    myFixture.addFileToProject("pack2/a.go", "package foo;");
    myFixture.addFileToProject("pack3/a.go", "package foo; func Hi() {}");
    doTest();
  }

  public void testCheckSamePackage_test() {
    myFixture.configureByText("a_test.go", "package check; func TestStringer(t *testing.T) {}");
    doTest();
  }

  public void testRelativeImportIgnoringDirectories() throws IOException {
    myFixture.getTempDirFixture().findOrCreateDir("to_import/testdata");
    myFixture.getTempDirFixture().findOrCreateDir("to_import/.name");
    myFixture.getTempDirFixture().findOrCreateDir("to_import/_name");
    doTest();
  }

  public void testImportWithSlashAtTheEnd() {
    myFixture.addFileToProject("a/pack/pack.go", "package pack; func Foo() {}");
    PsiFile file = myFixture.addFileToProject("pack2/pack2.go",
                                              "package main; import \"a/pack/<error descr=\"Cannot resolve file ''\"></error>\"; import \"../a/pack/\"; func main() { pack.Foo() }");
    myFixture.configureFromExistingVirtualFile(file.getVirtualFile());
    myFixture.checkHighlighting();
  }

  @SuppressWarnings("ConstantConditions")
  public void testDoNotReportNonLastMultiResolvedImport() {
    myFixture.addFileToProject("root1/src/to_import/unique/foo.go", "package unique; func Foo() {}");
    myFixture.addFileToProject("root1/src/to_import/shared/a.go", "package shared");
    myFixture.addFileToProject("root2/src/to_import/shared/a.go", "package shared");
    GoModuleLibrariesService.getInstance(myFixture.getModule()).setLibraryRootUrls(myFixture.findFileInTempDir("root1").getUrl(),
                                                                                   myFixture.findFileInTempDir("root2").getUrl());
    doTest();

    PsiReference reference = myFixture.getFile().findReferenceAt(myFixture.getCaretOffset());
    PsiElement resolve = reference.resolve();
    assertInstanceOf(resolve, PsiDirectory.class);
    assertTrue(((PsiDirectory)resolve).getVirtualFile().getPath().endsWith("root1/src/to_import/shared"));

    GoModuleLibrariesService.getInstance(myFixture.getModule()).setLibraryRootUrls(myFixture.findFileInTempDir("root2").getUrl(),
                                                                                   myFixture.findFileInTempDir("root1").getUrl());
    reference = myFixture.getFile().findReferenceAt(myFixture.getCaretOffset());
    resolve = reference.resolve();
    assertInstanceOf(resolve, PsiDirectory.class);
    assertTrue(((PsiDirectory)resolve).getVirtualFile().getPath().endsWith("root2/src/to_import/shared"));
  }

  public void testLocalScope() {
    myFixture.configureByText("a.go", "package foo; func bar() {}");
    myFixture.configureByText("b.go", "package foo; func init(){bar()}");
    myFixture.checkHighlighting();
  }

  public void testInnerTypesFromOtherPackage() {
    myFixture.configureByText("main.go", "package main; import \"io\"; type Outer struct { *io.LimitedReader };" +
                                         "func main() { _ = &Outer{LimitedReader: &io.LimitedReader{}, } }");
    myFixture.checkHighlighting();
  }

  public void testNoLocalResolveForTest() {
    myFixture.configureByText("a.go", "package i; type P struct { v1 int }");
    myFixture
      .configureByText("b_test.go", "package i_test; import ( \".\" ); func <warning>f</warning>() { print(i.P{}.<error>v1</error>) }");
    myFixture.checkHighlighting();
  }

  public void testDuplicateFunctionsInOnePackage() {
    myFixture.configureByText("a.go", "package foo; func init() {bar()}; func bar() {};");
    myFixture.configureByText("b.go", "//+build appengine\n\npackage foo; func init() {buzz()}; func buzz() {}");
    myFixture.configureByText("c.go",
                              "package foo; func init() {bar(); buzz();}; func <error descr=\"Duplicate function name\">bar</error>() {}; func buzz() {}");
    myFixture.checkHighlighting();
  }

  public void testDuplicateFunctionsInDifferentPackages() {
    myFixture.configureByText("a.go", "package foo; func init() {bar()}; func bar() {};");
    myFixture.configureByText("b_test.go", "package foo_test; func init() {bar(); buzz();}; func bar() {}; func buzz() {}");
    myFixture.checkHighlighting();
  }

  public void testDoNotSearchFunctionDuplicatesForNotTargetMatchingFiles() {
    myFixture.configureByText("a.go", "//+build appengine\n\npackage foo; func init() {buzz()}; func buzz() {}");
    myFixture.configureByText("b.go", "package foo; func init() {buzz()}; func buzz() {}");
    myFixture.checkHighlighting();
  }

  public void testDuplicateMethodsInOnePackage() {
    myFixture.configureByText("a.go", "package main; type Foo int; func (f Foo) bar(a, b string) {}");
    myFixture.configureByText("b.go", "//+build appengine\n\npackage main; func (a *Foo) bar() {};func (a *Foo) buzz() {}");
    myFixture.configureByText("c.go", "package main; func (a *Foo) <error>bar</error>() {};func (a *Foo) buzz() {}");
    myFixture.checkHighlighting();
  }

  public void testDoNotSearchMethodDuplicatesForNotTargetMatchingFiles() {
    myFixture.configureByText("b.go", "//+build appengine\n\npackage main; func (a *Foo) bar() {}");
    myFixture.configureByText("a.go", "package main; type Foo int; func (f Foo) bar(_, _ string) {}");
    myFixture.checkHighlighting();
  }

  public void testNoDuplicateMethodsInOnePackage() {
    myFixture.configureByText("a.go", "package main; type Foo int; func (f Foo) bar() {}");
    myFixture.configureByText("b.go", "package main; type Baz int; func (f Baz) bar() {}");
    myFixture.checkHighlighting();
  }

  public void testInitInOnePackage() {
    myFixture.configureByText("a.go", "package foo; func init() {bar()}; func bar() {}");
    myFixture.configureByText("b.go", "package foo; func init() {bar()}; func <error>bar</error>() {}; func init() {}");
    myFixture.checkHighlighting();
  }

  public void testMainInFooPackage() {
    myFixture.configureByText("a.go", "package foo; func main() {bar()}; func bar() {}");
    myFixture.configureByText("b.go", "package foo; func <error>main</error>() {bar()}; func <error>bar</error>() {}");
    myFixture.checkHighlighting();
  }

  public void testMainInMainPackage() {
    myFixture.configureByText("a.go", "package main; func main() {bar()}; func bar() {}");
    myFixture.configureByText("b.go", "package main; func main() {bar()}; func <error>bar</error>() {}");
    myFixture.checkHighlighting();
  }

  public void testUseNilWithoutExplicitType() {
    myFixture.configureByText("a.go", "package main; func main() { var x string = nil; _ = x; var y = <error>nil</error>; _ = y}");
    myFixture.checkHighlighting();
  }

  public void testPackageWithTestPrefix() {
    myFixture.addFileToProject("pack1/pack1_test.go", "package pack1_test; func Test() {}");
    PsiFile file = myFixture.addFileToProject("pack2/pack2_test.go",
                                              "package pack2_test; import \"testing\"; func TestTest(*testing.T) {<error>pack1_test</error>.Test()}");
    myFixture.configureFromExistingVirtualFile(file.getVirtualFile());
    myFixture.checkHighlighting();
  }

  public void testMethodOnNonLocalType() {
    VirtualFile file = myFixture.copyFileToProject(getTestName(true) + ".go", "method/nonlocaltype.go");
    myFixture.configureFromExistingVirtualFile(file);
    GoImportOptimizerTest.resolveAllReferences(myFixture.getFile());
    myFixture.checkHighlighting();
  }

  public void testMethodOnNonLocalTypeInTheSameDirectory() {
    myFixture.addFileToProject("method/foo.go", "package a; type H struct {}");
    PsiFile psiFile = myFixture.addFileToProject("method/foo_test.go", "package a_test;\n" +
                                                                       "func (<error descr=\"Unresolved type 'H'\">H</error>) Hello() {}");
    myFixture.configureFromExistingVirtualFile(psiFile.getVirtualFile());
    myFixture.checkHighlighting();
  }

  public void testPackageWithTestPrefixNotInsideTestFile() {
    myFixture.addFileToProject("pack1/pack1.go", "package pack1_test; func Test() {}");
    PsiFile file = myFixture.addFileToProject("pack2/pack2_test.go",
                                              "package pack2_test; import `pack1`; import \"testing\"; func TestTest(*testing.T) {pack1_test.Test()}");
    myFixture.configureFromExistingVirtualFile(file.getVirtualFile());
    myFixture.checkHighlighting();
  }

  public void testBlankPackage() {
    myFixture.configureByText("a.go", "package <error descr=\"Invalid package name\">_</error>");
    myFixture.checkHighlighting();
  }

  public void testMultiplePackages() {
    myFixture.addFileToProject("a.go", "package a");
    myFixture.configureByText("b.go", "<error>package b</error>");
    myFixture.checkHighlighting();
  }

  public void testMultiplePackagesWithIgnoredFile() {
    myFixture.addFileToProject("a.go", "// +build ignored\n\npackage a");
    myFixture.addFileToProject(".c.go", "// package a");
    myFixture.addFileToProject("_c.go", "// package a");
    // Should be OK to have package b because package a has a non-matching
    // build tag.
    myFixture.configureByText("b.go", "package b");
    myFixture.checkHighlighting();
  }

  public void testMultiplePackagesWithDocumentationPackage() {
    myFixture.addFileToProject("a.go", "package documentation");
    myFixture.configureByText("b.go", "package b");
    myFixture.checkHighlighting();
  }

  public void testDocumentationPackage() {
    myFixture.addFileToProject("a.go", "package a");
    myFixture.configureByText("docs.go", "package documentation");
    myFixture.checkHighlighting();
  }

  public void testTestPackage() {
    myFixture.addFileToProject("a.go", "package a");
    myFixture.configureByText("a_test.go", "package a_test");
    myFixture.checkHighlighting();
  }

  public void testCGOImportInTestFile() {
    myFixture.configureByText("a_test.go", "package a; import<error>\"C\"</error>; import<error>\"C\"</error>;");
    myFixture.checkHighlighting();
  }

  public void testDeferInLoop() { doWeakTest(); }
  public void testDeferGo()     { doWeakTest(); }

  public void testCommentStart() { doWeakTest(); }

  private long doWeakTest() {return myFixture.testHighlighting(true, false, true, getTestName(true) + ".go");}

  public void testDoNotHighlightCommentOfMainPackage() {
    myFixture.configureByText("a.go", "// Some comment\npackage main; func main() {}");
    myFixture.checkHighlighting(true, false, true);
  }

  public void testCGOImportInNonTestFile() {
    myFixture.configureByText("a.go", "package a; import \"C\"");
    myFixture.checkHighlighting();
  }

  public void testVendoringImportPaths() {
    myFixture.addFileToProject("vendor/vendoringPackage/v.go", "package vendoringPackage; func Hello() {}");
    myFixture.addFileToProject("subPackage/vendor/subVendoringPackage/v.go", "package subVendoringPackage; func Hello() {}");
    doTest();
  }

  public void testVendoredBuiltinImport() {
    myFixture.addFileToProject("vendor/builtin/builtin.go", "package builtin; func Hello() {}");
    myFixture.configureByText("a.go", "package a; import _ `builtin`");
    myFixture.checkHighlighting();
  }

  public void testDuplicatePackageAlias() {
    myFixture.addFileToProject("pack1/pack1.go", "package pack1; func Foo() {}");
    myFixture.addFileToProject("pack2/pack2.go", "package pack2");
    PsiFile file = myFixture.addFileToProject("pack3/pack3.go",
                                              "package main; import p \"pack1\"; import <error>p \"pack2\"</error>; func main() { p.Foo() }");
    myFixture.configureFromExistingVirtualFile(file.getVirtualFile());
    myFixture.checkHighlighting();
  }

  public void testDuplicatePackageImport() {
    myFixture.addFileToProject("pack/pack1.go", "package pack; func Foo() {}");
    PsiFile file = myFixture.addFileToProject("pack3/pack3.go",
                                              "package main; import \"pack\"; import <error>\"pack\"</error>; func main() { pack.Foo() }");
    myFixture.configureFromExistingVirtualFile(file.getVirtualFile());
    myFixture.checkHighlighting();
  }

  public void testDuplicateFinalPackageComponent() {
    myFixture.addFileToProject("a/pack/pack1.go", "package pack; func Foo() {}");
    myFixture.addFileToProject("b/pack/pack2.go", "package pack");
    PsiFile file = myFixture.addFileToProject("pack3/pack3.go", "package main; import \"a/pack\"\n" +
                                                                "import <error>\"b/pack\"</error>\n" +
                                                                "func main() { pack.Foo() }");
    myFixture.configureFromExistingVirtualFile(file.getVirtualFile());
    myFixture.checkHighlighting();
  }

  public void testIgnoredBuildTag() {
    myFixture.addFileToProject("a/pack1.go", "package a; func Foo() {}");
    myFixture.addFileToProject("a/pack2.go", "// +build ignored\n\npackage main");
    myFixture.addFileToProject("b/pack1.go", "package b; func Bar() {}");
    myFixture.addFileToProject("b/pack2.go", "// +build ignored\n\npackage main");
    // There should be no errors: package main exists in the a/ and b/
    // directories, but it is not imported as it has a non-matching build tag. 
    // For more details see https://github.com/go-lang-plugin-org/go-lang-idea-plugin/issues/1858#issuecomment-139794391.
    PsiFile file = myFixture.addFileToProject("c/pack1.go", "package main; import \"a\"; import \"b\"; func main() { a.Foo(); b.Bar(); }");
    myFixture.configureFromExistingVirtualFile(file.getVirtualFile());
    myFixture.checkHighlighting();
  }

  public void testImportUnderscore() {
    myFixture.addFileToProject("a/pack/pack1.go", "package pack; func Foo() {}");
    myFixture.addFileToProject("b/pack/pack2.go", "package pack");
    myFixture.addFileToProject("c/pack/pack3.go", "package whatever; func Bar() {}");
    myFixture.addFileToProject("d/pack/pack4.go", "package another; func Baz() {}");
    PsiFile file = myFixture.addFileToProject("pack3/pack3.go",
                                              "package main; import _ \"a/pack\"; import _ \"b/pack\"; import . \"c/pack\"; import . \"d/pack\"; func main() { Bar(); Baz() }");
    myFixture.configureFromExistingVirtualFile(file.getVirtualFile());
    myFixture.checkHighlighting();
  }
}
