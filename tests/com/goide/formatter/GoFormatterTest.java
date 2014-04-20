package com.goide.formatter;

import com.goide.GoCodeInsightFixtureTestCase;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.psi.codeStyle.CodeStyleManager;

import java.io.File;
import java.io.IOException;

public class GoFormatterTest extends GoCodeInsightFixtureTestCase {
  public static final boolean OVERRIDE_TEST_DATA = false;

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
    myFixture.checkResultByFile(after);
  }

  private String doTest(boolean format, String testName) throws IOException {
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

    String after = String.format("%s-after.go", testName);
    if (OVERRIDE_TEST_DATA) {
      FileUtil.writeToFile(new File(myFixture.getTestDataPath() + "/" + after), myFixture.getFile().getText());
    }
    return after;
  }
}
