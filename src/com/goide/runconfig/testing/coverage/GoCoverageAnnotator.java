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

package com.goide.runconfig.testing.coverage;

import com.intellij.coverage.BaseCoverageAnnotator;
import com.intellij.coverage.CoverageDataManager;
import com.intellij.coverage.CoverageSuite;
import com.intellij.coverage.CoverageSuitesBundle;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.progress.ProgressIndicatorProvider;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.FileIndexFacade;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.Factory;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileVisitor;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.rt.coverage.data.ProjectData;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Map;

public class GoCoverageAnnotator extends BaseCoverageAnnotator {
  private static final String STATEMENTS_SUFFIX = "% statements";
  private static final String FILES_SUFFIX = "% files";

  private final Map<String, FileCoverageInfo> myFileCoverageInfos = ContainerUtil.newHashMap();
  private final Map<String, DirCoverageInfo> myDirCoverageInfos = ContainerUtil.newHashMap();

  public GoCoverageAnnotator(@NotNull Project project) {
    super(project);
  }

  public static GoCoverageAnnotator getInstance(Project project) {
    return ServiceManager.getService(project, GoCoverageAnnotator.class);
  }

  @Nullable
  @Override
  public String getDirCoverageInformationString(@NotNull PsiDirectory directory,
                                                @NotNull CoverageSuitesBundle bundle,
                                                @NotNull CoverageDataManager manager) {
    DirCoverageInfo dirCoverageInfo = myDirCoverageInfos.get(directory.getVirtualFile().getPath());
    if (dirCoverageInfo == null) {
      return null;
    }

    if (manager.isSubCoverageActive()) {
      return dirCoverageInfo.coveredLineCount > 0 ? "covered" : null;
    }

    return getDirCoverageString(dirCoverageInfo);
  }

  @Nullable
  private static String getDirCoverageString(@NotNull DirCoverageInfo dirCoverageInfo) {
    String filesCoverageInfo = getFilesCoverageString(dirCoverageInfo);
    if (filesCoverageInfo != null) {
      StringBuilder builder = new StringBuilder();
      builder.append(filesCoverageInfo);
      String statementsCoverageInfo = getStatementsCoverageString(dirCoverageInfo);
      if (statementsCoverageInfo != null) {
        builder.append(", ").append(statementsCoverageInfo);
      }
      return builder.toString();
    }
    return null;
  }

  @Nullable
  @TestOnly
  public String getDirCoverageInformationString(@NotNull VirtualFile file) {
    DirCoverageInfo coverageInfo = myDirCoverageInfos.get(file.getPath());
    return coverageInfo != null ? getDirCoverageString(coverageInfo) : null;
  }

  @Nullable
  @Override
  public String getFileCoverageInformationString(@NotNull PsiFile file,
                                                 @NotNull CoverageSuitesBundle bundle,
                                                 @NotNull CoverageDataManager manager) {
    FileCoverageInfo coverageInfo = myFileCoverageInfos.get(file.getVirtualFile().getPath());
    if (coverageInfo == null) {
      return null;
    }

    if (manager.isSubCoverageActive()) {
      return coverageInfo.coveredLineCount > 0 ? "covered" : null;
    }

    return getStatementsCoverageString(coverageInfo);
  }

  @Nullable
  @TestOnly
  public String getFileCoverageInformationString(@NotNull VirtualFile file) {
    FileCoverageInfo coverageInfo = myFileCoverageInfos.get(file.getPath());
    return coverageInfo != null ? getStatementsCoverageString(coverageInfo) : null;
  }

  @Override
  public void onSuiteChosen(CoverageSuitesBundle newSuite) {
    super.onSuiteChosen(newSuite);
    myFileCoverageInfos.clear();
    myDirCoverageInfos.clear();
  }

  @Nullable
  @Override
  protected Runnable createRenewRequest(@NotNull CoverageSuitesBundle bundle, @NotNull CoverageDataManager manager) {
    GoCoverageProjectData data = new GoCoverageProjectData();
    for (CoverageSuite suite : bundle.getSuites()) {
      ProjectData toMerge = suite.getCoverageData(manager);
      if (toMerge != null) {
        data.merge(toMerge);
      }
    }

    return () -> {
      annotateAllFiles(data, manager.doInReadActionIfProjectOpen(() -> ProjectRootManager.getInstance(getProject()).getContentRoots()));
      manager.triggerPresentationUpdate();
    };
  }

