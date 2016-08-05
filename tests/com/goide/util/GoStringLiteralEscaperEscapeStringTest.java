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

package com.goide.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class GoStringLiteralEscaperEscapeStringTest {
  @Parameterized.Parameter 
  public String input;
  @Parameterized.Parameter(1)
  public String expected;

  @Parameterized.Parameters(name = "{0}")
  public static Collection<Object[]> data() {
    return Arrays.asList(new Object[][]{
      {"abc", "abc"},
      {"歡迎", "歡迎"},
      {"歡\t迎", "歡\\t迎"},
      {"a\u0007b", "a\\ab"},
      {"a\u0008b", "a\\bb"},
      {"a\u000Cb", "a\\fb"},
      {"a\nb", "a\\nb"},
      {"a\rb", "a\\rb"},
      {"a\tb", "a\\tb"},
      {"a\u000bb", "a\\vb"},
      {"a\\b", "a\\\\b"},
      {"a'b", "a\\'b"},
      {"a\"b", "a\\\"b"},
      {"a\u0001b", "a\\u0001b"},
      {"\uD801\uDC37", "\uD801\uDC37"},
      {"\uD852\uDF62", "\uD852\uDF62"},
    });
  }

  @Test
  public void testEscapeString() {
    StringBuilder outChars = new StringBuilder();
    GoStringLiteralEscaper.escapeString(input, outChars);
    assertEquals(expected, outChars.toString());
  }
}
