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

package com.goide.psi.legacy;

import com.goide.GoCodeInsightFixtureTestCase;
import com.goide.project.GoModuleLibrariesService;
import com.goide.psi.GoFile;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.FilteringProcessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public abstract class GoLegacyResolveTestBase extends GoCodeInsightFixtureTestCase {
  @NotNull private static final String REF_MARK = "/*ref*/";
  @NotNull private static final String NO_REF_MARK = "/*no ref*/";
  @NotNull private static final String DEF_MARK = "/*def*/";

  @Nullable private PsiReference myReference;
  @Nullable private PsiElement myDefinition;
  private boolean myShouldBeResolved = true;

  @Override
  protected String getBasePath() {
    return "psi/resolve";
  }

  protected void doFileTest() {
    processPsiFile((GoFile)myFixture.configureByFile(getTestName(false) + ".go"));
    doResolveTest();
  }
  
  protected void doDirTest() {
    String testDataPath = getTestDataPath();
    String testName = getTestName(false);
    File fromDir = new File(testDataPath + "/" + testName);
    if (!fromDir.isDirectory()) {
      throw new RuntimeException("Given file is not directory: " + fromDir);
    }
    //noinspection ConstantConditions
    for (File file : fromDir.listFiles()) {
      if (file.isDirectory()) {
        myFixture.copyDirectoryToProject(testName + "/" + file.getName(), file.getName());
      }
      else {
        myFixture.copyFileToProject(testName + "/" + file.getName());
      }
    }
    VirtualFile dirToTest = myFixture.getTempDirFixture().getFile(".");
    assertNotNull(dirToTest);
    GoModuleLibrariesService.getInstance(myFixture.getModule()).setLibraryRootUrls(dirToTest.getUrl());
    doDirectoryTest(dirToTest);
  }

  private void doResolveTest() {
    if (myReference == null) fail("no reference defined in test case");
    if (myShouldBeResolved && !allowNullDefinition() && myDefinition == null) fail("no definition defined in test case");
    PsiElement resolve = myReference.resolve();
    if (myShouldBeResolved) {
      assertNotNull("cannot resolve reference " + myReference.getCanonicalText(), resolve);
      if (myDefinition != null) {
        PsiElement def = PsiTreeUtil.getParentOfType(myDefinition, resolve.getClass(), false);
        assertSame("element resolved in non-expected element from " + getFileName(resolve) + ":\n" + resolve.getText(), 
                   def, resolve);
      }
    }
    else if (resolve != null) {
        fail("element is resolved but it wasn't should. resolved to element from " + getFileName(resolve) + ":\n" + resolve.getText());
      }
  }

  @NotNull
  private static String getFileName(@NotNull PsiElement resolve) {
    return resolve instanceof PsiFile ? ((PsiFile)resolve).getName() : resolve.getContainingFile().getName();
  }

  protected boolean allowNullDefinition() {
    return false;
  }
  
  private void processPsiFile(@NotNull GoFile file) {
    String fileContent = loadText(file.getVirtualFile());

    String fileName = file.getName();
    int refIndex = fileContent.indexOf(REF_MARK);
    if (refIndex != -1) {
      int offset = refIndex + REF_MARK.length();
      myReference = findReference(file, offset);
    }
    int noRefIndex = fileContent.indexOf(NO_REF_MARK);
    if (noRefIndex != -1) {
      int offset = noRefIndex + NO_REF_MARK.length();
      myReference = findReference(file, offset);
      myShouldBeResolved = false;
    }

    int defIndex = fileContent.indexOf(DEF_MARK);
    if (defIndex != -1) {
      if (myDefinition != null) fail("only one definition should be allowed in a resolve test case, see file: " + fileName);
      int offset = defIndex + DEF_MARK.length();
      myDefinition = file.findElementAt(offset);
      if (myDefinition == null) fail("no definition was found at mark in file: " + fileName + ", offset: " + offset);
    }
  }

  @NotNull
  private PsiReference findReference(@NotNull GoFile file, int offset) {
    if (myReference != null) fail("only one reference should be declared in a test case, see file: " + file.getName());
    PsiReference result = file.findReferenceAt(offset);
    if (result == null) fail("no reference was found at mark in file: " + file.getName() + ", offset: " + offset);
    return result;
  }

  private void doDirectoryTest(@NotNull VirtualFile file) {
    VfsUtilCore.processFilesRecursively(file, new FilteringProcessor<>(
                                          virtualFile -> !virtualFile.isDirectory() && virtualFile.getName().endsWith(".go"),
                                          virtualFile -> {
                                            PsiFile goFile = myFixture.getPsiManager().findFile(virtualFile);
                                            assert goFile instanceof GoFile;
                                            processPsiFile((GoFile)goFile);
                                            return true;
                                          }
                                        ));
    doResolveTest();
  }
}
