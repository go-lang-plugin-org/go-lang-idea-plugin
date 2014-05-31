package com.goide.psi.impl;

import com.goide.psi.GoBlock;
import com.goide.psi.GoLabelDefinition;
import com.goide.psi.GoLabelRef;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.ResolveState;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ArrayUtil;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class GoLabelReference extends PsiReferenceBase<GoLabelRef> {
  private final GoScopeProcessorBase myProcessor = new GoScopeProcessorBase(myElement.getText(), myElement, false) {
    @Override
    protected boolean condition(@NotNull PsiElement element) {
      return !(element instanceof GoLabelDefinition);
    }
  };

  public GoLabelReference(@NotNull GoLabelRef element) {
    super(element, TextRange.from(0, element.getTextLength()));
  }

  @NotNull
  private Collection<GoLabelDefinition> getLabelDefinitions() {
    GoBlock block = PsiTreeUtil.getTopmostParentOfType(myElement, GoBlock.class);
    return PsiTreeUtil.findChildrenOfType(block, GoLabelDefinition.class);
  }

  @Nullable
  @Override
  public PsiElement resolve() {
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
      result.add(GoPsiImplUtil.createLabelLookupElement(element));
    }
    return ArrayUtil.toObjectArray(result);
  }
  
  @Override
  public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
    myElement.replace(GoElementFactory.createIdentifierFromText(myElement.getProject(), newElementName));
    return myElement;
  }
  
}
