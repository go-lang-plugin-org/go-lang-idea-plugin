/*
 * Copyright 2013-2016 Sergey Ignatov, Alexander Zolotov, Florin Patan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.goide.lexer;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.CharsetToolkit;
import com.intellij.testFramework.LexerTestCase;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public class GoLexerTest extends LexerTestCase {
  private static final String PATH = "testData/lexer";

  public void testBasicTypes()        { doTest(); }
  public void testConstants()         { doTest(); }
  public void testFor()               { doTest(); }
  public void testFunctionArguments() { doTest(); }
  public void testHelloWorld()        { doTest(); }
  public void testIf()                { doTest(); }
  public void testImports()           { doTest(); }
  public void testMultipleResult()    { doTest(); }
  public void testNamedResult()       { doTest(); }
  public void testPointers()          { doTest(); }
  public void testRangeFor()          { doTest(); }
  public void testSlices()            { doTest(); }
  public void testStructs()           { doTest(); }
  public void testVariables()         { doTest(); }
  public void testEscapedQuote()      { doTest(); }
  public void testUtf16()             { doTest(); }
  public void testCouldNotMatch()     { doTest(); }

  private void doTest() {
    try {
      String text = FileUtil.loadFile(new File("./" + PATH + "/" + getTestName(true) + ".go"), CharsetToolkit.UTF8);
      String actual = printTokens(StringUtil.convertLineSeparators(text.trim()), 0);
      assertSameLinesWithFile(new File(PATH + "/" + getTestName(true) + ".txt").getAbsolutePath(), actual);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @NotNull
  @Override
  protected Lexer createLexer() { return new GoLexer(); }

  @NotNull
  @Override
  protected String getDirPath() { 
    return "../" + PATH; 
  }
}
