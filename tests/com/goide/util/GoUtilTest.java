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

package com.goide.util;

import com.goide.GoCodeInsightFixtureTestCase;

public class GoUtilTest  extends GoCodeInsightFixtureTestCase {
  public void testPackageNameOfTestPackageInNonTestFile() {
    myFixture.configureByText("foo.go", "package foo");
    myFixture.configureByText("foo_test.go", "package foo_test");
    myFixture.configureByText("main.go", "package main");
    myFixture.configureByText("docs.go", "package documentation");
    myFixture.configureByText("bar_test.go", "package tricky_package_name");
    assertSameElements(GoUtil.getAllPackagesInDirectory(myFixture.getFile().getContainingDirectory()), "foo", "main", "documentation", "tricky_package_name");
  }

}
