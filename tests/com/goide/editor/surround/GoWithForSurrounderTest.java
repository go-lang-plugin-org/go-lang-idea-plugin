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

package com.goide.editor.surround;

public class GoWithForSurrounderTest extends GoSurrounderTestBase {
  private static final String SURROUNDER_DESCRIPTION = new GoWithForSurrounder().getTemplateDescription();

  public void testWithOneStatement() {
    doTest("<selection>var b bool</selection>", "for <caret>{\n\tvar b bool\n}\n", SURROUNDER_DESCRIPTION, true);
  }

  public void testWithThreeStatements() {
    doTest("<selection>var b int = 1\nb = true\ntype Type int</selection>", "for <caret>{\n\tvar b int = 1\n\tb = true\n\ttype Type int\n}\n",
           SURROUNDER_DESCRIPTION, true);
  }

  public void testNoFor() {
    doTest("var b, c bool = true, <selection>false</selection>", "var b, c bool = true, false", SURROUNDER_DESCRIPTION, false);
  }

  public void testWithCaretAtMultilineStatement() {
    doTest("fo<caret>r {\n\n}", "for <caret>{\n\tfor {\n\n\t}\n}\n", SURROUNDER_DESCRIPTION, true);
  }
}
