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

package com.goide.psi;

import com.goide.GoCodeInsightFixtureTestCase;

public class GoFileTest extends GoCodeInsightFixtureTestCase {
  public void testRetrieveBuildFlags() {
    myFixture.configureByText("a.go", "// Copyright 2009 The Go Authors.  All rights reserved.\n" +
                                      "// Use of this source code is governed by a BSD-style\n" +
                                      "// license that can be found in the LICENSE file.\n" +
                                      "\n" +
                                      "// +build darwin dragonfly\n" +
                                      "\n" +
                                      "package net\n");
    assertEquals("darwin dragonfly", ((GoFile)myFixture.getFile()).getBuildFlags());
  }

  public void testRetrieveMultipleBuildFlags() {
    myFixture.configureByText("a.go", "// +build openbsd windows\n" +
                                      "// +build linux\n" +
                                      "\n" +
                                      "package net\n");
    assertEquals("openbsd windows|linux", ((GoFile)myFixture.getFile()).getBuildFlags());
  }

  public void testRetrieveBuildFlagsWithoutNewLineAfter() {
    myFixture.configureByText("a.go", "// Copyright 2009 The Go Authors.  All rights reserved.\n" +
                                      "// Use of this source code is governed by a BSD-style\n" +
                                      "// license that can be found in the LICENSE file.\n" +
                                      "\n" +
                                      "// +build freebsd linux\n" +
                                      "package net\n");
    assertNull(((GoFile)myFixture.getFile()).getBuildFlags());
  }

  public void testPackageNameOfTestFile() {
    myFixture.configureByText("foo_test.go", "package foo_test");
    assertEquals("foo", ((GoFile)myFixture.getFile()).getPackageName());
  }

  public void testPackageNameOfNonTestPackageInTestFile() {
    myFixture.configureByText("foo_test.go", "package fooa");
    assertEquals("fooa",((GoFile)myFixture.getFile()).getPackageName());
  }

  public void testPackageNameOfTestPackageInNonTestFile() {
    myFixture.configureByText("foo.go", "package foo_test");
    assertEquals("foo_test", ((GoFile)myFixture.getFile()).getPackageName());
  }
}
