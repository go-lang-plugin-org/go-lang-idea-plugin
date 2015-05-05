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

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @see "$GOROOT/src/go/build/build.go" and relevant functions
 */
public class GoBuildMatcher {
  private static final Pattern WHITESPACES = Pattern.compile("\\s+");

  private Set<String> myKnownOS;
  private Set<String> myKnownArch;
  private Set<String> mySupportedBuildTags;
  private String myTargetOS;
  private String myTargetArch;
                                                                                              
  public GoBuildMatcher(@NotNull Set<String> knownOS, @NotNull Set<String> knownArch, @Nullable Set<String> supportedBuildTags, 
                        @NotNull String targetOS, @NotNull String targetArch) {
    myKnownOS = knownOS;
    myKnownArch = knownArch;
    mySupportedBuildTags = supportedBuildTags;
    myTargetOS = targetOS;
    myTargetArch = targetArch;
  }

  public boolean matchFile(@NotNull PsiFile file) {
    return matchFile(file, true);
  }

  /**
   * @param checkBuildFlags should be false for directly used files: go run gen.go
   */
  public boolean matchFile(@NotNull PsiFile file, boolean checkBuildFlags) {
    if (!(file instanceof GoFile)) {
      // TODO support .c, .cpp and other
      return false;
    }
    return match(file.getName(), ((GoFile)file).getBuildFlags(), checkBuildFlags);
  }

  public boolean match(@NotNull String fileName, @Nullable String buildFlags, boolean checkBuildFlags) {
    if (StringUtil.startsWithChar(fileName, '_') || StringUtil.startsWithChar(fileName, '.')) return false;

    if (!matchFileName(fileName)) return false;
    
    if (!checkBuildFlags || buildFlags == null) return true;
    for (final String line : StringUtil.split(buildFlags, "|")) {
      if (!matchBuildFlagsLine(line)) return false;
    }

    return true;
  }

  private boolean matchBuildFlagsLine(@NotNull String line) {
    for (final String tag : WHITESPACES.split(line)) {
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
    if (mySupportedBuildTags != null && mySupportedBuildTags.contains(name)) return true;
    return false;
  }

  public boolean matchFileName(@NotNull String fileName) {
    String name = StringUtil.substringAfter(fileName, "_");
    if (StringUtil.isEmpty(name)) {
      return true;
    }

    name = StringUtil.trimEnd(FileUtil.getNameWithoutExtension(name), GoConstants.TEST_SUFFIX);

    List<String> parts = StringUtil.split(name, "_");
    final int n = parts.size();

    if (n >= 2 && myKnownOS.contains(parts.get(n - 2)) && myKnownArch.contains(parts.get(n - 1))) {
      if (!myTargetArch.equals(parts.get(n - 1))) {
        return false;
      }

      return matchOS(parts.get(n - 2));
    }

    if (n >= 1) {
      if (myKnownOS.contains(parts.get(n - 1))) {
        return matchOS(parts.get(n - 1));
      }

      if (myKnownArch.contains(parts.get(n - 1))) {
        return myTargetArch.equals(parts.get(n - 1));
      }
    }

    return true;
  }

  private boolean matchOS(@NotNull String name) {
    if (myTargetOS.equals(name) || myTargetArch.equals(name)) {
      return true;
    }

    return GoConstants.LINUX_OS.equals(name) && GoConstants.ANDROID_OS.equals(myTargetOS);
  }
}