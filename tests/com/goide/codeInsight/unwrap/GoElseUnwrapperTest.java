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

public class GoElseUnwrapperTest extends GoUnwrapTestCase {
  public void testNoAction() {
    assertUnwrapped("if true { \n}<caret>", "if true { \n}<caret>", 1);
  }

  public void testVoidElse() {
    assertUnwrapped("if true {\n } else <caret> {  \n}\n", "if true {\n }\n", 1);
  }

  public void testElse() {
    assertUnwrapped("if true {\n } else <caret> {  \n  var i int \n }\n", "if true {\n }\nvar i int\n", 1);
  }

  public void testElseWithTwoStatements() {
    assertUnwrapped("if true {\n } else <caret> {  \nvar i int\n i = 23\n }\n", "if true {\n }\nvar i int\ni = 23\n", 1);
  }

  public void testElseIf() {
    assertUnwrapped("if true {\n } else if <caret> {  \nvar i int\n i = 23\n }\n", "if true {\n }\nif {\n\tvar i int\n\ti = 23\n}\n", 1);
  }
}