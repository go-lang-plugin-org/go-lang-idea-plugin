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

package com.goide.codeInsight.imports;

import com.goide.GoCodeInsightFixtureTestCase;
import com.goide.inspections.unresolved.GoUnresolvedReferenceInspection;
import com.intellij.codeInsight.CodeInsightSettings;
import com.intellij.codeInsight.daemon.DaemonCodeAnalyzerSettings;
import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.openapi.command.undo.UndoManager;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.fixtures.TempDirTestFixture;
import com.intellij.testFramework.fixtures.impl.CodeInsightTestFixtureImpl;

import java.io.IOException;
import java.util.List;


public class GoReferenceImporterTest extends GoCodeInsightFixtureTestCase {
  private boolean defaultJavaOnTheFly;
  private boolean defaultGoOnTheFly;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    setUpProjectSdk();
    myFixture.enableInspections(GoUnresolvedReferenceInspection.class);
    ((CodeInsightTestFixtureImpl)myFixture).canChangeDocumentDuringHighlighting(true);
    defaultJavaOnTheFly = CodeInsightSettings.getInstance().ADD_UNAMBIGIOUS_IMPORTS_ON_THE_FLY;
    defaultGoOnTheFly = GoCodeInsightSettings.getInstance().isAddUnambiguousImportsOnTheFly();
  }


  @Override
  protected void tearDown() throws Exception {
    try {
      updateSettings(defaultGoOnTheFly, defaultJavaOnTheFly);
    }
    finally {
      super.tearDown();
    }
  }

  @Override
  protected LightProjectDescriptor getProjectDescriptor() {
    return createMockProjectDescriptor();
  }

  @Override
  protected boolean isWriteActionRequired() {
    return false;
  }

  private void doTestAddOnTheFly(boolean goOnTheFlyEnabled, boolean javaOnTheFlyEnabled) {
    DaemonCodeAnalyzerSettings.getInstance().setImportHintEnabled(true);
    updateSettings(goOnTheFlyEnabled, javaOnTheFlyEnabled);

    String initial = "package a; func a() {\n fmt.Println() <caret> \n}";
    myFixture.configureByText("a.go", initial);
    myFixture.doHighlighting();
    myFixture.doHighlighting();
    String after = "package a;\nimport \"fmt\" func a() {\n fmt.Println() <caret> \n}";
    String result = goOnTheFlyEnabled && javaOnTheFlyEnabled ? after : initial;
    myFixture.checkResult(result);
  }

  private static void updateSettings(boolean goOnTheFlyEnabled, boolean javaOnTheFlyEnabled) {
    CodeInsightSettings.getInstance().ADD_UNAMBIGIOUS_IMPORTS_ON_THE_FLY = javaOnTheFlyEnabled;
    GoCodeInsightSettings.getInstance().setAddUnambiguousImportsOnTheFly(goOnTheFlyEnabled);
  }

  public void testUndo() {
    DaemonCodeAnalyzerSettings.getInstance().setImportHintEnabled(true);
    updateSettings(true, true);
    myFixture.configureByText("a.go", "package main\n\nfunc main() { <caret> }");
    myFixture.type("fmt.");
    myFixture.doHighlighting();
    myFixture.doHighlighting();
    myFixture.checkResult("package main\nimport \"fmt\"\n\nfunc main() { fmt. }");
    FileEditor editor = FileEditorManager.getInstance(myFixture.getProject()).getSelectedEditor(myFixture.getFile().getVirtualFile());
    UndoManager.getInstance(myFixture.getProject()).undo(editor);
    myFixture.checkResult("package main\n\nfunc main() { <caret> }");
  }

  public void testOnTheFlyEnabled() {
    doTestAddOnTheFly(true, true);
  }

  public void testOnTheFlyDisabled() {
    doTestAddOnTheFly(false, true);
  }

  public void testOnTheFlyEnabledJavaOnTheFlyDisabled() {
    doTestAddOnTheFly(true, false);
  }

  public void testOnTheFlyDisabledJavaOnTheFlyDisabled() {
    doTestAddOnTheFly(false, false);
  }

  private void doTestImportOwnPath(String file, String text, String testFile, String testText, String path, boolean shouldImport)
    throws IOException {
    DaemonCodeAnalyzerSettings.getInstance().setImportHintEnabled(true);
    updateSettings(true, true);

    TempDirTestFixture dir = myFixture.getTempDirFixture();
    dir.createFile(path + "/" + file, text);
    VirtualFile test = dir.createFile(path + "/" + testFile, testText);
    myFixture.configureFromExistingVirtualFile(test);
    List<IntentionAction> actions = myFixture.filterAvailableIntentions("Import " + path + "?");
    assertTrue(shouldImport != actions.isEmpty());
  }

  public void testOwnAddPathFromTest() throws IOException {
    doTestImportOwnPath("a.go", "package myPack; func Func() {}",
                        "a_test.go", "package myPack_test; func TestFunc() { my<caret>Pack.Func() }",
                        "pack", true);
  }

  public void testDoNotImportOwnPathFromDifferentPackage() throws IOException {
    doTestImportOwnPath("a.go", "package pack1; func Func() {}",
                        "a_test.go", "package pack2_test; func TestFunc() { pack<caret>1.Func() }",
                        "pack", false);
  }

  public void testCompleteDifferentPackageFromTest() {
    myFixture.configureByText("a.go", "package foo; func a() { fmt.Print<caret> }");
    assertNotEmpty(myFixture.getLookupElementStrings());
  }
}
