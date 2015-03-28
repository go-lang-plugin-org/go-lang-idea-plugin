package com.goide.appengine;

import com.goide.sdk.GoSdkService;
import com.intellij.appengine.AppEngineExtension;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;

public class GoAppEngineExtension extends AppEngineExtension {
  @Override
  public boolean isAppEngineEnabled(@Nullable PsiElement context) {
    return context != null &&
           GoSdkService.getInstance(context.getProject()).isAppEngineSdk(ModuleUtilCore.findModuleForPsiElement(context));
  }
}
