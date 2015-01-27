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

import com.goide.sdk.GoSdkType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.projectRoots.ProjectJdkTable;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkModificator;
import com.intellij.openapi.projectRoots.impl.ProjectJdkImpl;
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
    sdkModificator.setVersionString(version); // must be set after home path, otherwise setting home path clears the version string
    sdkModificator.commitChanges();
    instance.setupSdkPaths(sdk);
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
