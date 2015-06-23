/*
 * Copyright 2013-2015 Sergey Ignatov, Alexander Zolotov, Mihai Toader, Florin Patan
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

package com.goide.runconfig.testing.frameworks.gocheck;

import com.goide.runconfig.testing.GoTestRunningState;
import com.goide.util.GoExecutor;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.module.Module;
import org.jetbrains.annotations.NotNull;

public class GocheckRunningState extends GoTestRunningState {
  public GocheckRunningState(@NotNull ExecutionEnvironment env,
                             @NotNull Module module,
                             @NotNull GocheckRunConfiguration configuration) {
    super(env, module, configuration);
  }
  
  @Override
  protected GoExecutor patchExecutor(@NotNull GoExecutor executor) throws ExecutionException {
    return super.patchExecutor(executor).withParameters("-check.vv");
  }
}
