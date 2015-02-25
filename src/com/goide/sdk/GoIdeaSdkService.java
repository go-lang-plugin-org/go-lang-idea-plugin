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
  public GoIdeaSdkService(@NotNull Project project) {
    super(project);
  }

  @Override
  public String getSdkHomePath(@Nullable Module module) {
    Sdk sdk = getGoSdk(module);
    return sdk != null ? sdk.getHomePath() : null;
  }

  @Nullable
  @Override
  public String getSdkVersion(@Nullable Module module) {
    Sdk sdk = getGoSdk(module);
    return sdk != null ? sdk.getVersionString() : null;
  }

  @Override
  public void chooseAndSetSdk(@Nullable final Module module) {
    Sdk projectSdk = ProjectSettingsService.getInstance(myProject).chooseAndSetSdk();
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

  private Sdk getGoSdk(@Nullable Module module) {
    if (module != null) {
      Sdk sdk = ModuleRootManager.getInstance(module).getSdk();
      if (sdk != null && sdk.getSdkType() instanceof GoSdkType) {
        return sdk;
      }
    }
    Sdk sdk = ProjectRootManager.getInstance(myProject).getProjectSdk();
    return sdk != null && sdk.getSdkType() instanceof GoSdkType ? sdk : null;
  }
}
