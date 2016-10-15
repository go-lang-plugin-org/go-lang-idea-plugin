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

package com.goide.codeInsight.imports;

import com.goide.GoCodeInsightFixtureTestCase;
import com.goide.SdkAware;
import com.goide.inspections.unresolved.GoUnresolvedReferenceInspection;
import com.intellij.codeInsight.CodeInsightSettings;
import com.intellij.codeInsight.daemon.DaemonCodeAnalyzerSettings;
import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.openapi.command.undo.UndoManager;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.fixtures.impl.CodeInsightTestFixtureImpl;

import java.util.List;

@SdkAware
public class GoReferenceImporterTest extends GoCodeInsightFixtureTestCase {
  private boolean defaultJavaOnTheFly;
  private boolean defaultJavaMemberOnTheFly;
  private boolean defaultGoOnTheFly;

  private static void updateSettings(boolean onTheFlyEnabled) {
    GoCodeInsightSettings.getInstance().setAddUnambiguousImportsOnTheFly(onTheFlyEnabled);
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    myFixture.enableInspections(GoUnresolvedReferenceInspection.class);
    ((CodeInsightTestFixtureImpl)myFixture).canChangeDocumentDuringHighlighting(true);
    CodeInsightSettings codeInsightSettings = CodeInsightSettings.getInstance();
    defaultJavaOnTheFly = codeInsightSettings.ADD_UNAMBIGIOUS_IMPORTS_ON_THE_FLY;
    defaultJavaMemberOnTheFly = codeInsightSettings.ADD_MEMBER_IMPORTS_ON_THE_FLY;
    defaultGoOnTheFly = GoCodeInsightSettings.getInstance().isAddUnambiguousImportsOnTheFly();

    codeInsightSettings.ADD_UNAMBIGIOUS_IMPORTS_ON_THE_FLY = true;
    codeInsightSettings.ADD_MEMBER_IMPORTS_ON_THE_FLY = true;
  }

  @Override
  protected void tearDown() throws Exception {
    try {
      updateSettings(defaultGoOnTheFly);
      CodeInsightSettings codeInsightSettings = CodeInsightSettings.getInstance();
      codeInsightSettings.ADD_MEMBER_IMPORTS_ON_THE_FLY = defaultJavaMemberOnTheFly;
      codeInsightSettings.ADD_UNAMBIGIOUS_IMPORTS_ON_THE_FLY = defaultJavaOnTheFly;
    }
    finally {
      //noinspection ThrowFromFinallyBlock
      super.tearDown();
    }
  }

  private void doTestAddOnTheFly(boolean goOnTheFlyEnabled) {
    DaemonCodeAnalyzerSettings.getInstance().setImportHintEnabled(true);
    updateSettings(goOnTheFlyEnabled);

    String initial = "package a; func a() {\n fmt.Println() <caret> \n}";
    myFixture.configureByText("a.go", initial);
    myFixture.doHighlighting();
    myFixture.doHighlighting();
    String after = "package a;\n\nimport \"fmt\"\n\nfunc a() {\n fmt.Println()  \n}";
    String result = goOnTheFlyEnabled ? after : initial;
    myFixture.checkResult(result);
  }

  public void testUndo() {
    DaemonCodeAnalyzerSettings.getInstance().setImportHintEnabled(true);
    updateSettings(true);
    myFixture.configureByText("a.go", "package main\n\nfunc main() { <caret> }");
    myFixture.type("fmt.");
    myFixture.doHighlighting();
    myFixture.doHighlighting();
    myFixture.checkResult("package main\n\nimport \"fmt\"\n\nfunc main() { fmt. }");
    FileEditor editor = FileEditorManager.getInstance(myFixture.getProject()).getSelectedEditor(myFixture.getFile().getVirtualFile());
    UndoManager.getInstance(myFixture.getProject()).undo(editor);
    myFixture.checkResult("package main\n\nfunc main() { <caret> }");
  }

  public void testOnTheFlyEnabled() {
    doTestAddOnTheFly(true);
  }

  public void testOnTheFlyDisabled() {
    doTestAddOnTheFly(false);
  }

  private void doTestImportOwnPath(String text, String testText, boolean shouldImport) {
    updateSettings(false);
    myFixture.addFileToProject(FileUtil.join("pack", "a.go"), text);
    PsiFile testFile = myFixture.addFileToProject(FileUtil.join("pack", "a_test.go"), testText);
    myFixture.configureFromExistingVirtualFile(testFile.getVirtualFile());
    List<IntentionAction> actions = myFixture.filterAvailableIntentions("Import " + "pack" + "?");
    assertTrue(shouldImport != actions.isEmpty());
  }

  public void testOwnAddPathFromTest() {
    doTestImportOwnPath("package myPack; func Func() {}",
                        "package myPack_test; func TestFunc() { my<caret>Pack.Func() }",
                        true);
  }

  public void testDoNotImportOwnPathFromDifferentPackage() {
    doTestImportOwnPath("package pack1; func Func() {}",
                        "package pack2_test; func TestFunc() { pack<caret>1.Func() }",
                        false);
  }

  public void testCompleteDifferentPackageFromTest() {
    myFixture.configureByText("a.go", "package foo; func a() { fmt.Print<caret> }");
    assertNotEmpty(myFixture.getLookupElementStrings());
  }

  public void testImportVendoringPackage() {
    myFixture.addFileToProject("vendor/a/b/c.go", "package b");
    myFixture.configureByText("a.go", "package a; func a() { b<caret>.Println() }");
    myFixture.launchAction(myFixture.findSingleIntention("Import a/b?"));
    myFixture.checkResult("package a;\n\nimport \"a/b\"\n\nfunc a() { b<caret>.Println() }");
  }

  public void testImportVendoringPackageWithDisabledVendoring() {
    disableVendoring();
    myFixture.addFileToProject("vendor/a/b/c.go", "package b");
    myFixture.configureByText("a.go", "package a; func a() { b<caret>.Println() }");
    myFixture.launchAction(myFixture.findSingleIntention("Import vendor/a/b?"));
    myFixture.checkResult("package a;\n\nimport \"vendor/a/b\"\n\nfunc a() { b<caret>.Println() }");
  }

  public void testImportBuiltinPackage() {
    myFixture.configureByText("a.go", "package a; func a() { built<caret>in.Println() }");
    assertEmpty(myFixture.filterAvailableIntentions("Import builtin?"));
  }

  public void testImportSdkTestData() {
    myFixture.configureByText("a.go", "package a; func _() { println(p<caret>kg.ExportedConstant) } ");
    assertEmpty(myFixture.filterAvailableIntentions("Import doc/testdata?"));
  }

  public void testImportVendoredBuiltinPackage() {
    myFixture.addFileToProject("vendor/builtin/builtin.go", "package builtin");
    myFixture.configureByText("a.go", "package a; func a() { built<caret>in.Println() }");
    myFixture.launchAction(myFixture.findSingleIntention("Import builtin?"));
    myFixture.checkResult("package a;\n\nimport \"builtin\"\n\nfunc a() { builtin.Println() }");
  }
}
