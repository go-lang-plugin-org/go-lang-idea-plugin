package com.goide;

import com.intellij.openapi.application.PathMacros;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.util.EnvironmentUtil;
import com.intellij.util.PathUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class GoEnvironmentUtil {

  private GoEnvironmentUtil() {
  }

  @NotNull
  public static String getExecutableResultForModule(@NotNull String modulePath, @NotNull String outputDirectory) {
    return outputDirectory + File.separatorChar + getBinaryFileNameForPath(modulePath);
  }

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
    String path = EnvironmentUtil.getValue(GoConstants.GO_PATH);
    return path != null ? path : PathMacros.getInstance().getValue(GoConstants.GO_PATH);
  }
}
