/*
 * Copyright 2013-2015 Sergey Ignatov, Alexander Zolotov, Florin Patan
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

import com.intellij.lexer.FlexAdapter;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.testFramework.LexerTestCase;

import java.io.File;
import java.io.IOException;
import java.io.Reader;

/**
 * Created by stuartcarnie on 12/22/15.
 */
public class AsmIntelLexerTest extends LexerTestCase {

  //public void testSimpleFile() { doTest(); }
  public void testCommentsAndWhitespace() { doTest(); }
  public void testIdentifiers() { doTest(); }

  private void doTest() {
    try {
      String text = FileUtil.loadFile(new File("./plan9/testData/intel/lexer/" + getTestName(true) + ".s"));
      String actual = printTokens(StringUtil.convertLineSeparators(text.trim()), 0);
      assertSameLinesWithFile(new File("plan9/testData/intel/lexer/" + getTestName(true) + ".txt").getAbsolutePath(), actual);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  protected Lexer createLexer() {
    return new FlexAdapter(new AsmIntelLexer((Reader)null));
  }

  @Override
  protected String getDirPath() {
    return "../plan9/testData/intel/lexer";
  }
}