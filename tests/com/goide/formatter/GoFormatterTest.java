package com.goide.formatter;

import com.goide.GoCodeInsightFixtureTestCase;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.psi.codeStyle.CodeStyleManager;

import java.io.IOException;

public class GoFormatterTest extends GoCodeInsightFixtureTestCase {
  @Override
  protected String getBasePath() {
    return "formatting";
  }

  public void testSimple() throws Exception {
    doTest();
  }

  public void doTest() throws Exception {
    doTest(true);
  }

  public void doTest(boolean format) throws Exception {
    String testName = getTestName(true);
    myFixture.configureByFile(testName + ".go");
    String after = doTest(format, testName);
    assertSameLinesWithFile(getTestDataPath() + "/" + after, myFixture.getFile().getText());
  }

  private String doTest(boolean format, String testName) {
    if (format) {
      ApplicationManager.getApplication().runWriteAction(new Runnable() {
        @Override
        public void run() {
          CodeStyleManager.getInstance(getProject()).reformat(myFixture.getFile());
        }
      });
    }
    else {
      myFixture.type('\n');
    }
    return String.format("%s-after.go", testName);
  }
}
