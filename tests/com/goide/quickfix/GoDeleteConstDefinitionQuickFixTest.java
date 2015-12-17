/*
 * Copyright 2013-2015 Sergey Ignatov, Alexander Zolotov, Florin Patan
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

package com.goide.quickfix;

import com.goide.inspections.unresolved.GoUnusedConstInspection;

public class GoDeleteConstDefinitionQuickFixTest extends GoQuickFixTestBase {
  @Override
  public void setUp() throws Exception {
    super.setUp();
    myFixture.enableInspections(GoUnusedConstInspection.class);
  }
  
  public void testSimple() {
    myFixture.configureByText("a.go", "package main; func main() { const fo<caret>o int = 2 }");
    applySingleQuickFix("Delete const 'foo'");
    myFixture.checkResult("package main; func main() {}");
  }
  
  public void testRemoveFromMultiSpec() {
    myFixture.configureByText("a.go", "package main; func main() { const (fo<caret>o, bar int = 2, 3) }");
    applySingleQuickFix("Delete const 'foo'");
    myFixture.checkResult("package main; func main() { const (\n" +
                          "\tbar int = 3) }");
  }
}
