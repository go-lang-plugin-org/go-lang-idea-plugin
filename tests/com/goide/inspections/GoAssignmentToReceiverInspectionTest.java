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

import com.goide.GoCodeInsightFixtureTestCase;

public class GoAssignmentToReceiverInspectionTest extends GoCodeInsightFixtureTestCase {

  @Override
  public void setUp() throws Exception {
    super.setUp();
    myFixture.enableInspections(GoAssignmentToReceiverInspection.class);
  }

  public void testDoesNotPropagateToOtherCallsForNonPointerReceiver() {
    doTest("func (r MyType) foo() { <weak_warning descr=\"Assignment to method receiver doesn't propagate to other calls\">r<caret></weak_warning> = 0 }");
  }

  public void testDoesNotPropagateToOtherCallsForPointerReceiver() {
    doTest("func (r *MyType) foo() { <weak_warning descr=\"Assignment to method receiver propagates only to callees but not to callers\">r<caret></weak_warning> = 0 }");
  }

  public void testNoFixForDereferencedPointerReceiver() {
    doTest("func (r *MyType) foo() { *r<caret> = 0 }");
  }

  private void doTest(String code) {
    myFixture.configureByText("a.go", "package main; type MyType int; " + code);
    myFixture.checkHighlighting();
  }
}
