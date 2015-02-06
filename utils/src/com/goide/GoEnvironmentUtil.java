package com.goide;

import com.intellij.execution.configurations.PathEnvironmentVariableUtil;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.util.PathUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class GoEnvironmentUtil {
  public static final String GO_EXECUTABLE_NAME = "go";
  
  private GoEnvironmentUtil() {
  }

  @NotNull
  public static File getExecutableForSdk(@NotNull String sdkHome) {
    File fromSdkPath = getExecutable(new File(sdkHome, "bin").getAbsolutePath(), GO_EXECUTABLE_NAME);
    File fromEnvironment = PathEnvironmentVariableUtil.findInPath(GO_EXECUTABLE_NAME);
    return fromSdkPath.canExecute() || fromEnvironment == null ? fromSdkPath : fromEnvironment;
  }

  @NotNull
  public static String getExecutableResultForModule(@NotNull String modulePath, @NotNull String outputDirectory) {
    return outputDirectory + File.separatorChar + getBinaryFileNameForPath(modulePath);
  }

  @NotNull
  private static String getBinaryFileNameForPath(@NotNull String path) {
    String resultBinaryName = FileUtil.getNameWithoutExtension(PathUtil.getFileName(path));
    return SystemInfo.isWindows ? resultBinaryName + ".exe" : resultBinaryName;
  }

  @NotNull
  private static File getExecutable(@NotNull String path, @NotNull String command) {
    return new File(path, getBinaryFileNameForPath(command));
  }
}
