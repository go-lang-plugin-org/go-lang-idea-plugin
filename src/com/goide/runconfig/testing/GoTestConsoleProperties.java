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

package com.goide.runconfig.testing;

import com.goide.psi.GoFunctionOrMethodDeclaration;
import com.goide.runconfig.GoRunConfigurationBase;
import com.goide.sdk.GoPackageUtil;
import com.goide.util.GoUtil;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.Location;
import com.intellij.execution.PsiLocation;
import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.testframework.AbstractTestProxy;
import com.intellij.execution.testframework.Filter;
import com.intellij.execution.testframework.TestConsoleProperties;
import com.intellij.execution.testframework.actions.AbstractRerunFailedTestsAction;
import com.intellij.execution.testframework.sm.SMCustomMessagesParsing;
import com.intellij.execution.testframework.sm.runner.OutputToGeneralTestEventsConverter;
import com.intellij.execution.testframework.sm.runner.SMTRunnerConsoleProperties;
import com.intellij.execution.testframework.sm.runner.SMTestLocator;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.GlobalSearchScopesCore;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GoTestConsoleProperties extends SMTRunnerConsoleProperties implements SMCustomMessagesParsing {
  public GoTestConsoleProperties(@NotNull GoTestRunConfiguration configuration, @NotNull Executor executor) {
    super(configuration, configuration.getTestFramework().getName(), executor);
    setPrintTestingStartedTime(false);
  }

  @NotNull
  @Override
  protected GlobalSearchScope initScope() {
    RunProfile configuration = getConfiguration();
    if (configuration instanceof GoTestRunConfiguration) {
      Project project = ((GoTestRunConfiguration)configuration).getProject();
      Module module = ((GoTestRunConfiguration)configuration).getConfigurationModule().getModule();
      switch (((GoTestRunConfiguration)configuration).getKind()) {
        case DIRECTORY:
          String directoryUrl = VfsUtilCore.pathToUrl(((GoTestRunConfiguration)configuration).getDirectoryPath());
          VirtualFile directory = VirtualFileManager.getInstance().findFileByUrl(directoryUrl);
          if (directory != null) {
            return GlobalSearchScopesCore.directoryScope(project, directory, true);
          }
          break;
        case PACKAGE:
          VirtualFile packageDir = GoPackageUtil.findByImportPath(((GoTestRunConfiguration)configuration).getPackage(), project, module);
          PsiDirectory psiDirectory = packageDir != null ? PsiManager.getInstance(project).findDirectory(packageDir) : null;
          if (psiDirectory != null) {
            return GoPackageUtil.packageScope(psiDirectory, null);
          }
          break;
        case FILE:
          String fileUrl = VfsUtilCore.pathToUrl(((GoTestRunConfiguration)configuration).getFilePath());
          VirtualFile file = VirtualFileManager.getInstance().findFileByUrl(fileUrl);
          if (file != null) {
            return GlobalSearchScope.fileScope(project, file);
          }
          break;
      }
    }
    if (configuration instanceof GoRunConfigurationBase) {
      GlobalSearchScope scope = GlobalSearchScope.EMPTY_SCOPE;
      for (Module module : ((GoRunConfigurationBase)configuration).getModules()) {
        scope = new GoUtil.TestsScope(GoUtil.goPathResolveScope(module, null));
      }
      return scope;
    }
    return super.initScope();
  }

  @Nullable
  @Override
  public SMTestLocator getTestLocator() {
    return GoTestLocator.INSTANCE;
  }

  @NotNull
  @Override
  public OutputToGeneralTestEventsConverter createTestEventsConverter(@NotNull String testFrameworkName,
                                                                      @NotNull TestConsoleProperties consoleProperties) {
    RunProfile configuration = getConfiguration();
    assert configuration instanceof GoTestRunConfiguration;
    return ((GoTestRunConfiguration)configuration).createTestEventsConverter(consoleProperties);
  }

  @Nullable
  @Override
  public AbstractRerunFailedTestsAction createRerunFailedTestsAction(ConsoleView consoleView) {
    AnAction rerunFailedTestsAction = ActionManager.getInstance().getAction("RerunFailedTests");
    return rerunFailedTestsAction != null ? new GoRerunFailedTestsAction(this, consoleView) : null;
  }

  private static class GoRerunFailedTestsAction extends AbstractRerunFailedTestsAction {
    public GoRerunFailedTestsAction(GoTestConsoleProperties properties, ConsoleView view) {
      super(view);
      init(properties);
    }

    @NotNull
    @Override
    protected Filter getFilter(@NotNull Project project, @NotNull GlobalSearchScope searchScope) {
      return super.getFilter(project, searchScope).and(new Filter() {
        @Override
        public boolean shouldAccept(AbstractTestProxy test) {
          Location location = test.getLocation(project, searchScope);
          return location instanceof PsiLocation && location.getPsiElement() instanceof GoFunctionOrMethodDeclaration;
        }
      });
    }

    @Nullable
    @Override
    protected MyRunProfile getRunProfile(@NotNull ExecutionEnvironment environment) {
      return new MyRunProfile((RunConfigurationBase)myConsoleProperties.getConfiguration()) {
        @NotNull
        @Override
        public Module[] getModules() {
          return Module.EMPTY_ARRAY;
        }

        @Nullable
        @Override
        public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment environment) throws ExecutionException {
          RunConfigurationBase configurationBase = getPeer();
          if (configurationBase instanceof GoTestRunConfiguration) {
            List<AbstractTestProxy> failedTests = getFailedTests(configurationBase.getProject());
            if (failedTests.isEmpty()) {
              return null;
            }

            GoTestRunConfiguration goTestRunConfiguration = (GoTestRunConfiguration)configurationBase;
            Module module = goTestRunConfiguration.getConfigurationModule().getModule();
            GoTestRunningState runningState = goTestRunConfiguration.newRunningState(environment, module);
            runningState.setFailedTests(failedTests);
            return runningState;
          }
          return null;
        }
      };
    }
  }
}
