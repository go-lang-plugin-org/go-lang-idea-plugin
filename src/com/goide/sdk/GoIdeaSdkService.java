package com.goide.sdk;

import com.goide.GoModuleType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ModuleRootModificationUtil;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.roots.ui.configuration.ProjectSettingsService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GoIdeaSdkService extends GoSdkService {
  @Override
  public String getSdkHomePath(@NotNull Module module) {
    Sdk sdk = ModuleRootManager.getInstance(module).getSdk();
    return sdk != null && sdk.getSdkType() instanceof GoSdkType ? sdk.getHomePath() : null;
  }

  @Override
  public String getSdkHomePath(@NotNull Project project) {
    Sdk sdk = ProjectRootManager.getInstance(project).getProjectSdk();
    return sdk != null && sdk.getSdkType() instanceof GoSdkType ? sdk.getHomePath() : null;
  }

  @Nullable
  @Override
  public String getSdkVersion(@NotNull Project project) {
    Sdk sdk = ProjectRootManager.getInstance(project).getProjectSdk();
    return sdk != null && sdk.getSdkType() instanceof GoSdkType ? sdk.getVersionString() : null;
  }
  
  @Nullable
  @Override
  public String getSdkVersion(@NotNull Module module) {
    Sdk sdk = ModuleRootManager.getInstance(module).getSdk();
    return sdk != null && sdk.getSdkType() instanceof GoSdkType ? sdk.getVersionString() : null;
  }

  @Override
  public void chooseAndSetSdk(@NotNull Project project, @Nullable final Module module) {
    Sdk projectSdk = ProjectSettingsService.getInstance(project).chooseAndSetSdk();
    if (projectSdk == null && module != null) {
      ApplicationManager.getApplication().runWriteAction(new Runnable() {
        @Override
        public void run() {
          if (!module.isDisposed()) {
            ModuleRootModificationUtil.setSdkInherited(module);
          }
        }
      });
    }
  }

  @Override
  public boolean isGoModule(@Nullable Module module) {
    return super.isGoModule(module) && ModuleUtil.getModuleType(module) == GoModuleType.getInstance();
  }
}
