package com.goide.runconfig.application;

import com.goide.runconfig.GoRunConfigurationProducerBase;

public class GoApplicationRunConfigurationProducer extends GoRunConfigurationProducerBase<GoApplicationConfiguration> implements Cloneable {
  public GoApplicationRunConfigurationProducer() {
    super(GoApplicationRunConfigurationType.getInstance());
  }
}
