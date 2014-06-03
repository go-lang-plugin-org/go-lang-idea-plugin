package com.goide;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.projectRoots.ProjectJdkTable;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkModificator;
import com.intellij.openapi.projectRoots.impl.ProjectJdkImpl;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.testFramework.fixtures.DefaultLightProjectDescriptor;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;
import org.jetbrains.annotations.NotNull;

import java.io.File;

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
}
