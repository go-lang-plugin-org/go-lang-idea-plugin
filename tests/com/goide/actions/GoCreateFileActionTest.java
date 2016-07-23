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

package com.goide.actions;

import com.goide.GoCodeInsightFixtureTestCase;
import com.goide.psi.GoFile;
import com.intellij.ide.actions.CreateFileFromTemplateAction;
import com.intellij.ide.fileTemplates.impl.CustomFileTemplate;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.PsiDirectory;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class GoCreateFileActionTest extends GoCodeInsightFixtureTestCase {
  public void testPackageNameInEmptyDirectory() throws Exception {
    doTestInEmptyDirectory("empty-dir", "a", "empty_dir", "empty_dir");
  }

  public void testPackageNameInEmptyDirectoryWithTestSuffix() throws Exception {
    doTestInEmptyDirectory("empty-dir-test", "a_test", "empty_dir_test", "empty_dir_test_test");
  }

  public void testPackageNameInExistingPackage() {
    doTestWithExistingPackage("b", "a", "a");
  }

  public void testTestPackageNameInExistingPackage() {
    doTestWithExistingPackage("a_test", "a", "a_test");
  }

  public void testPackageNameInExistingPackageWithExtension() {
    doTestWithExistingPackage("b.go", "a", "a");
  }

  public void testTestPackageNameInExistingPackageWithExtension() {
    doTestWithExistingPackage("a_test.go", "a", "a_test");
  }

  private void doTestWithExistingPackage(@NotNull String fileName,
                                         @NotNull String expectedPackage,
                                         @NotNull String expectedPackageWithTestSuffix) {
    myFixture.configureByText("a.go", "package a");
    doTest(myFixture.getFile().getContainingDirectory(), fileName, expectedPackage, expectedPackageWithTestSuffix);
  }

  private void doTestInEmptyDirectory(@NotNull String directoryName,
                                      @NotNull String newFileName,
                                      @NotNull String expectedPackage,
                                      @NotNull String expectedPackageWithTestSuffix) {
    try {
      PsiDirectory dir = myFixture.getPsiManager().findDirectory(myFixture.getTempDirFixture().findOrCreateDir(directoryName));
      assertNotNull(dir);
      doTest(dir, newFileName, expectedPackage, expectedPackageWithTestSuffix);
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static void doTest(@NotNull PsiDirectory dir,
                             @NotNull String newFileName,
                             @NotNull String expectedPackage,
                             @NotNull String expectedPackageWithTestSuffix) {
    CustomFileTemplate template = new CustomFileTemplate("testTemplate", "go");
    template.setText("package ${GO_PACKAGE_NAME}");

    CustomFileTemplate templateWithSuffix = new CustomFileTemplate("testTemplate", "go");
    templateWithSuffix.setText("package ${GO_PACKAGE_NAME_WITH_TEST_SUFFIX}");
    
    doTemplateTest(dir, newFileName, expectedPackage, template);
    doTemplateTest(dir, newFileName, expectedPackageWithTestSuffix, templateWithSuffix);
  }

  private static void doTemplateTest(@NotNull PsiDirectory dir, @NotNull String newFileName, @NotNull String expectedPackage, @NotNull  CustomFileTemplate template) {
    GoFile file = (GoFile)CreateFileFromTemplateAction.createFileFromTemplate(newFileName, template, dir, null, true);
    assertNotNull(file);
    assertEquals(expectedPackage, file.getPackageName());
    WriteCommandAction.runWriteCommandAction(dir.getProject(), file::delete);
  }
}

