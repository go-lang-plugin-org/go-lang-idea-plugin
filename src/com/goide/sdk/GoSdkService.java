package com.goide.sdk;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.SimpleModificationTracker;
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
  
  @Nullable
  public abstract String getSdkVersion(@Nullable Module module);
  
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
}
