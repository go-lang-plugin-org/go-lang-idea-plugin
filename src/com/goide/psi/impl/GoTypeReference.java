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

package com.goide.psi.impl;

import com.goide.psi.*;
import com.goide.sdk.GoSdkUtil;
import com.goide.util.GoUtil;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ArrayUtil;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.containers.OrderedSet;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public class GoTypeReference extends PsiPolyVariantReferenceBase<GoTypeReferenceExpression> {
  private final boolean myInsideInterfaceType;

  public GoTypeReference(@NotNull GoTypeReferenceExpression o) {
    super(o, TextRange.from(o.getIdentifier().getStartOffsetInParent(), o.getIdentifier().getTextLength()));
    myInsideInterfaceType = myElement.getParent() instanceof GoMethodSpec;
  }

  private static final ResolveCache.PolyVariantResolver<PsiPolyVariantReferenceBase> MY_RESOLVER =
    new ResolveCache.PolyVariantResolver<PsiPolyVariantReferenceBase>() {
      @NotNull
      @Override
      public ResolveResult[] resolve(@NotNull PsiPolyVariantReferenceBase psiPolyVariantReferenceBase, boolean incompleteCode) {
        return ((GoTypeReference)psiPolyVariantReferenceBase).resolveInner();
      }
  };

  @NotNull
  private ResolveResult[] resolveInner() {
    String identifierText = getName();
    Collection<ResolveResult> result = new OrderedSet<ResolveResult>();
    processResolveVariants(GoReference.createResolveProcessor(identifierText, result, myElement));
    
    if (result.isEmpty() && myElement.getParent() instanceof GoReceiverType) {
      PsiElement resolve = new GoReference(myElement).resolve();
      if (resolve != null) {
        return PsiElementResolveResult.createResults(resolve);
      }
    }
    
    return result.toArray(new ResolveResult[result.size()]);
  }
  
  @Override
  public boolean isReferenceTo(PsiElement element) {
    return GoUtil.couldBeReferenceTo(element, myElement) && super.isReferenceTo(element);
  }

  private String getName() {
    return myElement.getIdentifier().getText();
  }

  @Override
  @NotNull
  public ResolveResult[] multiResolve(final boolean incompleteCode) {
    return myElement.isValid()
           ? ResolveCache.getInstance(myElement.getProject()).resolveWithCaching(this, MY_RESOLVER, false, false)
           : ResolveResult.EMPTY_ARRAY;
  }

  @NotNull
  @Override
  public Object[] getVariants() {
    List<LookupElement> variants = ContainerUtil.newArrayList();
    final PsiElement spec = PsiTreeUtil.getParentOfType(myElement, GoFieldDeclaration.class, GoTypeSpec.class);
    final boolean insideParameter = PsiTreeUtil.getParentOfType(myElement, GoParameterDeclaration.class) != null;
    processResolveVariants(GoReference.createCompletionProcessor(variants, true, new Condition<PsiElement>() {
      @Override
      public boolean value(PsiElement e) {
        return e != spec && 
               !(insideParameter && (e instanceof GoNamedSignatureOwner || e instanceof GoVarDefinition || e instanceof GoConstDefinition));
      }
    }));
    Object[] array = ArrayUtil.toObjectArray(variants);
    if (myElement.getParent() instanceof GoReceiverType) {
      return ArrayUtil.mergeArrays(array, new GoReference(myElement).getVariants());
    }
    return array;
  }

  private boolean processResolveVariants(@NotNull GoReference.MyScopeProcessor processor) {
    PsiFile file = myElement.getContainingFile();
    if (!(file instanceof GoFile)) return false;
    ResolveState state = ResolveState.initial();
    GoTypeReferenceExpression qualifier = myElement.getQualifier();
    if (qualifier != null) {
      return processQualifierExpression(((GoFile)file), qualifier, processor, state);
    }
    return processUnqualifiedResolve(((GoFile)file), processor, state, true);
  }

  private static boolean processQualifierExpression(@NotNull GoFile file,
                                                    @NotNull GoTypeReferenceExpression qualifier,
                                                    @NotNull GoReference.MyScopeProcessor processor,
                                                    @NotNull ResolveState state) {
    PsiElement target = qualifier.getReference().resolve();
    if (target == null || target == qualifier) return false;
    if (target instanceof GoImportSpec) target = ((GoImportSpec)target).getImportString().resolve();
    if (target instanceof PsiDirectory) {
      GoReference.processDirectory((PsiDirectory)target, file, null, processor, state, false);
    }
    return false;
  }

  private boolean processUnqualifiedResolve(@NotNull GoFile file,
                                            @NotNull GoReference.MyScopeProcessor processor,
                                            @NotNull ResolveState state,
                                            boolean localResolve) {
    GoScopeProcessorBase delegate = createDelegate(processor);
    ResolveUtil.treeWalkUp(myElement, delegate);
    Collection<? extends GoNamedElement> result = delegate.getVariants();
    if (!processNamedElements(processor, state, result, localResolve)) return false;
    if (!processFileEntities(file, processor, state, localResolve)) return false;
    PsiDirectory dir = file.getOriginalFile().getParent();
    if (!GoReference.processDirectory(dir, file, file.getPackageName(), processor, state, true)) return false;
    if (GoReference.processImports(file, processor, state, myElement)) return false;
    if (processBuiltin(processor, state, myElement)) return false;
    if (PsiTreeUtil.getParentOfType(myElement, GoTypeSwitchCase.class) != null && "nil".equals(getName())) {
      GoFile builtinFile = GoSdkUtil.findBuiltinFile(myElement);
      if (builtinFile == null) return false;
      GoVarDefinition nil = ContainerUtil.find(builtinFile.getVars(), new Condition<GoVarDefinition>() {
        @Override
        public boolean value(GoVarDefinition v) {
          return "nil".equals(v.getName());
        }
      });
      if (nil != null && !processor.execute(nil, state)) return false;
    }
    return true;
  }

  // todo: unify references, extract base class
  private boolean processBuiltin(@NotNull GoReference.MyScopeProcessor processor, @NotNull ResolveState state, @NotNull GoCompositeElement element) {
    GoFile builtinFile = GoSdkUtil.findBuiltinFile(element);
    if (builtinFile != null && !processFileEntities(builtinFile, processor, state, true)) return true;
    return false;
  }

  @NotNull
  private GoTypeProcessor createDelegate(@NotNull GoReference.MyScopeProcessor processor) {
    return new GoTypeProcessor(getName(), myElement, processor.isCompletion());
  }

  private boolean processFileEntities(@NotNull GoFile file,
                                      @NotNull GoReference.MyScopeProcessor processor,
                                      @NotNull ResolveState state,
                                      boolean localProcessing) {
    if (!processNamedElements(processor, state, file.getTypes(), localProcessing)) return false;
    return true;
  }

  private boolean processNamedElements(@NotNull PsiScopeProcessor processor,
                                      @NotNull ResolveState state,
                                      @NotNull Collection<? extends GoNamedElement> elements, boolean localResolve) {
    for (GoNamedElement definition : elements) {
      if (definition instanceof GoTypeSpec && !allowed((GoTypeSpec)definition)) continue;
      if ((definition.isPublic() || localResolve) && !processor.execute(definition, state)) return false;
    }
    return true;
  }

  public boolean allowed(@NotNull GoTypeSpec definition) {
    return !myInsideInterfaceType || (definition.getType() instanceof GoInterfaceType);
  }

  @Override
  public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
    myElement.getIdentifier().replace(GoElementFactory.createIdentifierFromText(myElement.getProject(), newElementName));
    return myElement;
  }
}
