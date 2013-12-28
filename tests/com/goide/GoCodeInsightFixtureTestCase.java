package com.goide;

import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;

abstract public class GoCodeInsightFixtureTestCase extends LightPlatformCodeInsightFixtureTestCase {
  @Override
  protected String getTestDataPath() {
    return "testData/" + getBasePath();
  }
}
