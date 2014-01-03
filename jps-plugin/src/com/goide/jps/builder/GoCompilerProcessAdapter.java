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
      CompilerMessage msg = new CompilerMessage(myBuilderName, kind, error.getErrorMessage(),
        VirtualFileManager.extractPath(error.getUrl()), -1, -1, -1, error.getLine(), -1);
      myContext.processMessage(msg);
    }
  }
}
