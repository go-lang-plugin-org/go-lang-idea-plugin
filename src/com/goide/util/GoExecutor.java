package com.goide.util;

import com.goide.GoConstants;
import com.goide.runconfig.GoConsoleFilter;
import com.goide.sdk.GoSdkService;
import com.goide.sdk.GoSdkUtil;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionHelper;
import com.intellij.execution.ExecutionModes;
import com.intellij.execution.RunContentExecutor;
import com.intellij.execution.configurations.EncodingEnvironmentUtil;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.configurations.ParametersList;
import com.intellij.execution.process.*;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.CharsetToolkit;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.util.ObjectUtils;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

public class GoExecutor {  
  private static final Logger LOGGER = Logger.getInstance(GoExecutor.class);
  @NotNull private final Map<String, String> myExtraEnvironment = ContainerUtil.newHashMap();
  @NotNull private final ParametersList myParameterList = new ParametersList();
  @NotNull private ProcessOutput myProcessOutput = new ProcessOutput();
  @NotNull private final Project myProject;
  @Nullable private final Module myModule;
  @Nullable private String myGoRoot;
  @Nullable private String myGoPath;
  @Nullable private String myWorkDirectory;
  private boolean myShowOutputOnError = false;
  private boolean myShowNotificationsOnError = false;
  private boolean myShowNotificationsOnSuccess = false;
  private boolean myPassParentEnvironment = true;
  @Nullable private String myExePath = null;
  @Nullable private String myPresentableName;
  private OSProcessHandler myProcessHandler;
  private Collection<ProcessListener> myProcessListeners = ContainerUtil.newArrayList();

  private GoExecutor(@NotNull Project project, @Nullable Module module) {
    myProject = project;
    myModule = module;
  }

  @NotNull
  public static GoExecutor in(@NotNull Project project) {
    return new GoExecutor(project, null)
      .withGoRoot(GoSdkService.getInstance(project).getSdkHomePath(null))
      .withGoPath(GoSdkUtil.retrieveGoPath(project));
  }

  @NotNull
  public static GoExecutor in(@NotNull Module module) {
    Project project = module.getProject();
    return new GoExecutor(project, module)
      .withGoRoot(GoSdkService.getInstance(project).getSdkHomePath(module))
      .withGoPath(GoSdkUtil.retrieveGoPath(module));
  }

  @NotNull
  public GoExecutor withPresentableName(@Nullable String presentableName) {
    myPresentableName = presentableName;
    return this;
  }

  @NotNull
  public GoExecutor withExePath(@Nullable String exePath) {
    myExePath = exePath;
    return this;
  }

  @NotNull
  public GoExecutor withWorkDirectory(@Nullable String workDirectory) {
    myWorkDirectory = workDirectory;
    return this;
  }

  @NotNull
  public GoExecutor withGoRoot(@Nullable String goRoot) {
    myGoRoot = goRoot;
    return this;
  }

  @NotNull
  public GoExecutor withGoPath(@Nullable String goPath) {
    myGoPath = goPath;
    return this;
  }
  
  public GoExecutor withProcessListener(@NotNull ProcessListener listener) {
    myProcessListeners.add(listener);
    return this;
  }

  @NotNull
  public GoExecutor withExtraEnvironment(@NotNull Map<String, String> environment) {
    myExtraEnvironment.putAll(environment);
    return this;
  }

  @NotNull
  public GoExecutor withPassParentEnvironment(boolean passParentEnvironment) {
    myPassParentEnvironment = passParentEnvironment;
    return this;
  }

  @NotNull
  public GoExecutor withParameterString(@NotNull String parameterString) {
    myParameterList.addParametersString(parameterString);
    return this;
  }

  @NotNull
  public GoExecutor withParameters(@NotNull String... parameters) {
    myParameterList.addAll(parameters);
    return this;
  }

  @NotNull
  public GoExecutor showOutputOnError() {
    myShowOutputOnError = true;
    return this;
  }

  @NotNull
  public GoExecutor showNotifications(boolean onErrorOnly) {
    myShowNotificationsOnError = true;
    myShowNotificationsOnSuccess = !onErrorOnly;
    return this;
  }

