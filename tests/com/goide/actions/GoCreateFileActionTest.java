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
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;

import java.io.IOException;

public class GoCreateFileActionTest extends GoCodeInsightFixtureTestCase {
  @Override
  protected String getBasePath() {
    return "actions";
  }

  public void testPackageNameCreateFileInEmptyDirectory() throws IOException {
    PsiDirectory dir = myFixture.getPsiManager().findDirectory(getProject().getBaseDir().createChildDirectory(this, "empty-dir"));
    assertNotNull(dir);
    PsiFile file = new GoCreateFileAction().createFile("a", "Go File", dir);
    assertNotNull(file);
    assertEquals(((GoFile)file).getPackageName(), "empty_dir");
  }

  public void testPackageNameCreateFilesInDirectoryWithExistingPackage() {
    VirtualFile virtualDir = myFixture.copyDirectoryToProject("createFile", "");
    PsiDirectory dir = myFixture.getPsiManager().findDirectory(virtualDir);
    assertNotNull(dir);
    GoCreateFileAction action = new GoCreateFileAction();

    GoFile file = (GoFile)action.createFile("b", "Go File", dir);
    assertNotNull(file);
    assertEquals(file.getPackageName(), "a");

    file = (GoFile)action.createFile("a_test", "Go File", dir);
    assertNotNull(file);
    assertEquals(file.getPackageName(), "a_test");

    file = (GoFile)action.createFile("c.go", "Go File", dir);
    assertNotNull(file);
    assertEquals(file.getPackageName(), "a");

    file = (GoFile)action.createFile("c_test.go", "Go File", dir);
    assertNotNull(file);
    assertEquals(file.getPackageName(), "a_test");
  }
}

