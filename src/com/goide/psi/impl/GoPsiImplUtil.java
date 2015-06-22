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

import com.goide.GoConstants;
import com.goide.GoTypes;
import com.goide.psi.*;
import com.goide.psi.impl.imports.GoImportReferenceSet;
import com.goide.stubs.GoImportSpecStub;
import com.goide.stubs.GoNamedStub;
import com.goide.stubs.GoParameterDeclarationStub;
import com.goide.stubs.GoTypeStub;
import com.goide.stubs.index.GoMethodIndex;
import com.goide.util.GoStringLiteralEscaper;
import com.goide.util.GoUtil;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.impl.light.LightElement;
import com.intellij.psi.impl.source.resolve.reference.impl.PsiMultiReference;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReferenceOwner;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.PsiFileReference;
import com.intellij.psi.impl.source.tree.LeafElement;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.GlobalSearchScopesCore;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.Function;
import com.intellij.util.PathUtil;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class GoPsiImplUtil {
  public static boolean builtin(@NotNull PsiElement resolve) {
    PsiFile file = resolve.getContainingFile();
    if (!(file instanceof GoFile)) return false;
    return isBuiltinFile(file);
  }

  public static boolean isPanic(@NotNull GoCallExpr o) {
    GoExpression e = o.getExpression();
    if (StringUtil.equals("panic", e.getText()) && e instanceof GoReferenceExpression) {
      PsiReference reference = e.getReference();
      PsiElement resolve = reference != null ? reference.resolve() : null;
      if (!(resolve instanceof GoFunctionDeclaration)) return false;
      GoFile file = ((GoFunctionDeclaration)resolve).getContainingFile();
      return isBuiltinFile(file);
    }
    return false;
  }

  private static boolean isBuiltinFile(@NotNull PsiFile file) {
    return StringUtil.equals(((GoFile)file).getPackageName(), GoConstants.BUILTIN_PACKAGE_NAME)
           && StringUtil.equals(file.getName(), GoConstants.BUILTIN_FILE_NAME);
  }

  @NotNull
  public static GlobalSearchScope packageScope(@NotNull GoFile file) {
    List<GoFile> files = getAllPackageFiles(file);
    return GlobalSearchScope.filesScope(file.getProject(), ContainerUtil.map(files, new Function<GoFile, VirtualFile>() {
      @Override
      public VirtualFile fun(GoFile file) {
        return file.getVirtualFile();
      }
    }));
  }

  @NotNull
  public static List<GoFile> getAllPackageFiles(@NotNull GoFile file) {
    String name = file.getPackageName();
    PsiDirectory parent = file.getParent();
    if (parent == null || StringUtil.isEmpty(name)) return ContainerUtil.list(file);
    PsiElement[] children = parent.getChildren();
    List<GoFile> files = ContainerUtil.newArrayListWithCapacity(children.length);
    for (PsiElement element : children) {
      if (element instanceof GoFile && Comparing.equal(((GoFile)element).getPackageName(), name)) {
        files.add(((GoFile)element));
      }
    }
    return files;
  }

  @Nullable
  public static GoTopLevelDeclaration getTopLevelDeclaration(@Nullable PsiElement startElement) {
    GoTopLevelDeclaration declaration = PsiTreeUtil.getTopmostParentOfType(startElement, GoTopLevelDeclaration.class);
    if (declaration == null || !(declaration.getParent() instanceof GoFile)) return null;
    return declaration;
  }
  
  public static int getArity(@Nullable GoSignature s) {
    return s == null ? -1 : s.getParameters().getParameterDeclarationList().size();
  }

  @Nullable
  public static GoTypeReferenceExpression getQualifier(@NotNull GoTypeReferenceExpression o) {
    return PsiTreeUtil.getChildOfType(o, GoTypeReferenceExpression.class);
  }

  @Nullable
  public static PsiDirectory resolve(@NotNull GoImportString importString) {
    PsiReference[] references = importString.getReferences();
    for (PsiReference reference : references) {
      if (reference instanceof FileReferenceOwner) {
        PsiFileReference lastFileReference = ((FileReferenceOwner)reference).getLastFileReference();
        PsiElement result = lastFileReference != null ? lastFileReference.resolve() : null;
        return result instanceof PsiDirectory ? (PsiDirectory)result : null;
      }
    }
    return null;
  }

  @NotNull
  public static PsiReference getReference(@NotNull GoTypeReferenceExpression o) {
    return new GoTypeReference(o);
  }

  @NotNull
  public static PsiReference getReference(@NotNull GoLabelRef o) {
    return new GoLabelReference(o);
  }

  @Nullable
  public static PsiReference getReference(@NotNull GoVarDefinition o) {
    GoShortVarDeclaration shortDeclaration = PsiTreeUtil.getParentOfType(o, GoShortVarDeclaration.class);
    boolean createRef = PsiTreeUtil.getParentOfType(shortDeclaration, GoBlock.class, GoIfStatement.class, GoSwitchStatement.class, GoSelectStatement.class) instanceof GoBlock;
    return createRef ? new GoVarReference(o) : null;
  }

  @NotNull
  public static GoReference getReference(@NotNull final GoReferenceExpression o) {
    return new GoReference(o);
  }

  @NotNull
  public static PsiReference getReference(@NotNull GoFieldName o) {
    return new PsiMultiReference(new PsiReference[]{new GoFieldNameReference(o), new GoReference(o)}, o) {
      @Override
      public PsiElement resolve() {
        PsiReference[] refs = getReferences();
        PsiElement resolve = refs.length > 0 ? refs[0].resolve() : null;
        if (resolve != null) return resolve;
        return refs.length > 1 ? refs[1].resolve() : null;
      }

      public boolean isReferenceTo(PsiElement element) {
        return GoUtil.couldBeReferenceTo(element, getElement()) && getElement().getManager().areElementsEquivalent(resolve(), element);
      }
    };
  }

  @Nullable
  public static GoReferenceExpression getQualifier(@SuppressWarnings("UnusedParameters") @NotNull GoFieldName o) {
    return null;
  }

  @NotNull
  public static PsiReference[] getReferences(@NotNull GoImportString o) {
    if (o.getTextLength() < 2) return PsiReference.EMPTY_ARRAY;
    return new GoImportReferenceSet(o).getAllReferences();
  }

  @Nullable
  public static GoReferenceExpression getQualifier(@NotNull GoReferenceExpression o) {
    return PsiTreeUtil.getChildOfType(o, GoReferenceExpression.class);
  }

  public static boolean processDeclarations(@NotNull GoCompositeElement o, @NotNull PsiScopeProcessor processor, @NotNull ResolveState state, PsiElement lastParent, @NotNull PsiElement place) {
    boolean isAncestor = PsiTreeUtil.isAncestor(o, place, false);
    if (o instanceof GoVarSpec) return isAncestor || GoCompositeElementImpl.processDeclarationsDefault(o, processor, state, lastParent, place);

    if (isAncestor) return GoCompositeElementImpl.processDeclarationsDefault(o, processor, state, lastParent, place);

    if (o instanceof GoBlock ||
        o instanceof GoIfStatement ||
        o instanceof GoForStatement ||
        o instanceof GoCommClause ||
        o instanceof GoFunctionLit ||
        o instanceof GoTypeCaseClause ||
        o instanceof GoExprCaseClause) {
      return processor.execute(o, state);
    }
    return GoCompositeElementImpl.processDeclarationsDefault(o, processor, state, lastParent, place);
  }

  @Nullable
  public static GoType getGoType(@NotNull GoReceiver o, @SuppressWarnings("UnusedParameters") @Nullable ResolveState context) {
    return o.getType();
  }

  @Nullable
  public static GoType getGoType(@NotNull GoAnonymousFieldDefinition o, @SuppressWarnings("UnusedParameters") @Nullable ResolveState context) {
    return getType(o.getTypeReferenceExpression());
  }

  @Nullable
  public static PsiElement getIdentifier(@SuppressWarnings("UnusedParameters") @NotNull GoAnonymousFieldDefinition o) {
    return null;
  }

  @NotNull
  public static String getName(@NotNull GoAnonymousFieldDefinition o) {
    return o.getTypeReferenceExpression().getIdentifier().getText();
  }

  public static int getTextOffset(@NotNull GoAnonymousFieldDefinition o) {
    return o.getTypeReferenceExpression().getIdentifier().getTextOffset();
  }

  @Nullable
  public static String getName(@NotNull GoMethodSpec o) {
    GoNamedStub<GoMethodSpec> stub = o.getStub();
    if (stub != null) {
      return stub.getName();
    }
    PsiElement identifier = o.getIdentifier();
    if (identifier != null) return identifier.getText();
    GoTypeReferenceExpression typeRef = o.getTypeReferenceExpression();
    return typeRef != null ? typeRef.getIdentifier().getText() : null;
  }

  @Nullable
  public static GoTypeReferenceExpression getTypeReference(@Nullable GoType o) {
    if (o == null) return null;
    if (o instanceof GoChannelType) {
      GoType type = ((GoChannelType)o).getType();
      return type != null ? type.getTypeReferenceExpression() : null;
    }
    if (o instanceof GoReceiverType) {
      return PsiTreeUtil.findChildOfAnyType(o, GoTypeReferenceExpression.class);
    }
    return o.getTypeReferenceExpression();
  }

  @Nullable
  public static GoType getGoType(@NotNull GoConstDefinition o, @Nullable ResolveState context) {
    GoType sibling = o.findSiblingType();
    if (sibling != null) return sibling;
    
    // todo: stubs 
    GoType fromSpec = findTypeInConstSpec(o);
    if (fromSpec != null) return fromSpec;

    GoConstSpec prev = PsiTreeUtil.getPrevSiblingOfType(o.getParent(), GoConstSpec.class);
    while (prev != null) {
      GoType type = prev.getType();
      if (type != null) return type;
      GoExpression expr = ContainerUtil.getFirstItem(prev.getExpressionList()); // not sure about first
      if (expr != null) return expr.getGoType(context);
      prev = PsiTreeUtil.getPrevSiblingOfType(prev, GoConstSpec.class);
    }
    
    return null;
  }

  @Nullable
  private static GoType findTypeInConstSpec(@NotNull GoConstDefinition o) {
    PsiElement parent = o.getParent();
    if (!(parent instanceof GoConstSpec)) return null;
    GoConstSpec spec = (GoConstSpec)parent;
    GoType commonType = spec.getType();
    if (commonType != null) return commonType;
    List<GoConstDefinition> varList = spec.getConstDefinitionList();
    int i = varList.indexOf(o);
    i = i == -1 ? 0 : i;
    List<GoExpression> exprs = spec.getExpressionList();
    if (exprs.size() <= i) return null;
    return exprs.get(i).getGoType(null);
  }

  @Nullable
  public static GoType getGoType(@NotNull final GoExpression o, @Nullable ResolveState context) {
    if (o instanceof GoUnaryExpr) {
      GoExpression expression = ((GoUnaryExpr)o).getExpression();
      if (expression != null) {
        GoType type = expression.getGoType(context);
        if (type instanceof GoChannelType && ((GoUnaryExpr)o).getSendChannel() != null) return ((GoChannelType)type).getType();
        if (type instanceof GoPointerType && ((GoUnaryExpr)o).getMul() != null) return ((GoPointerType)type).getType();
        return type;
      }
      return null;
    }
    else if (o instanceof GoCompositeLit) {
      GoType type = ((GoCompositeLit)o).getType();
      if (type != null) return type;
      GoTypeReferenceExpression expression = ((GoCompositeLit)o).getTypeReferenceExpression();
      return getType(expression);
    }
    else if (o instanceof GoFunctionLit) {
      GoSignature signature = ((GoFunctionLit)o).getSignature();
      GoResult result = signature != null ? signature.getResult() : null;
      return result != null ? result.getType() : null;
    }
    else if (o instanceof GoBuiltinCallExpr) {
      String text = ((GoBuiltinCallExpr)o).getReferenceExpression().getText();
      if ("new".equals(text) || "make".equals(text)) {
        GoBuiltinArgs args = ((GoBuiltinCallExpr)o).getBuiltinArgs();
        GoType type = args != null ? args.getType() : null;
        if (type instanceof GoMapType || type instanceof GoArrayOrSliceType || type instanceof GoChannelType) return type;
        if (type != null) {
          GoTypeReferenceExpression expression = getTypeReference(type);
          return getType(expression);
        }
      }
    }
    else if (o instanceof GoCallExpr) {
      return ((GoCallExpr)o).getExpression().getGoType(context);
    }
    else if (o instanceof GoReferenceExpression) {
      PsiReference reference = o.getReference();
      PsiElement resolve = reference != null ? reference.resolve() : null;
      if (resolve instanceof GoTypeOwner) return typeOrParameterType((GoTypeOwner)resolve, context);
    }
    else if (o instanceof GoParenthesesExpr) {
      GoExpression expression = ((GoParenthesesExpr)o).getExpression();
      return expression != null ? expression.getGoType(context) : null;
    }
    else if (o instanceof GoSelectorExpr) {
      GoExpression item = ContainerUtil.getLastItem(((GoSelectorExpr)o).getExpressionList());
      return item != null ? item.getGoType(context) : null;
    }
    else if (o instanceof GoIndexOrSliceExpr) {
      GoExpression first = ContainerUtil.getFirstItem(((GoIndexOrSliceExpr)o).getExpressionList());
      GoType type = first == null ? null : getGoType(first, context);
      if (o.getNode().findChildByType(GoTypes.COLON) != null) return type; // means slice expression, todo: extract if needed
      GoTypeReferenceExpression typeRef = getTypeReference(type);
      if (typeRef != null) {
        type = getType(typeRef);
      }
      if (type instanceof GoMapType) {
        List<GoType> list = ((GoMapType)type).getTypeList();
        if (list.size() == 2) {
          return list.get(1);
        }
      }
      else if (type instanceof GoArrayOrSliceType) {
        GoType tt = ((GoArrayOrSliceType)type).getType();
        return typeFromRefOrType(tt);
      }
    }
    else if (o instanceof GoTypeAssertionExpr) {
      return ((GoTypeAssertionExpr)o).getType();
    }
    else if (o instanceof GoConversionExpr) {
      return ((GoConversionExpr)o).getType();
    }
    else if (o instanceof GoMethodExpr) {
      GoReferenceExpression e = ((GoMethodExpr)o).getReferenceExpression();
      GoReference reference = e != null ? e.getReference() : null;
      PsiElement resolve = reference != null ? reference.resolve() : null;
      return resolve instanceof GoTypeOwner ? ((GoTypeOwner)resolve).getGoType(context) : null;
    }
    return null;
  }

  @Nullable
  private static GoType typeFromRefOrType(@Nullable GoType tt) {
    if (tt == null) return null;
    GoTypeReferenceExpression tr = getTypeReference(tt);
    return tr != null ? getType(tr) : tt;
  }

  @Nullable
  public static GoType typeOrParameterType(@NotNull final GoTypeOwner resolve, @Nullable ResolveState context) {
    GoType type = resolve.getGoType(context);
    if (resolve instanceof GoParamDefinition && ((GoParamDefinition)resolve).isVariadic()) {
      class MyArrayType extends LightElement implements GoArrayOrSliceType {
        private GoType myType;

        protected MyArrayType(GoType type) {
          super(resolve.getManager(), resolve.getLanguage());
          myType = type;
        }

        @Override
        public String getText() {
          return myType != null ? ("[]" + myType.getText()) : null;
        }

        @Nullable
        @Override
        public GoExpression getExpression() {
          return null;
        }

        @Nullable
        @Override
        public GoType getType() {
          return myType;
        }

        @Nullable
        @Override
        public GoTypeReferenceExpression getTypeReferenceExpression() {
          return null;
        }

        @NotNull
        @Override
        public PsiElement getLbrack() {
          //noinspection ConstantConditions
          return null; // todo: mock?
        }

        @Nullable
        @Override
        public PsiElement getRbrack() {
          return null;
        }

        @Nullable
        @Override
        public PsiElement getTripleDot() {
          return null;
        }

        @Override
        public String toString() {
          return null;
        }

        @Override
        public IStubElementType getElementType() {
          return null;
        }

        @Override
        public GoTypeStub getStub() {
          return null;
        }

        @Override
        public boolean shouldGoDeeper() {
          return false;
        }
      }
      return new MyArrayType(type);
    }
    return type;
  }

  @Nullable
  public static GoType getType(@Nullable GoTypeReferenceExpression expression) {
    PsiReference reference = expression != null ? expression.getReference() : null;
    PsiElement resolve = reference != null ? reference.resolve() : null;
    return resolve instanceof GoTypeSpec ? ((GoTypeSpec)resolve).getType() : null;
  }

  public static boolean isVariadic(@NotNull GoParamDefinition o) {
    PsiElement parent = o.getParent();
    return parent instanceof GoParameterDeclaration && ((GoParameterDeclaration)parent).isVariadic();
  }

  public static boolean isVariadic(@NotNull GoParameterDeclaration o) {
    GoParameterDeclarationStub stub = o.getStub();
    return stub != null ? stub.isVariadic() : o.getTripleDot() != null;
  }

  @Nullable
  public static GoType getGoType(@NotNull GoTypeSpec o, @SuppressWarnings("UnusedParameters") @Nullable ResolveState context) {
    return o.getType();
  }

  @Nullable
  public static GoType getGoType(@NotNull GoVarDefinition o, @Nullable ResolveState context) {
    // see http://golang.org/ref/spec#RangeClause
    PsiElement parent = o.getParent();
    if (parent instanceof GoRangeClause) {
      return processRangeClause(o, (GoRangeClause)parent);
    }
    if (parent instanceof GoVarSpec) {
      return findTypeInVarSpec(o);
    }
    GoCompositeLit literal = PsiTreeUtil.getNextSiblingOfType(o, GoCompositeLit.class);
    if (literal != null) {
      return literal.getType();
    }
    GoType siblingType = o.findSiblingType();
    if (siblingType != null) return siblingType;

    if (parent instanceof GoTypeSwitchGuard) {
      SmartPsiElementPointer<GoReferenceExpressionBase> pointer = context == null ? null : context.get(GoReference.CONTEXT);
      GoTypeCaseClause typeCase = PsiTreeUtil.getParentOfType(pointer != null ? pointer.getElement() : null, GoTypeCaseClause.class);
      GoTypeSwitchCase switchCase = typeCase != null ? typeCase.getTypeSwitchCase() : null;
      return switchCase != null ? switchCase.getType() : null;
    }
    return null;
  }

  @Nullable
  private static GoType findTypeInVarSpec(@NotNull GoVarDefinition o) {
    GoVarSpec parent = (GoVarSpec)o.getParent();
    GoType commonType = parent.getType();
    if (commonType != null) return commonType;
    List<GoVarDefinition> varList = parent.getVarDefinitionList();
    int i = varList.indexOf(o);
    i = i == -1 ? 0 : i;
    List<GoExpression> exprs = parent.getExpressionList();
    if (exprs.size() == 1 && exprs.get(0) instanceof GoCallExpr) {
      GoExpression call = exprs.get(0);
      GoType type = funcType(typeFromRefOrType(call.getGoType(null)));
      if (type instanceof GoTypeList) {
        if (((GoTypeList)type).getTypeList().size() > i) {
          return ((GoTypeList)type).getTypeList().get(i);
        }
      }
      return type;
    }
    if (exprs.size() <= i) return null;
    return exprs.get(i).getGoType(null);
  }

  @Nullable
  private static GoType funcType(@Nullable GoType type) {
    if (type instanceof GoFunctionType) {
      GoSignature signature = ((GoFunctionType)type).getSignature();
      GoResult result = signature != null ? signature.getResult() : null;
      return result != null ? result.getType() : type;
    }
    return type;
  }

  @Nullable
  private static GoType processRangeClause(@NotNull GoVarDefinition o, @NotNull GoRangeClause parent) {
    List<GoExpression> exprs = parent.getExpressionList();
    GoExpression last = ContainerUtil.getLastItem(exprs);
    int rangeOffset = parent.getRange().getTextOffset();
    last = last != null && last.getTextOffset() > rangeOffset ? last : null;

    if (last != null) {
      List<GoVarDefinition> varList = parent.getVarDefinitionList();
      int i = varList.indexOf(o);
      i = i == -1 ? 0 : i;
      GoType type = last.getGoType(null);
      if (type instanceof GoChannelType) {
        return ((GoChannelType)type).getType();
      }
      GoTypeReferenceExpression typeRef = type != null ? type.getTypeReferenceExpression() : null;
      if (typeRef != null) {
        PsiElement resolve = typeRef.getReference().resolve();
        if (resolve instanceof GoTypeSpec) {
          type = ((GoTypeSpec)resolve).getType();
          if (type instanceof GoChannelType) {
            return ((GoChannelType)type).getType();
          }
        }
      }
      if (type instanceof GoArrayOrSliceType && i == 1) return ((GoArrayOrSliceType)type).getType();
      if (type instanceof GoMapType) {
        List<GoType> list = ((GoMapType)type).getTypeList();
        if (i == 0) return ContainerUtil.getFirstItem(list);
        if (i == 1) return ContainerUtil.getLastItem(list);
      }
    }
    return null;
  }

  @NotNull
  public static String getText(@Nullable GoType o) {
    if (o == null) return "";
    boolean s = o instanceof GoStructType;
    boolean i = o instanceof GoInterfaceType;
    if (s || i) {
      GoTypeStub stub = o.getStub();
      PsiElement parent = stub != null ? stub.getParentStub().getPsi() : o.getParent();
      if (parent instanceof GoTypeSpec) {
        String n = ((GoTypeSpec)parent).getName();
        String p = ((GoTypeSpec)parent).getContainingFile().getPackageName();
        if (n != null && p != null) return p + "." + n;
      }
      return s ? "struct {...}" : "interface {...}";
    }
    String text = o.getText();
    if (text == null) return "";
    return text.replaceAll("\\s+", " ");
  }

  @NotNull
  public static List<GoMethodSpec> getMethods(@NotNull final GoInterfaceType o) {
    return ContainerUtil.filter(o.getMethodSpecList(), new Condition<GoMethodSpec>() {
      @Override
      public boolean value(@NotNull GoMethodSpec spec) {
        return spec.getIdentifier() != null;
      }
    });
  }

  @NotNull
  public static List<GoTypeReferenceExpression> getBaseTypesReferences(@NotNull final GoInterfaceType o) {
    final List<GoTypeReferenceExpression> refs = ContainerUtil.newArrayList();
    o.accept(new GoRecursiveVisitor() {
      @Override
      public void visitMethodSpec(@NotNull GoMethodSpec o) {
        ContainerUtil.addIfNotNull(refs, o.getTypeReferenceExpression());
      }
    });
    return refs;
  }

  @NotNull
  public static List<GoMethodDeclaration> getMethods(@NotNull final GoTypeSpec o) {
    final PsiDirectory dir = o.getContainingFile().getOriginalFile().getParent();
    if (dir != null) {
      return CachedValuesManager.getCachedValue(o, new CachedValueProvider<List<GoMethodDeclaration>>() {
        @Nullable
        @Override
        public Result<List<GoMethodDeclaration>> compute() {
          return Result.create(calcMethods(o), dir);
        }
      });
    }
    return calcMethods(o);
  }

  @NotNull
  private static List<GoMethodDeclaration> calcMethods(@NotNull GoTypeSpec o) {
    PsiFile file = o.getContainingFile().getOriginalFile();
    if (file instanceof GoFile) {
      String packageName = ((GoFile)file).getPackageName();
      String typeName = o.getName();
      if (StringUtil.isEmpty(packageName) || StringUtil.isEmpty(typeName)) return Collections.emptyList();
      String key = packageName + "." + typeName;
      Project project = ((GoFile)file).getProject();
      PsiDirectory parent = file.getParent();
      GlobalSearchScope scope = parent == null ? GlobalSearchScope.allScope(project) : GlobalSearchScopesCore.directoryScope(parent, false);
      Collection<GoMethodDeclaration> declarations = GoMethodIndex.find(key, project, scope);
      return ContainerUtil.newArrayList(declarations);
    }
    return Collections.emptyList();
  }

  @Nullable
  public static GoType getGoType(@NotNull GoSignatureOwner o, @SuppressWarnings("UnusedParameters") @Nullable ResolveState context) {
    GoSignature signature = o.getSignature();
    GoResult result = signature != null ? signature.getResult() : null;
    if (result != null) {
      GoType type = result.getType();
      if (type instanceof GoTypeList && ((GoTypeList)type).getTypeList().size() == 1) {
        return ((GoTypeList)type).getTypeList().get(0);
      }
      if (type != null) return type;
      final GoParameters parameters = result.getParameters();
      if (parameters != null) {
        GoType parametersType = parameters.getType();
        if (parametersType != null) return parametersType;
        List<GoType> composite = ContainerUtil.newArrayList();
        for (GoParameterDeclaration p : parameters.getParameterDeclarationList()) {
          for (GoParamDefinition definition : p.getParamDefinitionList()) {
            composite.add(definition.getGoType(context));
          }
        }
        if (composite.size() == 1) return composite.get(0);
        class MyGoTypeList extends LightElement implements GoTypeList {
          @NotNull private final List<GoType> myTypes;

          public MyGoTypeList(@NotNull List<GoType> types) {
            super(parameters.getManager(), parameters.getLanguage());
            myTypes = types;
          }

          @NotNull
          @Override
          public List<GoType> getTypeList() {
            return myTypes;
          }

          @Nullable
          @Override
          public GoTypeReferenceExpression getTypeReferenceExpression() {
            return null;
          }

          @Override
          public String toString() {
            return "MyGoTypeList{myTypes=" + myTypes + '}';
          }

          @NotNull
          @Override
          public PsiElement getNavigationElement() {
            return parameters;
          }

          @Override
          public IStubElementType getElementType() {
            return null;
          }

          @Override
          public GoTypeStub getStub() {
            return null;
          }

          @Override
          public boolean shouldGoDeeper() {
            return false;
          }
        }
        return new MyGoTypeList(composite);
      }
    }
    return null;
  }

  @NotNull
  public static GoImportSpec addImport(@NotNull GoImportList importList, @NotNull String packagePath, @Nullable String alias) {
    Project project = importList.getProject();
    GoImportDeclaration lastImportDeclaration = ContainerUtil.getLastItem(importList.getImportDeclarationList());
    GoImportDeclaration newDeclaration = GoElementFactory.createImportDeclaration(project, packagePath, alias, false);
    if (lastImportDeclaration != null) {
      List<GoImportSpec> importSpecList = lastImportDeclaration.getImportSpecList();
      if (lastImportDeclaration.getRparen() == null && importSpecList.size() == 1) {
        GoImportSpec firstItem = ContainerUtil.getFirstItem(importSpecList);
        assert firstItem != null;
        String path = firstItem.getPath();
        String oldAlias = firstItem.getAlias();
        
        GoImportDeclaration importWithParens = GoElementFactory.createImportDeclaration(project, path, oldAlias, true);
        lastImportDeclaration = (GoImportDeclaration)lastImportDeclaration.replace(importWithParens);
      }
      return lastImportDeclaration.addImportSpec(packagePath, alias);
    }
    else {
      return addImportDeclaration(importList, newDeclaration);
    }
  }

  @NotNull
  private static GoImportSpec addImportDeclaration(@NotNull GoImportList importList, @NotNull GoImportDeclaration newImportDeclaration) {
    GoImportDeclaration importDeclaration = (GoImportDeclaration)importList.addAfter(newImportDeclaration, null);
    final PsiElement importListNextSibling = importList.getNextSibling();
    if (!(importListNextSibling instanceof PsiWhiteSpace)) {
      importList.addAfter(GoElementFactory.createNewLine(importList.getProject()), importDeclaration);
      if (importListNextSibling != null) {
        // double new line if there is something valuable after import list
        importList.addAfter(GoElementFactory.createNewLine(importList.getProject()), importDeclaration);
      }
    }
    importList.addBefore(GoElementFactory.createNewLine(importList.getProject()), importDeclaration);
    GoImportSpec result = ContainerUtil.getFirstItem(importDeclaration.getImportSpecList());
    assert result != null;
    return result;
  }

  @NotNull
  public static GoImportSpec addImportSpec(@NotNull GoImportDeclaration declaration, @NotNull String packagePath, @Nullable String alias) {
    PsiElement rParen = declaration.getRparen();
    assert rParen != null;
    declaration.addBefore(GoElementFactory.createNewLine(declaration.getProject()), rParen);
    GoImportSpec spec = (GoImportSpec)declaration.addBefore(GoElementFactory.createImportSpec(declaration.getProject(), packagePath, alias), rParen);
    declaration.addBefore(GoElementFactory.createNewLine(declaration.getProject()), rParen);
    return spec;
  }

  public static String getLocalPackageName(@NotNull String importPath) {
    return PathUtil.getFileName(importPath);
  }

  public static String getLocalPackageName(@NotNull GoImportSpec importSpec) {
    return getLocalPackageName(importSpec.getPath());
  }
  
  public static boolean isDot(@NotNull GoImportSpec importSpec) {
    GoImportSpecStub stub = importSpec.getStub();
    return stub != null ? stub.isDot() : importSpec.getDot() != null;
  }
  
  @NotNull
  public static String getPath(@NotNull GoImportSpec importSpec) {
    GoImportSpecStub stub = importSpec.getStub();
    return stub != null ? stub.getPath() : importSpec.getImportString().getPath();
  }

  public static String getAlias(@NotNull GoImportSpec importSpec) {
    GoImportSpecStub stub = importSpec.getStub();
    if (stub != null) {
      return stub.getAlias();
    }
    
    final PsiElement identifier = importSpec.getIdentifier();
    if (identifier != null) {
      return identifier.getText();
    }
    return importSpec.isDot() ? "." : null;
  }
  
  public static boolean shouldGoDeeper(@SuppressWarnings("UnusedParameters") GoImportSpec o) {
    return false;
  }

  public static boolean shouldGoDeeper(@SuppressWarnings("UnusedParameters") GoTypeSpec o) {
    return false;
  }

  public static boolean isForSideEffects(@NotNull GoImportSpec o) {
    return "_".equals(o.getAlias());
  }

  @NotNull
  public static String getPath(@NotNull GoImportString importString) {
    String text = importString.getText();
    if (!text.isEmpty()) {
      char quote = text.charAt(0);
      return isQuote(quote) ? StringUtil.unquoteString(text, quote) : text;
    }
    return "";
  }

  @NotNull
  public static TextRange getPathTextRange(@NotNull GoImportString importString) {
    String text = importString.getText();
    return !text.isEmpty() && isQuote(text.charAt(0)) ? TextRange.create(1, text.length() - 1) : TextRange.EMPTY_RANGE;
  }
  
  public static boolean isQuotedImportString(@NotNull String s) {
    return s.length() > 1 && isQuote(s.charAt(0)) && s.charAt(0) == s.charAt(s.length() - 1);
  }
  
  public static boolean isQuote(char ch) {
    return ch == '"' || ch == '\'' || ch == '`';
  }

  public static boolean isValidHost(@SuppressWarnings("UnusedParameters") @NotNull GoStringLiteral o) {
      return true;
  }

  @NotNull
  public static GoStringLiteralImpl updateText(@NotNull GoStringLiteral o, @NotNull String text) {
    if (text.length() > 2) {
      if (o.getString() != null) {
        StringBuilder outChars = new StringBuilder();
        GoStringLiteralEscaper.escapeString(text.substring(1, text.length()-1), outChars);
        outChars.insert(0, '"');
        outChars.append('"');
        text = outChars.toString();
      }
    }

    ASTNode valueNode = o.getNode().getFirstChildNode();
    assert valueNode instanceof LeafElement;

    ((LeafElement)valueNode).replaceWithText(text);
    return (GoStringLiteralImpl)o;
  }

  @NotNull
  public static GoStringLiteralEscaper createLiteralTextEscaper(@NotNull GoStringLiteral o) {
    return new GoStringLiteralEscaper(o);
  }

  public static boolean shouldGoDeeper(@SuppressWarnings("UnusedParameters") GoSignature o) {
    return false;
  }

  public static boolean prevDot(@Nullable PsiElement parent) {
    PsiElement prev = parent == null ? null : PsiTreeUtil.prevVisibleLeaf(parent);
    return prev instanceof LeafElement && ((LeafElement)prev).getElementType() == GoTypes.DOT;
  }

  @Nullable
  public static PsiReference getCallReference(@Nullable GoExpression first) {
    if (!(first instanceof GoCallExpr)) return null;
    GoExpression e = ((GoCallExpr)first).getExpression();
    if (e instanceof GoSelectorExpr) {
      GoExpression right = ((GoSelectorExpr)e).getRight();
      return right instanceof GoReferenceExpression ? right.getReference() : null;
    }
    GoReferenceExpression r = e instanceof GoReferenceExpression ? ((GoReferenceExpression)e) : PsiTreeUtil.getChildOfType(e, GoReferenceExpression.class);
    return (r != null ? r : e).getReference();
  }

  public static boolean isUnaryBitAndExpression(@Nullable PsiElement parent) {
    PsiElement grandParent = parent != null ? parent.getParent() : null;
    return grandParent instanceof GoUnaryExpr && ((GoUnaryExpr)grandParent).getBitAnd() != null;
  }
}
