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

package com.goide.psi.impl;

import com.goide.completion.GoCompletionUtil;
import com.goide.psi.GoBlock;
import com.goide.psi.GoLabelDefinition;
import com.goide.psi.GoLabelRef;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ArrayUtil;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class GoLabelReference extends GoCachedReference<GoLabelRef> {
  private final GoScopeProcessorBase myProcessor = new GoScopeProcessorBase(myElement.getText(), myElement, false) {
    @Override
    protected boolean condition(@NotNull PsiElement element) {
      return !(element instanceof GoLabelDefinition);
    }
  };

  public GoLabelReference(@NotNull GoLabelRef element) {
    super(element);
  }

  @NotNull
  private Collection<GoLabelDefinition> getLabelDefinitions() {
    GoBlock block = PsiTreeUtil.getTopmostParentOfType(myElement, GoBlock.class);
    return PsiTreeUtil.findChildrenOfType(block, GoLabelDefinition.class);
  }

  @Nullable
  @Override
  protected PsiElement resolveInner() {
    Collection<GoLabelDefinition> defs = getLabelDefinitions();
    for (GoLabelDefinition def : defs) {
      if (!myProcessor.execute(def, ResolveState.initial())) return def;
    }
    return null;
  }

  @NotNull
  @Override
  public Object[] getVariants() {
    Collection<LookupElement> result = ContainerUtil.newArrayList();
    for (GoLabelDefinition element : getLabelDefinitions()) {
      String name = element.getName();
      if (name != null) {
        result.add(GoCompletionUtil.createLabelLookupElement(element, name));
      }
    }
    return ArrayUtil.toObjectArray(result);
  }
}
