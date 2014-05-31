package com.goide.runconfig;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.CommandLineState;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ModuleRootManager;
import org.jetbrains.annotations.NotNull;

public abstract class GoRunningState extends CommandLineState {
  @NotNull protected final Module myModule;

  public GoRunningState(@NotNull ExecutionEnvironment env, @NotNull Module module) {
    super(env);
    myModule = module;
  }

  @NotNull
  @Override
  protected ProcessHandler startProcess() throws ExecutionException {
    Sdk sdk = ModuleRootManager.getInstance(myModule).getSdk();
    assert sdk != null;
    GeneralCommandLine commandLine = getCommand(sdk);
    return new OSProcessHandler(commandLine.createProcess(), commandLine.getCommandLineString());
  }

  @NotNull
  protected abstract GeneralCommandLine getCommand(@NotNull Sdk sdk) throws ExecutionException;
}
