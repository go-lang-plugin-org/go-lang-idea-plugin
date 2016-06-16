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

package com.goide.codeInsight.unwrap;

public class GoElseRemoverTest extends GoUnwrapTestCase {
  public void testNoAction() {
    assertUnwrapped(
      "if true { \n" +
      "}<caret>",
      "if true { \n" +
      "}<caret>"
    );
  }

  public void testVoidElse() {
    assertUnwrapped("if true { } else <caret> {  }", "if true { }");
  }

  public void testVoidElseWithCaretReturn() {
    assertUnwrapped("if true {\n } else <caret> {  \n}\n", "if true {\n }\n");
  }

  public void testElse() {
    assertUnwrapped("if true {\n } else <caret> {  \n  var i int \n }\n", "if true {\n }\n");
  }
}