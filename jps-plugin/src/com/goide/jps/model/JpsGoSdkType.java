package com.goide.jps.model;

import com.intellij.openapi.util.SystemInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.model.JpsDummyElement;
import org.jetbrains.jps.model.JpsElementFactory;
import org.jetbrains.jps.model.JpsElementTypeWithDefaultProperties;
import org.jetbrains.jps.model.library.sdk.JpsSdkType;

import java.io.File;

public class JpsGoSdkType extends JpsSdkType<JpsDummyElement> implements JpsElementTypeWithDefaultProperties<JpsDummyElement> {
  public static final JpsGoSdkType INSTANCE = new JpsGoSdkType();

  @NotNull
  public static File getExecutable(@NotNull final String path, @NotNull final String command) {
    return new File(path, SystemInfo.isWindows ? command + ".exe" : command);
  }

  @NotNull
  public static File getGoExecutableFile(@NotNull final String sdkHome) {
    return getExecutable(new File(sdkHome, "bin").getAbsolutePath(), "go");
  }

  @NotNull
  @Override
  public JpsDummyElement createDefaultProperties() {
    return JpsElementFactory.getInstance().createDummyElement();
  }
}
