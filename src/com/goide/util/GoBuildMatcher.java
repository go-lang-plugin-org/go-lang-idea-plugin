/*
 * Copyright 2013-2016 Sergey Ignatov, Alexander Zolotov, Florin Patan
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
import com.goide.sdk.GoSdkUtil;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiFile;
import com.intellij.util.ThreeState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.regex.Pattern;

/**
 * @see "$GOROOT/src/go/build/build.go" and relevant functions
 */
public class GoBuildMatcher {
  private static final Pattern WHITESPACES = Pattern.compile("\\s+");

  @NotNull private final GoTargetSystem myTarget;

  public GoBuildMatcher(@NotNull GoTargetSystem target) {
    myTarget = target;
  }

  public boolean matchFile(@NotNull PsiFile file) {
    return matchFile(file, true);
  }

  /**
   * @param checkBuildFlags should be false for directly used files: go run gen.go
   */
  private boolean matchFile(@NotNull PsiFile file, boolean checkBuildFlags) {
    if (!(file instanceof GoFile)) {
      // TODO support .c, .cpp and other
      return false;
    }
    if (((GoFile)file).hasCPathImport() && myTarget.cgoEnabled != ThreeState.YES) return false;

    return match(file.getName(), ((GoFile)file).getBuildFlags(), checkBuildFlags);
  }

  private boolean match(@NotNull String fileName, @Nullable String buildFlags, boolean checkBuildFlags) {
    if (!matchFileName(fileName)) return false;

    if (!checkBuildFlags || buildFlags == null) return true;
    for (String line : StringUtil.split(buildFlags, "|")) {
      if (!matchBuildFlagsLine(line)) return false;
    }
    return true;
  }

  private boolean matchBuildFlagsLine(@NotNull String line) {
    for (String tag : WHITESPACES.split(line)) {
      if (matchBuildFlag(tag)) return true;
    }
    return false;
  }

  public boolean matchBuildFlag(@NotNull String name) {
    if (name.isEmpty()) return false;

    if (StringUtil.containsChar(name, ',')) { // comma separated list
      for (String tag : StringUtil.split(name, ",")) {
        if (!matchBuildFlag(tag)) {
          return false;
        }
      }
      return true;
    }

    // bad syntax, reject always
    if (name.startsWith("!!")) return false;

    // negation
    if (name.startsWith("!")) return !matchBuildFlag(name.substring(1));

    if (matchOS(name)) return true;
    if (GoConstants.KNOWN_COMPILERS.contains(name)) {
      return myTarget.compiler == null || name.equals(myTarget.compiler);
    }
    if (GoConstants.KNOWN_VERSIONS.contains(name)) {
      return myTarget.goVersion == null || GoSdkUtil.compareVersions(myTarget.goVersion, StringUtil.trimStart(name, "go")) >= 0;
    }
    if ("cgo".equals(name)) {
      return myTarget.cgoEnabled == ThreeState.YES;
    }
    return myTarget.supportsFlag(name);
  }

  public boolean matchFileName(@NotNull String fileName) {
    String name = StringUtil.substringAfter(fileName, "_");
    if (StringUtil.isEmpty(name)) {
      return true;
    }

    name = StringUtil.trimEnd(FileUtil.getNameWithoutExtension(name), GoConstants.TEST_SUFFIX);

    List<String> parts = StringUtil.split(name, "_");
    int n = parts.size();

    if (n >= 2 && GoConstants.KNOWN_OS.contains(parts.get(n - 2)) && GoConstants.KNOWN_ARCH.contains(parts.get(n - 1))) {
      if (!myTarget.arch.equals(parts.get(n - 1))) {
        return false;
      }

      return matchOS(parts.get(n - 2));
    }

    if (n >= 1) {
      if (GoConstants.KNOWN_OS.contains(parts.get(n - 1))) {
        return matchOS(parts.get(n - 1));
      }

      if (GoConstants.KNOWN_ARCH.contains(parts.get(n - 1))) {
        return myTarget.arch.equals(parts.get(n - 1));
      }
    }

    return true;
  }

  private boolean matchOS(@NotNull String name) {
    if (myTarget.os.equals(name) || myTarget.arch.equals(name)) {
      return true;
    }

    return GoConstants.LINUX_OS.equals(name) && GoConstants.ANDROID_OS.equals(myTarget.os);
  }
}