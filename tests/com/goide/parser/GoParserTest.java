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

package com.goide.parser;

import com.goide.GoParserDefinition;
import com.intellij.core.CoreApplicationEnvironment;
import com.intellij.lang.LanguageExtensionPoint;
import com.intellij.openapi.extensions.Extensions;
import com.intellij.testFramework.ParsingTestCase;
import org.jetbrains.annotations.NotNull;

public class GoParserTest extends ParsingTestCase {
  public GoParserTest() {
    super("parser", "go", new GoParserDefinition());
  }

  @NotNull
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

  public void testError()                           { doTest(true);  }
  public void testWriter()                          { doTest(true);  }
  public void testPrimer()                          { doTest(true);  }
  public void testTypes()                           { doTest(true);  }
  public void testStr2Num()                         { doTest(true);  }
  public void testCars()                            { doTest(true);  }
  public void testIfWithNew()                       { doTest(true);  }
  public void testRanges()                          { doTest(true);  }
  public void testTorture()                         { doTest(true);  }
  public void testLiteralValues()                   { doTest(true);  }
  public void testIfComposite()                     { doTest(true);  }
  public void testArrayTypes()                      { doTest(true);  }
  public void testArrayTypesInRanges()              { doTest(true);  }
  public void testIf()                              { doTest(false); }
  public void testSimple()                          { doTest(false); }
  public void testRecover()                         { doTest(false); }
  public void testRecover2()                        { doTest(false); }
  public void testMethodExpr()                      { doTest(false); }
  public void testLabels()                          { doTest(false); }
  public void testBlockRecover()                    { doTest(false); }
  public void testMethodWithoutReceiverIdentifier() { doTest(false); }
  public void testExpressionPerformance()           { doTest(false); }
  public void testElementRecover()                  { doTest(false); }
}
