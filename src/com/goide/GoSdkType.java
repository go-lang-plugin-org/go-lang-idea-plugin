package com.goide;

import com.intellij.execution.configurations.PathEnvironmentVariableUtil;
import com.intellij.openapi.projectRoots.*;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.containers.ContainerUtil;
import org.jdom.Element;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class GoSdkType extends SdkType {
  public static String GO_SDK_NAME = "Go SDK";

  public GoSdkType() {
    super(GO_SDK_NAME);
  }

  @NotNull
  public static GoSdkType getInstance() {
    return SdkType.findInstance(GoSdkType.class);
  }

  @NotNull
  @Override
  public Icon getIcon() {
    return GoIcons.ICON;
  }

  @NotNull
  @Override
  public Icon getIconForAddAction() {
    return getIcon();
  }

  @Nullable
  @Override
  public String suggestHomePath() {
    if (SystemInfo.isWindows) {
      return "C:\\cygwin\\bin";
    } else {
      String fromEnv = findPathInEnvironment();
      if (fromEnv != null) {
        return fromEnv;
      }
      else if (SystemInfo.isMac) {
        String defaultPath = "/usr/local/go";
        if (new File(defaultPath).exists()) return defaultPath;
        String macPorts = "/opt/local/lib/go";
        if (new File(macPorts).exists()) return macPorts;
        return null;
      } 
      else if (SystemInfo.isLinux) {
        return "/usr/lib/go";
      }
    }
    return null;
  }

  @Nullable
  private static String findPathInEnvironment() {
    File fileFromPath = PathEnvironmentVariableUtil.findInPath("go");
    if (fileFromPath != null) {
      File canonicalFile;
      try {
        canonicalFile = fileFromPath.getCanonicalFile();
        String path = canonicalFile.getPath();
        if (path.endsWith("bin/go")) {
          return StringUtil.trimEnd(path, "bin/go");
        }
      } catch (IOException ignore) {
      }
    }
    return null;
  }

  @Override
  public boolean isValidSdkHome(@NotNull String path) {
    return true; // todo
  }

  @NotNull
  @Override
  public String suggestSdkName(@Nullable String currentSdkName, @NotNull String sdkHome) {
    String version = getVersionString(sdkHome);
    if (version == null) return "Unknown Go version at " + sdkHome;
    return "Go " + version;
  }

  @Nullable
  @Override
  public String getVersionString(@NotNull String sdkHome) {
    // todo
    try {
      String s = FileUtil.loadFile(new File(sdkHome, "src/pkg/runtime/zversion.go"));
      List<String> split = StringUtil.split(s, " ");
      String lastItem = ContainerUtil.getLastItem(split);
      if (lastItem == null) return null;
      return lastItem.replace("go", "").replaceAll("`", "");
    } catch (IOException ignore) {
    }
    return null;
  }

  @Nullable
  @Override
  public String getDefaultDocumentationUrl(@NotNull Sdk sdk) {
    return null;
  }

  @Nullable
  @Override
  public AdditionalDataConfigurable createAdditionalDataConfigurable(@NotNull SdkModel sdkModel, @NotNull SdkModificator sdkModificator) {
    return null;
  }

  @Override
  public void saveAdditionalData(@NotNull SdkAdditionalData additionalData, @NotNull Element additional) {
  }

  @NonNls
  @Override
  public String getPresentableName() {
    return GO_SDK_NAME;
  }

  @Override
  public void setupSdkPaths(@NotNull Sdk sdk) {
    SdkModificator sdkModificator = sdk.getSdkModificator();
    // todo
    sdkModificator.commitChanges();
  }
}