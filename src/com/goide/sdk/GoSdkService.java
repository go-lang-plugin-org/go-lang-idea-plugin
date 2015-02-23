package com.goide.sdk;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class GoSdkService {
  public static GoSdkService getInstance() {
    return ServiceManager.getService(GoSdkService.class);
  }

  @Nullable
  public abstract String getSdkHomePath(@NotNull Module module);

  @Nullable
  public abstract String getSdkHomePath(@NotNull Project project);
  
  @Nullable
  public abstract String getSdkVersion(@NotNull Module module);

  @Nullable
  public abstract String getSdkVersion(@NotNull Project project);
  
  public abstract void chooseAndSetSdk(@NotNull Project project, @Nullable Module module);

  /**
   * Use this method in order to check whether the method is appropriate for providing Go-specific code insight
   */
  @Contract("null -> false")
  public boolean isGoModule(@Nullable Module module) {
    return module != null && !module.isDisposed();
  }
}
