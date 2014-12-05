/*
 * Copyright 2013-2014 Sergey Ignatov, Alexander Zolotov, Mihai Toader
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

package com.goide;

import com.goide.psi.GoFile;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;

import java.io.IOException;

public abstract class GoResolvingTestCase extends GoCodeInsightFixtureTestCase {

  public String REF_MARK = "/*ref*/";
  public String DEF_MARK = "/*def*/";

  PsiReference ref;
  PsiElement def;

  @Override
  protected void setUp() throws Exception {
    super.setUp();

    ref = null;
    def = null;
  }

  protected void doResolveTest() {
    doResolveTest(getTestName(true) + ".go");
  }

  private void doResolveTest(String referenceFile) {
    processPsiFile((GoFile)myFixture.configureByFile(referenceFile));

    if (ref == null)
      fail("no reference defined in test case");

    PsiElement foundDef = ref.resolve();

    if (foundDef != null && def == null)
      fail("element resolved but it shouldn't have");

    if (foundDef == null && def != null) {
      fail("element didn't resolve when it should have");
    }

    if (foundDef != null && def != null) {
      while ( def.getNavigationElement() != foundDef.getNavigationElement() && def.getStartOffsetInParent() == 0)
        def = def.getParent();

      assertSame(def.getNavigationElement(), foundDef.getNavigationElement());
    }
  }

  @Override
  protected GoFile[] addPackage(String importPath, String... fileNames) {
    GoFile[] files = super.addPackage(importPath, fileNames);
    for (GoFile file : files) {
      if ( file != null ) {
        processPsiFile(file);
      }
    }

    return files;
  }

  private void processPsiFile(GoFile file)  {
    String fileContent = null;
    try {
      fileContent = VfsUtilCore.loadText(file.getVirtualFile());
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }

    int refIndex = fileContent.indexOf(REF_MARK);
    if (refIndex != -1) {
      if (ref != null)
        fail("only once reference should be declared in a test case");

      ref = file.findReferenceAt(refIndex + REF_MARK.length());

      if (ref == null)
        fail("no reference was found as marked");
    }

    int defIndex = fileContent.indexOf(DEF_MARK);

    if ( defIndex != -1 ) {
      if (def != null)
        fail("only one definition should be allowed in a resolve test case");

      def = file.findElementAt(defIndex + DEF_MARK.length());

      if ( def == null )
        fail("no definition was found where marked");
    }
  }

  @Override
  protected String getBasePath() {
    return "psi/resolve";
  }
}
