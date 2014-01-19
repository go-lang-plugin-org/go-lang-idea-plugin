package com.goide.lexer;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.testFramework.LexerTestCase;

import java.io.File;
import java.io.IOException;

public class GoLexerTest extends LexerTestCase {
  public void testBasicTypes() { doTest(); }
  public void testConstants() { doTest(); }
  public void testFor() { doTest(); }
  public void testFunctionArguments() { doTest(); }
  public void testHelloWorld() { doTest(); }
  public void testIf() { doTest(); }
  public void testImports() { doTest(); }
  public void testMultipleResult() { doTest(); }
  public void testNamedResult() { doTest(); }
  public void testPointers() { doTest(); }
  public void testRangeFor() { doTest(); }
  public void testSlices() { doTest(); }
  public void testStructs() { doTest(); }
  public void testVariables() { doTest(); }
  public void testEscapedQuote() { doTest(); }

  private void doTest() {
    try {
      String text = FileUtil.loadFile(new File("./testData/lexer/" + getTestName(true) + ".go"));
      String actual = printTokens(StringUtil.convertLineSeparators(text.trim()), 0);
      assertSameLinesWithFile("testData/lexer/" + getTestName(true) + ".txt", actual);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  protected Lexer createLexer() { return new GoLexer(); }
  @Override
  protected String getDirPath() { return "../testData/lexer"; }
}
