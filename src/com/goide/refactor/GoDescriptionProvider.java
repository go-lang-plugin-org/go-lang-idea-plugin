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
      return getElementDescription(o, UsageViewTypeLocation.INSTANCE) + " " +
             "'" + getElementDescription(o, UsageViewShortNameLocation.INSTANCE) + "'";
    }
    if (o instanceof GoNamedElement && (location == UsageViewShortNameLocation.INSTANCE || location == UsageViewLongNameLocation.INSTANCE)) {
      return ((GoNamedElement)o).getName();
    }
    if (location == UsageViewTypeLocation.INSTANCE) {
      if (o instanceof GoMethodDeclaration) return "Method";
      if (o instanceof GoFunctionDeclaration) return "Function";
      if (o instanceof GoConstDefinition) return "Constant";
      if (o instanceof GoVarDefinition) return "Variable";
      if (o instanceof GoParamDefinition) return "Parameter";
      if (o instanceof GoFieldDefinition) return "Field";
      if (o instanceof GoAnonymousFieldDefinition) return "Anonymous field";
      if (o instanceof GoTypeSpec) return "Type";
      if (o instanceof GoImportSpec) return "Import alias";
      if (o instanceof GoReceiver) return "Receiver";
      if (o instanceof GoMethodSpec) return "Method specification";
      if (o instanceof GoLabelDefinition) return "Label";
    }
    return null;
  }
}
