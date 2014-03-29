package com.goide.inspections;

import com.goide.GoCodeInsightFixtureTestCase;
import com.goide.GoSdkType;
import com.goide.inspections.unresolved.GoAssignmentToConstantInspection;
import com.goide.inspections.unresolved.GoUnresolvedReferenceInspection;
import com.goide.inspections.unresolved.GoUnusedFunctionInspection;
import com.goide.inspections.unresolved.GoUnusedVariableInspection;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.projectRoots.ProjectJdkTable;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkModificator;
import com.intellij.openapi.projectRoots.impl.ProjectJdkImpl;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.fixtures.DefaultLightProjectDescriptor;
import org.jetbrains.annotations.NotNull;

public class GoHighlightingTest extends GoCodeInsightFixtureTestCase {
  @Override
  public void setUp() throws Exception {
    super.setUp();
    setUpProjectSdk();
    myFixture.enableInspections(
      new GoUnresolvedReferenceInspection(),
      new GoDuplicateFieldsOrMethodsInspection(),
      new GoUnusedVariableInspection(),
      new GoUnusedFunctionInspection(),
      new GoAssignmentToConstantInspection()
    );
  }

  private void doTest() {
    myFixture.testHighlighting(true, false, false, getTestName(true) + ".go");
  }

  @Override
  protected String getBasePath() {
    return "highlighting";
  }

  @Override
  protected boolean isWriteActionRequired() {
    return false;
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

  @Override
  protected LightProjectDescriptor getProjectDescriptor() {
    return new DefaultLightProjectDescriptor() {
      @Override
      public Sdk getSdk() {
        String version = "1.1.2";
        return createMockSdk("testData/mockSdk-" + version + "/", version);
      }
    };
  }

  @NotNull
  public static Sdk createMockSdk(@NotNull String sdkHome, @NotNull String version) {
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

  public void testSimple() throws Exception { doTest(); }
  public void testStruct() throws Exception { doTest(); }
  public void testBoxes()  throws Exception { doTest(); }
  public void testRanges() throws Exception { doTest(); }
}
