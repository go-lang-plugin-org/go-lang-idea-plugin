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

package com.goide;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.PathMacros;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.util.EnvironmentUtil;
import com.intellij.util.PathUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GoEnvironmentUtil {
  private GoEnvironmentUtil() {}

  @NotNull
  public static String getBinaryFileNameForPath(@NotNull String path) {
    String resultBinaryName = FileUtil.getNameWithoutExtension(PathUtil.getFileName(path));
    return SystemInfo.isWindows ? resultBinaryName + ".exe" : resultBinaryName;
  }

  @NotNull
  public static String getGaeExecutableFileName(boolean gcloudInstallation) {
    if (SystemInfo.isWindows) {
      return gcloudInstallation ? GoConstants.GAE_CMD_EXECUTABLE_NAME : GoConstants.GAE_BAT_EXECUTABLE_NAME;
    }
    return GoConstants.GAE_EXECUTABLE_NAME;
  }

  @Nullable
  public static String retrieveGoPathFromEnvironment() {
    if (ApplicationManager.getApplication().isUnitTestMode()) return null;
    
    String path = EnvironmentUtil.getValue(GoConstants.GO_PATH);
    return path != null ? path : PathMacros.getInstance().getValue(GoConstants.GO_PATH);
  }
}
