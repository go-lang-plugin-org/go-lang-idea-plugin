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

package com.goide.runconfig;

import com.goide.GoCodeInsightFixtureTestCase;
import com.goide.project.GoApplicationLibrariesService;
import com.intellij.execution.filters.Filter;
import com.intellij.execution.filters.HyperlinkInfo;
import com.intellij.execution.filters.OpenFileHyperlinkInfo;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.util.ThrowableComputable;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

public class GoConsoleFilterTest extends GoCodeInsightFixtureTestCase {
  private GoConsoleFilter myFilter;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    VirtualFile workingDirectory = createTestRoot("workingDirectory");
    VirtualFile goPath = createTestRoot("goPath");
    GoApplicationLibrariesService.getInstance().setLibraryRootUrls(goPath.getUrl());
    myFilter = new GoConsoleFilter(myFixture.getProject(), myFixture.getModule(), workingDirectory.getUrl());
  }

  @Override
  protected void tearDown() throws Exception {
    myFilter = null;
    super.tearDown();
  }
  
  public void testFileNameInParens() {
    doFileLineTest("Message (nestedWorkingDirectory.go) with file", 9, 34, "/src/workingDirectory/src/nestedWorkingDirectory.go", 1, 1);
  }
  
  public void testSimpleFileName() {
    doFileLineTest("nestedWorkingDirectory.go:57", 0, 28, "/src/workingDirectory/src/nestedWorkingDirectory.go", 57, 1);
  }
  
  public void testAbsolutePath() {
    doFileLineTest("\t/src/goPath/src/nestedGoPath.go:57: expected operand, found '<'",
                   1, 35, "/src/goPath/src/nestedGoPath.go", 57, 1);
  }
  
  public void testAbsolutePathWithoutColumn() {
    doFileLineTest("\t/src/goPath/src/nestedGoPath.go:57:1: expected operand, found '<'",
                   1, 37, "/src/goPath/src/nestedGoPath.go", 57, 1);
  }
  
  public void testAbsolutePathWithLoggingTime() {
    doFileLineTest("2015/03/30 09:15:13 /src/goPath/src/nestedGoPath.go:57:1: expected operand, found '<'",
                   20, 56, "/src/goPath/src/nestedGoPath.go", 57, 1);
  }

  public void testRelativeToWorkingDirectory() {
    doFileLineTest("src/nestedWorkingDirectory.go:5:5: found packages file.go (main) and file2.go (my) in /Some/path/to/directory",
                   0, 33, "/src/workingDirectory/src/nestedWorkingDirectory.go", 5, 5);
  }

  public void testRelativeToGoPath() {
    doFileLineTest("src/nestedGoPath.go:2: found packages file.go (main) and file2.go (my) in /Some/path/to/directory",
                   0, 21, "/src/goPath/src/nestedGoPath.go", 2, 1);
  }

  public void testAppEngineFullPaths() {
    doFileLineTest("/var/folders/m8/8sm6vlls2cs8xdmgs16y27zh0000gn/T/tmpzNDwX9appengine-go-bin/nestedGoPath.go:5:9: error",
                   0, 94, "/src/goPath/src/nestedGoPath.go", 5, 9);
  }

  public void testGoGetLines() {
    doGoGetTest("\tgo get golang.org/x/tools/cmd/cover", 1, 36, "golang.org/x/tools/cmd/cover");
  }

  private void doGoGetTest(@NotNull String line, int startOffset, int endOffset, @NotNull String packageName) {
    Filter.Result result = myFilter.applyFilter(line, line.length());
    assertNotNull(result);
    HyperlinkInfo info = assertResultAndGetHyperlink(result, startOffset, endOffset);
    assertInstanceOf(info, GoConsoleFilter.GoGetHyperlinkInfo.class);
    assertEquals(packageName, ((GoConsoleFilter.GoGetHyperlinkInfo)info).getPackageName());
  }

  private void doFileLineTest(@NotNull String line, int startOffset, int endOffset, String targetPath, int targetLine, int targetColumn) {
    Filter.Result result = myFilter.applyFilter(line, line.length());
    assertNotNull(result);
    HyperlinkInfo info = assertResultAndGetHyperlink(result, startOffset, endOffset);
    assertInstanceOf(info, OpenFileHyperlinkInfo.class);
    OpenFileDescriptor fileDescriptor = ((OpenFileHyperlinkInfo)info).getDescriptor();
    assertNotNull(fileDescriptor);
    assertEquals(targetPath, fileDescriptor.getFile().getPath());
    assertEquals("line", targetLine, fileDescriptor.getLine() + 1);
    assertEquals("column", targetColumn, fileDescriptor.getColumn() + 1);
  }

  @NotNull
  private static HyperlinkInfo assertResultAndGetHyperlink(@NotNull Filter.Result result, int startOffset, int endOffset) {
    List<Filter.ResultItem> items = result.getResultItems();
    assertSize(1, items);
    Filter.ResultItem item = ContainerUtil.getFirstItem(items);
    assertNotNull(item);
    assertEquals("start", startOffset, item.getHighlightStartOffset());
    assertEquals("end", endOffset, item.getHighlightEndOffset());
    HyperlinkInfo hyperlinkInfo = item.getHyperlinkInfo();
    assertNotNull(hyperlinkInfo);
    return hyperlinkInfo;
  }

  @NotNull
  private VirtualFile createTestRoot(@NotNull String rootName) throws IOException {
    return ApplicationManager.getApplication().runWriteAction(new ThrowableComputable<VirtualFile, IOException>() {
      @NotNull
      @Override
      public VirtualFile compute() throws IOException {
        VirtualFile workingDirectory = myFixture.getTempDirFixture().findOrCreateDir(rootName);
        workingDirectory.createChildData(this, rootName + ".go");
        VirtualFile childDirectory = workingDirectory.createChildDirectory(this, "src");
        childDirectory.createChildData(this, "nested" + Character.toUpperCase(rootName.charAt(0)) + rootName.substring(1) + ".go");
        return workingDirectory;
      }
    });
  }
}
