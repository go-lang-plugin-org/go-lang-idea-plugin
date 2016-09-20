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

import com.goide.runconfig.testing.frameworks.gobench.GobenchFramework;
import org.jetbrains.annotations.NotNull;

public class GobenchEventsConverterTest extends GoEventsConverterTestCase {
  public void testSuccessBenchmark() {
    doTest();
  }

  public void testBenchmarkWithoutSuffix() {
    doTest();
  }

  public void testFailedBenchmark() {
    doTest();
  }
  
  public void testMixedBenchmark() {
    doTest();
  }
  
  public void testFailedCompilation() {
    doTest();
  }

  @NotNull
  @Override
  protected String getBasePath() {
    return "testing/gobench";
  }
  
  @NotNull
  @Override
  protected GoTestFramework getTestFramework() {
    return GobenchFramework.INSTANCE;
  }
}
