package com.goide.runconfig.testing.coverage;

import com.goide.runconfig.testing.GoTestRunConfigurationBase;
import com.intellij.coverage.CoverageRunner;
import com.intellij.execution.configurations.coverage.CoverageEnabledConfiguration;

public class GoCoverageEnabledConfiguration extends CoverageEnabledConfiguration {
  public GoCoverageEnabledConfiguration(final GoTestRunConfigurationBase configuration) {
    super(configuration);
    setCoverageRunner(CoverageRunner.getInstance(GoCoverageRunner.class));
  }
}
