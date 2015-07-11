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

package com.goide.actions;

import com.goide.GoCodeInsightFixtureTestCase;
import com.goide.psi.GoFile;
import com.intellij.psi.PsiDirectory;
import org.jetbrains.annotations.NotNull;

public class GoCreateFileActionTest extends GoCodeInsightFixtureTestCase {
  private static void createFileAndCheckPackage(@NotNull PsiDirectory dir, @NotNull String name, @NotNull String packageName) {
    GoFile file = (GoFile)new GoCreateFileAction().createFile(name, "Go File", dir);
    assertNotNull(file);
    assertEquals(packageName, file.getPackageName());
  }

  public void testPackageNameInEmptyDirectory() throws Exception {
    PsiDirectory dir = myFixture.getPsiManager().findDirectory(getProject().getBaseDir().createChildDirectory(this, "1empty-dir"));
    assertNotNull(dir);
    createFileAndCheckPackage(dir, "a", "_empty_dir");
  }

  private void createFileInExistingPackage(@NotNull String name, @NotNull String packageName) {
    myFixture.configureByText("a.go", "package a");
    PsiDirectory dir = myFixture.getFile().getContainingDirectory();
    createFileAndCheckPackage(dir, name, packageName);
  }

  public void testPackageNameInExistingPackage() {
    createFileInExistingPackage("b", "a");
  }

  public void testTestPackageNameInExistingPackage() {
    createFileInExistingPackage("a_test", "a_test");
  }

  public void testPackageNameInExistingPackageWithExtension() {
    createFileInExistingPackage("b.go", "a");
  }

  public void testTestPackageNameInExistingPackageWithExtension() {
    createFileInExistingPackage("a_test.go", "a_test");
  }
}

