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

import com.goide.SdkAware;
import com.goide.quickfix.GoDeleteImportQuickFix;
import com.goide.quickfix.GoQuickFixTestBase;
import com.goide.sdk.GoSdkService;
import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.PsiFile;
import com.intellij.util.containers.ContainerUtil;

import java.util.Collection;

@SdkAware
public class GoInvalidPackageImportInspectionTest extends GoQuickFixTestBase {
  @Override
  public void setUp() throws Exception {
    super.setUp();
    myFixture.enableInspections(GoInvalidPackageImportInspection.class);
  }

  public void testImportBuiltinPackage() {
    myFixture.addFileToProject("builtin/hello.go", "package builtin");
    myFixture.configureByText("a.go", "package pack; import <error descr=\"Cannot import 'builtin' package\">`builtin`</error>");
    myFixture.checkHighlighting();
  }

  public void testImportBuiltinSubPackage() {
    myFixture.addFileToProject("builtin/hello.go", "package builtin");
    myFixture.addFileToProject("builtin/sub/hello.go", "package builtin");
    myFixture.configureByText("a.go", "package pack; import `builtin/sub`");
    myFixture.checkHighlighting();
  }

  public void testImportVendoredBuiltin() {
    myFixture.addFileToProject("vendor/builtin/hello.go", "package builtin");
    myFixture.configureByText("a.go", "package pack; import `builtin`");
    myFixture.checkHighlighting();
  }

  public void testImportUnreachableVendoredPackage() {
    myFixture.addFileToProject("pack/vendor/foo/a.go", "package foo");
    myFixture.configureByText("a.go", "package bar\n" +
                                      "import _ `pack/vendor/unresolved`\n" +
                                      "import <error descr=\"Use of vendored package is not allowed\">_ `pack/ve<caret>ndor/foo`</error>");
    myFixture.checkHighlighting();
    assertContainsElements(getIntentionNames(), "Disable vendoring experiment support in module 'light_idea_test_case'", "Delete import");
    assertDoesntContain(getIntentionNames(), "Replace with `foo`");
  }

  public void testReplaceImportWithVendoredPath() {
    myFixture.addFileToProject("vendor/foo/a.go", "package foo");
    myFixture.configureByText("a.go", "package pack\n" +
                                      "import _ `pack/vendor/unresolved`\n" +
                                      "import <error descr=\"Must be imported as 'foo'\">_ `vend<caret>or/foo`</error>");
    myFixture.checkHighlighting();
    assertContainsElements(getIntentionNames(), "Disable vendoring experiment support in module 'light_idea_test_case'", "Delete import");
    myFixture.launchAction(myFixture.findSingleIntention("Replace with 'foo'"));
    myFixture.checkResult("package pack\n" +
                          "import _ `pack/vendor/unresolved`\n" +
                          "import _ `foo`");
  }

  public void testDoNoSuggestDisablingVendoringOnGo1_7() {
    GoSdkService.setTestingSdkVersion("1.7", getTestRootDisposable());
    myFixture.addFileToProject("vendor/foo/a.go", "package foo");
    myFixture.configureByText("a.go", "package pack\n" +
                                      "import _ `pack/vendor/unresolved`\n" +
                                      "import <error descr=\"Must be imported as 'foo'\">_ `vend<caret>or/foo`</error>");
    myFixture.checkHighlighting();
    assertDoesntContain(getIntentionNames(), "Disable vendoring experiment support in module 'light_idea_test_case'");
  }

  public void testWithDisabledVendoring() {
    disableVendoring();
    myFixture.addFileToProject("pack/vendor/foo/a.go", "package foo");
    myFixture.addFileToProject("vendor/foo/a.go", "package foo");
    myFixture.configureByText("a.go", "package pack\n" +
                                      "import _ `pack/vendor/unresolved`\n" +
                                      "import _ `pack/vendor/foo/a`\n" +
                                      "import _ `vendor/foo/a`");
    myFixture.checkHighlighting();
  }

