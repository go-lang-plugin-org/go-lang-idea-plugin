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

public class GoForUnwrapperTest extends GoUnwrapTestCase {
  public void testVoidFor() {
    assertUnwrapped("for <caret> { \n}", "\n");
  }

  public void testForWithOneStatement() {
    assertUnwrapped(
      "for <caret> { \n" +
      "var i int\n" +
      "}", "var i int\n");
  }

  public void testForWithThreeStatements() {
    assertUnwrapped(
      "for <caret> { \n" +
      "var a, b int\n" +
      "a = 2\n" +
      "b = 3\n" +
      "}",
      "var a, b int\n" +
      "a = 2\n" +
      "b = 3\n"
    );
  }

  public void testForWithForClause() {
    assertUnwrapped(
      "for i := 1; i < 10; i++ { \n" +
      "<caret>var a int\n" +
      "a = i\n" +
      "}",
      "i := 1\n" +
      "var a int\n" +
      "a = i\n"
    );
  }

  public void testForWithRangeClause() {
    assertUnwrapped(
      "for i := range \"asd\" { \n" +
      "<caret>var a string\n" +
      "a = i\n" +
      "}",
      "var a string\n" +
      "a = i\n"
    );
  }
}