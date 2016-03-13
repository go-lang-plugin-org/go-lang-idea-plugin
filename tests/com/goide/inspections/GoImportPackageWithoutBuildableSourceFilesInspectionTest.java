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
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.PsiFile;

public class GoImportPackageWithoutBuildableSourceFilesInspectionTest extends GoQuickFixTestBase {
  @Override
  public void setUp() throws Exception {
    super.setUp();
    myFixture.enableInspections(GoImportPackageWithoutBuildableSourceFilesInspection.class);
  }

  @Override
  protected boolean isWriteActionRequired() {
    return false;
  }

  @SuppressWarnings("ConstantConditions")
  public void testSimple() {
    PsiFile file = myFixture.addFileToProject("withSources/a.go", "");
    WriteCommandAction.runWriteCommandAction(myFixture.getProject(), new Runnable() {
      @Override
      public void run() {
        file.getParent().getParent().createSubdirectory("withoutSources");
      }
    });
    myFixture.configureByText("a.go", "package pack\n" +
                                      "import `withSources`\n" +
                                      "import <error descr=\"'/src/withoutSources' has no buildable Go source files\">`withoutSources`</error>\n" +
                                      "import <error descr=\"'/src/withoutSources' has no buildable Go source files\">_ `without<caret>Sources`</error>\n" +
                                      "import `unresolved`\n");
    myFixture.checkHighlighting();
    applySingleQuickFix(GoDeleteImportQuickFix.QUICK_FIX_NAME);
    myFixture.checkResult("package pack\n" +
                          "import `withSources`\n" +
                          "import `withoutSources`\n" +
                          "import `unresolved`\n");
  }
}
