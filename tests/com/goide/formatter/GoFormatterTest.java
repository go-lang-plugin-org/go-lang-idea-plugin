package com.goide.formatter;

import com.goide.GoCodeInsightFixtureTestCase;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.psi.codeStyle.CodeStyleManager;
import org.jetbrains.annotations.Nullable;

public class GoFormatterTest extends GoCodeInsightFixtureTestCase {
  @Override
  protected String getBasePath() {
    return "formatting";
  }

  public void testSimple()      { doTest(); }
  public void testCaseE()       { doTest('e'); }
  public void testCaseEnter()   { doTestEnter(); }
  public void testSwitchEnter() { doTestEnter(); }
  public void testTypeEnter()   { doTestEnter(); }

  public void doTest() {
    doTest(null);
  }

  public void doTestEnter() {
    doTest('\n');
  }

  public void doTest(@Nullable Character c) {
    String testName = getTestName(true);
    myFixture.configureByFile(testName + ".go");
    String after = doTest(c, testName);
    assertSameLinesWithFile(getTestDataPath() + "/" + after, myFixture.getFile().getText());
  }

  private String doTest(@Nullable Character c, String testName) {
    if (c == null) {
      ApplicationManager.getApplication().runWriteAction(new Runnable() {
        @Override
        public void run() {
          CodeStyleManager.getInstance(getProject()).reformat(myFixture.getFile());
        }
      });
    }
    else {
      myFixture.type(c);
    }
    return String.format("%s-after.go", testName);
  }
}
