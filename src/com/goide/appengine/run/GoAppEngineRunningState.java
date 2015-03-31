package com.goide.appengine.run;

import com.goide.runconfig.GoRunningState;
import com.goide.util.GoExecutor;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.NotNull;

public class GoAppEngineRunningState extends GoRunningState<GoAppEngineRunConfiguration> {
  public GoAppEngineRunningState(@NotNull ExecutionEnvironment env,
                                 @NotNull Module module,
                                 @NotNull GoAppEngineRunConfiguration configuration) {
    super(env, module, configuration);
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
