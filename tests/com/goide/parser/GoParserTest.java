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

package com.goide.parser;

import com.goide.GoParserDefinition;

public class GoParserTest extends GoParserTestBase {
  public GoParserTest() {
    super("parser", "go", new GoParserDefinition());
  }

  public void testError()                           { doTest(true);  }
  public void testWriter()                          { doTest(true);  }
  public void testPrimer()                          { doTest(true);  }
  public void testTypes()                           { doTest(true);  }
  public void testStr2Num()                         { doTest(true);  }
  public void testCars()                            { doTest(true);  }
  public void testIfWithNew()                       { doTest(true);  }
  public void testRanges()                          { doTest(true);  }
  public void testIncompleteRanges()                { doTest(false); }
  public void testTorture()                         { doTest(true);  }
  public void testLiteralValues()                   { doTest(true);  }
  public void testLiteralValuesElse()               { doTest(true);  }
  public void testIfComposite()                     { doTest(true);  }
  public void testArrayTypes()                      { doTest(true);  }
  public void testArrayTypesInRanges()              { doTest(true);  }
  public void testIf()                              { doTest(false); }
  public void testSimple()                          { doTest(false); }
  public void testRecover()                         { doTest(false); }
  public void testRecover2()                        { doTest(false); }
  public void testRecover3()                        { doTest(false); }
  public void testMethodExpr()                      { doTest(false); }
  public void testLabels()                          { doTest(false); }
  public void testBlockRecover()                    { doTest(false); }
  public void testMethodWithoutReceiverIdentifier() { doTest(false); }
  public void testExpressionPerformance()           { doTest(false); }
  public void testElementRecover()                  { doTest(false); }
  public void testChanRecover()                     { doTest(false); }
  public void testMapLiteralRecover()               { doTest(false); }
  public void testPlusPlusRecover()                 { doTest(false); }
  public void testTypeComma()                       { doTest(false); }
  public void testIncDec()                          { doTest(false); }
  public void testIncompleteTypeDeclaration()       { doTest(false); } 
  public void testIncompleteVarDeclaration()        { doTest(false); } 
}
