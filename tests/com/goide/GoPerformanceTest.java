/*
 * Copyright 2013-2015 Sergey Ignatov, Alexander Zolotov, Mihai Toader, Florin Patan
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

import com.goide.inspections.GoUnusedImportDeclaration;
import com.goide.inspections.unresolved.GoUnresolvedReferenceInspection;
import com.goide.inspections.unresolved.GoUnusedFunctionInspection;
import com.goide.inspections.unresolved.GoUnusedGlobalVariableInspection;
import com.goide.inspections.unresolved.GoUnusedVariableInspection;
import com.intellij.analysis.AnalysisScope;
import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.InspectionProfileEntry;
import com.intellij.codeInspection.ex.InspectionManagerEx;
import com.intellij.codeInspection.ex.InspectionToolRegistrar;
import com.intellij.codeInspection.ex.InspectionToolWrapper;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileVisitor;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.impl.DebugUtil;
import com.intellij.testFramework.InspectionTestUtil;
import com.intellij.testFramework.PlatformTestUtil;
import com.intellij.testFramework.UsefulTestCase;
import com.intellij.testFramework.fixtures.impl.CodeInsightTestFixtureImpl;
import com.intellij.testFramework.fixtures.impl.GlobalInspectionContextForTests;
import com.intellij.util.ThrowableRunnable;
import com.intellij.util.indexing.FileContentImpl;
import com.intellij.util.indexing.IndexingDataKeys;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class GoPerformanceTest extends GoCodeInsightFixtureTestCase {

  public void _testUnusedVariable() {
    doInspectionTest(new GoUnusedVariableInspection(), TimeUnit.MINUTES.toMillis(4));
  }

  public void _testUnusedGlobalVariable() {
    doInspectionTest(new GoUnusedGlobalVariableInspection(), TimeUnit.MINUTES.toMillis(4));
  }

  public void _testUnresolvedReference() {
    doInspectionTest(new GoUnresolvedReferenceInspection(), TimeUnit.MINUTES.toMillis(4));
  }

  public void testUnusedFunction() {
    doInspectionTest(new GoUnusedFunctionInspection(), TimeUnit.MINUTES.toMillis(3));
  }

  public void testUnusedImport() {
    doInspectionTest(new GoUnusedImportDeclaration(), TimeUnit.MINUTES.toMillis(1));
  }

  public void testPerformanceA() {
    doHighlightingTest(TimeUnit.SECONDS.toMillis(10));
  }

  public void testPerformanceA2() {
    doHighlightingTest(TimeUnit.SECONDS.toMillis(15));
  }

  public void testCompletionPerformance() {
    doCompletionTest("package main; func main() { <caret> }", TimeUnit.SECONDS.toMillis(30));
  }

  public void testCompletionWithPrefixPerformance() {
    doCompletionTest("package main; func main() { slee<caret> }", TimeUnit.SECONDS.toMillis(10));
  }

  private void doCompletionTest(String source, long expectation) {
    VirtualFile go = installTestData("go");
    if (go == null) return;
    
    myFixture.configureByText(GoFileType.INSTANCE, source);
    PlatformTestUtil.startPerformanceTest(getTestName(true), (int)expectation, new ThrowableRunnable() {
      @Override
      public void run() throws Throwable {
        myFixture.completeBasic();
      }
    }).cpuBound().usesAllCPUCores().assertTiming();
  }

  private void doHighlightingTest(long expectation) {
    PlatformTestUtil.startPerformanceTest(getTestName(true), (int)expectation, new ThrowableRunnable() {
      @Override
      public void run() throws Throwable {
        myFixture.testHighlighting(true, false, false, getTestName(true) + ".go");
      }
    }).cpuBound().usesAllCPUCores().assertTiming();
  }

  private void doInspectionTest(@NotNull InspectionProfileEntry tool, long expected) {
    VirtualFile sourceDir = installTestData("docker");
    if (sourceDir == null) return;
    //noinspection ConstantConditions
    final AnalysisScope scope = new AnalysisScope(getPsiManager().findDirectory(sourceDir));

    scope.invalidate();

    final InspectionManagerEx inspectionManager = (InspectionManagerEx)InspectionManager.getInstance(getProject());
    final InspectionToolWrapper wrapper = InspectionToolRegistrar.wrapTool(tool);
    final GlobalInspectionContextForTests globalContext =
      CodeInsightTestFixtureImpl.createGlobalContextForTool(scope, getProject(), inspectionManager, wrapper);

    PlatformTestUtil.startPerformanceTest(getTestName(true), (int)expected, new ThrowableRunnable() {
      @Override
      public void run() throws Throwable {
        InspectionTestUtil.runTool(wrapper, scope, globalContext);
      }
    }).cpuBound().usesAllCPUCores().assertTiming();
    InspectionTestUtil.compareToolResults(globalContext, wrapper, false, new File(getTestDataPath(), wrapper.getShortName()).getPath());
  }

  @Nullable
  private VirtualFile installTestData(String testData) {
    if (!new File(myFixture.getTestDataPath(), testData).exists()) {
      System.err.println("For performance tests you need to have a docker project inside testData/" + getBasePath() + " directory");
      return null;
    }

    return myFixture.copyDirectoryToProject(testData, testData);
  }
  
  public void testParserAndStubs() {
    final File go = new File(getTestDataPath(), "go");
    if (!go.exists()) {
      System.err.println(
        "For performance tests you need to have a go sources (https://storage.googleapis.com/golang/go1.4.2.src.tar.gz) inside testData/" +
        getBasePath() +
        " directory");
      return;
    }

    PlatformTestUtil.startPerformanceTest(getTestName(true), (int)TimeUnit.MINUTES.toMillis(1), new ThrowableRunnable() {
      @Override
      public void run() throws Throwable {
        final VirtualFile root = LocalFileSystem.getInstance().findFileByIoFile(go);
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
              String path = file.getPath();
              String fileContent = FileUtil.loadFile(new File(path), "UTF-8", true).trim();
              PsiFile psi = PsiFileFactory.getInstance(getProject()).createFileFromText(file.getName(), file.getFileType(), fileContent);
              assertFalse(path + " contains error elements", DebugUtil.psiToString(psi, true).contains("PsiErrorElement"));
              String full = DebugUtil.stubTreeToString(GoFileElementType.INSTANCE.getBuilder().buildStubTree(psi));
              psi.putUserData(IndexingDataKeys.VIRTUAL_FILE, file);
              FileContentImpl content = new FileContentImpl(file, fileContent, file.getCharset());
              PsiFile psiFile = content.getPsiFile();
              String fast = DebugUtil.stubTreeToString(GoFileElementType.INSTANCE.getBuilder().buildStubTree(psiFile));
              if (!Comparing.strEqual(full, fast)) {
                System.err.println(path);
                UsefulTestCase.assertSameLines(full, fast);
              }
            }
            catch (IOException ignored) {
            }
            return CONTINUE;
          }
        });
      }
    }).usesAllCPUCores().assertTiming();
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
