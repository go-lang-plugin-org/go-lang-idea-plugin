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

package com.plan9.intel.lang.core.parser;

import com.plan9.intel.lang.core.AsmIntelParserDefinition;
import com.intellij.testFramework.ParsingTestCase;
import org.jetbrains.annotations.NotNull;

/**
 * Created by stuartcarnie on 12/23/15.
 */
public class AsmIntelParserTest extends ParsingTestCase {

  public AsmIntelParserTest() {
    super("parser", "s", new AsmIntelParserDefinition());
  }

  @NotNull
  @Override
  protected String getTestDataPath() {
    return "plan9/testData/intel";
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

  public void testSingleFunction() { doTest(false); }
}