  public void testVendorInsideVendorShouldBeUnreachable() {
    myFixture.addFileToProject("vendor/foo/a.go", "package foo");
    myFixture.addFileToProject("vendor/foo/vendor/bar/a.go", "package bar");
    myFixture.configureByText("a.go", "package bar\n" +
                                      "import _ `foo`\n" +
                                      "import <error descr=\"Use of vendored package is not allowed\">_ `foo/vendor/bar`</error>");
    myFixture.checkHighlighting();
  }

  public void testImportMainPackage() {
    myFixture.addFileToProject("foo/main/main.go", "package main");
    myFixture.addFileToProject("bar/main/main_test.go", "package main_test");
    myFixture.addFileToProject("buzz/main/other_main_test.go", "package main");
    myFixture.addFileToProject("not_program/main/other_main.go", "package main_test");
    myFixture.addFileToProject("foo/not_main/main.go", "package main");
    myFixture.addFileToProject("bar/not_main/main_test.go", "package main_test");
    myFixture.addFileToProject("buzz/not_main/other_main_test.go", "package main");
    myFixture.addFileToProject("not_program/not_main/other_main.go", "package main_test");
    myFixture.configureByText("a.go", "package a\n" +
                                      "import <error descr=\"'foo/main' is a program, not an importable package\">`foo/main`</error>\n" +
                                      "import <error descr=\"'bar/main' is a program, not an importable package\">`bar/main`</error>\n" +
                                      "import <error descr=\"'buzz/main' is a program, not an importable package\">`buzz/main`</error>\n" +
                                      "import `not_program/main`\n" +
                                      "import <error descr=\"'foo/not_main' is a program, not an importable package\">`foo/not_main`</error>\n" +
                                      "import <error descr=\"'bar/not_main' is a program, not an importable package\">`bar/not_main`</error>\n" +
                                      "import <error descr=\"'buzz/not_main' is a program, not an importable package\">`buzz/not_main`</error>\n" +
                                      "import `not_program/not_main`");
    myFixture.checkHighlighting();
  }
  
  public void testImportMainPackageInTest() {
    myFixture.addFileToProject("foo/main/main.go", "package main");
    myFixture.configureByText("a_test.go", "package a\nimport `foo/main`");
    myFixture.checkHighlighting();
  }

  public void testInternalPackageOn1_2_SDK() {
    myFixture.addFileToProject("internal/internal.go", "package internalPackage; func InternalFunction() {}");
    myFixture.addFileToProject("sub/internal/internal.go", "package subInternalPackage; func InternalFunction() {}");
    myFixture.configureByText("a.go", "package src\n" +
                                      "import (\n" +
                                      "  `internal`\n" +
                                      "  `sub/internal`\n" +
                                      "  `net/internal`\n" +
                                      ")");
    myFixture.checkHighlighting();
  }

  public void testInternalPackageOn1_4_SDK() {
    GoSdkService.setTestingSdkVersion("1.4", getTestRootDisposable());
    myFixture.addFileToProject("internal/internal.go", "package internalPackage; func InternalFunction() {}");
    myFixture.addFileToProject("sub/internal/internal.go", "package subInternalPackage; func InternalFunction() {}");
    myFixture.configureByText("a.go", "package src\n" +
                                      "import (\n" +
                                      "  `internal`\n" +
                                      "  `sub/internal`\n" +
                                      "  <error descr=\"Use of internal package is not allowed\">`net/internal`</error>\n" +
                                      ")");
    myFixture.checkHighlighting();
  }

