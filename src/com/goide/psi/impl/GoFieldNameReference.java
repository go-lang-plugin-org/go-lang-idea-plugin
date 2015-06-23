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

import com.goide.psi.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveState;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GoFieldNameReference extends GoCachedReference<GoReferenceExpressionBase> {
  private GoCompositeElement myValue;

  public GoFieldNameReference(@NotNull GoReferenceExpressionBase element) {
    super(element);
    GoCompositeElement place = myElement;
    while ((place = PsiTreeUtil.getParentOfType(place, GoLiteralValue.class)) != null) {
      if (place.getParent() instanceof GoValue) {
        myValue = (GoValue)place.getParent();
        break;
      }
    }
  }

  @Override
  public boolean processResolveVariants(@NotNull final GoScopeProcessor processor) {
    GoScopeProcessor fieldProcessor = processor instanceof GoFieldProcessor ? processor : new GoFieldProcessor(myElement) {
      @Override
      public boolean execute(@NotNull PsiElement psiElement, @NotNull ResolveState resolveState) {
        return super.execute(psiElement, resolveState) && processor.execute(psiElement, resolveState);
      }
    };
    GoKey key = PsiTreeUtil.getParentOfType(myElement, GoKey.class);
    GoValue value = PsiTreeUtil.getParentOfType(myElement, GoValue.class);
    if (key == null && (value == null || PsiTreeUtil.getPrevSiblingOfType(value, GoKey.class) != null)) return true;

    GoCompositeLit lit = PsiTreeUtil.getParentOfType(myElement, GoCompositeLit.class);

    GoType type = lit != null ? lit.getType() : null;
    if (type == null && lit != null) {
      type = GoPsiImplUtil.getType(lit.getTypeReferenceExpression());
    }

    type = getType(type);

    if (type instanceof GoStructType && !type.processDeclarations(fieldProcessor, ResolveState.initial(), null, myElement)) return false;

    return true;
  }

  @Nullable
  private GoType getType(GoType type) { // todo: rethink and unify this algorithm
    boolean inValue = myValue != null;
    
    if (inValue && type instanceof GoArrayOrSliceType) type = ((GoArrayOrSliceType)type).getType();
    else if (type instanceof GoMapType) type = inValue ? ((GoMapType)type).getValueType() : ((GoMapType)type).getKeyType();
    else if (inValue && type instanceof GoStructType) {
      GoKey key = PsiTreeUtil.getPrevSiblingOfType(myValue, GoKey.class);
      GoFieldName field = key != null ? key.getFieldName() : null;
      PsiReference reference = field != null ? field.getReference() : null;
      PsiElement resolve = reference != null ? reference.resolve() : null;
      if (resolve instanceof GoFieldDefinition) {
        type = PsiTreeUtil.getNextSiblingOfType(resolve, GoType.class);
      }
    }

    if (type != null && type.getTypeReferenceExpression() != null) {
      type = GoPsiImplUtil.getType(type.getTypeReferenceExpression());
    }

    if (type instanceof GoPointerType) {
      GoType inner = ((GoPointerType)type).getType();
      if (inner != null && inner.getTypeReferenceExpression() != null) {
        type = GoPsiImplUtil.getType(inner.getTypeReferenceExpression());
      }
    }

    return type;
  }

  @Nullable
  @Override
  public PsiElement resolveInner() {
    GoScopeProcessorBase p = new GoFieldProcessor(myElement);
    processResolveVariants(p);
    return p.getResult();
  }

  private static class GoFieldProcessor extends GoScopeProcessorBase {
    public GoFieldProcessor(@NotNull PsiElement element) {
      super(element.getText(), element, false);
    }

    @Override
    protected boolean condition(@NotNull PsiElement element) {
      return !(element instanceof GoFieldDefinition) && !(element instanceof GoAnonymousFieldDefinition);
    }
  }
}
