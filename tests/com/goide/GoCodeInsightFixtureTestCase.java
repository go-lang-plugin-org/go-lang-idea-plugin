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

package com.goide;

import com.goide.project.GoApplicationLibrariesService;
import com.goide.sdk.GoSdkType;
import com.goide.sdk.GoSdkUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.projectRoots.ProjectJdkTable;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkModificator;
import com.intellij.openapi.projectRoots.impl.ProjectJdkImpl;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.testFramework.fixtures.DefaultLightProjectDescriptor;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

abstract public class GoCodeInsightFixtureTestCase extends LightPlatformCodeInsightFixtureTestCase {
  @Override
  protected void tearDown() throws Exception {
    try {
      GoApplicationLibrariesService.getInstance().setLibraryRootUrls();
    }
    finally {
      super.tearDown();
    }
  }

  @Override
  protected final String getTestDataPath() {
    return new File("testData/" + getBasePath()).getAbsolutePath();
  }

  @NotNull
  protected static DefaultLightProjectDescriptor createMockProjectDescriptor() {
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
  private static Sdk createMockSdk(@NotNull String version) {
    Sdk sdk = new ProjectJdkImpl("Go " + version, GoSdkType.getInstance());
    SdkModificator sdkModificator = sdk.getSdkModificator();

    String homePath = new File("testData/mockSdk-" + version + "/").getAbsolutePath();
    sdkModificator.setHomePath(homePath);
    sdkModificator.setVersionString(version); // must be set after home path, otherwise setting home path clears the version string
    for (VirtualFile file : GoSdkUtil.getSdkDirectoriesToAttach(homePath, version)) {
      sdkModificator.addRoot(file, OrderRootType.CLASSES);
    }
    sdkModificator.commitChanges();
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
}
