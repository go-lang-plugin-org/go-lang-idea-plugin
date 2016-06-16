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

public class GoFunctionArgumentUnwrapperTest extends GoUnwrapTestCase {
  public void testNoAction() {
    assertUnwrapped("asd(<caret>)", "asd(<caret>)");
  }

  public void testOneArg() {
    assertUnwrapped("asd(<caret>3)", "3");
  }

  public void testThreeArgs() {
    assertUnwrapped("asd(f(), 3, <caret>f())", "f()");
  }

  public void testInsertedCalls() {
    assertUnwrapped("f1(f2(<caret>4))", "f1(4)", 0);
  }

  public void testInsertedCallsUseSecodAction() {
    assertUnwrapped("f1(f2(<caret>4))", "f2(4)", 1);
  }
}