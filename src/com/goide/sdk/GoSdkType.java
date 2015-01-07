/*
 * Copyright 2013-2014 Sergey Ignatov, Alexander Zolotov
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

package com.goide.sdk;

import com.goide.GoIcons;
import com.goide.jps.model.JpsGoModelSerializerExtension;
import com.goide.jps.model.JpsGoSdkType;
import com.intellij.execution.configurations.PathEnvironmentVariableUtil;
import com.intellij.openapi.projectRoots.*;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.jdom.Element;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GoSdkType extends SdkType {
  private static final Pattern RE_GO_VERSION = Pattern.compile("theVersion\\s*=\\s*`go(.*)`");

  public GoSdkType() {
    super(JpsGoModelSerializerExtension.GO_SDK_TYPE_ID);
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
    }
    else {
      if (SystemInfo.isMac) {
        String fromEnv = findPathInEnvironment();
        if (fromEnv != null) return fromEnv;
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
      }
      catch (IOException ignore) {
      }
    }
    return null;
  }

  @Override
  public boolean isValidSdkHome(@NotNull String path) {
    path = adjustSdkPath(path);
    return JpsGoSdkType.getGoExecutableFile(path).canExecute();
  }

  @NotNull
  public static String adjustSdkPath(@NotNull String path) {
    if (isAppEngine(path)) path = path + "/" + "goroot";
    return path;
  }

  private static boolean isAppEngine(@Nullable String path) {
    if (path == null) return false;
    return new File(path, "appcfg.py").exists();
  }

  @NotNull
  @Override
  public String suggestSdkName(@Nullable String currentSdkName, @NotNull String sdkHome) {
    final String version = getVersionString(sdkHome);
    if (version == null) {
      return "Unknown Go version at " + sdkHome;
    }
    return "Go " + version;
  }

  @Nullable
  @Override
  public String getVersionString(@NotNull String sdkHome) {
    sdkHome = adjustSdkPath(sdkHome);
    try {
      final String oldStylePath = new File(sdkHome, "src/pkg/runtime/zversion.go").getPath();
      final String newStylePath = new File(sdkHome, "src/runtime/zversion.go").getPath();
      final File zVersionGo = FileUtil.findFirstThatExist(oldStylePath, newStylePath);
      final String text = FileUtil.loadFile(zVersionGo);
      final Matcher matcher = RE_GO_VERSION.matcher(text);
      if (!matcher.find()) {
        return null;
      }
      return matcher.group(1);
    }
    catch (IOException ignore) {
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

  @NotNull
  @NonNls
  @Override
  public String getPresentableName() {
    return "Go SDK";
  }

  @Override
  public void setupSdkPaths(@NotNull Sdk sdk) {
    if (sdk.getVersionString() == null) {
      throw new RuntimeException("SDK version is not defined");
    }
    final SdkModificator modificator = sdk.getSdkModificator();
    String path = sdk.getHomePath();
    if (path == null) {
      return;
    }
    path = adjustSdkPath(path);
    modificator.setHomePath(path);
    final String srcPath = GoSdkUtil.getSrcLocation(sdk.getVersionString());
    // scr is enough at the moment, possible process binaries from pkg
    add(modificator, new File(path, srcPath));
    modificator.commitChanges();
  }

  private static void add(@NotNull SdkModificator modificator, @NotNull File file) {
    if (file.isDirectory()) {
      VirtualFile dir = LocalFileSystem.getInstance().findFileByIoFile(file);
      add(modificator, dir);
    }
  }

  private static void add(@NotNull SdkModificator modificator, @Nullable VirtualFile dir) {
    if (dir != null && dir.isDirectory()) {
      modificator.addRoot(dir, OrderRootType.CLASSES);
      modificator.addRoot(dir, OrderRootType.SOURCES);
    }
  }
}
