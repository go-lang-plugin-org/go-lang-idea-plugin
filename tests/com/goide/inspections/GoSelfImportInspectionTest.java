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

import com.goide.quickfix.GoDeleteImportQuickFix;
import com.goide.quickfix.GoQuickFixTestBase;
import com.intellij.psi.PsiFile;

public class GoSelfImportInspectionTest extends GoQuickFixTestBase {
  @Override
  public void setUp() throws Exception {
    super.setUp();
    myFixture.enableInspections(GoSelfImportInspection.class);
  }

  public void testRemoveSelfImport() {
    PsiFile file = myFixture.addFileToProject("path/a.go", "package pack;" +
                                                           "import <error descr=\"Self import is not allowed\"><caret>\"path\"</error>");
    myFixture.configureFromExistingVirtualFile(file.getVirtualFile());
    myFixture.checkHighlighting();
    applySingleQuickFix(GoDeleteImportQuickFix.QUICK_FIX_NAME);
    myFixture.checkResult("package pack;");
  }

  public void testRemoveRelativeSelfImport() {
    myFixture.configureByText("a.go", "package pack;import <error descr=\"Self import is not allowed\"><caret>\".\"</error>");
    myFixture.checkHighlighting();
    applySingleQuickFix(GoDeleteImportQuickFix.QUICK_FIX_NAME);
    myFixture.checkResult("package pack;");
  }

  public void testDoNotConsiderImportFromTestPackageAsSelfImport() {
    PsiFile file = myFixture.addFileToProject("path/a_test.go", "package pack_test; import <caret>\"path\"");
    myFixture.configureFromExistingVirtualFile(file.getVirtualFile());
    myFixture.checkHighlighting();
  }

  public void testVendoringSelfImport() {
    PsiFile file = myFixture.addFileToProject("vendor/vendorPackage/a.go", "package vendorPackage;" +
                                                                           "import <error descr=\"Self import is not allowed\"><caret>\"vendorPackage\"</error>");
    myFixture.configureFromExistingVirtualFile(file.getVirtualFile());
    myFixture.checkHighlighting();
  }
}
