/*
 * Copyright 2013-2015 Sergey Ignatov, Alexander Zolotov, Mihai Toader, Florin Patan
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

import com.goide.GoConstants;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum GoTestFunctionType {
  TEST(GoConstants.TEST_PREFIX, "T"),
  TEST_MAIN(GoConstants.TEST_PREFIX, "M"),
  BENCHMARK(GoConstants.BENCHMARK_PREFIX, "B"),
  EXAMPLE(GoConstants.EXAMPLE_PREFIX, null);

  private final String myPrefix;
  private final String myParamType;

  GoTestFunctionType(String prefix, String paramType) {
    myPrefix = prefix;
    myParamType = paramType;
  }

  @NotNull
  public String getParamType() {
    return myParamType;
  }

  @Nullable
  public static GoTestFunctionType fromName(@Nullable String functionName) {
    if (StringUtil.isEmpty(functionName)) return null;
    if (GoConstants.TEST_MAIN.equals(functionName)) return TEST_MAIN;
    for (GoTestFunctionType type : values()) {
      if (checkPrefix(functionName, type.myPrefix)) return type;
    }
    return null;
  }

  private static boolean checkPrefix(@Nullable String name, @NotNull String prefix) {
    // https://github.com/golang/go/blob/master/src/cmd/go/test.go#L1161 – isTest()
    if (name == null || !name.startsWith(prefix)) return false;
    if (prefix.length() == name.length()) return true;
    final char c = name.charAt(prefix.length());
    return !Character.isLetter(c) || !Character.isLowerCase(c);
  }
}
