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

package com.goide.editor;

import com.goide.GoCodeInsightFixtureTestCase;

public class GoQuoteHandlerTest extends GoCodeInsightFixtureTestCase {
  public void testNewlineInStringLiteral() {
    myFixture.configureByText("t.go", "package t; const c = \"const<caret>value\"");
    myFixture.type('\n');
    myFixture.checkResult("package t; const c = \"const\" +\n\t\"<caret>value\"");
  }

  public void testRemoveSingleQuote() {
    myFixture.configureByText("t.go", "package main; const _ = '\\n''<caret>'");
    myFixture.type('\b');
    myFixture.checkResult("package main; const _ = '\\n'<caret>");
  }

  public void testAddSingleQuote() {
    myFixture.configureByText("t.go", "package main; const _ = '\\n'<caret>");
    myFixture.type('\'');
    myFixture.checkResult("package main; const _ = '\\n''<caret>'");
  }

  public void testTypeSingleQuote() { doTypeQuoteTest('\''); }
  public void testTypeDoubleQuote() { doTypeQuoteTest('\"'); }
  public void testTypeBacktick()    { doTypeQuoteTest('`'); }

  private void doTypeQuoteTest(char q) {
    myFixture.configureByText("t.go", "package t; const c = <caret>");
    myFixture.type(q);
    myFixture.checkResult("package t; const c = " + q + "<caret>" + q);
  }
}
