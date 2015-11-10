/*
 * Copyright 2013-2015 Sergey Ignatov, Alexander Zolotov, Florin Patan
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
    executor.withParameterString(myConfiguration.getGoToolParams());
    String host = myConfiguration.getHost();
    String port = myConfiguration.getPort();
    String adminPort = myConfiguration.getAdminPort();
    if (StringUtil.isNotEmpty(host)) {
      executor.withParameters("-host", host);
    }
    if (StringUtil.isNotEmpty(port)) {
      executor.withParameters("-port", port);
    }
    if (StringUtil.isNotEmpty(adminPort)) {
      executor.withParameters("-admin_port", adminPort);
    }
    executor.withParameters(StringUtil.notNullize(myConfiguration.getConfigFile(), "."));
    return executor;
  }
}
