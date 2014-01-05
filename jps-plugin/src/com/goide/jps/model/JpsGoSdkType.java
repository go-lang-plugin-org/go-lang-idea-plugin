package com.goide.jps.model;

import com.intellij.execution.configurations.PathEnvironmentVariableUtil;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.util.PathUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.model.JpsDummyElement;
import org.jetbrains.jps.model.JpsElementFactory;
import org.jetbrains.jps.model.JpsElementTypeWithDefaultProperties;
import org.jetbrains.jps.model.library.sdk.JpsSdkType;

import java.io.File;

public class JpsGoSdkType extends JpsSdkType<JpsDummyElement> implements JpsElementTypeWithDefaultProperties<JpsDummyElement> {
  public static final JpsGoSdkType INSTANCE = new JpsGoSdkType();

  @NotNull
  public static File getGoExecutableFile(@NotNull final String sdkHome) {
    File fromSdkPath = getExecutable(new File(sdkHome, "bin").getAbsolutePath(), "go");
    File fromEnvironment = PathEnvironmentVariableUtil.findInPath("go");
    return fromSdkPath.canExecute() || fromEnvironment == null ? fromSdkPath : fromEnvironment;
  }

  @NotNull
  public static String getBinaryFileNameForPath(@NotNull String path) {
    String resultBinaryName = FileUtil.getNameWithoutExtension(PathUtil.getFileName(path));
    return SystemInfo.isWindows ? resultBinaryName + ".exe" : resultBinaryName;
  }

  @NotNull
  @Override
  public JpsDummyElement createDefaultProperties() {
    return JpsElementFactory.getInstance().createDummyElement();
  }

  @NotNull
  private static File getExecutable(@NotNull final String path, @NotNull final String command) {
    return new File(path, getBinaryFileNameForPath(command));
  }
}
