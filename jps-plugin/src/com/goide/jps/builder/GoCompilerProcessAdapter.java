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

package com.goide.jps.builder;

import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.openapi.compiler.CompilerMessageCategory;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFileManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.incremental.CompileContext;
import org.jetbrains.jps.incremental.messages.BuildMessage;
import org.jetbrains.jps.incremental.messages.CompilerMessage;

public class GoCompilerProcessAdapter extends ProcessAdapter {
  @NotNull private final CompileContext myContext;
  @NotNull private final String myBuilderName;
  @NotNull private final String myCompileTargetRootPath;

  public GoCompilerProcessAdapter(@NotNull CompileContext context, @NotNull String builderName, @NotNull String compileTargetRootPath) {
    myContext = context;
    myBuilderName = builderName;
    myCompileTargetRootPath = compileTargetRootPath;
  }

  @Override
  public void onTextAvailable(@NotNull ProcessEvent event, Key outputType) {
    GoCompilerError error = GoCompilerError.create(myCompileTargetRootPath, event.getText());
    if (error != null) {
      boolean isError = error.getCategory() == CompilerMessageCategory.ERROR;
      BuildMessage.Kind kind = isError ? BuildMessage.Kind.ERROR : BuildMessage.Kind.WARNING;
      String errorUrl = error.getUrl();
      String sourcePath = errorUrl != null ? VirtualFileManager.extractPath(errorUrl) : null;
      CompilerMessage msg = new CompilerMessage(myBuilderName, kind, error.getErrorMessage(), sourcePath, -1, -1, -1, error.getLine(), -1);
      myContext.processMessage(msg);
    }
  }
}
