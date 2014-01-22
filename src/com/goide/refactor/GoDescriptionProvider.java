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
    }
    return null;
  }
}
