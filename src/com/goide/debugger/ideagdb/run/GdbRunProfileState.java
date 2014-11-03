/*
 * Copyright 2013-2014 Sergey Ignatov, Alexander Zolotov
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

package com.goide.debugger.ideagdb.run;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.filters.TextConsoleBuilder;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.project.Project;
import com.intellij.xdebugger.DefaultDebugProcessHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GdbRunProfileState implements RunProfileState {
  private final GdbRunConfiguration myConfiguration;

  public GdbRunProfileState(GdbRunConfiguration configuration) {
    myConfiguration = configuration;
  }

  @Nullable
  @Override
  public ExecutionResult execute(Executor executor, @NotNull ProgramRunner runner)
    throws ExecutionException {
    ProcessHandler processHandler = new DefaultDebugProcessHandler();

    // Create the console
    Project project = myConfiguration.getProject();
    TextConsoleBuilder builder = TextConsoleBuilderFactory.getInstance().createBuilder(project);
    ConsoleView console = builder.getConsole();

    return new GdbExecutionResult(console, processHandler, myConfiguration);
  }
}
