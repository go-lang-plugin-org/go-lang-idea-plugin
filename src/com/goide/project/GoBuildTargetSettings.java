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

package com.goide.project;

import com.intellij.openapi.util.SimpleModificationTracker;
import com.intellij.util.ArrayUtil;
import com.intellij.util.ThreeState;
import com.intellij.util.xmlb.annotations.Tag;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

@Tag("buildTags")
public class GoBuildTargetSettings extends SimpleModificationTracker {
  public static final String ANY_COMPILER = "Any";
  public static final String DEFAULT = "default";

  @NotNull public String os = DEFAULT;
  @NotNull public String arch = DEFAULT;
  @NotNull public ThreeState cgo = ThreeState.UNSURE;
  @NotNull public String compiler = ANY_COMPILER;
  @NotNull public String goVersion = DEFAULT;
  @NotNull public String[] customFlags = ArrayUtil.EMPTY_STRING_ARRAY;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof GoBuildTargetSettings)) return false;

    GoBuildTargetSettings settings = (GoBuildTargetSettings)o;

    if (!os.equals(settings.os)) return false;
    if (!arch.equals(settings.arch)) return false;
    if (cgo != settings.cgo) return false;
    if (!compiler.equals(settings.compiler)) return false;
    if (!goVersion.equals(settings.goVersion)) return false;
    return Arrays.equals(customFlags, settings.customFlags);
  }

  @Override
  public int hashCode() {
    int result = os.hashCode();
    result = 31 * result + arch.hashCode();
    result = 31 * result + cgo.hashCode();
    result = 31 * result + compiler.hashCode();
    result = 31 * result + goVersion.hashCode();
    result = 31 * result + Arrays.hashCode(customFlags);
    return result;
  }
}
