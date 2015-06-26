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

package com.goide.runconfig.testing.coverage;

import com.goide.GoFileType;
import com.goide.runconfig.testing.GoTestFinder;
import com.goide.runconfig.testing.GoTestRunConfiguration;
import com.intellij.coverage.*;
import com.intellij.coverage.view.CoverageViewExtension;
import com.intellij.coverage.view.CoverageViewManager;
import com.intellij.coverage.view.DirectoryCoverageViewExtension;
import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.execution.configurations.coverage.CoverageEnabledConfiguration;
import com.intellij.execution.testframework.AbstractTestProxy;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class GoCoverageEngine extends CoverageEngine {
  private static final Condition<AbstractTreeNode> NODE_TO_COVERAGE = new Condition<AbstractTreeNode>() {
    @Override
    public boolean value(AbstractTreeNode node) {
      Object value = node.getValue();
      if (value instanceof PsiFile) {
        return isProductionGoFile(((PsiFile)value));
      }
      return !StringUtil.equals(node.getName(), Project.DIRECTORY_STORE_FOLDER);
    }
  };

  public static final GoCoverageEngine INSTANCE = new GoCoverageEngine();
  private static final String PRESENTABLE_TEXT = "Go Coverage";

  @Override
  public boolean isApplicableTo(@Nullable final RunConfigurationBase conf) {
    return conf instanceof GoTestRunConfiguration;
  }

  @Override
  public boolean canHavePerTestCoverage(@Nullable final RunConfigurationBase conf) {
    return false;
  }

  @NotNull
  @Override
  public CoverageEnabledConfiguration createCoverageEnabledConfiguration(@Nullable final RunConfigurationBase conf) {
    return new GoCoverageEnabledConfiguration((GoTestRunConfiguration)conf);
  }

  @Override
  public CoverageSuite createCoverageSuite(@NotNull final CoverageRunner runner,
                                           @NotNull final String name,
                                           @NotNull final CoverageFileProvider coverageDataFileProvider,
                                           @Nullable final String[] filters,
                                           final long lastCoverageTimeStamp,
                                           @Nullable final String suiteToMerge,
                                           final boolean coverageByTestEnabled,
                                           final boolean tracingEnabled,
                                           final boolean trackTestFolders,
                                           final Project project) {
    // unable on teamcity
    return null;
  }

  @Override
  public CoverageSuite createCoverageSuite(@NotNull final CoverageRunner runner,
                                           @NotNull final String name,
                                           @NotNull final CoverageFileProvider coverageDataFileProvider,
                                           @NotNull final CoverageEnabledConfiguration config) {
    if (config instanceof GoCoverageEnabledConfiguration) {
      return new GoCoverageSuite(name, coverageDataFileProvider, new Date().getTime(), runner, config.getConfiguration().getProject());
    }
    return null;
  }

  @Override
  public CoverageSuite createEmptyCoverageSuite(@NotNull final CoverageRunner coverageRunner) {
    return new GoCoverageSuite();
  }

  @NotNull
  @Override
  public CoverageAnnotator getCoverageAnnotator(final Project project) {
    return GoCoverageAnnotator.getInstance(project);
  }

  @Override
  public boolean coverageEditorHighlightingApplicableTo(@NotNull final PsiFile psiFile) {
    return isProductionGoFile(psiFile);
  }

  @Override
  public boolean acceptedByFilters(@NotNull final PsiFile psiFile, @NotNull final CoverageSuitesBundle suite) {
    return isProductionGoFile(psiFile);
  }

  private static boolean isProductionGoFile(@NotNull PsiFile psiFile) {
    return psiFile.getFileType() == GoFileType.INSTANCE && !GoTestFinder.isTestFile(psiFile);
  }

  @Override
  public boolean recompileProjectAndRerunAction(@NotNull final Module module,
                                                @NotNull final CoverageSuitesBundle suite,
                                                @NotNull final Runnable chooseSuiteAction) {
    return false;
  }

  @Override
  public String getQualifiedName(@NotNull final File outputFile, @NotNull final PsiFile sourceFile) {
    return sourceFile.getVirtualFile().getPath();
  }

  @NotNull
  @Override
  public Set<String> getQualifiedNames(@NotNull final PsiFile sourceFile) {
    return Collections.singleton(sourceFile.getVirtualFile().getPath());
  }

  @Override
  public boolean includeUntouchedFileInCoverage(@NotNull final String qualifiedName,
                                                @NotNull final File outputFile,
                                                @NotNull final PsiFile sourceFile,
                                                @NotNull final CoverageSuitesBundle suite) {
    return false;
  }

  @Override
  public List<Integer> collectSrcLinesForUntouchedFile(@NotNull final File classFile, @NotNull final CoverageSuitesBundle suite) {
    return null;
  }

  @Override
  public List<PsiElement> findTestsByNames(@NotNull final String[] testNames, @NotNull final Project project) {
    return Collections.emptyList();
  }

  @Override
  public String getTestMethodName(@NotNull final PsiElement element, @NotNull final AbstractTestProxy testProxy) {
    return null;
  }

  @Override
  public String getPresentableText() {
    return PRESENTABLE_TEXT;
  }

  @Override
  public boolean coverageProjectViewStatisticsApplicableTo(final VirtualFile fileOrDir) {
    return !(fileOrDir.isDirectory()) && fileOrDir.getFileType() == GoFileType.INSTANCE && !GoTestFinder.isTestFile(fileOrDir);
  }

  @Override
  public CoverageViewExtension createCoverageViewExtension(Project project,
                                                           CoverageSuitesBundle suiteBundle,
                                                           CoverageViewManager.StateBean stateBean) {
    return new DirectoryCoverageViewExtension(project, getCoverageAnnotator(project), suiteBundle, stateBean) {
      @Override
      public List<AbstractTreeNode> getChildrenNodes(AbstractTreeNode node) {
        return ContainerUtil.filter(super.getChildrenNodes(node), NODE_TO_COVERAGE);
      }
    };
  }

  @Override
  public boolean isReportGenerationAvailable(@NotNull Project project,
                                             @NotNull DataContext dataContext,
                                             @NotNull CoverageSuitesBundle currentSuite) {
    // todo
    return super.isReportGenerationAvailable(project, dataContext, currentSuite);
  }

  @Override
  public void generateReport(@NotNull Project project, @NotNull DataContext dataContext, @NotNull CoverageSuitesBundle currentSuite) {
    // todo
    super.generateReport(project, dataContext, currentSuite);
  }
}
