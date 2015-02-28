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
  public static final String GO_EXECUTABLE_NAME = "go";
  public static final String GAE_EXECUTABLE_NAME = "goapp";

  private GoEnvironmentUtil() {
  }

  @NotNull
  public static File getExecutableForSdk(@NotNull String sdkHome) {
    File goFromSdkPath = getExecutable(new File(sdkHome, "bin").getAbsolutePath(), GO_EXECUTABLE_NAME);
    File gaeFromSdkPath = getExecutable(new File(sdkHome, "bin").getAbsolutePath(), GAE_EXECUTABLE_NAME);
    return gaeFromSdkPath.canExecute() ? gaeFromSdkPath : goFromSdkPath;
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
  private static File getExecutable(@NotNull String path, @NotNull String command) {
    return new File(path, getBinaryFileNameForPath(command));
  }

  @Nullable
  public static String retrieveGoPathFromEnvironment() {
    String path = EnvironmentUtil.getValue(GoConstants.GO_PATH);
    return path != null ? path : PathMacros.getInstance().getValue(GoConstants.GO_PATH);
  }
}
