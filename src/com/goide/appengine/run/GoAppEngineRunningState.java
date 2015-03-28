package com.goide.appengine.run;

import com.goide.runconfig.GoConsoleFilter;
import com.goide.runconfig.GoRunningState;
import com.goide.util.GoExecutor;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GoAppEngineRunningState extends GoRunningState<GoAppEngineRunConfiguration> {
  public GoAppEngineRunningState(@NotNull ExecutionEnvironment env,
                                 @NotNull Module module,
                                 @NotNull GoAppEngineRunConfiguration configuration) {
    super(env, module, configuration);
  }
  
  @Nullable
  @Override
  protected ConsoleView createConsole(@NotNull Executor executor) throws ExecutionException {
    ConsoleView consoleView = super.createConsole(executor);
    if (consoleView != null) {
      consoleView.addMessageFilter(new GoConsoleFilter(myConfiguration.getProject(), myModule, myConfiguration.getWorkingDirectory()));
    }
    return consoleView;
  }

  @Override
  protected GoExecutor patchExecutor(@NotNull GoExecutor executor) throws ExecutionException {
    executor.withParameters("serve");
    String host = myConfiguration.getHost();
    String port = myConfiguration.getPort();
    if (StringUtil.isNotEmpty(host)) {
      executor.withParameters("-host", host);
    }
    if (StringUtil.isNotEmpty(port)) {
      executor.withParameters("-port", port);
    }
    executor.withParameters(".");
    return executor;
  }
}