  public void testInternalPackageOn1_5_SDK() {
    GoSdkService.setTestingSdkVersion("1.5", getTestRootDisposable());
    myFixture.addFileToProject("internal/internal.go", "package internalPackage; func InternalFunction() {}");
    myFixture.addFileToProject("sub/internal/internal.go", "package subInternalPackage; func InternalFunction() {}");
    myFixture.configureByText("a.go", "package src\n" +
                                      "import (\n" +
                                      "  `internal`\n" +
                                      "  <error descr=\"Use of internal package is not allowed\">`sub/internal`</error>\n" +
                                      "  <error descr=\"Use of internal package is not allowed\">`net/internal`</error>\n" +
                                      ")");
    myFixture.checkHighlighting();
  }

  public void testImportPackageWithoutBuildableSource() {
    PsiFile file = myFixture.addFileToProject("withSources/a.go", "package withSources");
    myFixture.addFileToProject("withIgnoredFiles/a.go", "package documentation");
    myFixture.addFileToProject("withIgnoredFiles/.b.go", "package withIgnoredFiles");
    myFixture.addFileToProject("withIgnoredFiles/_b.go", "package withIgnoredFiles");
    WriteCommandAction.runWriteCommandAction(myFixture.getProject(), () -> {
      //noinspection ConstantConditions
      file.getParent().getParent().createSubdirectory("withoutSources");
    });
    myFixture.configureByText("a.go", "package pack\n" +
                                      "import `withSources`\n" +
                                      "import <error descr=\"'/src/withIgnoredFiles' has no buildable Go source files\">`withIgnoredFiles`</error>\n" +
                                      "import <error descr=\"'/src/withoutSources' has no buildable Go source files\">`withoutSources`</error>\n" +
                                      "import <error descr=\"'/src/withoutSources' has no buildable Go source files\">_ `without<caret>Sources`</error>\n" +
                                      "import `unresolved`\n");
    myFixture.checkHighlighting();
    applySingleQuickFix(GoDeleteImportQuickFix.QUICK_FIX_NAME);
    myFixture.checkResult("package pack\n" +
                          "import `withSources`\n" +
                          "import `withIgnoredFiles`\n" +
                          "import `withoutSources`\n" +
                          "import `unresolved`\n");
  }

  public void testImportAbsolutePath() {
    myFixture.configureByText("a.go", "package a; import <error descr=\"Cannot import absolute path\">`/f<caret>mt`</error>");
    myFixture.checkHighlighting();
    assertEmpty(myFixture.filterAvailableIntentions("go get"));
    applySingleQuickFix(GoDeleteImportQuickFix.QUICK_FIX_NAME);
    myFixture.checkResult("package a; ");
  }

  public void testImportTestDataDirectory() {
    myFixture.addFileToProject("pack/testdata/pack/foo.go", "package test");
    myFixture.configureByText("a.go", "package a\n" +
                                      "import `pack/testdata/pack`\n" +
                                      "import <error descr=\"Use of testdata package from SDK is not allowed\">`doc/te<caret>stdata`</error>");
    myFixture.checkHighlighting();
    applySingleQuickFix(GoDeleteImportQuickFix.QUICK_FIX_NAME);
    myFixture.checkResult("package a\nimport `pack/testdata/pack`\n");
  }

  public void testImportCWithAlias() {
    myFixture.configureByText("a.go", 
                              "package t; import `C`; import <error descr=\"Cannot import 'builtin' package\">alias `<caret>C`</error>;");
    myFixture.checkHighlighting();
    applySingleQuickFix(GoInvalidPackageImportInspection.DELETE_ALIAS_QUICK_FIX_NAME);
    myFixture.checkResult("package t; import `C`; import `C`;");
  }

  public void testImportCWithDot() {
    myFixture.configureByText("a.go", "package t; import `C`; import <error descr=\"Cannot rename import `C`\">. `<caret>C`</error>;");
    myFixture.checkHighlighting();
    applySingleQuickFix(GoInvalidPackageImportInspection.DELETE_ALIAS_QUICK_FIX_NAME);
    myFixture.checkResult("package t; import `C`; import `C`;");
  }

  private Collection<String> getIntentionNames() {
    return ContainerUtil.map(myFixture.getAvailableIntentions(), IntentionAction::getText);
  }

}
