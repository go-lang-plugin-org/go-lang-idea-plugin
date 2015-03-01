package com.goide.util;

import com.goide.GoConstants;
import com.goide.GoEnvironmentUtil;
import com.goide.sdk.GoSdkService;
import com.goide.sdk.GoSdkUtil;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionHelper;
import com.intellij.execution.ExecutionModes;
import com.intellij.execution.configurations.EncodingEnvironmentUtil;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.configurations.ParametersList;
import com.intellij.execution.process.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.CharsetToolkit;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.util.ObjectUtils;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Map;

public class GoExecutor {
  private static final int EXECUTION_TIMEOUT_S = 60;
  private static final ExecutionModes.SameThreadMode EXECUTION_MODE = new ExecutionModes.SameThreadMode(EXECUTION_TIMEOUT_S);

  @NotNull private final Map<String, String> myExtraEnvironment = ContainerUtil.newHashMap();
  @NotNull private final ParametersList myParameterList = new ParametersList();
  @NotNull private ProcessOutput myProcessOutput = new ProcessOutput();
  @Nullable private Project myProject;
  @Nullable private String myGoRoot;
  @Nullable private String myGoPath;
  @Nullable private String myWorkDirectory;
  private boolean myShowOutputOnError = false;
  private boolean myPassParentEnvironment = true;
  private String myExePath = null;

  private GoExecutor(@Nullable Project project) {
    myProject = project;
  }

  @NotNull
  public static GoExecutor empty() {
    return new GoExecutor(null);
  }

  @NotNull
  public static GoExecutor in(@NotNull Project project) {
    return new GoExecutor(project)
      .withGoRoot(GoSdkService.getInstance(project).getSdkHomePath(null))
      .withGoPath(GoSdkUtil.retrieveGoPath(project));
  }

  @NotNull
  public static GoExecutor in(@NotNull Module module) {
    Project project = module.getProject();
    return new GoExecutor(project)
      .withGoRoot(GoSdkService.getInstance(project).getSdkHomePath(module))
      .withGoPath(GoSdkUtil.retrieveGoPath(module));
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

  @NotNull
  public GoExecutor withProcessOutput(@NotNull ProcessOutput processOutput) {
    myProcessOutput = processOutput;
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
  public GoExecutor addParameterString(@NotNull String parameterString) {
    myParameterList.addParametersString(parameterString);
    return this;
  }
  
  @NotNull
  public GoExecutor addParameters(@NotNull String... parameters) {
    myParameterList.addAll(parameters);
    return this;
  }

  @NotNull
  public GoExecutor showOutputOnError() {
    myShowOutputOnError = true;
    return this;
  }

  public boolean executeSilent() {
    try {
      return execute();
    }
    catch (ExecutionException e) {
      if (myProject != null && myShowOutputOnError) {
        ExecutionHelper.showErrors(myProject, Collections.singletonList(e), "Executing `go`", null);
      }
      else {
        myProcessOutput.appendStderr(e.getMessage());
      }
      return false;
    }
  }

  public boolean execute() throws ExecutionException {
    GeneralCommandLine commandLine = createCommandLine();

    final Ref<Boolean> result = Ref.create(false);
    final OSProcessHandler processHandler = new KillableColoredProcessHandler(commandLine);
    final CapturingProcessAdapter processAdapter = new CapturingProcessAdapter(myProcessOutput) {
      @Override
      public void processTerminated(@NotNull ProcessEvent event) {
        super.processTerminated(event);
        result.set(event.getExitCode() == 0);
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
    ExecutionHelper.executeExternalProcess(myProject, processHandler, EXECUTION_MODE, commandLine);

    if (myProject != null && myShowOutputOnError) {
      ExecutionHelper.showOutput(myProject, processAdapter.getOutput(), "Executing `go`", null, !result.get());
    }

    return result.get();
  }

  @NotNull
  public GeneralCommandLine createCommandLine() throws ExecutionException {
    if (myGoRoot == null) {
      throw new ExecutionException("Sdk is not set or Sdk home path is empty for module");
    }

    String executable = GoEnvironmentUtil.getExecutableForSdk(myGoRoot).getAbsolutePath();
    GeneralCommandLine commandLine = new GeneralCommandLine();
    commandLine.setExePath(ObjectUtils.notNull(myExePath, executable));
    commandLine.getEnvironment().put(GoConstants.GO_ROOT, StringUtil.notNullize(myGoRoot));
    commandLine.getEnvironment().put(GoConstants.GO_PATH, StringUtil.notNullize(myGoPath));
    commandLine.withWorkDirectory(myWorkDirectory);
    commandLine.addParameters(myParameterList.getList());    
    commandLine.setPassParentEnvironment(myPassParentEnvironment);
    commandLine.withCharset(CharsetToolkit.UTF8_CHARSET);
    EncodingEnvironmentUtil.setLocaleEnvironmentIfMac(commandLine);
    return commandLine;
  }
}
