package com.goide.runconfig;

import com.goide.psi.GoFile;
import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.actions.RunConfigurationProducer;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GoRunConfigurationProducerBase<T extends GoRunConfigurationWithMain> extends RunConfigurationProducer<T> implements Cloneable {
  protected GoRunConfigurationProducerBase(@NotNull ConfigurationType configurationType) {
    super(configurationType);
  }

  @Override
  protected boolean setupConfigurationFromContext(T configuration, ConfigurationContext context, Ref<PsiElement> sourceElement) {
    PsiFile file = getFileFromContext(context);
    if (file != null) {
      if ("main".equals(((GoFile)file).getPackageName()) && ((GoFile)file).findMainFunction() != null) {
        setConfigurationName(configuration, file);
        configuration.setFilePath(file.getVirtualFile().getPath());
        Module module = context.getModule();
        if (module != null) {
          configuration.setModule(module);
        }
        return true;
      }
    }
    return false;
  }

  protected void setConfigurationName(@NotNull T configuration, @NotNull PsiFile file) {
    configuration.setName(file.getName());
  }

  @Override
  public boolean isConfigurationFromContext(T configuration, ConfigurationContext context) {
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
