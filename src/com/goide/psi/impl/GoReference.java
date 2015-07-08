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
import com.goide.runconfig.testing.GoTestFinder;
import com.goide.sdk.GoSdkUtil;
import com.goide.util.GoUtil;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ArrayUtil;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.ObjectUtils;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.containers.OrderedSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.goide.psi.impl.GoPsiImplUtil.*;

public class GoReference extends PsiPolyVariantReferenceBase<GoReferenceExpressionBase> {
  public static final Key<List<? extends PsiElement>> IMPORT_USERS = Key.create("IMPORT_USERS");
  public static final Key<String > ACTUAL_NAME = Key.create("ACTUAL_NAME");
  public static final Key<Object> POINTER = Key.create("POINTER");
  public static final Key<Object> RECEIVER = Key.create("RECEIVER");
  public static final Key<SmartPsiElementPointer<GoReferenceExpressionBase>> CONTEXT = Key.create("CONTEXT");
  
  private static final ResolveCache.PolyVariantResolver<PsiPolyVariantReferenceBase> MY_RESOLVER =
    new ResolveCache.PolyVariantResolver<PsiPolyVariantReferenceBase>() {
      @NotNull
      @Override
      public ResolveResult[] resolve(@NotNull PsiPolyVariantReferenceBase psiPolyVariantReferenceBase, boolean incompleteCode) {
        return ((GoReference)psiPolyVariantReferenceBase).resolveInner();
      }
    };

  public GoReference(@NotNull GoReferenceExpressionBase o) {
    super(o, TextRange.from(o.getIdentifier().getStartOffsetInParent(), o.getIdentifier().getTextLength()));
  }
  
  @NotNull
  private ResolveResult[] resolveInner() {
    Collection<ResolveResult> result = new OrderedSet<ResolveResult>();
    processResolveVariants(createResolveProcessor(result, myElement));
    return result.toArray(new ResolveResult[result.size()]);
  }
  
  @Override
  public boolean isReferenceTo(@NotNull PsiElement element) {
    return GoUtil.couldBeReferenceTo(element, myElement) && super.isReferenceTo(element);
  }

  @NotNull
  private String getName() {
    return getIdentifier().getText();
  }

  @NotNull
  private PsiElement getIdentifier() {
    return myElement.getIdentifier();
  }

  @NotNull
  static GoScopeProcessor createResolveProcessor(@NotNull final Collection<ResolveResult> result, @NotNull final GoReferenceExpressionBase o) {
    return new GoScopeProcessor() {
      @Override
      public boolean execute(@NotNull PsiElement element, @NotNull ResolveState state) {
        if (element.equals(o)) return !result.add(new PsiElementResolveResult(element));
        String name = ObjectUtils.chooseNotNull(state.get(ACTUAL_NAME), 
                                                element instanceof PsiNamedElement ? ((PsiNamedElement)element).getName() : null);
        if (o.getIdentifier().textMatches(name)) {
          result.add(new PsiElementResolveResult(element));
          return false;
        }
        return true;
      }
    };
  }

  @Override
  @NotNull
  public ResolveResult[] multiResolve(final boolean incompleteCode) {
    if (!myElement.isValid()) return ResolveResult.EMPTY_ARRAY;
    return ResolveCache.getInstance(myElement.getProject()).resolveWithCaching(this, MY_RESOLVER, false, false);
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
    GoReferenceExpressionBase qualifier = myElement.getQualifier();
    return qualifier != null
           ? processQualifierExpression(((GoFile)file), qualifier, processor, state)
           : processUnqualifiedResolve(((GoFile)file), processor, state, true);
  }

  private boolean processQualifierExpression(@NotNull GoFile file,
                                             @NotNull GoReferenceExpressionBase qualifier,
                                             @NotNull GoScopeProcessor processor,
                                             @NotNull ResolveState state) {
    PsiReference reference = qualifier.getReference();
    PsiElement target = reference != null ? reference.resolve() : null;
    if (target == null || target == qualifier) return false;
    if (target instanceof GoImportSpec) target = ((GoImportSpec)target).getImportString().resolve();
    if (target instanceof PsiDirectory && !processDirectory((PsiDirectory)target, file, null, processor, state, false)) return false;
    if (target instanceof GoTypeOwner) {
      GoType type = typeOrParameterType((GoTypeOwner)target, createContext());
      if (type != null && !processGoType(type, processor, state)) return false;
    }
    return true;
  }

