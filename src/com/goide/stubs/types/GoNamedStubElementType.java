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

package com.goide.stubs.types;

import com.goide.psi.GoNamedElement;
import com.goide.stubs.GoFileStub;
import com.goide.stubs.GoNamedStub;
import com.goide.stubs.index.GoAllPrivateNamesIndex;
import com.goide.stubs.index.GoAllPublicNamesIndex;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubIndexKey;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;

public abstract class GoNamedStubElementType<S extends GoNamedStub<T>, T extends GoNamedElement> extends GoStubElementType<S, T> {
  public GoNamedStubElementType(@NonNls @NotNull String debugName) {
    super(debugName);
  }

  @Override
  public boolean shouldCreateStub(@NotNull ASTNode node) {
    PsiElement psi = node.getPsi();
    return super.shouldCreateStub(node) && psi instanceof GoNamedElement && StringUtil.isNotEmpty(((GoNamedElement)psi).getName());
  }

  public void indexStub(@NotNull final S stub, @NotNull final IndexSink sink) {
    String name = stub.getName();
    if (shouldIndex() && StringUtil.isNotEmpty(name)) {
      String packageName = null;
      StubElement parent = stub.getParentStub();
      while (parent != null) {
        if (parent instanceof GoFileStub) {
          packageName = ((GoFileStub)parent).getPackageName();
        }
        parent = parent.getParentStub();
      }
      
      String indexingName = StringUtil.isNotEmpty(packageName) ? packageName + "." + name : name;
      if (stub.isPublic()) {
        sink.occurrence(GoAllPublicNamesIndex.ALL_PUBLIC_NAMES, indexingName);
      }
      else {
        sink.occurrence(GoAllPrivateNamesIndex.ALL_PRIVATE_NAMES, indexingName);
      }
      for (StubIndexKey<String, ? extends GoNamedElement> key : getExtraIndexKeys()) {
        sink.occurrence(key, name);
      }
    }
  }

  protected boolean shouldIndex() {
    return true;
  }

  @NotNull
  protected Collection<StubIndexKey<String, ? extends GoNamedElement>> getExtraIndexKeys() {
    return Collections.emptyList();
  }
}
