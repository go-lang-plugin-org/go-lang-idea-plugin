/*
 * Copyright 2013-2015 Sergey Ignatov, Alexander Zolotov, Mihai Toader, Florin Patan
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

import java.io.IOException;

public class GoHighlightingTest extends GoCodeInsightFixtureTestCase {
  @Override
  public void setUp() throws Exception {
    super.setUp();
    setUpProjectSdk();
    myFixture.enableInspections(
      GoUnresolvedReferenceInspection.class,
      GoDuplicateFieldsOrMethodsInspection.class,
      GoUnusedVariableInspection.class,
      GoUnusedGlobalVariableInspection.class,
      GoUnusedFunctionInspection.class,
      GoAssignmentToConstantInspection.class,
      GoDuplicateFunctionInspection.class,
      GoDuplicateMethodInspection.class,
      GoDuplicateArgumentInspection.class,
      GoDuplicateReturnArgumentInspection.class,
      GoFunctionVariadicParameterInspection.class,
      GoVarDeclarationInspection.class,
      GoNoNewVariablesInspection.class,
      GoReturnInspection.class,
      GoFunctionCallInspection.class,
      GoDeferGoInspection.class,
      GoReservedWordUsedAsName.class,
      GoMultiplePackagesInspection.class
    );
  }

  private void doTest() {
    myFixture.testHighlighting(true, false, false, getTestName(true) + ".go");
  }

  @Override
  protected String getBasePath() {
    return "highlighting";
  }

  @Override
  protected boolean isWriteActionRequired() {
    return false;
  }

  public void testSimple()      { doTest(); }
  public void testStruct()      { doTest(); }
  public void testBoxes()       { doTest(); }
  public void testRanges()      { doTest(); }
  public void testSelector()    { doTest(); }
  public void testComposite()   { doTest(); }
  public void testVars()        { doTest(); }
  public void testRecv()        { doTest(); }
  public void testPointers()    { doTest(); }
  public void testSlices()      { doTest(); }
  public void testShortVars()   { doTest(); }
  public void testReturns()     { doTest(); }
  public void testRequest()     { doTest(); }
  public void testStop()        { doTest(); }
  public void testVarBlocks()   { doTest(); }
  public void testBlankImport() { doTest(); }
  public void testVariadic()    { doTest(); }
  public void testCheck()       { doTest(); }
  public void testCheck_test()  { doTest(); }
  public void testFuncCall()    { doTest(); }
  public void testBackticks()   { doTest(); }
  public void testConsts()      { doTest(); }
  public void testFields()      { doTest(); }
  public void testFuncLiteral() { doTest(); }
  public void testTypeLiterals(){ doTest(); }
  public void testFuncType()    { doTest(); }
  public void testTemplates()   { doTest(); }
  public void testInterfaces()  { doTest(); }
  public void testReceiverType(){ doTest(); }
  public void testForRange()    { doTest(); }
  public void testMismatch()    { doTest(); }
  public void testStubParams()  { doTest(); }

  public void testRelativeImportIgnoringDirectories() throws IOException {
    myFixture.getTempDirFixture().findOrCreateDir("to_import/testdata");
    myFixture.getTempDirFixture().findOrCreateDir("to_import/.name");
    myFixture.getTempDirFixture().findOrCreateDir("to_import/_name");
    doTest();
  }

  public void testDoNotReportNonLastMultiResolvedImport() throws IOException {
    final VirtualFile root1 = myFixture.getTempDirFixture().findOrCreateDir("root1");
    final VirtualFile root2 = myFixture.getTempDirFixture().findOrCreateDir("root2");

    myFixture.getTempDirFixture().findOrCreateDir("root1/src/to_import/unique");
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

  public void testDuplicatesNoLocalResolveForTest() {
    myFixture.configureByText("a.go", "package i; type P struct { v1 int }");
    myFixture.configureByText("b.go", "<error>package i_test</error>; import ( \".\" ); func <warning>f</warning>() { print(i.P{}.<error>v1</error>) }");
    myFixture.checkHighlighting();
  }

  public void testDuplicateFunctionsInOnePackage() {
    myFixture.configureByText("a.go", "package foo; func init() {bar()}; func bar() {};");
    myFixture.configureByText("b.go", "//+build appengine\n\npackage foo; func init() {buzz()}; func buzz() {}");
    myFixture.configureByText("c.go", "package foo; func init() {bar(); buzz();}; func <error>bar</error>() {}; func buzz() {}");
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

  public void _testPackageWithTestPrefix() throws Throwable {
    VirtualFile file = WriteCommandAction.runWriteCommandAction(myFixture.getProject(), new ThrowableComputable<VirtualFile, Throwable>() {
      @Override
      public VirtualFile compute() throws Throwable {
        myFixture.getTempDirFixture().createFile("pack1/pack1_test.go", "package pack1_test; func Test() {}");
        return myFixture.getTempDirFixture().createFile("pack2/pack2_test.go",
                                                        "package pack2_test; import `pack1`; func TestTest() {pack1_test.Test()}");
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

  @Override
  protected LightProjectDescriptor getProjectDescriptor() {
    return createMockProjectDescriptor();
  }
}
