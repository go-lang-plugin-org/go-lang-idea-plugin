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
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class GoLegacyResolveTestBase extends GoCodeInsightFixtureTestCase {
  @NotNull public String REF_MARK = "/*ref*/";
  @NotNull public String DEF_MARK = "/*def*/";

  @Nullable PsiReference myReference;
  @Nullable PsiElement myDefinition;

  @Override
  protected String getBasePath() {
    return "psi/resolve";
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    myReference = null;
    myDefinition = null;
  }

  @Override
  protected void tearDown() throws Exception {
    myReference = null;
    myDefinition = null;
    super.tearDown();
  }

  protected void doResolveTest() {
    doResolveTest(getTestName(true) + ".go");
  }

  private void doResolveTest(@NotNull String referenceFile) {
    processPsiFile((GoFile)myFixture.configureByFile(referenceFile));
    if (myReference == null) fail("no reference defined in test case");
    PsiElement resolve = myReference.resolve();
    if (resolve != null && myDefinition == null) fail("element resolved but it shouldn't have");
    if (resolve == null && myDefinition != null) fail("element didn't resolve when it should have");
    if (resolve != null) {
      while (!myDefinition.getNavigationElement().isEquivalentTo(resolve.getNavigationElement()) && myDefinition.getStartOffsetInParent() == 0) {
        myDefinition = myDefinition.getParent();
      }
      assertSame(myDefinition.getNavigationElement(), resolve.getNavigationElement());
    }
  }

  @NotNull
  @Override
  protected List<GoFile> addPackage(@NotNull String importPath, @NotNull String... fileNames) {
    List<GoFile> files = super.addPackage(importPath, fileNames);
    for (GoFile file : files) processPsiFile(file);
    return files;
  }

  private void processPsiFile(@NotNull GoFile file) {
    String fileContent = loadText(file.getVirtualFile());
    
    int refIndex = fileContent.indexOf(REF_MARK);
    if (refIndex != -1) {
      if (myReference != null) fail("only once reference should be declared in a test case, see file: " + file);
      int offset = refIndex + REF_MARK.length();
      myReference = file.findReferenceAt(offset);
      if (myReference == null) fail("no reference was found as marked in file: " + file + ", offset: " + offset);
    }

    int defIndex = fileContent.indexOf(DEF_MARK);
    if (defIndex != -1) {
      if (myDefinition != null) fail("only one definition should be allowed in a resolve test case, see file: " + file);
      int offset = defIndex + DEF_MARK.length();
      myDefinition = file.findElementAt(offset);
      if (myDefinition == null) fail("no definition was found where marked in file: " + file + ", offset: " + offset);
    }
  }
}
