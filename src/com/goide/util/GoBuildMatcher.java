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

package com.goide.util;

import com.goide.GoConstants;
import com.goide.psi.GoFile;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.regex.Pattern;

/**
 * @see "$GOROOT/src/go/build/build.go" and relevant functions
 */
public class GoBuildMatcher {
  private static final Pattern WHITESPACES = Pattern.compile("\\s+");

  private Set<CharSequence> knownOS;
  private Set<CharSequence> knownArch;
  private Set<CharSequence> buildTags;
  private String os;
  private String arch;

  public GoBuildMatcher(
    @NotNull final Set<CharSequence> knownOS,
    @NotNull final Set<CharSequence> knownArch,
    @Nullable final Set<CharSequence> buildTags,
    @NotNull final String targetOS,
    @NotNull final String targetArch
  ) {
    this.knownOS = knownOS;
    this.knownArch = knownArch;
    this.buildTags = buildTags;
    this.os = targetOS;
    this.arch = targetArch;
  }

  public boolean matchFile(@NotNull PsiFile file) {
    return matchFile(file, true);
  }

  /**
   * @param file
   * @param checkBuildFlags should be false for directly used files: go run gen.go
   */
  public boolean matchFile(@NotNull PsiFile file, boolean checkBuildFlags) {
    final String name = file.getName();
    if (StringUtil.startsWithChar(name, '_') || StringUtil.startsWithChar(name, '.')) {
      return false;
    }

    // TODO support .c, .cpp and other
    if (!(file instanceof GoFile)) {
      return false;
    }

    if (!goodOSArchFile(file.getName())) {
      return false;
    }

    if (!checkBuildFlags) {
      return true;
    }

    // Look for +build comments to accept or reject the file.
    String flags = ((GoFile)file).getBuildFlags();

    if (null == flags) {
      return true;
    }

    lines:
    for (final String line : StringUtil.split(flags, "|")) {
      for (final String tag : WHITESPACES.split(line)) {
        if (match(tag)) {
          continue lines;
        }
      }

      return false;
    }

    return true;
  }

  private boolean match(@NotNull String name) {
    if (name.isEmpty()) {
      return false;
    }

    if (name.contains(",")) { // comma separated list
      for (final String tag : name.split(",")) {
        if (!match(tag)) {
          return false;
        }
      }
      return true;
    }

    if (name.startsWith("!!")) { // bad syntax, reject always
      return false;
    }

    if (name.startsWith("!")) { // negation
      return !match(name.substring(1));
    }

    if (os.equals(name) || arch.equals(name)) {
      return true;
    }

    if ("linux".equals(name) && "android".equals(os)) {
      return true;
    }

    if (buildTags != null && buildTags.contains(name)) {
      return true;
    }

    return false;
  }

  private boolean goodOSArchFile(@NotNull String fileName) {
    String name = StringUtil.substringAfter(fileName, "_");
    if (name == null || name.length() == 0) {
      return true;
    }

    name = StringUtil.trimEnd(FileUtil.getNameWithoutExtension(name), GoConstants.TEST_SUFFIX);

    String[] parts = name.split("_");
    final int n = parts.length;

    if (n >= 2 && knownOS.contains(parts[n - 2]) && knownArch.contains(parts[n - 1])) {
      if (!arch.equals(parts[n-1])) {
        return false;
      }

      if ("linux".equals(parts[n-2]) && "android".equals(os)) {
        return true;
      }

      return os.equals(parts[n-2]);
    }

    if (n >= 1) {
      if (knownOS.contains(parts[n-1])) {
        if ("linux".equals(parts[n-1]) && "android".equals(os)) {
          return true;
        }

        return os.equals(parts[n-1]);
      }

      if (knownArch.contains(parts[n-1])) {
        return arch.equals(parts[n-1]);
      }
    }

    return true;
  }
}