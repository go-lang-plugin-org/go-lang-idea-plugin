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

import com.intellij.openapi.compiler.CompilerMessageCategory;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VfsUtilCore;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GoCompilerError {
  private static final Pattern COMPILER_MESSAGE_PATTERN = Pattern.compile("^(.+):(\\d+):\\s*(.+)$");

  private final String errorMessage;
  @Nullable private final String url;
  private final long line;
  private final CompilerMessageCategory category;

  private GoCompilerError(String errorMessage, @Nullable String url, long line, CompilerMessageCategory category) {
    this.errorMessage = errorMessage;
    this.url = url;
    this.line = line;
    this.category = category;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  @Nullable
  public String getUrl() {
    return url;
  }

  public long getLine() {
    return line;
  }

  @Nullable
  public static GoCompilerError create(@NotNull String rootPath, @NotNull String compilerMessage) {
    Matcher matcher = COMPILER_MESSAGE_PATTERN.matcher(StringUtil.trimTrailing(compilerMessage));
    if (matcher.matches()) {
      String relativeFilePath = FileUtil.toSystemIndependentName(StringUtil.notNullize(FileUtil.toCanonicalPath(matcher.group(1))));
      String line = matcher.group(2);
      String details = matcher.group(3);

      String path = rootPath.isEmpty() ? relativeFilePath : new File(FileUtil.toSystemIndependentName(rootPath), relativeFilePath).getPath();
      long lineNumber = parseLong(line);
      return new GoCompilerError(details, VfsUtilCore.pathToUrl(path), lineNumber, CompilerMessageCategory.ERROR);
    }
    else if (!compilerMessage.isEmpty() && Character.isJavaIdentifierPart(compilerMessage.charAt(0))) {
      return new GoCompilerError(compilerMessage, null, -1L, CompilerMessageCategory.ERROR);
    }
    return null;
  }

  public CompilerMessageCategory getCategory() {
    return category;
  }
  
  private static long parseLong(@NotNull String s) {
    try {
      return Long.parseLong(s);
    }
    catch (NumberFormatException e) {
      return -1;
    }
  }
}
