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

package com.goide.quickfix;

import com.goide.SdkAware;
import com.goide.inspections.unresolved.GoUnresolvedReferenceInspection;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

@SdkAware
public class GoIntroduceFunctionFixTest extends GoQuickFixTestBase {

  private final static String QUICK_FIX_NAME = "Create function asd";

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    myFixture.enableInspections(GoUnresolvedReferenceInspection.class);
  }

  public void testSimpleFunction() {
    doTest(QUICK_FIX_NAME, false);
  }

  public void testFuncWithOneParam() {
    doTest(QUICK_FIX_NAME, false);
  }

  public void testFuncWithUnknownTypeParam() {
    doTest(QUICK_FIX_NAME, false);
  }

  public void testFuncWithResultType() {
    doTest(QUICK_FIX_NAME, false);
  }

  public void testFuncWithUnknownResultType() {
    doTest(QUICK_FIX_NAME, false);
  }

  public void testFuncWithThreeParams() {
    doTest(QUICK_FIX_NAME, false);
  }

  public void testFuncWithThreeResultValues() {
    doTest(QUICK_FIX_NAME, false);
  }

  public void testDontCreate() {
    doTestNoFix(QUICK_FIX_NAME, false);
  }

  public void testShortVarDecl() {
    doTest(QUICK_FIX_NAME, false);
  }

  public void testInConstDeclaration() {
    doTestNoFix(QUICK_FIX_NAME);
  }

  public void testVarSpec() {
    doTest(QUICK_FIX_NAME, false);
  }

  public void testAsFunctionArg() {
    doTest(QUICK_FIX_NAME, false);
  }

  public void testAsFunctionArg2() {
    doTest(QUICK_FIX_NAME, false);
  }

  public void testAsFunctionArgWithoutReference() {
    doTest(QUICK_FIX_NAME, false);
  }

  public void testWithChan() {
    doTest(QUICK_FIX_NAME, false);
  }

  public void testInSamePackage() {
    myFixture.configureByText("a.go", "package foo; type MyType int; func createMyType() MyType { return MyType{}};");
    myFixture.configureByText("b.go", "package foo; func _() { asd<caret>(createMyType());};");
    myFixture.doHighlighting();
    applySingleQuickFix(QUICK_FIX_NAME);
    myFixture.checkResult("package foo; func _() { asd(createMyType());}\nfunc asd(myType MyType) {\n\t<caret>\n};");
  }

  public void testInOtherPackage() {
    myFixture.addFileToProject("a/a.go", "package a; type MyType int; func CreateMyType() MyType { return MyType{}};");
    PsiFile file = myFixture.addFileToProject("b/b.go", "package b; import \"a\"; func _() { asd<caret>(a.CreateMyType());};");
    myFixture.configureFromExistingVirtualFile(file.getVirtualFile());
    applySingleQuickFix(QUICK_FIX_NAME);
    myFixture.checkResult("package b; import \"a\"; func _() { asd(a.CreateMyType());}\nfunc asd(myType a.MyType) {\n\t<caret>\n};");
  }

  public void testInOtherPackageWithAlias() {
    myFixture.addFileToProject("a/a.go", "package a; type MyType int; func CreateMyType() MyType { return MyType{}};");
    PsiFile file = myFixture.addFileToProject("b/b.go", "package b; import alias \"a\"; func _() { asd<caret>(alias.CreateMyType());};");
    myFixture.configureFromExistingVirtualFile(file.getVirtualFile());
    applySingleQuickFix(QUICK_FIX_NAME);
    myFixture
      .checkResult("package b; import alias \"a\"; func _() { asd(alias.CreateMyType());}\nfunc asd(myType alias.MyType) {\n\t<caret>\n};");
  }

  public void testInOtherPackageWithDotAlias() {
    myFixture.addFileToProject("a/a.go", "package a; type MyType int; func CreateMyType() MyType { return MyType{}};");
    PsiFile file = myFixture.addFileToProject("b/b.go", "package b; import . \"a\"; func _() { asd<caret>(CreateMyType());};");
    myFixture.configureFromExistingVirtualFile(file.getVirtualFile());
    applySingleQuickFix(QUICK_FIX_NAME);
    myFixture.checkResult("package b; import . \"a\"; func _() { asd(CreateMyType());}\nfunc asd(myType MyType) {\n\t<caret>\n};");
  }

  public void testInOtherPackageWithImportForSideEffects() {
    myFixture.addFileToProject("a/a.go", "package a; type MyType int");
    myFixture.addFileToProject("b/b.go", "package b; import `a`; func CreateMyType() b.MyType {return b.MyType{}};");
    PsiFile file = myFixture.addFileToProject("c/c.go", "package c; import `b`; import _ `a`; func _() { asd<caret>(b.CreateMyType());};");
    myFixture.configureFromExistingVirtualFile(file.getVirtualFile());
    applySingleQuickFix(QUICK_FIX_NAME);
    myFixture.checkResult(
      "package c; import `b`; import _ `a`; func _() { asd(b.CreateMyType());}\nfunc asd(myType interface{}) {\n\t<caret>\n};");
  }

  public void testInOtherPackageWithTwoAlias() {
    myFixture.addFileToProject("c/c.go", "package c; type MyType int;");
    myFixture.addFileToProject("a/a.go", "package a; import myC \"c\" func CreateMyType() myC.MyType { return myC.MyType{}};");
    PsiFile file =
      myFixture.addFileToProject("b/b.go", "package b; import (. \"a\"; importC \"c\"); func _() { asd<caret>(CreateMyType());};");
    myFixture.configureFromExistingVirtualFile(file.getVirtualFile());
    applySingleQuickFix(QUICK_FIX_NAME);
    myFixture
      .checkResult(
        "package b; import (. \"a\"; importC \"c\"); func _() { asd(CreateMyType());}\nfunc asd(myType importC.MyType) {\n\t<caret>\n};");
  }

  public void testInOtherPackageWithPrivateType() {
    myFixture.addFileToProject("a/a.go", "package a; type myType int; func CreateMyType() myType { return myType{}};");
    PsiFile file = myFixture.addFileToProject("b/b.go", "package b; import . \"a\"; func _() { asd<caret>(CreateMyType());};");
    myFixture.configureFromExistingVirtualFile(file.getVirtualFile());
    applySingleQuickFix(QUICK_FIX_NAME);
    myFixture.checkResult("package b; import . \"a\"; func _() { asd(CreateMyType());}\nfunc asd(myType interface{}) {\n\t<caret>\n};");
  }

  public void testInOtherPackageWithVendoring() {
    myFixture.addFileToProject("a/vendor/c.go", "package c; type MyType int;");
    myFixture.addFileToProject("a/a.go", "package a; import myC \"vendor\" func CreateMyType() myC.MyType { return myC.MyType{}};");
    PsiFile file = myFixture.addFileToProject("b/b.go", "package b; import . \"a\"; func _() { asd<caret>(CreateMyType());};");
    myFixture.configureFromExistingVirtualFile(file.getVirtualFile());
    applySingleQuickFix(QUICK_FIX_NAME);
    myFixture.checkResult("package b; import . \"a\"; func _() { asd(CreateMyType());}\nfunc asd(myType interface{}) {\n\t<caret>\n};");
  }

  public void testInOtherPackageWithChanOfImportedTypes() {
    myFixture.addFileToProject("a/a.go", "package a; type MyType int; func CreateChanOfMyType() chan MyType { return nil};");
    PsiFile file = myFixture.addFileToProject("b/b.go",
                                              "package b; import alias \"a\"; func _() { asd<caret>(alias.CreateChanOfMyType());};");
    myFixture.configureFromExistingVirtualFile(file.getVirtualFile());
    applySingleQuickFix(QUICK_FIX_NAME);
    myFixture.checkResult("package b; import alias \"a\"; " +
                          "func _() { asd(alias.CreateChanOfMyType());}\nfunc asd(myType chan alias.MyType) {\n\t<caret>\n};");
  }

  public void testInOtherPackageWithStruct() {
    myFixture.addFileToProject("a/a.go", "package a; type MyType int; func CreateChanOfMyType() struct{ ch chan chan MyType} { return nil};");
    PsiFile file = myFixture.addFileToProject("b/b.go",
                                              "package b; import alias \"a\"; func _() { asd<caret>(alias.CreateChanOfMyType());};");
    myFixture.configureFromExistingVirtualFile(file.getVirtualFile());
    applySingleQuickFix(QUICK_FIX_NAME);
    myFixture.checkResult("package b; import alias \"a\"; " +
                          "func _() { asd(alias.CreateChanOfMyType());}\nfunc asd(myType struct {ch chan chan alias.MyType}) {\n\t<caret>\n};");
  }

  @NotNull
  @Override
  protected String getBasePath() {
    return "quickfixes/introduce-function";
  }
}
