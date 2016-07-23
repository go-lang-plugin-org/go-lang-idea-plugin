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

package com.goide.runconfig.before;

import com.goide.GoIcons;
import com.goide.runconfig.GoRunConfigurationBase;
import com.goide.sdk.GoSdkService;
import com.goide.util.GoExecutor;
import com.intellij.execution.BeforeRunTaskProvider;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.util.concurrency.Semaphore;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class GoBeforeRunTaskProvider extends BeforeRunTaskProvider<GoCommandBeforeRunTask> {
  public static final Key<GoCommandBeforeRunTask> ID = Key.create("GoBeforeRunTask");

  @Override
  public Key<GoCommandBeforeRunTask> getId() {
    return ID;
  }

  @Override
  public String getName() {
    return "Go Command";
  }

  @Override
  public String getDescription(GoCommandBeforeRunTask task) {
    return "Run `" + task + "`";
  }

  @Nullable
  @Override
  public Icon getIcon() {
    return GoIcons.APPLICATION_RUN;
  }

  @Override
  public boolean isConfigurable() {
    return true;
  }

  @Nullable
  @Override
  public Icon getTaskIcon(GoCommandBeforeRunTask task) {
    return getIcon();
  }

  @Nullable
  @Override
  public GoCommandBeforeRunTask createTask(RunConfiguration runConfiguration) {
    return runConfiguration instanceof GoRunConfigurationBase ? new GoCommandBeforeRunTask() : null;
  }

  @Override
  public boolean configureTask(RunConfiguration configuration, GoCommandBeforeRunTask task) {
    Project project = configuration.getProject();
    if (!(configuration instanceof GoRunConfigurationBase)) {
      showAddingTaskErrorMessage(project, "Go Command task supports only Go Run Configurations");
      return false;
    }

    Module module = ((GoRunConfigurationBase)configuration).getConfigurationModule().getModule();
    if (!GoSdkService.getInstance(project).isGoModule(module)) {
      showAddingTaskErrorMessage(project, "Go Command task supports only Go Modules");
      return false;
    }

    GoCommandConfigureDialog dialog = new GoCommandConfigureDialog(project);
    if (dialog.showAndGet()) {
      task.setCommand(dialog.getCommand());
      return true;
    }
    return false;
  }

  @Override
  public boolean canExecuteTask(RunConfiguration configuration, GoCommandBeforeRunTask task) {
    if (configuration instanceof GoRunConfigurationBase) {
      Module module = ((GoRunConfigurationBase)configuration).getConfigurationModule().getModule();
      GoSdkService sdkService = GoSdkService.getInstance(configuration.getProject());
      if (sdkService.isGoModule(module)) {
        return StringUtil.isNotEmpty(sdkService.getSdkHomePath(module)) && StringUtil.isNotEmpty(task.getCommand());
      }
    }
    return false;
  }

  @Override
  public boolean executeTask(DataContext context,
                             RunConfiguration configuration,
                             ExecutionEnvironment env,
                             GoCommandBeforeRunTask task) {
    Semaphore done = new Semaphore();
    Ref<Boolean> result = Ref.create(false);

    GoRunConfigurationBase goRunConfiguration = (GoRunConfigurationBase)configuration;
    Module module = goRunConfiguration.getConfigurationModule().getModule();
    Project project = configuration.getProject();
    String workingDirectory = goRunConfiguration.getWorkingDirectory();

    UIUtil.invokeAndWaitIfNeeded(new Runnable() {
      @Override
      public void run() {
        if (StringUtil.isEmpty(task.getCommand())) return;
        if (project == null || project.isDisposed()) return;
        GoSdkService sdkService = GoSdkService.getInstance(project);
        if (!sdkService.isGoModule(module)) return;

        done.down();
        GoExecutor.in(module).withParameterString(task.getCommand())
          .withWorkDirectory(workingDirectory)
          .showOutputOnError()
          .showNotifications(false, true)
          .withPresentableName("Executing `" + task + "`")
          .withProcessListener(new ProcessAdapter() {
            @Override
            public void processTerminated(ProcessEvent event) {
              done.up();
              result.set(event.getExitCode() == 0);
            }
          })
          .executeWithProgress(false, result1 -> VirtualFileManager.getInstance().asyncRefresh(null));
      }
    });

    done.waitFor();
    return result.get();
  }

  private static void showAddingTaskErrorMessage(Project project, String message) {
    Messages.showErrorDialog(project, message, "Go Command Task");
  }
}