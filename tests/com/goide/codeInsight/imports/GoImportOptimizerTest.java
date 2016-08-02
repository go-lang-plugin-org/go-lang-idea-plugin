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

import com.goide.SdkAware;
import com.goide.inspections.GoUnusedImportInspection;
import com.goide.quickfix.GoQuickFixTestBase;
import com.intellij.codeInsight.actions.OptimizeImportsAction;
import com.intellij.ide.DataManager;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiRecursiveElementVisitor;
import com.intellij.psi.PsiReference;
import com.intellij.testFramework.fixtures.impl.CodeInsightTestFixtureImpl;
import org.jetbrains.annotations.NotNull;

@SdkAware
public class GoImportOptimizerTest extends GoQuickFixTestBase {
  public void testUnusedImports()                                        { doTest(); }
  public void testUnusedImportsWithSemicolon()                           { doTest(); }
  public void testUnusedImplicitImports()                                { doTest(); }
  public void testUsedImplicitImports()                                  { doTest(); }
  public void testUsedDuplicatedImports()                                { doTest(); }
  public void testDuplicatedImportsWithSameStringAndDifferentQuotes()    { doTest(); }
  public void testDuplicatedImportsWithSameString()                      { doTest(); }
  public void testDuplicatedImportsWithDifferentString()                 { doTest(); }
  public void testUnusedDuplicatedImports()                              { doTest(); }
  public void testImportPackageWithMainFiles()                           { doTest(); }
  public void testImportDirectoryWithoutPackages()                       { doTest(); } 
  public void testUnusedImportsWithBacktick()                            { doTest(); } 
  public void testDoNotOptimizeSideEffectImports()                       { doTest(); } 
  public void testRedundantImportQualifier()                             { doTest(); } 

  public void testUnusedImportsQuickFix() { 
    myFixture.configureByFile(getTestName(true) + ".go");
    myFixture.checkHighlighting();
    applySingleQuickFix("Optimize imports");
    myFixture.checkResultByFile(getTestName(true) + "_after.go");
  }

  public void testImportWithSameIdentifier() {
    myFixture.addFileToProject("foo/bar/buzz.go", "package bar; func Hello() {}");
    doTest();
  }

  public void testImportWithMultiplePackages() {
    myFixture.addFileToProject("pack/pack_test.go", "package pack_test; func Test() {}");
    myFixture.addFileToProject("pack/pack.go", "package pack;");
    doTest();
  }

  private void doTest() {
    PsiFile file = myFixture.configureByFile(getTestName(true) + ".go");
    resolveAllReferences(file);
    myFixture.checkHighlighting();
    ApplicationManager.getApplication().runWriteAction(
      () -> OptimizeImportsAction.actionPerformedImpl(DataManager.getInstance().getDataContext(myFixture.getEditor().getContentComponent())));
    myFixture.checkResultByFile(getTestName(true) + "_after.go");
  }

  public static void resolveAllReferences(PsiFile file) {
    file.accept(new PsiRecursiveElementVisitor() {
      @Override
      public void visitElement(@NotNull PsiElement o) {
        for (PsiReference reference : o.getReferences()) {
          reference.resolve();
        }
        super.visitElement(o);
      }
    });
  }

  @Override
  public void setUp() throws Exception {
    super.setUp();
    ((CodeInsightTestFixtureImpl)myFixture).canChangeDocumentDuringHighlighting(true);
    myFixture.enableInspections(GoUnusedImportInspection.class);
  }

  @NotNull
  @Override
  protected String getBasePath() {
    return "imports/optimize";
  }
}
