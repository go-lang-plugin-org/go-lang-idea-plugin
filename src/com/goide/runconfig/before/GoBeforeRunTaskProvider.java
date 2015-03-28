package com.goide.runconfig.before;

import com.goide.GoConstants;
import com.goide.GoIcons;
import com.goide.runconfig.GoRunConfigurationBase;
import com.goide.sdk.GoSdkService;
import com.goide.sdk.GoSdkUtil;
import com.intellij.execution.BeforeRunTaskProvider;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionHelper;
import com.intellij.execution.ExecutionModes;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.process.CapturingProcessAdapter;
import com.intellij.execution.process.KillableColoredProcessHandler;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.util.concurrency.Semaphore;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    return "Run `" + task.toString() + "`";
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
    final Project project = configuration.getProject();
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
  public boolean executeTask(final DataContext context,
                             final RunConfiguration configuration,
                             ExecutionEnvironment env,
                             final GoCommandBeforeRunTask task) {
    final Semaphore done = new Semaphore();
    final Ref<Boolean> result = new Ref<Boolean>(false);
    final List<Exception> exceptions = new ArrayList<Exception>();

    GoRunConfigurationBase goRunConfiguration = (GoRunConfigurationBase)configuration;
    final Module module = goRunConfiguration.getConfigurationModule().getModule();
    final Project project = configuration.getProject();
    final String workingDirectory = goRunConfiguration.getWorkingDirectory();

    UIUtil.invokeAndWaitIfNeeded(new Runnable() {
      public void run() {
        if (StringUtil.isEmpty(task.getCommand())) return;
        if (project == null || project.isDisposed()) return;
        GoSdkService sdkService = GoSdkService.getInstance(project);
        if (!sdkService.isGoModule(module)) return;

        final String executablePath = sdkService.getGoExecutablePath(module);
        if (StringUtil.isEmpty(executablePath)) return;

        FileDocumentManager.getInstance().saveAllDocuments();

        done.down();
        Task.Backgroundable t = new Task.Backgroundable(project, "Executing " + task.toString(), true) {
          public void run(@NotNull ProgressIndicator indicator) {
            try {
              GeneralCommandLine commandLine = new GeneralCommandLine();
              final String goPath = GoSdkUtil.retrieveGoPath(module);
              commandLine.setExePath(executablePath);
              commandLine.getEnvironment().put(GoConstants.GO_PATH, goPath);
              commandLine.withWorkDirectory(workingDirectory);
              commandLine.getParametersList().addParametersString(task.getCommand());

              try {
                final OSProcessHandler processHandler = new KillableColoredProcessHandler(commandLine);
                final CapturingProcessAdapter processAdapter = new CapturingProcessAdapter() {
                  @Override
                  public void processTerminated(@NotNull ProcessEvent event) {
                    super.processTerminated(event);
                    result.set(event.getExitCode() == 0);
                    done.up();
                    ApplicationManager.getApplication().invokeLater(new Runnable() {
                      @Override
                      public void run() {
                        ApplicationManager.getApplication().runWriteAction(new Runnable() {
                          @Override
                          public void run() {
                            VirtualFileManager.getInstance().syncRefresh();
                          }
                        });
                      }
                    });
                  }
                };
                processHandler.addProcessListener(processAdapter);
                processHandler.startNotify();
                ExecutionHelper.executeExternalProcess(project, processHandler, new ExecutionModes.SameThreadMode(60), commandLine);

                ExecutionHelper.showOutput(project, processAdapter.getOutput(), "Executing `" + task.toString() + "`", null, !result.get());
              }
              catch (ExecutionException e) {
                exceptions.add(e);
              }
            }
            finally {
              done.up();
            }
          }
        };
        ProgressManager.getInstance().run(t);
      }
    });

    if (!exceptions.isEmpty()) {
      ExecutionHelper.showExceptions(project, exceptions, Collections.<Exception>emptyList(),
                                     "Cannot run `" + task.toString() + "`", null);
    }
    done.waitFor();
    return result.get();
  }

  private static void showAddingTaskErrorMessage(final Project project, final String message) {
    Messages.showErrorDialog(project, message, "Go Command Task");
  }
}