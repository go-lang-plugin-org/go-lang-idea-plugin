/*
 * Copyright 2013-2014 Sergey Ignatov, Alexander Zolotov
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

package com.goide.refactor;

import com.goide.psi.*;
import com.intellij.codeInsight.highlighting.HighlightUsagesDescriptionLocation;
import com.intellij.psi.ElementDescriptionLocation;
import com.intellij.psi.ElementDescriptionProvider;
import com.intellij.psi.PsiElement;
import com.intellij.usageView.UsageViewLongNameLocation;
import com.intellij.usageView.UsageViewNodeTextLocation;
import com.intellij.usageView.UsageViewShortNameLocation;
import com.intellij.usageView.UsageViewTypeLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GoDescriptionProvider implements ElementDescriptionProvider {
  @Nullable
  @Override
  public String getElementDescription(@NotNull PsiElement o, @NotNull ElementDescriptionLocation location) {
    if (o instanceof GoNamedElement && location == UsageViewNodeTextLocation.INSTANCE) {
      return getElementDescription(o, UsageViewShortNameLocation.INSTANCE);
    }
    if (o instanceof GoNamedElement && (location == UsageViewShortNameLocation.INSTANCE || location == UsageViewLongNameLocation.INSTANCE)) {
      return ((GoNamedElement)o).getName();
    }
    if (location == HighlightUsagesDescriptionLocation.INSTANCE) {
      return getElementDescription(o, UsageViewShortNameLocation.INSTANCE);
    }
    if (location == UsageViewTypeLocation.INSTANCE) {
      if (o instanceof GoMethodDeclaration) return "method";
      if (o instanceof GoFunctionDeclaration) return "function";
      if (o instanceof GoConstDefinition) return "constant";
      if (o instanceof GoVarDefinition) return "variable";
      if (o instanceof GoParamDefinition) return "parameter";
      if (o instanceof GoFieldDefinition) return "field";
      if (o instanceof GoAnonymousFieldDefinition) return "anonymous field";
      if (o instanceof GoTypeSpec) return "type";
      if (o instanceof GoImportSpec) return "import alias";
      if (o instanceof GoReceiver) return "receiver";
      if (o instanceof GoMethodSpec) return "method specification";
      if (o instanceof GoLabelDefinition) return "label";
    }
    return null;
  }
}
