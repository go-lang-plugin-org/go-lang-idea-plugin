package com.goide.formatter;

import com.goide.GoCodeInsightFixtureTestCase;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.psi.codeStyle.CodeStyleManager;

public class GoFormatterTest extends GoCodeInsightFixtureTestCase {
  @Override
  protected String getBasePath() {
    return "formatting";
  }

  public void testSimple()      { doTest(); }
  public void testCaseEnter()   { doTest(false); }
  public void testSwitchEnter() { doTest(false); }

  public void doTest() {
    doTest(true);
  }

  public void doTest(boolean format) {
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
