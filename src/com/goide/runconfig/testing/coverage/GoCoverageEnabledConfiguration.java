package com.goide.runconfig.testing.coverage;

import com.goide.runconfig.testing.GoTestRunConfiguration;
import com.intellij.coverage.CoverageRunner;
import com.intellij.execution.configurations.coverage.CoverageEnabledConfiguration;

public class GoCoverageEnabledConfiguration extends CoverageEnabledConfiguration {
  public GoCoverageEnabledConfiguration(final GoTestRunConfiguration configuration) {
    super(configuration);
    setCoverageRunner(CoverageRunner.getInstance(GoCoverageRunner.class));
  }
}
