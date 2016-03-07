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

import com.goide.sdk.GoSdkService;
import com.goide.util.GoTargetSystem;
import com.goide.util.GoUtil;
import com.intellij.openapi.module.Module;
import com.intellij.util.ArrayUtil;
import com.intellij.util.ThreeState;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GoBuildTargetUtil {
  private static final String GAE_BUILD_FLAG = "appengine";

  @NotNull
  public static GoTargetSystem getTargetSystemDescriptor(@NotNull Module module) {
    GoBuildTargetSettings settings = GoModuleSettings.getInstance(module).getBuildTargetSettings();
    String os = realValue(settings.os, GoUtil.systemOS());
    String arch = realValue(settings.arch, GoUtil.systemArch());
    ThreeState cgo = settings.cgo == ThreeState.UNSURE ? GoUtil.systemCgo(os, arch) : settings.cgo;
    String moduleSdkVersion = GoSdkService.getInstance(module.getProject()).getSdkVersion(module);
    String[] customFlags = GoSdkService.getInstance(module.getProject()).isAppEngineSdk(module)
                           ? ArrayUtil.prepend(GAE_BUILD_FLAG, settings.customFlags)
                           : settings.customFlags;
    String compiler = GoBuildTargetSettings.ANY_COMPILER.equals(settings.compiler) ? null : settings.compiler;
    return new GoTargetSystem(os, arch, realValue(settings.goVersion, moduleSdkVersion), compiler, cgo, customFlags);
  }

  @Contract("_,null->!null")
  private static String realValue(@NotNull String value, @Nullable String defaultValue) {
    return GoBuildTargetSettings.DEFAULT.equals(value) ? defaultValue : value;
  }
}
