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

package com.goide.stubs;

import com.goide.GoCodeInsightFixtureTestCase;
import com.goide.psi.GoFile;
import com.goide.psi.GoPackageClause;
import com.intellij.openapi.vfs.VirtualFileFilter;
import com.intellij.psi.impl.PsiManagerImpl;
import com.intellij.testFramework.exceptionCases.AssertionErrorCase;

public class GoPackageClauseStubTest extends GoCodeInsightFixtureTestCase {
  public void testStub() {
    GoFile file = (GoFile)myFixture.addFileToProject("bar/bar.go", "package bar; import `foo`; func _() { println(CONST_NAME) }");
    failOnFileLoading();
    file.getPackageName();
  }

  public void testParsingPsi() throws Throwable {
    GoFile file = (GoFile)myFixture.addFileToProject("bar/bar.go", "package bar; import `foo`; func _() { println(CONST_NAME) }");
    failOnFileLoading();
    GoPackageClause packageClause = file.getPackage();
    assertNotNull(packageClause);
    assertException(new AssertionErrorCase() {
      @Override
      public void tryClosure() {
        try {
          packageClause.getIdentifier();
        }
        catch (AssertionError e) {
          String message = e.getMessage();
          assertTrue(message.contains("Access to tree elements not allowed in tests"));
          assertTrue(message.contains("bar.go"));
          throw e;
        }
      }
    });
  }

  private void failOnFileLoading() {
    ((PsiManagerImpl)myFixture.getPsiManager()).setAssertOnFileLoadingFilter(VirtualFileFilter.ALL, getTestRootDisposable());
  }
}
