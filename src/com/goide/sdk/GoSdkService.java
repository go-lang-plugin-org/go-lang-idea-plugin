package com.goide.sdk;

import com.goide.GoConstants;
import com.goide.GoEnvironmentUtil;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.SimpleModificationTracker;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.PathUtil;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class GoSdkService extends SimpleModificationTracker {
  @NotNull
  protected final Project myProject;

  protected GoSdkService(@NotNull Project project) {
    myProject = project;
  }

  public static GoSdkService getInstance(@NotNull Project project) {
    return ServiceManager.getService(project, GoSdkService.class);
  }

  @Nullable
  public abstract String getSdkHomePath(@Nullable Module module);

  @NotNull
  public static String libraryRootToSdkPath(@NotNull VirtualFile root) {
    return VfsUtilCore.urlToPath(StringUtil.trimEnd(StringUtil.trimEnd(StringUtil.trimEnd(root.getUrl(), "src/pkg"), "src"), "/"));
  }

  @Nullable
  public abstract String getSdkVersion(@Nullable Module module);

  public boolean isAppEngineSdk(@Nullable Module module) {
    return isAppEngineSdkPath(getSdkHomePath(module));
  }

  public static boolean isAppEngineSdkPath(@Nullable String path) {
    return path != null && path.endsWith(GoConstants.APP_ENGINE_GO_ROOT_DIRECTORY_PATH);
  }

  public abstract void chooseAndSetSdk(@Nullable Module module);

  /**
   * Use this method in order to check whether the method is appropriate for providing Go-specific code insight
   */
  @Contract("null -> false")
  public boolean isGoModule(@Nullable Module module) {
    return module != null && !module.isDisposed();
  }

  @Nullable
  public Configurable createSdkConfigurable() {
    return null;
  }

  @Nullable
  public String getGoExecutablePath(@Nullable Module module) {
    return getGoExecutablePath(getSdkHomePath(module));
  }

  public static String getGoExecutablePath(@Nullable String sdkHomePath) {
    if (sdkHomePath != null) {
      if (isAppEngineSdkPath(sdkHomePath)) {
        sdkHomePath = StringUtil.trimEnd(PathUtil.toSystemIndependentName(sdkHomePath), GoConstants.APP_ENGINE_GO_ROOT_DIRECTORY_PATH);
        // gcloud and standalone installations
        return sdkHomePath.endsWith(GoConstants.GCLOUD_APP_ENGINE_DIRECTORY_PATH)
               ? FileUtil.join(StringUtil.trimEnd(sdkHomePath, GoConstants.GCLOUD_APP_ENGINE_DIRECTORY_PATH), "bin", SystemInfo.isWindows ? GoConstants.GAE_CMD_EXECUTABLE_NAME : GoConstants.GAE_EXECUTABLE_NAME)
               : FileUtil.join(sdkHomePath, SystemInfo.isWindows ? GoConstants.GAE_BAT_EXECUTABLE_NAME : GoConstants.GAE_EXECUTABLE_NAME);
      }
      else {
        return FileUtil.join(sdkHomePath, "bin", GoEnvironmentUtil.getBinaryFileNameForPath(GoConstants.GO_EXECUTABLE_NAME));
      }
    }
    return null;
  }
}
