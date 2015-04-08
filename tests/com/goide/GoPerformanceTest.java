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
import com.intellij.util.indexing.IndexingDataKeys;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
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
    final GlobalInspectionContextForTests globalContext =
      CodeInsightTestFixtureImpl.createGlobalContextForTool(scope, getProject(), inspectionManager, wrapper);

    PlatformTestUtil.startPerformanceTest(getTestName(true), expected, new ThrowableRunnable() {
      @Override
      public void run() throws Throwable {
        InspectionTestUtil.runTool(wrapper, scope, globalContext);
        InspectionTestUtil.compareToolResults(globalContext, wrapper, false, new File(getTestDataPath(), wrapper.getShortName()).getPath());
      }
    }).cpuBound().attempts(3).usesAllCPUCores().assertTiming();
  }

  public void testParserAndStubs() {
    final File go = new File(getTestDataPath(), "go");
    if (!go.exists()) {
      System.err.println("For performance tests you need to have a go sources (https://storage.googleapis.com/golang/go1.4.2.src.tar.gz) inside testData/" + getBasePath() + " directory");
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
              String path = file.getPath();
              String trim = FileUtil.loadFile(new File(path), "UTF-8", true).trim();
              PsiFile psi = PsiFileFactory.getInstance(getProject()).createFileFromText(file.getName(), file.getFileType(), trim);
              System.out.print(".");
              assertFalse(path + " contains error elements", DebugUtil.psiToString(psi, true).contains("PsiErrorElement"));
              String full = DebugUtil.stubTreeToString(GoFileElementType.INSTANCE.getBuilder().buildStubTree(psi));
              psi.putUserData(IndexingDataKeys.VIRTUAL_FILE, file);
              String fast = DebugUtil.stubTreeToString(GoFileElementType.INSTANCE.getBuilder().buildStubTree(psi));
              if (!Comparing.strEqual(full, fast)) {
                UsefulTestCase.assertSameLines(full, fast);
                System.err.println(path);
              }
            }
            catch (IOException e) {
              return CONTINUE;
            }
            return CONTINUE;
          }
        });
      }
    }).cpuBound().usesAllCPUCores().assertTiming();
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
