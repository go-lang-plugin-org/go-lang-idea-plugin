package com.goide.formatter;

import com.goide.GoCodeInsightFixtureTestCase;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.psi.codeStyle.CodeStyleManager;

public class GoFormatterTest extends GoCodeInsightFixtureTestCase {
  @Override
  protected String getBasePath() {
    return "formatting";
  }

  public void testSimple() {
    doTest();
  }

  public void testComments() {
    doTest();
  }

  public void testComposites() {
    doTest();
  }

  public void testCrlf() {
    doTest();
  }

  public void testEmptydecl() {
    doTest();
  }

  public void testImport() {
    doTest();
  }

  public void testOld() {
    doTest();
  }

  public void testRanges() {
    doTest();
  }

  public void testRewrite1() {
    doTest();
  }

  public void testRewrite2() {
    doTest();
  }

  public void testRewrite3() {
    doTest();
  }

  public void testRewrite4() {
    doTest();
  }

  public void testRewrite5() {
    doTest();
  }

  public void testRewrite6() {
    doTest();
  }

  public void testRewrite7() {
    doTest();
  }

  public void testRewrite8() {
    doTest();
  }

  public void testSlices1() {
    doTest();
  }

  public void testSlices2() {
    doTest();
  }

  public void testStdin1() {
    doTest();
  }

  public void testStdin2() {
    doTest();
  }

  public void testStdin3() {
    doTest();
  }

  public void testStdin4() {
    doTest();
  }

  public void testStdin5() {
    doTest();
  }

  public void testStdin6() {
    doTest();
  }

  public void testStdin7() {
    doTest();
  }

  public void testTypeswitch() {
    doTest();
  }


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
