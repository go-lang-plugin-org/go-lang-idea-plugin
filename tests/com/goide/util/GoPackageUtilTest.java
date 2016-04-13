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

import com.goide.GoCodeInsightFixtureTestCase;
import com.goide.project.GoBuildTargetSettings;
import com.goide.project.GoModuleSettings;
import com.goide.sdk.GoPackageUtil;

public class GoPackageUtilTest extends GoCodeInsightFixtureTestCase {
  public void testPackageNameOfTestPackageInNonTestFile() {
    myFixture.configureByText("foo.go", "package foo");
    myFixture.configureByText("foo_test.go", "package foo_test");
    myFixture.configureByText("main.go", "package main");
    myFixture.configureByText("docs.go", "package documentation");
    myFixture.configureByText("bar_test.go", "package tricky_package_name");
    myFixture.configureByText("non_test_file.go", "package non_test");
    myFixture.configureByText("ignored.go", "// +build ignored\n\npackage ignored");

    assertSameElements(GoPackageUtil.getAllPackagesInDirectory(myFixture.getFile().getContainingDirectory(), null, true),
                       "foo", "main", "non_test", "tricky_package_name");

    assertSameElements(GoPackageUtil.getAllPackagesInDirectory(myFixture.getFile().getContainingDirectory(), null, false),
                       "foo", "foo_test", "main", "non_test", "tricky_package_name");
  }

  public void testInvalidateCacheOnChangingBuildTags() {
    myFixture.configureByText("foo.go", "// +build ignored\n\npackage ignored");
    myFixture.configureByText("bar.go", "package not_ignored");
    assertSameElements(GoPackageUtil.getAllPackagesInDirectory(myFixture.getFile().getContainingDirectory(), null, true),
                       "not_ignored");

    GoBuildTargetSettings newSettings = new GoBuildTargetSettings();
    newSettings.customFlags = new String[]{"ignored"};
    GoModuleSettings.getInstance(myFixture.getModule()).setBuildTargetSettings(newSettings);
    assertSameElements(GoPackageUtil.getAllPackagesInDirectory(myFixture.getFile().getContainingDirectory(), null, true),
                       "not_ignored", "ignored");
  }
}
