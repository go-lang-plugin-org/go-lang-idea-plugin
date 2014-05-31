package com.goide.runconfig.file;

import com.goide.runconfig.GoRunConfigurationProducerBase;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

public class GoRunFileConfigurationProducer extends GoRunConfigurationProducerBase<GoRunFileConfiguration> implements Cloneable {
  public GoRunFileConfigurationProducer() {
    super(GoRunFileConfigurationType.getInstance());
  }

  @Override
  protected void setConfigurationName(@NotNull GoRunFileConfiguration configuration, @NotNull PsiFile file) {
    configuration.setName("Run " + file.getName());
  }
}
