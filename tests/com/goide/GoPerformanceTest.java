package com.goide;

import com.goide.inspections.GoUnusedImportDeclaration;
import com.goide.inspections.unresolved.GoUnresolvedReferenceInspection;
import com.goide.inspections.unresolved.GoUnusedFunctionInspection;
import com.goide.inspections.unresolved.GoUnusedGlobalVariableInspection;
import com.goide.inspections.unresolved.GoUnusedVariableInspection;
import com.intellij.analysis.AnalysisScope;
import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.InspectionProfileEntry;
import com.intellij.codeInspection.ex.GlobalInspectionContextImpl;
import com.intellij.codeInspection.ex.InspectionManagerEx;
import com.intellij.codeInspection.ex.InspectionToolRegistrar;
import com.intellij.codeInspection.ex.InspectionToolWrapper;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.testFramework.InspectionTestUtil;
import com.intellij.testFramework.PlatformTestUtil;
import com.intellij.testFramework.fixtures.impl.CodeInsightTestFixtureImpl;
import com.intellij.util.ThrowableRunnable;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class GoPerformanceTest extends GoCodeInsightFixtureTestCase {

  public void _testUnusedVariable() {
    doInspectionTest(new GoUnusedVariableInspection(), (int)TimeUnit.MINUTES.toMillis(4));
  }

  public void _testUnusedGlobalVariable() {
    doInspectionTest(new GoUnusedGlobalVariableInspection(), (int)TimeUnit.MINUTES.toMillis(4));
  }

  public void _testUnresolvedReference() {
    doInspectionTest(new GoUnresolvedReferenceInspection(), (int)TimeUnit.MINUTES.toMillis(4));
  }

  public void testUnusedFunction() {
    doInspectionTest(new GoUnusedFunctionInspection(), (int)TimeUnit.MINUTES.toMillis(3));
  }

  public void testUnusedImport() {
    doInspectionTest(new GoUnusedImportDeclaration(), (int)TimeUnit.MINUTES.toMillis(1));
  }

  private void doInspectionTest(@NotNull InspectionProfileEntry tool, int expected) {
    if (!new File(myFixture.getTestDataPath(), "docker").exists()) {
      System.err.println("For performance tests you need to have a docker project inside testData/" + getBasePath() + " directory");
      return;
    }

    VirtualFile sourceDir = myFixture.copyDirectoryToProject("docker", "src");
    //noinspection ConstantConditions
    final AnalysisScope scope = new AnalysisScope(getPsiManager().findDirectory(sourceDir));

    scope.invalidate();

    final InspectionManagerEx inspectionManager = (InspectionManagerEx)InspectionManager.getInstance(getProject());
    final InspectionToolWrapper wrapper = InspectionToolRegistrar.wrapTool(tool);
    final GlobalInspectionContextImpl globalContext =
      CodeInsightTestFixtureImpl.createGlobalContextForTool(scope, getProject(), inspectionManager, wrapper);

    PlatformTestUtil.startPerformanceTest(getTestName(true), expected, new ThrowableRunnable() {
      @Override
      public void run() throws Throwable {
        InspectionTestUtil.runTool(wrapper, scope, globalContext, inspectionManager);
        InspectionTestUtil.compareToolResults(globalContext, wrapper, false, new File(getTestDataPath(), wrapper.getShortName()).getPath());
      }
    }).cpuBound().attempts(3).usesAllCPUCores().assertTiming();
  }

  @Override
  protected String getBasePath() {
    return "performance";
  }

  @Override
  protected boolean isWriteActionRequired() {
    return false;
  }
}