  private boolean processGoType(@NotNull GoType type, @NotNull GoScopeProcessor processor, @NotNull ResolveState state) {
    if (type instanceof GoParType) return processGoType(((GoParType)type).getType(), processor, state);
    if (type instanceof GoReceiverType) state = state.put(RECEIVER, Boolean.TRUE);
    if (!processExistingType(type, processor, state)) return false;
    if (type instanceof GoPointerType) {
      if (!processPointer((GoPointerType)type, processor, state.put(POINTER, Boolean.TRUE))) return false;
      GoType pointer = ((GoPointerType)type).getType();
      if (pointer instanceof GoPointerType) {
        return processPointer((GoPointerType)pointer, processor, state.put(POINTER, Boolean.TRUE));
      }
      else if (pointer != null && state.get(RECEIVER) != null && !processGoType(pointer, processor, state)) return false;
    }
    return processTypeRef(type, processor, state);
  }

  private boolean processPointer(@NotNull GoPointerType type, @NotNull GoScopeProcessor processor, @NotNull ResolveState state) {
    GoType pointer = type.getType();
    return pointer == null || processExistingType(pointer, processor, state) && processTypeRef(pointer, processor, state);
  }

  private boolean processTypeRef(@Nullable GoType type, @NotNull GoScopeProcessor processor, @NotNull ResolveState state) {
    return processInTypeRef(getTypeReference(type), type, processor, state);
  }

  private boolean processExistingType(@NotNull GoType type,
                                      @NotNull GoScopeProcessor processor,
                                      @NotNull ResolveState state) {
    PsiFile file = type.getContainingFile();
    if (!(file instanceof GoFile)) return true;
    PsiFile myFile = myElement.getContainingFile();
    if (!(myFile instanceof GoFile)) return true;
    GoFile o1 = (GoFile)myFile.getOriginalFile();
    GoFile o2 = (GoFile)file.getOriginalFile();
    boolean localResolve = Comparing.equal(o1.getImportPath(), o2.getImportPath()) && Comparing.equal(o1.getPackageName(), o2.getPackageName());

    PsiElement parent = type.getStub() == null ? type.getParent() : type.getStub().getParentStub().getPsi();
    if (parent instanceof GoTypeSpec && !processNamedElements(processor, state, ((GoTypeSpec)parent).getMethods(), localResolve)) {
      return false;
    }

    if (type instanceof GoStructType) {
      GoScopeProcessorBase delegate = createDelegate(processor);
      type.processDeclarations(delegate, ResolveState.initial(), null, myElement);
      final List<GoTypeReferenceExpression> interfaceRefs = ContainerUtil.newArrayList();
      final List<GoTypeReferenceExpression> structRefs = ContainerUtil.newArrayList();
      for (GoFieldDeclaration d : ((GoStructType)type).getFieldDeclarationList()) {
        if (!processNamedElements(processor, state, d.getFieldDefinitionList(), localResolve)) return false;
        GoAnonymousFieldDefinition anon = d.getAnonymousFieldDefinition();
        if (anon != null) {
          (anon.getMul() != null ? structRefs : interfaceRefs).add(anon.getTypeReferenceExpression());
          if (!processNamedElements(processor, state, ContainerUtil.createMaybeSingletonList(anon), localResolve)) return false;
        }
      }
      if (!processCollectedRefs(type, interfaceRefs, processor, state.put(POINTER, null))) return false;
      if (!processCollectedRefs(type, structRefs, processor, state)) return false;
    }
    else if (state.get(POINTER) == null && type instanceof GoInterfaceType) {
      if (!processNamedElements(processor, state, ((GoInterfaceType)type).getMethods(), localResolve)) return false;
      if (!processCollectedRefs(type, ((GoInterfaceType)type).getBaseTypesReferences(), processor, state)) return false;
    }
    else if (type instanceof GoFunctionType) {
      GoSignature signature = ((GoFunctionType)type).getSignature();
      GoResult result = signature != null ? signature.getResult() : null;
      GoType resultType = result != null ? result.getType() : null;
      if (resultType != null && !processGoType(resultType, processor, state)) return false;
    }
    return true;
  }

  private boolean processCollectedRefs(@NotNull GoType type,
                                       @NotNull List<GoTypeReferenceExpression> refs,
                                       @NotNull GoScopeProcessor processor,
                                       @NotNull ResolveState state) {
    for (GoTypeReferenceExpression ref : refs) {
      if (!processInTypeRef(ref, type, processor, state)) return false;
    }
    return true;
  }

  private boolean processInTypeRef(@Nullable GoTypeReferenceExpression refExpr,
                                   @Nullable GoType recursiveStopper,
                                   @NotNull GoScopeProcessor processor,
                                   @NotNull ResolveState state) {
    PsiReference reference = refExpr != null ? refExpr.getReference() : null;
    PsiElement resolve = reference != null ? reference.resolve() : null;
    if (resolve instanceof GoTypeSpec) {
      GoType resolveType = ((GoTypeSpec)resolve).getType();
      if (notMatchRecursiveStopper(recursiveStopper, resolveType) && !processGoType(resolveType, processor, state)) return false;
    }
    else if (resolve instanceof GoTypeOwner) {
      GoType type = ((GoTypeOwner)resolve).getGoType(state);
      if (notMatchRecursiveStopper(recursiveStopper, type) && !processGoType(type, processor, state)) return false;
    }
    return true;
  }

