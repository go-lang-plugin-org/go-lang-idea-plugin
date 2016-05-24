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

package com.goide.runconfig.testing;

import com.goide.runconfig.GoRunConfigurationTestCase;

public class GoRunLineMarkerTest extends GoRunConfigurationTestCase {
  public void testRunTestLineMarker() {
    myFixture.configureByText("a_test.go", "package <caret>main\n" +
                                           "import .`gopkg.in/check.v1`\n" +
                                           "func TestName(){}\n" +
                                           "func BenchmarkName(){}\n" +
                                           "func ExampleName(){}\n" +
                                           "type MySuite struct{}\n" +
                                           "var _ = Suite(&MySuite{})\n" +
                                           "func (s *MySuite) TestHelloWorld(c *C) {}\n" +
                                           "func Hello() {}");
    assertEquals(1, myFixture.findGuttersAtCaret().size());
    assertEquals(5, myFixture.findAllGutters().size());
  }

  public void testRunLineMarker() {
    myFixture.configureByText("a.go", "package main\n" +
                                      "func <caret>main(){}");
    assertEquals(1, myFixture.findGuttersAtCaret().size());
    assertEquals(1, myFixture.findAllGutters().size());
  }

  public void testRunLineMarkerInNonMainFile() {
    myFixture.configureByText("a.go", "package not_main\n" +
                                      "func <caret>main(){}");
    assertEquals(0, myFixture.findGuttersAtCaret().size());
    assertEquals(0, myFixture.findAllGutters().size());
  }
}
