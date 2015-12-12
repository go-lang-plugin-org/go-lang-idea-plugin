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

package com.goide.inspections;

import com.goide.GoCodeInsightFixtureTestCase;
import com.goide.inspections.unresolved.*;
import com.goide.project.GoModuleLibrariesService;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.util.ThrowableComputable;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.testFramework.LightProjectDescriptor;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class GoHighlightingTest extends GoCodeInsightFixtureTestCase {
  @Override
  public void setUp() throws Exception {
    super.setUp();
    setUpProjectSdk();
    myFixture.enableInspections(
      GoUnresolvedReferenceInspection.class,
      GoDuplicateFieldsOrMethodsInspection.class,
      GoUnusedImportInspection.class,
      GoUnusedVariableInspection.class,
      GoUnusedConstInspection.class,
      GoUnusedGlobalVariableInspection.class,
      GoUnusedFunctionInspection.class,
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
      GoMultiplePackagesInspection.class,
      GoCgoInTestInspection.class,
      GoTestSignaturesInspection.class,
      GoAssignmentNilWithoutExplicitTypeInspection.class,
      GoRedeclareImportAsFunctionInspection.class,
      GoStructTagInspection.class,
      GoUsedAsValueInCondition.class,
      GoDeferInLoop.class,
      GoCommentStartInspection.class
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

  public void testRelativeImportIgnoringDirectories() throws IOException {
    myFixture.getTempDirFixture().findOrCreateDir("to_import/testdata");
    myFixture.getTempDirFixture().findOrCreateDir("to_import/.name");
    myFixture.getTempDirFixture().findOrCreateDir("to_import/_name");
    doTest();
  }
  
  public void testImportWithSlashAtTheEnd() throws Throwable {
    VirtualFile file = WriteCommandAction.runWriteCommandAction(myFixture.getProject(), new ThrowableComputable<VirtualFile, Throwable>() {
      @NotNull
      @Override
      public VirtualFile compute() throws Throwable {
        myFixture.getTempDirFixture().createFile("a/pack/pack.go", "package pack; func Foo() {}");
        return myFixture.getTempDirFixture().createFile("pack2/pack2.go",
                                                        "package main; import \"a/pack/<error descr=\"Cannot resolve file ''\"></error>\"; import \"../a/pack/\"; func main() { pack.Foo() }");
      }
    });
    GoModuleLibrariesService.getInstance(myFixture.getModule()).setLibraryRootUrls(file.getParent().getParent().getUrl());
    myFixture.configureFromExistingVirtualFile(file);
    myFixture.checkHighlighting();
  }

  public void testDoNotReportNonLastMultiResolvedImport() throws IOException {
    final VirtualFile root1 = myFixture.getTempDirFixture().findOrCreateDir("root1");
    final VirtualFile root2 = myFixture.getTempDirFixture().findOrCreateDir("root2");

    myFixture.getTempDirFixture().createFile("root1/src/to_import/unique/foo.go", "package unique; func Foo() {}");
    myFixture.getTempDirFixture().findOrCreateDir("root1/src/to_import/shared");
    myFixture.getTempDirFixture().findOrCreateDir("root2/src/to_import/shared");
    GoModuleLibrariesService.getInstance(myFixture.getModule()).setLibraryRootUrls(root1.getUrl(), root2.getUrl());
    doTest();
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
    myFixture.configureByText("b_test.go", "package i_test; import ( \".\" ); func <warning>f</warning>() { print(i.P{}.<error>v1</error>) }");
    myFixture.checkHighlighting();
  }

  public void testDuplicateFunctionsInOnePackage() {
    myFixture.configureByText("a.go", "package foo; func init() {bar()}; func bar() {};");
    myFixture.configureByText("b.go", "//+build appengine\n\npackage foo; func init() {buzz()}; func buzz() {}");
    myFixture.configureByText("c.go", "package foo; func init() {bar(); buzz();}; func <error descr=\"Duplicate function name\">bar</error>() {}; func <error descr=\"Duplicate function name\">buzz</error>() {}");
    myFixture.checkHighlighting();
  }

  public void testDuplicateFunctionsInDifferentPackages() {
    myFixture.configureByText("a.go", "package foo; func init() {bar()}; func bar() {};");
    myFixture.configureByText("b_test.go", "package foo_test; func init() {bar(); buzz();}; func bar() {}; func buzz() {}");
    myFixture.checkHighlighting();
  }

  public void testDoNotSearchFunctionDuplicatesForNotTargetMatchingFiles() {
    myFixture.configureByText("a.go", "//+build appengine\n\npackage foo; func init() {buzz()}; func buzz() {}");
    myFixture.configureByText("b.go", "//+build appengine\n\npackage foo; func init() {buzz()}; func buzz() {}");
    myFixture.checkHighlighting();
  }

  public void testDuplicateMethodsInOnePackage() {
    myFixture.configureByText("a.go", "package main; type Foo int; func (f Foo) bar(a, b string) {}");
    myFixture.configureByText("b.go", "//+build appengine\n\npackage main; func (a *Foo) bar() {};func (a *Foo) buzz() {}");
    myFixture.configureByText("c.go", "package main; func (a *Foo) <error>bar</error>() {};func (a *Foo) buzz() {}");
    myFixture.checkHighlighting();
  }

  public void testDoNotSearchMethodDuplicatesForNotTargetMatchingFiles() {
    myFixture.configureByText("a.go", "package main; type Foo int; func (f Foo) bar(a, b string) {}");
    myFixture.configureByText("b.go", "//+build appengine\n\npackage main; func (a *Foo) bar() {}");
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

  public void testDuplicateBuiltinFunction() {
    myFixture.configureByText("a.go", "package main; func main() {new()}; func <warning descr=\"Function 'new' collides with builtin function\">new</warning>() {}");
    myFixture.checkHighlighting();
  }

  public void testDuplicateBuiltinType() {
    myFixture.configureByText("a.go", "package main; func main() {<warning descr=\"Variable 'string' collides with builtin type\">string</warning> := 3; _ = string}");
    myFixture.checkHighlighting();
  }

  public void testUseNilWithoutExplicitType() {
    myFixture.configureByText("a.go", "package main; func main() { var x string = nil; _ = x; var y = <error>nil</error>; _ = y}");
    myFixture.checkHighlighting();
  }

  public void testPackageWithTestPrefix() throws Throwable {
    VirtualFile file = WriteCommandAction.runWriteCommandAction(myFixture.getProject(), new ThrowableComputable<VirtualFile, Throwable>() {
      @NotNull
      @Override
      public VirtualFile compute() throws Throwable {
        myFixture.getTempDirFixture().createFile("pack1/pack1_test.go", "package pack1_test; func Test() {}");
        return myFixture.getTempDirFixture().createFile("pack2/pack2_test.go",
                                                        "package pack2_test; import \"testing\"; func TestTest(t *testing.T) {<error>pack1_test</error>.Test()}");
      }
    });
    GoModuleLibrariesService.getInstance(myFixture.getModule()).setLibraryRootUrls(file.getParent().getParent().getUrl());
    myFixture.configureFromExistingVirtualFile(file);
    myFixture.checkHighlighting();
  }

  public void testPackageWithTestPrefixNotInsideTestFile() throws Throwable {
    VirtualFile file = WriteCommandAction.runWriteCommandAction(myFixture.getProject(), new ThrowableComputable<VirtualFile, Throwable>() {
      @NotNull
      @Override
      public VirtualFile compute() throws Throwable {
        myFixture.getTempDirFixture().createFile("pack1/pack1.go", "package pack1_test; func Test() {}");
        return myFixture.getTempDirFixture().createFile("pack2/pack2_test.go",
                                                        "package pack2_test; import `pack1`; import \"testing\"; func TestTest(t *testing.T) {pack1_test.Test()}");
      }
    });
    GoModuleLibrariesService.getInstance(myFixture.getModule()).setLibraryRootUrls(file.getParent().getParent().getUrl());
    myFixture.configureFromExistingVirtualFile(file);
    myFixture.checkHighlighting();
  }


  public void testMultiplePackages() {
    myFixture.addFileToProject("a.go", "package a");
    myFixture.configureByText("b.go", "<error>package b</error>");
    myFixture.checkHighlighting();
  }

  public void testMultiplePackagesWithIgnoredFile() {
    myFixture.addFileToProject("a.go", "// +build ignored\n\npackage a");
    // Should be OK to have package b because package a has a non-matching
    // build tag.
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

  public void testDeferInLoop() {
    myFixture.testHighlighting(true, false, true, getTestName(true) + ".go");
  }

  public void testCommentStart() {
    myFixture.testHighlighting(true, false, true, getTestName(true) + ".go");
  }

  public void testDoNotHighlightCommentOfMainPackage() {
    myFixture.configureByText("a.go", "// Some comment\npackage main; func main() {}");
    myFixture.checkHighlighting(true, false, true);
  }

  public void testCGOImportInNonTestFile() {
    myFixture.configureByText("a.go", "package a; import \"C\"");
    myFixture.checkHighlighting();
  }

  public void testDuplicatePackageAlias() throws Throwable {
    VirtualFile file = WriteCommandAction.runWriteCommandAction(myFixture.getProject(), new ThrowableComputable<VirtualFile, Throwable>() {
      @NotNull
      @Override
      public VirtualFile compute() throws Throwable {
        myFixture.getTempDirFixture().createFile("pack1/pack1.go", "package pack1; func Foo() {}");
        myFixture.getTempDirFixture().createFile("pack2/pack2.go", "package pack2");
        return myFixture.getTempDirFixture().createFile("pack3/pack3.go",
                                                        "package main; import p \"pack1\"; import <error>p \"pack2\"</error>; func main() { p.Foo() }");
      }
    });
    GoModuleLibrariesService.getInstance(myFixture.getModule()).setLibraryRootUrls(file.getParent().getParent().getUrl());
    myFixture.configureFromExistingVirtualFile(file);
    myFixture.checkHighlighting();
  }

  public void testDuplicatePackageImport() throws Throwable {
    VirtualFile file = WriteCommandAction.runWriteCommandAction(myFixture.getProject(), new ThrowableComputable<VirtualFile, Throwable>() {
      @NotNull
      @Override
      public VirtualFile compute() throws Throwable {
        myFixture.getTempDirFixture().createFile("pack/pack1.go", "package pack; func Foo() {}");
        return myFixture.getTempDirFixture().createFile("pack3/pack3.go",
                                                        "package main; import \"pack\"; import <error>\"pack\"</error>; func main() { pack.Foo() }");
      }
    });
    GoModuleLibrariesService.getInstance(myFixture.getModule()).setLibraryRootUrls(file.getParent().getParent().getUrl());
    myFixture.configureFromExistingVirtualFile(file);
    myFixture.checkHighlighting();
  }

  public void testDuplicateFinalPackageComponent() throws Throwable {
    VirtualFile file = WriteCommandAction.runWriteCommandAction(myFixture.getProject(), new ThrowableComputable<VirtualFile, Throwable>() {
      @NotNull
      @Override
      public VirtualFile compute() throws Throwable {
        myFixture.getTempDirFixture().createFile("a/pack/pack1.go", "package pack; func Foo() {}");
        myFixture.getTempDirFixture().createFile("b/pack/pack2.go", "package pack");
        return myFixture.getTempDirFixture().createFile("pack3/pack3.go",
                                                        "package main; import \"a/pack\"; import <error>\"b/pack\"</error>; func main() { pack.Foo() }");
      }
    });
    GoModuleLibrariesService.getInstance(myFixture.getModule()).setLibraryRootUrls(file.getParent().getParent().getUrl());
    myFixture.configureFromExistingVirtualFile(file);
    myFixture.checkHighlighting();
  }

  public void testIgnoredBuildTag() throws Throwable {
    VirtualFile file = WriteCommandAction.runWriteCommandAction(myFixture.getProject(), new ThrowableComputable<VirtualFile, Throwable>() {
      @NotNull
      @Override
      public VirtualFile compute() throws Throwable {
        myFixture.getTempDirFixture().createFile("a/pack1.go", "package a; func Foo() {}");
        myFixture.getTempDirFixture().createFile("a/pack2.go", "// +build ignored\n\npackage main");
        myFixture.getTempDirFixture().createFile("b/pack1.go", "package b; func Bar() {}");
        myFixture.getTempDirFixture().createFile("b/pack2.go", "// +build ignored\n\npackage main");
        // There should be no errors: package main exists in the a/ and b/
        // directories, but it is not imported as it has a non-matching build
        // tag.
        // For more details see https://github.com/go-lang-plugin-org/go-lang-idea-plugin/issues/1858#issuecomment-139794391.
        return myFixture.getTempDirFixture().createFile("c/pack1.go",
                                                        "package main; import \"a\"; import \"b\"; func main() { a.Foo(); b.Bar(); }");
      }
    });
    GoModuleLibrariesService.getInstance(myFixture.getModule()).setLibraryRootUrls(file.getParent().getParent().getUrl());
    myFixture.configureFromExistingVirtualFile(file);
    myFixture.checkHighlighting();
  }

  public void testImportUnderscore() throws Throwable {
    VirtualFile file = WriteCommandAction.runWriteCommandAction(myFixture.getProject(), new ThrowableComputable<VirtualFile, Throwable>() {
      @NotNull
      @Override
      public VirtualFile compute() throws Throwable {
        myFixture.getTempDirFixture().createFile("a/pack/pack1.go", "package pack; func Foo() {}");
        myFixture.getTempDirFixture().createFile("b/pack/pack2.go", "package pack");
        myFixture.getTempDirFixture().createFile("c/pack/pack3.go", "package whatever; func Bar() {}");
        myFixture.getTempDirFixture().createFile("d/pack/pack4.go", "package another; func Baz() {}");
        return myFixture.getTempDirFixture().createFile("pack3/pack3.go",
                                                        "package main; import _ \"a/pack\"; import _ \"b/pack\"; import . \"c/pack\"; import . \"d/pack\"; func main() { Bar(); Baz() }");
      }
    });
    GoModuleLibrariesService.getInstance(myFixture.getModule()).setLibraryRootUrls(file.getParent().getParent().getUrl());
    myFixture.configureFromExistingVirtualFile(file);
    myFixture.checkHighlighting();
  }

  @Override
  protected LightProjectDescriptor getProjectDescriptor() {
    return createMockProjectDescriptor();
  }
}
