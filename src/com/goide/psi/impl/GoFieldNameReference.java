/*
 * Copyright 2013-2014 Sergey Ignatov, Alexander Zolotov, Mihai Toader, Florin Patan
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
import com.goide.psi.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.ResolveState;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ArrayUtil;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public class GoFieldNameReference extends PsiReferenceBase<GoFieldName> {
  private GoCompositeElement myValue;

  @NotNull
  private GoScopeProcessorBase getProcessor(final boolean completion) {
    return new GoScopeProcessorBase(myElement.getText(), myElement, completion) {
      @Override
      protected boolean condition(@NotNull PsiElement element) {
        return !(element instanceof GoFieldDefinition) &&
               !(element instanceof GoConstDefinition);
      }
    };
  }

  public GoFieldNameReference(@NotNull GoFieldName element) {
    super(element, TextRange.from(0, element.getTextLength()));
    
    GoCompositeElement place = myElement;
    while ((place = PsiTreeUtil.getParentOfType(place, GoLiteralValue.class)) != null) {
      if (place.getParent() instanceof GoValue) {
        myValue = (GoValue)place.getParent();
        break;
      }
    }
  }

  private boolean processFields(@NotNull GoScopeProcessorBase processor) {
    GoCompositeLit lit = PsiTreeUtil.getParentOfType(myElement, GoCompositeLit.class);
    GoLiteralTypeExpr expr = lit != null ? lit.getLiteralTypeExpr() : null;
    if (expr == null) return false;

    GoType type = expr.getType();
    if (type == null) {
      type = GoPsiImplUtil.getType(expr.getTypeReferenceExpression());
    }

    type = getType(type);

    if (type instanceof GoStructType && !type.processDeclarations(processor, ResolveState.initial(), null, myElement)) return true;

    PsiFile file = myElement.getContainingFile();
    if (file instanceof GoFile && !GoReference.processNamedElements(processor, ResolveState.initial(), ((GoFile)file).getConstants(), true)) return true;

    return false;
  }

  @Nullable
  private GoType getType(GoType type) {
    boolean inValue = myValue != null;
    
    if (inValue && type instanceof GoArrayOrSliceType) type = type.getType();
    else if (type instanceof GoMapType) type = inValue ? ((GoMapType)type).getValueType() : ((GoMapType)type).getKeyType();
    else if (inValue && type instanceof GoStructType) {
      GoKey key = PsiTreeUtil.getPrevSiblingOfType(myValue, GoKey.class);
      GoFieldName field = key != null ? key.getFieldName() : null;
      GoFieldNameReference reference = field != null ? field.getReference() : null;
      PsiElement resolve = reference != null ? reference.resolve() : null;
      if (resolve instanceof GoFieldDefinition) {
        type = PsiTreeUtil.getNextSiblingOfType(resolve, GoType.class);
      }
    }

    if (type != null && type.getTypeReferenceExpression() != null) {
      type = GoPsiImplUtil.getType(type.getTypeReferenceExpression());
    }

    return type;
  }

  @Nullable
  @Override
  public PsiElement resolve() {
    GoScopeProcessorBase p = getProcessor(false);
    processFields(p);
    return p.getResult();
  }

  @NotNull
  @Override
  public Object[] getVariants() {
    GoScopeProcessorBase p = getProcessor(false);
    processFields(p);
    List<GoNamedElement> variants = p.getVariants();
    if (variants.isEmpty()) return EMPTY_ARRAY;
    Collection<LookupElement> result = ContainerUtil.newArrayList();
    for (GoNamedElement element : variants) {
      result.add(GoCompletionUtil.createVariableLikeLookupElement(element));
    }
    return ArrayUtil.toObjectArray(result);
  }

  @Override
  public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
    myElement.replace(GoElementFactory.createIdentifierFromText(myElement.getProject(), newElementName));
    return myElement;
  }
}
