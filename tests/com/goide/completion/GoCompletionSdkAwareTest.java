package com.goide.completion;

import com.intellij.testFramework.LightProjectDescriptor;

public class GoCompletionSdkAwareTest extends GoCompletionTestBase {
  @Override
  public void setUp() throws Exception {
    super.setUp();
    setUpProjectSdk();
  }

  @Override
  protected LightProjectDescriptor getProjectDescriptor() {
    return createMockProjectDescriptor();
  }

  public void testFormatter() throws Exception {
    doTestInclude("package main; import . \"fmt\"; type alias <caret>", "Formatter");
  }
  
  public void testImports() throws Exception {
    doTestInclude("package main; import \"<caret>", "fmt", "io");
  }
}
