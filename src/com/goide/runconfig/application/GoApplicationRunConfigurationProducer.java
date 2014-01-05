package com.goide.runconfig.application;

import com.goide.psi.GoFile;
import com.goide.psi.impl.GoPsiImplUtil;
import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.actions.RunConfigurationProducer;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.Nullable;

public class GoApplicationRunConfigurationProducer extends RunConfigurationProducer<GoApplicationConfiguration> implements Cloneable {

  public GoApplicationRunConfigurationProducer() {
    super(GoApplicationRunConfigurationType.getInstance());
  }

  @Override
  protected boolean setupConfigurationFromContext(GoApplicationConfiguration configuration,
                                                  ConfigurationContext context,
                                                  Ref sourceElement) {
    PsiFile containingFile = getFileFromContext(context);
    if (containingFile != null) {
      if (GoPsiImplUtil.findMainFunction((GoFile)containingFile) != null) {
        configuration.setName(containingFile.getName());
        configuration.setFilePath(containingFile.getVirtualFile().getPath());
        Module module = context.getModule();
        if (module != null) {
          configuration.setModule(module);
        }
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean isConfigurationFromContext(GoApplicationConfiguration configuration, ConfigurationContext context) {
    GoFile file = getFileFromContext(context);
    return file != null && FileUtil.pathsEqual(configuration.getFilePath(), file.getVirtualFile().getPath());
  }

  @Nullable
  private static GoFile getFileFromContext(@Nullable ConfigurationContext context) {
    if (context == null) {
      return null;
    }
    PsiElement psiElement = context.getPsiLocation();
    if (psiElement == null || !psiElement.isValid()) {
      return null;
    }
    PsiFile file = psiElement.getContainingFile();
    return file instanceof GoFile ? (GoFile)file : null;
  }
}