  public boolean execute() {
    Logger.getInstance(getClass()).assertTrue(!ApplicationManager.getApplication().isDispatchThread(),
                                              "It's bad idea to run external tool on EDT");
    Logger.getInstance(getClass()).assertTrue(myProcessHandler == null, "Process has already run with this executor instance");
    final Ref<Boolean> result = Ref.create(false);
    GeneralCommandLine commandLine = null;
    try {
      commandLine = createCommandLine();

      myProcessHandler = new KillableColoredProcessHandler(commandLine);
      final HistoryProcessListener historyProcessListener = new HistoryProcessListener();
      myProcessHandler.addProcessListener(historyProcessListener);
      for (ProcessListener listener : myProcessListeners) {
        myProcessHandler.addProcessListener(listener);
      }

      final CapturingProcessAdapter processAdapter = new CapturingProcessAdapter(myProcessOutput) {
        @Override
        public void processTerminated(@NotNull final ProcessEvent event) {
          super.processTerminated(event);
          final boolean success = event.getExitCode() == 0 && myProcessOutput.getStderr().isEmpty();
          result.set(success);
          if (success && myShowNotificationsOnSuccess) {
            showNotification("Finished successfully", NotificationType.INFORMATION);
          }
          ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
              if (!success && myShowOutputOnError) {
                showOutput(myProcessHandler, historyProcessListener);
              }
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

      myProcessHandler.addProcessListener(processAdapter);
      myProcessHandler.startNotify();
      ExecutionModes.SameThreadMode sameThreadMode = new ExecutionModes.SameThreadMode(getPresentableName());
      ExecutionHelper.executeExternalProcess(myProject, myProcessHandler, sameThreadMode, commandLine);
      
      LOGGER.debug("Finished `" + getPresentableName() + "` with result: " + result.get());
      return result.get();
    }
    catch (final ExecutionException e) {
      if (myShowOutputOnError) {
        ExecutionHelper.showErrors(myProject, Collections.singletonList(e), getPresentableName(), null);
      }
      if (myShowNotificationsOnError) {
        showNotification(StringUtil.notNullize(e.getMessage(), "Unknown error, see logs for details"), NotificationType.ERROR);
      }
      String commandLineInfo = commandLine != null ? commandLine.getCommandLineString() : "not constructed";
      LOGGER.debug("Finished `" + getPresentableName() + "` with an exception. Commandline: " + commandLineInfo, e);
      return false;
    }
  }

  public void executeWithProgress(final boolean modal) {
    ProgressManager.getInstance().run(new Task.Backgroundable(myProject, getPresentableName(), true) {
      private boolean doNotStart = false;

      @Override
      public void onCancel() {
        doNotStart = true;
        ProcessHandler handler = getProcessHandler();
        if (handler != null) {
          handler.destroyProcess();
        }
      }

      @Override
      public boolean shouldStartInBackground() {
        return !modal;
      }

      @Override
      public boolean isConditionalModal() {
        return modal;
      }

      public void run(@NotNull ProgressIndicator indicator) {
        if (doNotStart || myProject == null || myProject.isDisposed()) {
          return;
        }
        indicator.setIndeterminate(true);
        execute();
      }
    });
  }


  @Nullable
  public ProcessHandler getProcessHandler() {
    return myProcessHandler;
  }

  private void showNotification(@NotNull final String message, final NotificationType type) {
    ApplicationManager.getApplication().invokeLater(new Runnable() {
      @Override
      public void run() {
        String title = getPresentableName();
        Notifications.Bus.notify(GoConstants.GO_EXECUTION_NOTIFICATION_GROUP.createNotification(title, message, type, null), myProject);
      }
    });
  }

  private void showOutput(@NotNull OSProcessHandler originalHandler, @NotNull HistoryProcessListener historyProcessListener) {
    if (myShowOutputOnError) {
      BaseOSProcessHandler outputHandler = new ColoredProcessHandler(originalHandler.getProcess(), null);
      RunContentExecutor runContentExecutor = new RunContentExecutor(myProject, outputHandler)
        .withTitle(getPresentableName())
        .withActivateToolWindow(myShowOutputOnError)
        .withFilter(new GoConsoleFilter(myProject, myModule, StringUtil.notNullize(myWorkDirectory)));
      Disposer.register(myProject, runContentExecutor);
      runContentExecutor.run();
      historyProcessListener.apply(outputHandler);
    }
    if (myShowNotificationsOnError) {
      showNotification("Failed to run", NotificationType.ERROR);
    }
  }

  @NotNull
  public GeneralCommandLine createCommandLine() throws ExecutionException {
    if (myGoRoot == null) {
      throw new ExecutionException("Sdk is not set or Sdk home path is empty for module");
    }

    GeneralCommandLine commandLine = new GeneralCommandLine();
    commandLine.setExePath(ObjectUtils.notNull(myExePath, GoSdkService.getGoExecutablePath(myGoRoot)));
    commandLine.getEnvironment().putAll(myExtraEnvironment);
    commandLine.getEnvironment().put(GoConstants.GO_ROOT, StringUtil.notNullize(myGoRoot));
    commandLine.getEnvironment().put(GoConstants.GO_PATH, StringUtil.notNullize(myGoPath));
    commandLine.withWorkDirectory(myWorkDirectory);
    commandLine.addParameters(myParameterList.getList());
    commandLine.setPassParentEnvironment(myPassParentEnvironment);
    commandLine.withCharset(CharsetToolkit.UTF8_CHARSET);
    EncodingEnvironmentUtil.setLocaleEnvironmentIfMac(commandLine);
    return commandLine;
  }

  @NotNull
  private String getPresentableName() {
    return ObjectUtils.notNull(myPresentableName, "go");
  }

  private static class HistoryProcessListener extends ProcessAdapter {
    private final ConcurrentLinkedQueue<Pair<ProcessEvent, Key>> myHistory = new ConcurrentLinkedQueue<Pair<ProcessEvent, Key>>();

    @Override
    public void onTextAvailable(ProcessEvent event, Key outputType) {
      myHistory.add(Pair.create(event, outputType));
    }

    public void apply(ProcessHandler listener) {
      for (Pair<ProcessEvent, Key> pair : myHistory) {
        listener.notifyTextAvailable(pair.getFirst().getText(), pair.getSecond());
      }
    }
  }
}
