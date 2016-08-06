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

package com.goide;

import com.goide.project.GoApplicationLibrariesService;
import com.goide.project.GoBuildTargetSettings;
import com.goide.project.GoModuleSettings;
import com.goide.sdk.GoSdkType;
import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.ProjectJdkTable;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.impl.ProjectJdkImpl;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.impl.DebugUtil;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.UsefulTestCase;
import com.intellij.testFramework.fixtures.DefaultLightProjectDescriptor;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;
import com.intellij.util.ObjectUtils;
import com.intellij.util.ThreeState;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.indexing.FileContentImpl;
import com.intellij.util.indexing.IndexingDataKeys;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.List;

abstract public class GoCodeInsightFixtureTestCase extends LightPlatformCodeInsightFixtureTestCase {
  protected static String buildStubTreeText(@NotNull Project project,
                                            @NotNull VirtualFile file,
                                            @NotNull String fileContent,
                                            boolean checkErrors) throws IOException {
    String path = file.getPath();
    PsiFile psi = PsiFileFactory.getInstance(project).createFileFromText(file.getName(), file.getFileType(), fileContent);
    if (checkErrors) {
      assertFalse(path + " contains error elements", DebugUtil.psiToString(psi, true).contains("PsiErrorElement"));
    }
    String full = DebugUtil.stubTreeToString(GoFileElementType.INSTANCE.getBuilder().buildStubTree(psi));
    psi.putUserData(IndexingDataKeys.VIRTUAL_FILE, file);
    FileContentImpl content = new FileContentImpl(file, fileContent, file.getCharset());
    PsiFile psiFile = content.getPsiFile();
    String fast = DebugUtil.stubTreeToString(GoFileElementType.INSTANCE.getBuilder().buildStubTree(psiFile));
    if (!Comparing.strEqual(full, fast)) {
      System.err.println(path);
      UsefulTestCase.assertSameLines(full, fast);
    }
    return fast;
  }

  @NotNull
  protected PsiElement findElementAtCaretOrInSelection() {
    SelectionModel selectionModel = myFixture.getEditor().getSelectionModel();
    if (selectionModel.hasSelection()) {
      PsiElement left = myFixture.getFile().findElementAt(selectionModel.getSelectionStart());
      PsiElement right = myFixture.getFile().findElementAt(selectionModel.getSelectionEnd() - 1);
      assertNotNull(left);
      assertNotNull(right);
      return ObjectUtils.assertNotNull(PsiTreeUtil.findCommonParent(left, right));
    }
    else {
      return ObjectUtils.assertNotNull(myFixture.getFile().findElementAt(myFixture.getEditor().getCaretModel().getOffset()));
    }
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    GoApplicationLibrariesService.getInstance().setLibraryRootUrls("temp:///");
    GoModuleSettings.getInstance(myFixture.getModule()).setVendoringEnabled(ThreeState.YES);
    if (isSdkAware()) setUpProjectSdk();
  }
  
  @Override
  protected void tearDown() throws Exception {
    try {
      GoApplicationLibrariesService.getInstance().setLibraryRootUrls();
      GoModuleSettings.getInstance(myFixture.getModule()).setBuildTargetSettings(new GoBuildTargetSettings());
      GoModuleSettings.getInstance(myFixture.getModule()).setVendoringEnabled(ThreeState.UNSURE);
    }
    finally {
      //noinspection ThrowFromFinallyBlock
      super.tearDown();
    }
  }

  // todo[zolotov] remove after 2016.3
  @Override
  protected boolean isWriteActionRequired() {
    return false;
  }

  @Override
  protected final String getTestDataPath() {
    return new File("testData/" + getBasePath()).getAbsolutePath();
  }

  @NotNull
  private static DefaultLightProjectDescriptor createMockProjectDescriptor() {
    return new DefaultLightProjectDescriptor() {
      @NotNull
      @Override
      public Sdk getSdk() {
        return createMockSdk("1.1.2");
      }

      @NotNull
      @Override
      public ModuleType getModuleType() {
        return GoModuleType.getInstance();
      }
    };
  }

  private void setUpProjectSdk() {
    ApplicationManager.getApplication().runWriteAction(() -> {
      Sdk sdk = getProjectDescriptor().getSdk();
      ProjectJdkTable.getInstance().addJdk(sdk);
      ProjectRootManager.getInstance(myFixture.getProject()).setProjectSdk(sdk);
    });
  }

  @NotNull
  private static Sdk createMockSdk(@NotNull String version) {
    String homePath = new File("testData/mockSdk-" + version + "/").getAbsolutePath();
    GoSdkType sdkType = GoSdkType.getInstance();
    ProjectJdkImpl sdk = new ProjectJdkImpl("Go " + version, sdkType, homePath, version);
    sdkType.setupSdkPaths(sdk);
    sdk.setVersionString(version);
    return sdk;
  }

  @NotNull
  protected static String loadText(@NotNull VirtualFile file) {
    try {
      return StringUtil.convertLineSeparators(VfsUtilCore.loadText(file));
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  protected void disableVendoring() {
    GoModuleSettings.getInstance(myFixture.getModule()).setVendoringEnabled(ThreeState.NO);
  }

  protected static String normalizeCode(@NotNull String codeBefore) {
    StringBuilder result = new StringBuilder("package main\nfunc main() {\n");
    if ("\n".equals(codeBefore)) {
      result.append(codeBefore);
    }
    for (String line : StringUtil.splitByLines(codeBefore, false)) {
      result.append('\t').append(line).append('\n');
    }
    result.append("}");
    return result.toString();
  }

  @Override
  protected LightProjectDescriptor getProjectDescriptor() {
    return isSdkAware() ? createMockProjectDescriptor() : null;
  }

  private boolean isSdkAware() {return annotatedWith(SdkAware.class);}

  protected void applySingleQuickFix(@NotNull String quickFixName) {
    List<IntentionAction> availableIntentions = myFixture.filterAvailableIntentions(quickFixName);
    IntentionAction action = ContainerUtil.getFirstItem(availableIntentions);
    assertNotNull(action);
    myFixture.launchAction(action);
  }
}
