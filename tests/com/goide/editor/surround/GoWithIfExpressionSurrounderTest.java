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

public class GoWithIfExpressionSurrounderTest extends GoSurrounderTestBase {
  private static final String SURROUNDER_DESCRIPTION = new GoWithIfExpressionSurrounder().getTemplateDescription();

  public void testWithOneBoolVariable() {
    doTest("var b bool\n<selection>b</selection>", "var b bool\nif b {\n\n}", SURROUNDER_DESCRIPTION, true);
  }

  public void testWithNotBoolExpressions() {
    doTest("var b int = 1\n<selection>b</selection>", "var b int = 1\nb", SURROUNDER_DESCRIPTION, false);
  }

  public void testWithTwoBoolExpressions() {
    doTest("var b, c bool = true, false\n<selection>b c</selection>", "var b, c bool = true, false\nif b {\n\n}\nc", SURROUNDER_DESCRIPTION,
           true);
  }
}
