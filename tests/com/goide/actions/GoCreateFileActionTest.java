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

package com.goide.actions;

import com.goide.GoCodeInsightFixtureTestCase;
import com.goide.actions.file.GoCreateFileAction;
import com.goide.psi.GoFile;
import com.intellij.psi.PsiDirectory;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class GoCreateFileActionTest extends GoCodeInsightFixtureTestCase {
  public void testPackageNameInEmptyDirectory() throws Exception {
    doTestInEmptyDirectory("empty-dir", "a", "empty_dir");
  }

  public void testPackageNameInEmptyDirectoryWithTestSuffix() throws Exception {
    doTestInEmptyDirectory("empty-dir-test", "a_test", "empty_dir_test_test");
  }

  public void testPackageNameInExistingPackage() {
    doTestWithExistingPackage("b", "a");
  }

  public void testTestPackageNameInExistingPackage() {
    doTestWithExistingPackage("a_test", "a_test");
  }

  public void testPackageNameInExistingPackageWithExtension() {
    doTestWithExistingPackage("b.go", "a");
  }

  public void testTestPackageNameInExistingPackageWithExtension() {
    doTestWithExistingPackage("a_test.go", "a_test");
  }

  private void doTestWithExistingPackage(@NotNull String fileName, @NotNull String expectedPackage) {
    myFixture.configureByText("a.go", "package a");
    doTest(myFixture.getFile().getContainingDirectory(), fileName, expectedPackage);
  }

  private void doTestInEmptyDirectory(@NotNull String directoryName, @NotNull String newFileName, @NotNull String expectedPackage)
    throws IOException {
    PsiDirectory dir = myFixture.getPsiManager().findDirectory(myFixture.getTempDirFixture().findOrCreateDir(directoryName));
    assertNotNull(dir);
    doTest(dir, newFileName, expectedPackage);
  }

  private static void doTest(@NotNull PsiDirectory dir, @NotNull String newFileName, @NotNull String expectedPackage) {
    GoFile file = (GoFile)new GoCreateFileAction().createFile(newFileName, GoCreateFileAction.FILE_TEMPLATE, dir);
    assertNotNull(file);
    assertEquals(expectedPackage, file.getPackageName());
  }
}

