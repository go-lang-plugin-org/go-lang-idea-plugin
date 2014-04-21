package com.goide.completion;

import com.intellij.codeInsight.completion.CompletionType;
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

  public void testFormatter() {
    doTestInclude("package main; import . \"fmt\"; type alias <caret>", "Formatter");
  }

  public void testAutoImport() {
    doCheckResult("package main; \n" +
                  "func test(){Fprintl<caret>}", 
                  "package main;\n" +
                  "import \"fmt\"\n" +
                  "func test(){fmt.Fprintln(<caret>)}");
  }

  public void testDoNothingInsideSelector() {
    doTestVariants(
      "package main\n" +
      "import \"fmt\"\n" +
      "func test(){fmt.Sprintln().<caret>}", CompletionType.BASIC, 1, CheckType.EQUALS
    );
  }
  
  public void testImports() throws Exception {
    doTestInclude("package main; import \"<caret>", "fmt", "io");
  }
}
