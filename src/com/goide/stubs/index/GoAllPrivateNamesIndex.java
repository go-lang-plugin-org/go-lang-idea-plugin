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

package com.goide.stubs.index;

import com.goide.GoFileElementType;
import com.goide.psi.GoNamedElement;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import org.jetbrains.annotations.NotNull;

public class GoAllPrivateNamesIndex extends StringStubIndexExtension<GoNamedElement> {
  public static final StubIndexKey<String, GoNamedElement> ALL_PRIVATE_NAMES = StubIndexKey.createIndexKey("go.all.private.name");

  @Override
  public int getVersion() {
    return GoFileElementType.VERSION;
  }

  @NotNull
  @Override
  public StubIndexKey<String, GoNamedElement> getKey() {
    return ALL_PRIVATE_NAMES;
  }
}
