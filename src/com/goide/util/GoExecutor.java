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
import com.intellij.execution.process.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.CharsetToolkit;
import com.intellij.openapi.vfs.VirtualFileManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GoExecutor {
  private static final int EXECUTION_TIMEOUT_S = 60;
  private static final ExecutionModes.SameThreadMode EXECUTION_MODE = new ExecutionModes.SameThreadMode(EXECUTION_TIMEOUT_S);
  
  @NotNull private ProcessOutput myProcessOutput = new ProcessOutput();
  @Nullable private Project myProject;
  @Nullable private String myGoRoot;
  @Nullable private String myGoPath;
  @Nullable private String myWorkDirectory;
  
  public GoExecutor() {}

  public GoExecutor(@NotNull Module module) {
    myProject = module.getProject();
    myGoRoot = GoSdkService.getInstance(myProject).getSdkHomePath(module);
    myGoPath = GoSdkUtil.retrieveGoPath(module);
  }

  public GoExecutor(@Nullable Project project) {
    myProject = project;
    if (project != null) {
      myGoRoot = GoSdkService.getInstance(project).getSdkHomePath(null);
      myGoPath = GoSdkUtil.retrieveGoPath(project);
    }
  }

  @NotNull
  public GoExecutor withWorkDirectory(@Nullable String workDirectory) {
    myWorkDirectory = workDirectory;
    return this;
  }

  @NotNull
  public GoExecutor withGoRoot(@NotNull String goRoot) {
    myGoRoot = goRoot;
    return this;
  }

  @NotNull
  public GoExecutor withGoPath(@NotNull String goPath) {
    myGoPath = goPath;
    return this;
  }
  
  @NotNull
  public GoExecutor withProcessOutput(@NotNull ProcessOutput processOutput) {
    myProcessOutput = processOutput;
    return this;
  }

  public boolean execute(boolean showOutputOnError, @NotNull String... parameters) {
    if (myGoRoot == null) {
      myProcessOutput.appendStderr("GOROOT is empty");
      return false;
    }

    String executable = GoEnvironmentUtil.getExecutableForSdk(myGoRoot).getAbsolutePath();
    GeneralCommandLine commandLine = new GeneralCommandLine();
    commandLine.setExePath(executable);
    commandLine.getEnvironment().put(GoConstants.GO_ROOT, StringUtil.notNullize(myGoRoot));
    commandLine.getEnvironment().put(GoConstants.GO_PATH, StringUtil.notNullize(myGoPath));
    commandLine.withWorkDirectory(myWorkDirectory);
    commandLine.addParameters(parameters);
    commandLine.withCharset(CharsetToolkit.UTF8_CHARSET);
    EncodingEnvironmentUtil.setLocaleEnvironmentIfMac(commandLine);

    final Ref<Boolean> result = Ref.create(false);
    try {
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

      if (myProject != null && showOutputOnError) {
        ExecutionHelper.showOutput(myProject, processAdapter.getOutput(), "Executing `go`", null, !result.get());
      }
    }
    catch (ExecutionException e) {
      myProcessOutput.appendStderr(e.getMessage());
    }
    return result.get();
  }
}
