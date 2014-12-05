/*
 * Copyright 2013-2014 Sergey Ignatov, Alexander Zolotov
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
import com.goide.sdk.GoSdkType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.projectRoots.ProjectJdkTable;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkModificator;
import com.intellij.openapi.projectRoots.impl.ProjectJdkImpl;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.fixtures.DefaultLightProjectDescriptor;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.List;

abstract public class GoCodeInsightFixtureTestCase extends LightPlatformCodeInsightFixtureTestCase {
  @Override
  protected String getTestDataPath() {
    return new File("testData/" + getBasePath()).getAbsolutePath();
  }

  protected static DefaultLightProjectDescriptor createMockProjectDescriptor() {
    return new DefaultLightProjectDescriptor() {
      @Override
      public Sdk getSdk() {
        String version = "1.1.2";
        return createMockSdk("testData/mockSdk-" + version + "/", version);
      }
    };
  }

  protected void setUpProjectSdk() {
    ApplicationManager.getApplication().runWriteAction(new Runnable() {
      @Override
      public void run() {
        Sdk sdk = getProjectDescriptor().getSdk();
        ProjectJdkTable.getInstance().addJdk(sdk);
        ProjectRootManager.getInstance(myFixture.getProject()).setProjectSdk(sdk);
      }
    });
  }

  @NotNull
  private static Sdk createMockSdk(@NotNull String sdkHome, @NotNull String version) {
    GoSdkType instance = GoSdkType.getInstance();
    String release = "Go " + version;
    Sdk sdk = new ProjectJdkImpl(release, instance);
    SdkModificator sdkModificator = sdk.getSdkModificator();
    sdkModificator.setHomePath(sdkHome);
    sdkModificator.setVersionString(release); // must be set after home path, otherwise setting home path clears the version string
    sdkModificator.commitChanges();
    instance.setupSdkPaths(sdk);
    return sdk;
  }
  
  @NotNull
  protected List<GoFile> addPackage(@NotNull String importPath, @NotNull String... fileNames) {
    List<GoFile> files = ContainerUtil.newArrayListWithCapacity(fileNames.length);
    for (String fileName : fileNames) {
      VirtualFile virtualFile = getFile(fileName);
      assertNotNull(virtualFile);
      String text = loadText(virtualFile);
      PsiFile file = myFixture.addFileToProject(FileUtil.toCanonicalPath(importPath + "/" + virtualFile.getName()), text);
      if (file instanceof GoFile) {
        files.add((GoFile)file);
      }
      else {
        throw new RuntimeException("Can't create a new go file by the virtual one: " + virtualFile);
      }
    }
    return files;
  }

  @NotNull
  protected static String loadText(@NotNull VirtualFile file) {
    try {
      return VfsUtilCore.loadText(file);
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Nullable
  private VirtualFile getFile(@NotNull String fileName) {
    VirtualFile file = VfsUtil.findFileByIoFile(new File(getTestDataPath() + "/" + getTestName(true) + "/" + fileName), true);
    if (file != null) return file;
    file = VfsUtil.findFileByIoFile(new File(getTestDataPath() + "/" + fileName), true);
    if (file != null) return file;
    return VfsUtil.findFileByIoFile(new File(fileName), true);
  }
}
