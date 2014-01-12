package com.goide.parser;

import com.goide.GoParserDefinition;
import com.intellij.core.CoreApplicationEnvironment;
import com.intellij.lang.LanguageExtensionPoint;
import com.intellij.openapi.extensions.Extensions;
import com.intellij.testFramework.ParsingTestCase;

public class GoParserTest extends ParsingTestCase {
  public GoParserTest() {
    super("parser", "go", new GoParserDefinition());
  }

  @Override
  protected String getTestDataPath() {
    return "testData";
  }

  @Override
  protected boolean skipSpaces() {
    return true;
  }

  protected void doTest(boolean checkErrors) {
    super.doTest(true);
    if (checkErrors) {
      assertFalse(
        "PsiFile contains error elements",
        toParseTreeText(myFile, skipSpaces(), includeRanges()).contains("PsiErrorElement")
      );
    }
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    CoreApplicationEnvironment.registerExtensionPoint(
      Extensions.getRootArea(), "com.intellij.lang.braceMatcher", LanguageExtensionPoint.class);
  }

  public void testSimple()      { doTest(true);  }
  public void testError()       { doTest(true);  }
  public void testWriter()      { doTest(true);  }
  public void testPrimer()      { doTest(true);  }
  public void testTypes()       { doTest(true);  }
  public void testStr2Num()     { doTest(true);  }
  public void testRecover()     { doTest(false); }
  public void testMethodExpr()  { doTest(false); }
}
