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

package com.goide.psi.impl;

import com.goide.psi.*;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.ResolveState;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GoFieldNameReference extends GoCachedReference<GoReferenceExpressionBase> {
  public GoFieldNameReference(@NotNull GoReferenceExpressionBase element) {
    super(element);
  }

  @Override
  public boolean processResolveVariants(@NotNull GoScopeProcessor processor) {
    GoScopeProcessor fieldProcessor = processor instanceof GoFieldProcessor ? processor : new GoFieldProcessor(myElement) {
      @Override
      public boolean execute(@NotNull PsiElement e, @NotNull ResolveState state) {
        return super.execute(e, state) && processor.execute(e, state);
      }
    };
    GoKey key = PsiTreeUtil.getParentOfType(myElement, GoKey.class);
    GoValue value = PsiTreeUtil.getParentOfType(myElement, GoValue.class);
    if (key == null && (value == null || PsiTreeUtil.getPrevSiblingOfType(value, GoKey.class) != null)) return true;

    GoType type = GoPsiImplUtil.getLiteralType(myElement, true);
    if (!processStructType(fieldProcessor, type)) return false;
    return !(type instanceof GoPointerType && !processStructType(fieldProcessor, ((GoPointerType)type).getType()));
  }

  private boolean processStructType(@NotNull GoScopeProcessor fieldProcessor, @Nullable GoType type) {
    return !(type instanceof GoStructType && !type.processDeclarations(fieldProcessor, ResolveState.initial(), null, myElement));
  }

  public boolean inStructTypeKey() {
    return GoPsiImplUtil.getParentGoValue(myElement) == null && GoPsiImplUtil.getLiteralType(myElement, false) instanceof GoStructType;
  }

  @Nullable
  @Override
  public PsiElement resolveInner() {
    GoScopeProcessorBase p = new GoFieldProcessor(myElement);
    processResolveVariants(p);
    return p.getResult();
  }

  private static class GoFieldProcessor extends GoScopeProcessorBase {
    private final Module myModule;

    public GoFieldProcessor(@NotNull PsiElement element) {
      super(element);
      PsiFile containingFile = myOrigin.getContainingFile();
      myModule = containingFile != null ? ModuleUtilCore.findModuleForPsiElement(containingFile.getOriginalFile()) : null;
    }

    @Override
    protected boolean crossOff(@NotNull PsiElement e) {
      if (!(e instanceof GoFieldDefinition) && !(e instanceof GoAnonymousFieldDefinition)) return true;
      GoNamedElement named = (GoNamedElement)e;
      PsiFile myFile = myOrigin.getContainingFile();
      PsiFile file = e.getContainingFile();
      if (!(myFile instanceof GoFile) || !GoPsiImplUtil.allowed(file, myFile, myModule)) return true;
      boolean localResolve = GoReference.isLocalResolve(myFile, file);
      return !e.isValid() || !(named.isPublic() || localResolve);
    }
  }
}
