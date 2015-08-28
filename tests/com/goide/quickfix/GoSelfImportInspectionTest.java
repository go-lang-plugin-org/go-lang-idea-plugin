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

package com.goide.quickfix;

import com.goide.inspections.GoSelfImportInspection;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.IOException;

public class GoSelfImportInspectionTest extends GoQuickFixTestBase {
  @Override
  public void setUp() throws Exception {
    super.setUp();
    myFixture.enableInspections(GoSelfImportInspection.class);
  }

  @Override
  protected boolean isWriteActionRequired() {
    return false;
  }

  public void testRemoveSelfImport() throws IOException {
    VirtualFile file = myFixture.getTempDirFixture().createFile("path/a.go", "package pack;" +
                                                                             "import <error descr=\"Self import is not allowed\"><caret>\"path\"</error>");
    myFixture.configureFromExistingVirtualFile(file);
    myFixture.checkHighlighting();
    applySingleQuickFix("Remove self import");
  }

  public void testRemoveRelativeSelfImport() {
    myFixture.configureByText("a.go", "package pack; import <error descr=\"Self import is not allowed\"><caret>\".\"</error>");
    myFixture.checkHighlighting();
    applySingleQuickFix("Remove self import");
  }

  public void testDoNotConsiderImportFromTestPackageAsSelfImport() throws IOException {
    VirtualFile file = myFixture.getTempDirFixture().createFile("path/a_test.go", "package pack_test; import <caret>\"path\"");
    myFixture.configureFromExistingVirtualFile(file);
    myFixture.checkHighlighting();
  }
}