  @NotNull
  private DirCoverageInfo getOrCreateDirectoryInfo(VirtualFile file) {
    return ContainerUtil.getOrCreate(myDirCoverageInfos, file.getPath(), new Factory<DirCoverageInfo>() {
      @Override
      public DirCoverageInfo create() {
        return new DirCoverageInfo();
      }
    });
  }

  @NotNull
  private FileCoverageInfo getOrCreateFileInfo(VirtualFile file) {
    return ContainerUtil.getOrCreate(myFileCoverageInfos, file.getPath(), new Factory<FileCoverageInfo>() {
      @Override
      public FileCoverageInfo create() {
        return new FileCoverageInfo();
      }
    });
  }

  @Nullable
  private static String getStatementsCoverageString(@NotNull FileCoverageInfo info) {
    double percent = calcPercent(info.coveredLineCount, info.totalLineCount);
    return info.totalLineCount > 0 ? new DecimalFormat("##.#" + STATEMENTS_SUFFIX, DecimalFormatSymbols.getInstance(Locale.US))
      .format(percent) : null;
  }

  @Nullable
  private static String getFilesCoverageString(@NotNull DirCoverageInfo info) {
    double percent = calcPercent(info.coveredFilesCount, info.totalFilesCount);
    return info.totalFilesCount > 0
           ? new DecimalFormat("##.#" + FILES_SUFFIX, DecimalFormatSymbols.getInstance(Locale.US)).format(percent)
           : null;
  }

  private static double calcPercent(int covered, int total) {
    return total != 0 ? (double)covered / total : 0;
  }

  public void annotateAllFiles(@NotNull GoCoverageProjectData data,
                               @Nullable VirtualFile... contentRoots) {
    if (contentRoots != null) {
      for (VirtualFile root : contentRoots) {
        VfsUtilCore.visitChildrenRecursively(root, new VirtualFileVisitor() {
          @NotNull
          @Override
          public Result visitFileEx(@NotNull VirtualFile file) {
            ProgressIndicatorProvider.checkCanceled();

            if (file.isDirectory() && !FileIndexFacade.getInstance(getProject()).isInContent(file)) {
              return SKIP_CHILDREN;
            }
            if (!file.isDirectory() && GoCoverageEngine.INSTANCE.coverageProjectViewStatisticsApplicableTo(file)) {
              DirCoverageInfo dirCoverageInfo = getOrCreateDirectoryInfo(file.getParent());
              FileCoverageInfo fileCoverageInfo = getOrCreateFileInfo(file);
              data.processFile(file.getPath(), rangeData -> {
                if (rangeData.hits > 0) {
                  fileCoverageInfo.coveredLineCount += rangeData.statements;
                }
                fileCoverageInfo.totalLineCount += rangeData.statements;
                return true;
              });

              if (fileCoverageInfo.totalLineCount > 0) {
                dirCoverageInfo.totalLineCount += fileCoverageInfo.totalLineCount;
                dirCoverageInfo.totalFilesCount++;
              }
              if (fileCoverageInfo.coveredLineCount > 0) {
                dirCoverageInfo.coveredLineCount += fileCoverageInfo.coveredLineCount;
                dirCoverageInfo.coveredFilesCount++;
              }
            }
            return CONTINUE;
          }

          @Override
          public void afterChildrenVisited(@NotNull VirtualFile file) {
            if (file.isDirectory()) {
              DirCoverageInfo currentCoverageInfo = getOrCreateDirectoryInfo(file);
              DirCoverageInfo parentCoverageInfo = getOrCreateDirectoryInfo(file.getParent());
              parentCoverageInfo.totalFilesCount += currentCoverageInfo.totalFilesCount;
              parentCoverageInfo.coveredFilesCount += currentCoverageInfo.coveredFilesCount;
              parentCoverageInfo.totalLineCount += currentCoverageInfo.totalLineCount;
              parentCoverageInfo.coveredLineCount += currentCoverageInfo.coveredLineCount;
            }
          }
        });
      }
    }
  }
}
