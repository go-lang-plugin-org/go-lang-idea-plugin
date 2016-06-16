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

public class GoIfUnwrapperTest extends GoUnwrapTestCase {
  public void testNoAction() {
    assertUnwrapped(
      "if true { \n" +
      "}  <caret>",
      "if true { \n" +
      "}  "
    );
  }

  public void testVoidIf() {
    assertUnwrapped(
      "if true <caret> { \n" +
      "} else { }\n",
      "\n"
    );
  }

  public void testIfWithOneStatement() {
    assertUnwrapped(
      "if true <caret> { \n" +
      "var i int\n" +
      "} else { }",
      "var i int"
    );
  }

  public void testIfThreeStatements() {
    assertUnwrapped(
      "if true <caret> { \n" +
      "var a, b int\n" +
      "a = 2\n" +
      "b = 3\n" +
      "} else { }",
      "var a, b int\n" +
      "a = 2\n" +
      "b = 3\n"
    );
  }

  public void testInsertedIndents() {
    assertUnwrapped(
      "if true <caret> { \n" +
      "\tif {\n" +
      "\t\tvar i int\n" +
      "\t}\n" +
      "}",
      "if {\n" +
      "\tvar i int\n" +
      "}\n"
    );
  }
}