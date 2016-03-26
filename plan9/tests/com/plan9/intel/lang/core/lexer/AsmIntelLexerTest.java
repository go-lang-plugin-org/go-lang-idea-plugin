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

package com.plan9.intel.lang.core.lexer;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.testFramework.LexerTestCase;

import java.io.File;
import java.io.IOException;

public class AsmIntelLexerTest extends LexerTestCase {
  private static final String PATH = "testData/intel/lexer";

  public void _testSimpleFile()           { doTest(); }
  public void testCommentsAndWhitespace() { doTest(); }
  public void testIdentifiers()           { doTest(); }

  private void doTest() {
    try {
      String text = FileUtil.loadFile(new File("./" + PATH + "/" + getTestName(true) + ".s"));
      String actual = printTokens(StringUtil.convertLineSeparators(text.trim()), 0);
      assertSameLinesWithFile(new File(PATH + "/" + getTestName(true) + ".txt").getAbsolutePath(), actual);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  protected Lexer createLexer() {
    return new AsmIntelLexer();
  }

  @Override
  protected String getDirPath() {
    return "../" + PATH;
  }
}