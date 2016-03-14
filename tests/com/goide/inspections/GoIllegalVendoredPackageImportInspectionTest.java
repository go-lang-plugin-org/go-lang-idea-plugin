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

package com.goide.inspections;

import com.goide.quickfix.GoQuickFixTestBase;
import com.goide.sdk.GoSdkService;
import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.util.Function;
import com.intellij.util.containers.ContainerUtil;

import java.util.Collection;

public class GoIllegalVendoredPackageImportInspectionTest extends GoQuickFixTestBase {
  @Override
  public void setUp() throws Exception {
    super.setUp();
    myFixture.enableInspections(GoIllegalVendoredPackageImportInspection.class);
  }

  @Override
  protected boolean isWriteActionRequired() {
    return false;
  }

  public void testImportUnreachableVendoredPackage() {
    myFixture.addFileToProject("pack/vendor/foo/a.go", "package foo");
    myFixture.configureByText("a.go", "package bar\n" +
                                      "import _ `pack/vendor/unresolved`\n" +
                                      "import <error descr=\"Use of vendored package is not allowed\">_ `pack/ve<caret>ndor/foo`</error>");
    myFixture.checkHighlighting();
    assertContainsElements(getIntentionNames(), "Disable vendoring experiment support in module 'light_idea_test_case'", "Delete import");
    assertDoesntContain(getIntentionNames(), "Replace with `foo`");
  }

  public void testReplaceImportWithVendoredPath() {
    myFixture.addFileToProject("vendor/foo/a.go", "package foo");
    myFixture.configureByText("a.go", "package pack\n" +
                                      "import _ `pack/vendor/unresolved`\n" +
                                      "import <error descr=\"Must be imported as 'foo'\">_ `vend<caret>or/foo`</error>");
    myFixture.checkHighlighting();
    assertContainsElements(getIntentionNames(), "Disable vendoring experiment support in module 'light_idea_test_case'", "Delete import");
    myFixture.launchAction(myFixture.findSingleIntention("Replace with 'foo'"));
    myFixture.checkResult("package pack\n" +
                          "import _ `pack/vendor/unresolved`\n" +
                          "import _ `foo`");
  }

  public void testDoNoSuggestDisablingVendoringOnGo1_7() {
    GoSdkService.setTestingSdkVersion("1.7", getTestRootDisposable());
    myFixture.addFileToProject("vendor/foo/a.go", "package foo");
    myFixture.configureByText("a.go", "package pack\n" +
                                      "import _ `pack/vendor/unresolved`\n" +
                                      "import <error descr=\"Must be imported as 'foo'\">_ `vend<caret>or/foo`</error>");
    myFixture.checkHighlighting();
    assertDoesntContain(getIntentionNames(), "Disable vendoring experiment support in module 'light_idea_test_case'");
  }

  public void testWithDisabledVendoring() {
    disableVendoring();
    myFixture.addFileToProject("pack/vendor/foo/a.go", "package foo");
    myFixture.addFileToProject("vendor/foo/a.go", "package foo");
    myFixture.configureByText("a.go", "package pack\n" +
                                      "import _ `pack/vendor/unresolved`\n" +
                                      "import _ `pack/vendor/foo/a`\n" +
                                      "import _ `vendor/foo/a`");
    myFixture.checkHighlighting();
  }

  public void testVendorInsideVendorShouldBeUnreachable() {
    myFixture.addFileToProject("vendor/foo/a.go", "package foo");
    myFixture.addFileToProject("vendor/foo/vendor/bar/a.go", "package bar");
    myFixture.configureByText("a.go", "package bar\n" +
                                      "import _ `foor`\n" +
                                      "import <error descr=\"Use of vendored package is not allowed\">_ `foo/vendor/bar`</error>");
    myFixture.checkHighlighting();

  }

  private Collection<String> getIntentionNames() {
    return ContainerUtil.map(myFixture.getAvailableIntentions(), new Function<IntentionAction, String>() {
      @Override
      public String fun(IntentionAction action) {
        return action.getText();
      }
    });
  }
}
