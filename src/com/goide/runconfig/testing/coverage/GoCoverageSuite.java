package com.goide.runconfig.testing.coverage;

import com.intellij.coverage.BaseCoverageSuite;
import com.intellij.coverage.CoverageEngine;
import com.intellij.coverage.CoverageFileProvider;
import com.intellij.coverage.CoverageRunner;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GoCoverageSuite extends BaseCoverageSuite {
  public GoCoverageSuite() {
  }

  public GoCoverageSuite(final String name,
                         @Nullable final CoverageFileProvider fileProvider,
                         final long lastCoverageTimeStamp,
                         final boolean coverageByTestEnabled,
                         final boolean tracingEnabled,
                         final boolean trackTestFolders,
                         final CoverageRunner coverageRunner,
                         final Project project) {
    super(name, fileProvider, lastCoverageTimeStamp, coverageByTestEnabled, tracingEnabled, trackTestFolders, coverageRunner, project);
  }

  @NotNull
  @Override
  public CoverageEngine getCoverageEngine() {
    return GoCoverageEngine.INSTANCE;
  }
}
