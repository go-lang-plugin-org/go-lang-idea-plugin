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

import com.goide.codeInsight.imports.GoGetPackageFix;
import com.goide.sdk.GoPackageUtil;
import com.goide.util.GoPathResolveScope;
import com.goide.util.GoUtil;
import com.intellij.execution.filters.Filter;
import com.intellij.execution.filters.HyperlinkInfo;
import com.intellij.execution.filters.OpenFileHyperlinkInfo;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.ex.temp.TempFileSystem;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.ObjectUtils;
import com.intellij.util.PathUtil;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GoConsoleFilter implements Filter {
  private static final Pattern MESSAGE_PATTERN = Pattern.compile("(?:^|\\s)(\\S+\\.\\w+):(\\d+)(:(\\d+))?(?=[:\\s]|$).*");
  private static final Pattern GO_GET_MESSAGE_PATTERN = Pattern.compile("^[ \t]*(go get (.*))\n?$");
  private static final Pattern APP_ENGINE_PATH_PATTERN = Pattern.compile("/tmp[A-z0-9]+appengine-go-bin/");
  private static final Pattern GO_FILE_PATTERN = Pattern.compile("\\((\\w+\\.go)\\)");

  @NotNull private final Project myProject;
  @Nullable private final Module myModule;
  @Nullable private final String myWorkingDirectoryUrl;

  @SuppressWarnings("unused") //used by pico container
  public GoConsoleFilter(@NotNull Project project) {
    this(project, null, null);
  }

  public GoConsoleFilter(@NotNull Project project, @Nullable Module module, @Nullable String workingDirectoryUrl) {
    myProject = project;
    myModule = module;
    myWorkingDirectoryUrl = ObjectUtils.chooseNotNull(workingDirectoryUrl, VfsUtilCore.pathToUrl(System.getProperty("user.dir")));
  }

  @Override
  public Result applyFilter(@NotNull String line, int entireLength) {
    Matcher goGetMatcher = GO_GET_MESSAGE_PATTERN.matcher(line);
    if (goGetMatcher.find() && myModule != null) {
      String packageName = goGetMatcher.group(2).trim();
      HyperlinkInfo hyperlinkInfo = new GoGetHyperlinkInfo(packageName, myModule);
      int lineStart = entireLength - line.length();
      return new Result(lineStart + goGetMatcher.start(1), lineStart + goGetMatcher.end(2), hyperlinkInfo);
    }
    Matcher matcher = MESSAGE_PATTERN.matcher(line);
    if (!matcher.find()) {
      Matcher fileMatcher = GO_FILE_PATTERN.matcher(line);
      List<ResultItem> resultItems = ContainerUtil.newArrayList();
      while (fileMatcher.find()) {
        VirtualFile file = findSingleFile(fileMatcher.group(1));
        if (file != null) {
          resultItems.add(createResult(line, entireLength, fileMatcher.start(1), fileMatcher.end(1), 0, 0, file));
        }
      }
      return !resultItems.isEmpty() ? new Result(resultItems) : null;
    }

    int startOffset = matcher.start(1);
    int endOffset = matcher.end(2);

    String fileName = matcher.group(1);
    int lineNumber = StringUtil.parseInt(matcher.group(2), 1) - 1;
    if (lineNumber < 0) {
      return null;
    }

    int columnNumber = -1;
    if (matcher.groupCount() > 3) {
      columnNumber = StringUtil.parseInt(matcher.group(4), 1) - 1;
      endOffset = Math.max(endOffset, matcher.end(4));
    }

    Matcher appEnginePathMatcher = APP_ENGINE_PATH_PATTERN.matcher(fileName);
    if (appEnginePathMatcher.find()) {
      fileName = fileName.substring(appEnginePathMatcher.end());
    }

    VirtualFile virtualFile = null;
    if (FileUtil.isAbsolutePlatformIndependent(fileName)) {
      virtualFile = ApplicationManager.getApplication().isUnitTestMode()
                    ? TempFileSystem.getInstance().refreshAndFindFileByPath(fileName)
                    : VirtualFileManager.getInstance().refreshAndFindFileByUrl(VfsUtilCore.pathToUrl(fileName));
    }
    else {
      if (myWorkingDirectoryUrl != null) {
        virtualFile = VirtualFileManager.getInstance().refreshAndFindFileByUrl(myWorkingDirectoryUrl + "/" + fileName);
      }
      if (virtualFile == null && myModule != null) {
        virtualFile = findInGoPath(fileName);
        if (virtualFile == null && fileName.startsWith("src/")) {
          virtualFile = findInGoPath(StringUtil.trimStart(fileName, "src/"));
        }
      }
      if (virtualFile == null) {
        VirtualFile baseDir = myProject.getBaseDir();
        if (baseDir != null) {
          virtualFile = baseDir.findFileByRelativePath(fileName);
          if (virtualFile == null && fileName.startsWith("src/")) {
            // exclude src
            virtualFile = baseDir.findFileByRelativePath(StringUtil.trimStart(fileName, "src/"));
          }
        }
      }
    }
    if (virtualFile == null) {
      virtualFile = findSingleFile(fileName);
    }
    if (virtualFile == null) {
      return null;
    }
    return createResult(line, entireLength, startOffset, endOffset, lineNumber, columnNumber, virtualFile);
  }

  @NotNull
  private Result createResult(@NotNull String line,
                              int entireLength,
                              int startOffset,
                              int endOffset,
                              int lineNumber,
                              int columnNumber,
                              @NotNull VirtualFile virtualFile) {
    HyperlinkInfo hyperlinkInfo = new OpenFileHyperlinkInfo(myProject, virtualFile, lineNumber, columnNumber);
    int lineStart = entireLength - line.length();
    return new Result(lineStart + startOffset, lineStart + endOffset, hyperlinkInfo);
  }

  @Nullable
  private VirtualFile findSingleFile(@NotNull String fileName) {
    if (PathUtil.isValidFileName(fileName)) {
      Collection<VirtualFile> files = FilenameIndex.getVirtualFilesByName(myProject, fileName, GlobalSearchScope.allScope(myProject));
      if (files.size() == 1) {
        return ContainerUtil.getFirstItem(files);
      }
      if (!files.isEmpty()) {
        GlobalSearchScope goPathScope = GoPathResolveScope.create(myProject, myModule, null);
        files = ContainerUtil.filter(files, goPathScope::accept);
        if (files.size() == 1) {
          return ContainerUtil.getFirstItem(files);
        }
      }
      if (!files.isEmpty()) {
        GlobalSearchScope smallerScope = GoUtil.moduleScopeWithoutLibraries(myProject, myModule);
        files = ContainerUtil.filter(files, smallerScope::accept);
        if (files.size() == 1) {
          return ContainerUtil.getFirstItem(files);
        }
      }
    }
    return null;
  }

  @Nullable
  private VirtualFile findInGoPath(@NotNull String fileName) {
    return GoPackageUtil.findByImportPath(fileName, myProject, myModule);
  }

  public static class GoGetHyperlinkInfo implements HyperlinkInfo {
    private final String myPackageName;
    private final Module myModule;

    public GoGetHyperlinkInfo(@NotNull String packageName, @NotNull Module module) {
      myPackageName = packageName;
      myModule = module;
    }

    @NotNull
    public String getPackageName() {
      return myPackageName;
    }

    @Override
    public void navigate(Project project) {
      GoGetPackageFix.applyFix(project, myModule, myPackageName, false);
    }
  }
}
