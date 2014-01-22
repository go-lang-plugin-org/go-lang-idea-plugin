package com.goide.runconfig.testing;

import com.intellij.execution.filters.Filter;
import com.intellij.execution.filters.HyperlinkInfo;
import com.intellij.execution.filters.OpenFileHyperlinkInfo;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class GoTestConsoleFilter implements Filter {
  private static final Pattern MESSAGE_PATTERN = Pattern.compile("(\\S+\\.\\w+):(\\d+)[:\\s].*\n");

  @NotNull
  private final Module myModule;
  @NotNull
  private final String myWorkingDirectory;

  public GoTestConsoleFilter(@NotNull Module module, @NotNull String workingDirectory) {
    myModule = module;
    myWorkingDirectory = workingDirectory;
  }

  @Override
  public Result applyFilter(String line, int entireLength) {
    Matcher matcher = MESSAGE_PATTERN.matcher(line);
    if (!matcher.matches()) {
      return null;
    }
      
    String fileName = matcher.group(1);
    int lineNumber = StringUtil.parseInt(matcher.group(2), 0) - 1;
    if (lineNumber < 0) {
      return null;
    }

    VirtualFile virtualFile = LocalFileSystem.getInstance().refreshAndFindFileByPath(myWorkingDirectory + "/" + fileName);
    if (virtualFile == null) {
      VirtualFile moduleFile = myModule.getModuleFile();
      if (moduleFile != null) {
        VirtualFile moduleDirectory = moduleFile.getParent();
        if (moduleDirectory != null) {
          virtualFile = moduleDirectory.findFileByRelativePath(fileName);
        }
      }
    }
    if (virtualFile == null) {
      return null;
    }

    HyperlinkInfo hyperlinkInfo = new OpenFileHyperlinkInfo(myModule.getProject(), virtualFile, lineNumber);
    int lineStart = entireLength - line.length();
    return new Result(lineStart + matcher.start(1), lineStart + matcher.end(2), hyperlinkInfo);
  }
}
