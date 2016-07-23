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

import com.goide.GoConstants;
import com.goide.GoTypes;
import com.goide.psi.*;
import com.goide.sdk.GoSdkUtil;
import com.goide.util.GoUtil;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.formatter.FormatterUtil;
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
import java.util.Set;

public class GoTypeReference extends GoReferenceBase<GoTypeReferenceExpression> {
  private final boolean myInsideInterfaceType;

  public GoTypeReference(@NotNull GoTypeReferenceExpression o) {
    super(o, TextRange.from(o.getIdentifier().getStartOffsetInParent(), o.getIdentifier().getTextLength()));
    myInsideInterfaceType = myElement.getParent() instanceof GoMethodSpec;
  }

  private static final ResolveCache.PolyVariantResolver<PsiPolyVariantReferenceBase> MY_RESOLVER =
    (psiPolyVariantReferenceBase, incompleteCode) -> ((GoTypeReference)psiPolyVariantReferenceBase).resolveInner();

  @NotNull
  private ResolveResult[] resolveInner() {
    Collection<ResolveResult> result = new OrderedSet<>();
    processResolveVariants(createResolveProcessor(result, myElement));
    return result.toArray(new ResolveResult[result.size()]);
  }

  @Override
  public boolean isReferenceTo(PsiElement element) {
    return GoUtil.couldBeReferenceTo(element, myElement) && super.isReferenceTo(element);
  }

  @NotNull
  private PsiElement getIdentifier() {
    return myElement.getIdentifier();
  }

  @Override
  @NotNull
  public ResolveResult[] multiResolve(boolean incompleteCode) {
    return myElement.isValid()
           ? ResolveCache.getInstance(myElement.getProject()).resolveWithCaching(this, MY_RESOLVER, false, false)
           : ResolveResult.EMPTY_ARRAY;
  }

  @NotNull
  @Override
  public Object[] getVariants() {
    return ArrayUtil.EMPTY_OBJECT_ARRAY;
  }

  public boolean processResolveVariants(@NotNull GoScopeProcessor processor) {
    PsiFile file = myElement.getContainingFile();
    if (!(file instanceof GoFile)) return false;
    ResolveState state = ResolveState.initial();
    GoTypeReferenceExpression qualifier = myElement.getQualifier();
    if (qualifier != null) {
      return processQualifierExpression((GoFile)file, qualifier, processor, state);
    }
    return processUnqualifiedResolve((GoFile)file, processor, state, true);
  }

  private boolean processQualifierExpression(@NotNull GoFile file,
                                             @NotNull GoTypeReferenceExpression qualifier,
                                             @NotNull GoScopeProcessor processor,
                                             @NotNull ResolveState state) {
    PsiElement target = qualifier.resolve();
    if (target == null || target == qualifier) return false;
    if (target instanceof GoImportSpec) {
      if (((GoImportSpec)target).isCImport()) return processor.execute(myElement, state);
      target = ((GoImportSpec)target).getImportString().resolve();
    }
    if (target instanceof PsiDirectory) {
      processDirectory((PsiDirectory)target, file, null, processor, state, false);
    }
    return false;
  }

  private boolean processUnqualifiedResolve(@NotNull GoFile file,
                                            @NotNull GoScopeProcessor processor,
                                            @NotNull ResolveState state,
                                            boolean localResolve) {
    GoScopeProcessorBase delegate = createDelegate(processor);
    ResolveUtil.treeWalkUp(myElement, delegate);
    Collection<? extends GoNamedElement> result = delegate.getVariants();
    if (!processNamedElements(processor, state, result, localResolve)) return false;
    if (!processFileEntities(file, processor, state, localResolve)) return false;
    PsiDirectory dir = file.getOriginalFile().getParent();
    if (!processDirectory(dir, file, file.getPackageName(), processor, state, true)) return false;
    if (PsiTreeUtil.getParentOfType(getElement(), GoReceiver.class) != null) return true;
    if (!processImports(file, processor, state, myElement)) return false;
    if (!processBuiltin(processor, state, myElement)) return false;
    if (getIdentifier().textMatches(GoConstants.NIL) && PsiTreeUtil.getParentOfType(myElement, GoTypeCaseClause.class) != null) {
      GoType type = PsiTreeUtil.getParentOfType(myElement, GoType.class);
      if (FormatterUtil.getPrevious(type != null ? type.getNode() : null, GoTypes.CASE) == null) return true;
      GoFile builtinFile = GoSdkUtil.findBuiltinFile(myElement);
      if (builtinFile == null) return false;
      GoVarDefinition nil = ContainerUtil.find(builtinFile.getVars(), v -> GoConstants.NIL.equals(v.getName()));
      if (nil != null && !processor.execute(nil, state)) return false;
    }
    return true;
  }

  public final static Set<String> DOC_ONLY_TYPES = ContainerUtil.set("Type", "Type1", "IntegerType", "FloatType", "ComplexType");
  private static final Condition<GoTypeSpec> BUILTIN_TYPE = spec -> {
    String name = spec.getName();
    return name != null && !DOC_ONLY_TYPES.contains(name);
  };

  @NotNull
  private GoTypeProcessor createDelegate(@NotNull GoScopeProcessor processor) {
    return new GoTypeProcessor(myElement, processor.isCompletion());
  }

  @Override
  protected boolean processFileEntities(@NotNull GoFile file,
                                        @NotNull GoScopeProcessor processor,
                                        @NotNull ResolveState state,
                                        boolean localProcessing) {
    List<GoTypeSpec> types = GoPsiImplUtil.isBuiltinFile(file) ? ContainerUtil.filter(file.getTypes(), BUILTIN_TYPE) : file.getTypes();
    return processNamedElements(processor, state, types, localProcessing);
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
    return !myInsideInterfaceType || definition.getSpecType().getType() instanceof GoInterfaceType;
  }

  @Override
  public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
    getIdentifier().replace(GoElementFactory.createIdentifierFromText(myElement.getProject(), newElementName));
    return myElement;
  }
}
