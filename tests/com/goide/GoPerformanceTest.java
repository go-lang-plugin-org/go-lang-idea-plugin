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

import com.goide.categories.Performance;
import com.goide.completion.GoCompletionUtil;
import com.goide.inspections.GoUnusedImportInspection;
import com.goide.inspections.unresolved.*;
import com.goide.project.GoBuildTargetSettings;
import com.goide.project.GoModuleSettings;
import com.intellij.analysis.AnalysisScope;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.InspectionProfileEntry;
import com.intellij.codeInspection.ex.InspectionManagerEx;
import com.intellij.codeInspection.ex.InspectionToolRegistrar;
import com.intellij.codeInspection.ex.InspectionToolWrapper;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileVisitor;
import com.intellij.testFramework.InspectionTestUtil;
import com.intellij.testFramework.PlatformTestUtil;
import com.intellij.testFramework.fixtures.impl.CodeInsightTestFixtureImpl;
import com.intellij.testFramework.fixtures.impl.GlobalInspectionContextForTests;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.experimental.categories.Category;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Category(Performance.class)
public class GoPerformanceTest extends GoCodeInsightFixtureTestCase {
  @Override
  public void setUp() throws Exception {
    super.setUp();
    GoBuildTargetSettings buildTargetSettings = new GoBuildTargetSettings();
    buildTargetSettings.os = "darwin";
    buildTargetSettings.arch = "amd64";
    buildTargetSettings.goVersion = "1.5.1";
    GoModuleSettings.getInstance(myFixture.getModule()).setBuildTargetSettings(buildTargetSettings);
  }

  public void testUnusedVariable() {
    doInspectionTest(new GoUnusedVariableInspection(), TimeUnit.SECONDS.toMillis(30));
  }

  public void testUnusedGlobalVariable() {
    doInspectionTest(new GoUnusedGlobalVariableInspection(), TimeUnit.SECONDS.toMillis(30));
  }

  public void _testUnresolvedReference() {
    doInspectionTest(new GoUnresolvedReferenceInspection(), TimeUnit.MINUTES.toMillis(4));
  }

  public void testUnusedFunction() {
    doInspectionTest(new GoUnusedFunctionInspection(), TimeUnit.SECONDS.toMillis(15));
  }

  public void testUnusedExportedFunction() {
    doInspectionTest(new GoUnusedExportedFunctionInspection(), TimeUnit.SECONDS.toMillis(30));
  }

  public void testUnusedImport() {
    doInspectionTest(new GoUnusedImportInspection(), TimeUnit.SECONDS.toMillis(20));
  }

  public void testPerformanceA() {
    doHighlightingTest(TimeUnit.SECONDS.toMillis(10));
  }

  public void testPerformanceA2() {
    doHighlightingTest(TimeUnit.SECONDS.toMillis(10));
  }

  public void testCompletionPerformance() {
    doCompletionTest("package main; func main() { <caret> }", 2, TimeUnit.SECONDS.toMillis(15));
  }

  public void testCompletionWithPrefixPerformance() {
    doCompletionTest("package main; func main() { slee<caret> }", 1, TimeUnit.SECONDS.toMillis(5));
  }
  
  public void testCompletionPerformanceWithoutTypes() {
    GoCompletionUtil.disableTypeInfoInLookup(getTestRootDisposable());
    doCompletionTest("package main; func main() { <caret> }", 2, TimeUnit.SECONDS.toMillis(15));
  }

  public void testCompletionWithPrefixPerformanceWithoutTypes() {
    GoCompletionUtil.disableTypeInfoInLookup(getTestRootDisposable());
    doCompletionTest("package main; func main() { slee<caret> }", 1, TimeUnit.SECONDS.toMillis(5));
  }

  private void doCompletionTest(@NotNull String source, int invocationCount, long expectation) {
    VirtualFile go = installTestData("go");
    if (go == null) return;
    
    myFixture.configureByText(GoFileType.INSTANCE, source);
    PlatformTestUtil.startPerformanceTest(getTestName(true), (int)expectation,
                                          () -> myFixture.complete(CompletionType.BASIC, invocationCount)).cpuBound().usesAllCPUCores().assertTiming();
  }

  private void doHighlightingTest(long expectation) {
    PlatformTestUtil.startPerformanceTest(getTestName(true), (int)expectation,
                                          () -> myFixture.testHighlighting(true, false, false, getTestName(true) + ".go")).cpuBound().usesAllCPUCores().assertTiming();
  }

  private void doInspectionTest(@NotNull InspectionProfileEntry tool, long expected) {
    VirtualFile sourceDir = installTestData("docker");
    if (sourceDir == null) return;
    //noinspection ConstantConditions
    AnalysisScope scope = new AnalysisScope(getPsiManager().findDirectory(sourceDir));

    scope.invalidate();

    InspectionManagerEx inspectionManager = (InspectionManagerEx)InspectionManager.getInstance(getProject());
    InspectionToolWrapper wrapper = InspectionToolRegistrar.wrapTool(tool);
    GlobalInspectionContextForTests globalContext =
      CodeInsightTestFixtureImpl.createGlobalContextForTool(scope, getProject(), inspectionManager, wrapper);

    PlatformTestUtil.startPerformanceTest(getTestName(true), (int)expected, () -> InspectionTestUtil.runTool(wrapper, scope, globalContext)).cpuBound().usesAllCPUCores().assertTiming();
    InspectionTestUtil.compareToolResults(globalContext, wrapper, false, new File(getTestDataPath(), wrapper.getShortName()).getPath());
  }

  @Nullable
  private VirtualFile installTestData(@NotNull String testData) {
    if (!new File(myFixture.getTestDataPath(), testData).exists()) {
      System.err.println("For performance tests you need to have a docker project inside testData/" + getBasePath() + " directory");
      return null;
    }

    return myFixture.copyDirectoryToProject(testData, testData);
  }
  
  public void testParserAndStubs() {
    File go = new File(getTestDataPath(), "go");
    if (!go.exists()) {
      System.err.println(
        "For performance tests you need to have a go sources (https://storage.googleapis.com/golang/go1.4.2.src.tar.gz) inside testData/" +
        getBasePath() +
        " directory");
      return;
    }

    PlatformTestUtil.startPerformanceTest(getTestName(true), (int)TimeUnit.MINUTES.toMillis(1), () -> {
      VirtualFile root = LocalFileSystem.getInstance().findFileByIoFile(go);
      assertNotNull(root);
      VfsUtilCore.visitChildrenRecursively(root, new VirtualFileVisitor() {
        @NotNull
        @Override
        public Result visitFileEx(@NotNull VirtualFile file) {
          if (file.isDirectory() && "testdata".equals(file.getName())) return SKIP_CHILDREN;
          if (file.isDirectory() && "test".equals(file.getName()) && file.getParent().equals(root)) return SKIP_CHILDREN;
          if (file.getFileType() != GoFileType.INSTANCE) return CONTINUE;
          try {
            System.out.print(".");
            buildStubTreeText(getProject(), file, FileUtil.loadFile(new File(file.getPath()), "UTF-8", true).trim(), true);
          }
          catch (IOException ignored) {
          }
          return CONTINUE;
        }
      });
    }).usesAllCPUCores().assertTiming();
  }

  @NotNull
  @Override
  protected String getBasePath() {
    return "performance";
  }
}
