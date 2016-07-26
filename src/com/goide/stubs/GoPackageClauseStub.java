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

import com.goide.psi.GoPackageClause;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import com.intellij.util.io.StringRef;

public class GoPackageClauseStub extends StubBase<GoPackageClause> {
  private final String myName;
  public GoPackageClauseStub(StubElement parent, IStubElementType elementType, String name) {
    super(parent, elementType);
    myName = name;
  }

  public GoPackageClauseStub(StubElement stub, IStubElementType elementType, StringRef ref) {
    super(stub, elementType);
    myName = ref != null ? ref.getString() : null;
  }

  public String getName() {
    return myName;
  }
}