  private static boolean notMatchRecursiveStopper(@Nullable GoType recursiveStopper, @Nullable GoType resolveType) {
    return resolveType != null && (recursiveStopper == null || !resolveType.textMatches(recursiveStopper));
  }

  @Nullable
  private static String getPath(@Nullable PsiFile file) {
    if (file == null) return null;
    VirtualFile virtualFile = file.getOriginalFile().getVirtualFile();
    return virtualFile == null ? null : virtualFile.getPath();
  }

  protected static boolean processDirectory(@Nullable PsiDirectory dir,
                                            @Nullable GoFile file,
                                            @Nullable String packageName,
                                            @NotNull GoScopeProcessor processor,
                                            @NotNull ResolveState state,
                                            boolean localProcessing) {
    if (dir == null) return true;
    String filePath = getPath(file);
    boolean isTesting = GoTestFinder.isTestFile(file);
    for (PsiFile f : dir.getFiles()) {
      if (f instanceof GoFile && GoUtil.allowed(f) && !Comparing.equal(getPath(f), filePath)) {
        if (GoTestFinder.isTestFile(f) && !isTesting) continue;
        if (packageName != null && !packageName.equals(((GoFile)f).getPackageName())) continue;
        if (!processFileEntities((GoFile)f, processor, state, localProcessing)) return false;
      }
    }
    return true;
  }

  private boolean processUnqualifiedResolve(@NotNull GoFile file,
                                            @NotNull GoScopeProcessor processor,
                                            @NotNull ResolveState state,
                                            boolean localResolve) {
    // todo: rewrite with qualification not with siblings
    GoReceiverType receiverType = PsiTreeUtil.getPrevSiblingOfType(myElement, GoReceiverType.class);
    if (receiverType != null) {
      return processGoType(receiverType, processor, state);
    }

    if (getIdentifier().textMatches("_")) return processor.execute(myElement, state);

    PsiElement parent = myElement.getParent();

    if (parent instanceof GoSelectorExpr) {
      boolean result = processSelector((GoSelectorExpr)parent, processor, state, myElement);
      if (processor.isCompletion()) return result;
      if (!result || prevDot(myElement)) return false;
    }

    PsiElement grandPa = parent.getParent();
    if (grandPa instanceof GoSelectorExpr && !processSelector((GoSelectorExpr)grandPa, processor, state, parent)) return false;
    
    if (prevDot(parent)) return false;

    GoScopeProcessorBase delegate = createDelegate(processor);
    ResolveUtil.treeWalkUp(myElement, delegate);
    if (!processNamedElements(processor, state, delegate.getVariants(), localResolve)) return false;
    processReceiver(delegate);
    if (!processNamedElements(processor, state, delegate.getVariants(), localResolve)) return false;
    processFunctionParameters(myElement, delegate);
    if (!processNamedElements(processor, state, delegate.getVariants(), localResolve)) return false;
    if (!processFileEntities(file, processor, state, localResolve)) return false;
    if (!processDirectory(file.getOriginalFile().getParent(), file, file.getPackageName(), processor, state, true)) return false;
    if (processImports(file, processor, state, myElement)) return false;
    if (processBuiltin(processor, state, myElement)) return false;
    return true;
  }

  static boolean processBuiltin(@NotNull GoScopeProcessor processor, @NotNull ResolveState state, @NotNull GoCompositeElement element) {
    GoFile builtinFile = GoSdkUtil.findBuiltinFile(element);
    if (builtinFile != null && !processFileEntities(builtinFile, processor, state, true)) return true;
    return false;
  }

  static boolean processImports(@NotNull GoFile file,
                                @NotNull GoScopeProcessor processor,
                                @NotNull ResolveState state,
                                @NotNull GoCompositeElement element) {
    for (Map.Entry<String, Collection<GoImportSpec>> entry : file.getImportMap().entrySet()) {
      for (GoImportSpec o : entry.getValue()) {
        if (o.isForSideEffects()) continue;
        
        GoImportString importString = o.getImportString();
        if (o.isDot()) {
          PsiDirectory implicitDir = importString.resolve();
          boolean resolved = !processDirectory(implicitDir, file, null, processor, state, false);
          if (resolved && !processor.isCompletion()) {
            putIfAbsent(o, element);
          }
          if (resolved) return true;
        }
        else {
          PsiDirectory resolve = importString.resolve();
          // todo: multi-resolve into appropriate package clauses
          if (resolve != null && !processor.execute(resolve, state.put(ACTUAL_NAME, entry.getKey()))) return true; 
          if (!processor.execute(o, state.put(ACTUAL_NAME, entry.getKey()))) return true;
        }
      }
    }
    return false;
  }

