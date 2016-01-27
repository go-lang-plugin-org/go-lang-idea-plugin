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
import com.goide.GoFileType;

public class GoBraceMatcherTest extends GoCodeInsightFixtureTestCase {
  public void testCloseParenBeforeOpeningBrace() {
    myFixture.configureByText(GoFileType.INSTANCE, "package foo; func name<caret>{}");
    myFixture.type('(');
    myFixture.checkResult("package foo; func name(<caret>){}");
  }
}