  private boolean processSelector(@NotNull GoSelectorExpr parent,
                                  @NotNull GoScopeProcessor processor,
                                  @NotNull ResolveState state,
                                  @Nullable PsiElement another) {
    List<GoExpression> list = parent.getExpressionList();
    if (list.size() > 1 && list.get(1).isEquivalentTo(another)) {
      GoType type = list.get(0).getGoType(createContext());
      if (type != null && !processGoType(type, processor, state)) return false;
    }
    return true;
  }

  @NotNull
  public ResolveState createContext() {
    return ResolveState.initial().put(CONTEXT, SmartPointerManager.getInstance(myElement.getProject()).createSmartPsiElementPointer(myElement));
  }

  @NotNull
  private GoVarProcessor createDelegate(@NotNull GoScopeProcessor processor) {
    return new GoVarProcessor(getIdentifier(), myElement, processor.isCompletion(), true) {
      @Override
      protected boolean condition(@NotNull PsiElement e) {
        return super.condition(e) && !(e instanceof GoTypeSpec);
      }
    };
  }

  private static boolean processFileEntities(@NotNull GoFile file,
                                             @NotNull GoScopeProcessor processor,
                                             @NotNull ResolveState state,
                                             boolean localProcessing) {
    if (!processNamedElements(processor, state, file.getConstants(), localProcessing)) return false;
    if (!processNamedElements(processor, state, file.getVars(), localProcessing)) return false;
    if (!processNamedElements(processor, state, file.getFunctions(), localProcessing)) return false;
    if (!processNamedElements(processor, state, file.getTypes(), localProcessing)) return false;
    return true;
  }

  static boolean processNamedElements(@NotNull PsiScopeProcessor processor,
                                      @NotNull ResolveState state,
                                      @NotNull Collection<? extends GoNamedElement> elements, 
                                      boolean localResolve) {
    for (GoNamedElement definition : elements) {
      if ((localResolve || definition.isPublic()) && !processor.execute(definition, state)) return false;
    }
    return true;
  }

  // todo: return boolean for better performance 
  public static void processFunctionParameters(@NotNull GoCompositeElement e, @NotNull GoScopeProcessorBase processor) {
    GoSignatureOwner signatureOwner = PsiTreeUtil.getParentOfType(e, GoSignatureOwner.class);
    while (signatureOwner != null && processSignatureOwner(signatureOwner, processor)) {
      signatureOwner = PsiTreeUtil.getParentOfType(signatureOwner, GoSignatureOwner.class);
    }
  }

  private static boolean processSignatureOwner(@NotNull GoSignatureOwner o, @NotNull GoScopeProcessorBase processor) {
    GoSignature signature = o.getSignature();
    if (signature == null) return true;
    if (!processParameters(processor, signature.getParameters())) return false;
    GoResult result = signature.getResult();
    GoParameters resultParameters = result != null ? result.getParameters() : null;
    return !(resultParameters != null && !processParameters(processor, resultParameters));
  }

  private static boolean processParameters(@NotNull GoScopeProcessorBase processor, @NotNull GoParameters parameters) {
    for (GoParameterDeclaration declaration : parameters.getParameterDeclarationList()) {
      List<GoParamDefinition> list = declaration.getParamDefinitionList();
      if (!processNamedElements(processor, ResolveState.initial(), list, true)) return false;
    }
    return true;
  }

  private void processReceiver(@NotNull GoScopeProcessorBase processor) {
    GoMethodDeclaration method = PsiTreeUtil.getParentOfType(myElement, GoMethodDeclaration.class);
    GoReceiver receiver = method != null ? method.getReceiver() : null;
    if (receiver != null) receiver.processDeclarations(processor, ResolveState.initial(), null, myElement);
  }

  @NotNull
  @Override
  public PsiElement handleElementRename(@NotNull String newElementName) throws IncorrectOperationException {
    getIdentifier().replace(GoElementFactory.createIdentifierFromText(myElement.getProject(), newElementName));
    return myElement;
  }

  static void putIfAbsent(@NotNull PsiElement importElement, @NotNull PsiElement usage) {
    List<PsiElement> newList = ContainerUtil.newSmartList(usage);
    List<? extends PsiElement> list = importElement.getUserData(IMPORT_USERS);
    if (list != null) {
      newList.addAll(list);
    }
    importElement.putUserData(IMPORT_USERS, newList);
  }
}