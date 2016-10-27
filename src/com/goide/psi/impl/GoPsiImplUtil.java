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
import com.goide.psi.impl.imports.GoImportReferenceSet;
import com.goide.runconfig.testing.GoTestFinder;
import com.goide.sdk.GoPackageUtil;
import com.goide.sdk.GoSdkUtil;
import com.goide.stubs.*;
import com.goide.stubs.index.GoIdFilter;
import com.goide.stubs.index.GoMethodIndex;
import com.goide.util.GoStringLiteralEscaper;
import com.goide.util.GoUtil;
import com.intellij.codeInsight.highlighting.ReadWriteAccessDetector;
import com.intellij.diagnostic.AttachmentFactory;
import com.intellij.lang.ASTNode;
import com.intellij.lang.parser.GeneratedParserUtilBase;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.*;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.resolve.reference.impl.PsiMultiReference;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReferenceOwner;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.PsiFileReference;
import com.intellij.psi.impl.source.tree.LeafElement;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.tree.TokenSet;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.psi.util.PsiModificationTracker;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ObjectUtils;
import com.intellij.util.PathUtil;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.goide.psi.impl.GoLightType.*;
import static com.intellij.codeInsight.highlighting.ReadWriteAccessDetector.Access.*;
import static com.intellij.openapi.util.Conditions.equalTo;

public class GoPsiImplUtil {
  private static final Logger LOG = Logger.getInstance(GoPsiImplUtil.class);
  private static final Key<SmartPsiElementPointer<PsiElement>> CONTEXT = Key.create("CONTEXT");

  @NotNull
  public static SyntaxTraverser<PsiElement> goTraverser() {
    return SyntaxTraverser.psiTraverser().forceDisregardTypes(equalTo(GeneratedParserUtilBase.DUMMY_BLOCK));
  }

  public static boolean builtin(@Nullable PsiElement resolve) {
    return resolve != null && isBuiltinFile(resolve.getContainingFile());
  }

  public static boolean isConversionExpression(@Nullable GoExpression expression) {
    if (expression instanceof GoConversionExpr) {
      return true;
    }
    GoReferenceExpression referenceExpression = null;
    if (expression instanceof GoCallExpr) {
      referenceExpression = ObjectUtils.tryCast(((GoCallExpr)expression).getExpression(), GoReferenceExpression.class);
    }
    else if (expression instanceof GoBuiltinCallExpr) {
      referenceExpression = ((GoBuiltinCallExpr)expression).getReferenceExpression();
    }
    return referenceExpression != null && referenceExpression.resolve() instanceof GoTypeSpec;
  }

  public static boolean isPanic(@NotNull GoCallExpr o) {
    return stdLibCall(o, "panic");
  }

  public static boolean isRecover(@NotNull GoCallExpr o) {
    return stdLibCall(o, "recover");
  }

  private static boolean stdLibCall(@NotNull GoCallExpr o, @NotNull String name) {
    GoExpression e = o.getExpression();
    if (e.textMatches(name) && e instanceof GoReferenceExpression) {
      PsiReference reference = e.getReference();
      PsiElement resolve = reference != null ? reference.resolve() : null;
      if (!(resolve instanceof GoFunctionDeclaration)) return false;
      return isBuiltinFile(resolve.getContainingFile());
    }
    return false;
  }

  public static boolean isBuiltinFile(@Nullable PsiFile file) {
    return file instanceof GoFile
           && GoConstants.BUILTIN_PACKAGE_NAME.equals(((GoFile)file).getPackageName())
           && GoConstants.BUILTIN_PACKAGE_NAME.equals(((GoFile)file).getImportPath(false))
           && GoConstants.BUILTIN_FILE_NAME.equals(file.getName());
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
  public static PsiElement getContextElement(@Nullable ResolveState state) {
    SmartPsiElementPointer<PsiElement> context = state != null ? state.get(CONTEXT) : null;
    return context != null ? context.getElement() : null;
  }

  @NotNull
  public static ResolveState createContextOnElement(@NotNull PsiElement element) {
    return ResolveState.initial().put(CONTEXT, SmartPointerManager.getInstance(element.getProject()).createSmartPsiElementPointer(element));
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
    boolean createRef = PsiTreeUtil.getParentOfType(shortDeclaration, GoBlock.class, GoForStatement.class, GoIfStatement.class,
                                                    GoSwitchStatement.class, GoSelectStatement.class) instanceof GoBlock;
    return createRef ? new GoVarReference(o) : null;
  }

  @NotNull
  public static GoReference getReference(@NotNull GoReferenceExpression o) {
    return new GoReference(o);
  }

  @NotNull
  public static PsiReference getReference(@NotNull GoFieldName o) {
    GoFieldNameReference field = new GoFieldNameReference(o);
    GoReference ordinal = new GoReference(o);
    return new PsiMultiReference(new PsiReference[]{field, ordinal}, o) {
      @Override
      public PsiElement resolve() {
        PsiElement resolve = field.resolve();
        return resolve != null ? resolve : field.inStructTypeKey() ? null : ordinal.resolve();
      }

      @Override
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

  public static boolean processDeclarations(@NotNull GoCompositeElement o,
                                            @NotNull PsiScopeProcessor processor,
                                            @NotNull ResolveState state,
                                            PsiElement lastParent,
                                            @NotNull PsiElement place) {
    boolean isAncestor = PsiTreeUtil.isAncestor(o, place, false);
    if (o instanceof GoVarSpec) {
      return isAncestor || GoCompositeElementImpl.processDeclarationsDefault(o, processor, state, lastParent, place);
    }

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
  public static GoType getGoTypeInner(@NotNull GoReceiver o, @SuppressWarnings("UnusedParameters") @Nullable ResolveState context) {
    return o.getType();
  }

  @Nullable
  public static PsiElement getIdentifier(@SuppressWarnings("UnusedParameters") @NotNull GoAnonymousFieldDefinition o) {
    GoTypeReferenceExpression expression = o.getTypeReferenceExpression();
    return expression != null ? expression.getIdentifier() : null;
  }

  @Nullable
  public static String getName(@NotNull GoPackageClause packageClause) {
    GoPackageClauseStub stub = packageClause.getStub();
    if (stub != null) return stub.getName();
    PsiElement packageIdentifier = packageClause.getIdentifier();
    return packageIdentifier != null ? packageIdentifier.getText().trim() : null;
  }

  @Nullable
  public static String getName(@NotNull GoAnonymousFieldDefinition o) {
    PsiElement identifier = o.getIdentifier();
    return identifier != null ? identifier.getText() : null;
  }

  @Nullable
  public static GoTypeReferenceExpression getTypeReferenceExpression(@NotNull GoAnonymousFieldDefinition o) {
    return getTypeRefExpression(o.getType());
  }

  @Nullable
  public static GoType getGoTypeInner(@NotNull GoAnonymousFieldDefinition o,
                                      @SuppressWarnings("UnusedParameters") @Nullable ResolveState context) {
    return o.getType();
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
  public static GoType getReceiverType(@NotNull GoMethodDeclaration o) {
    GoReceiver receiver = o.getReceiver();
    return receiver == null ? null : receiver.getType();
  }

  // todo: merge with {@link this#getTypeRefExpression}
  @Nullable
  public static GoTypeReferenceExpression getTypeReference(@Nullable GoType o) {
    if (o instanceof GoPointerType) {
      return PsiTreeUtil.findChildOfAnyType(o, GoTypeReferenceExpression.class);
    }
    return o != null ? o.getTypeReferenceExpression() : null;
  }

  // todo: merge with {@link this#getTypeReference}
  @Nullable
  public static GoTypeReferenceExpression getTypeRefExpression(@Nullable GoType type) {
    GoType unwrap = unwrapPointerIfNeeded(type);
    return unwrap != null ? unwrap.getTypeReferenceExpression() : null;
  }

  @Nullable
  public static GoType getGoTypeInner(@NotNull GoConstDefinition o, @Nullable ResolveState context) {
    GoType fromSpec = findTypeInConstSpec(o);
    if (fromSpec != null) return fromSpec;
    // todo: stubs 
    return RecursionManager.doPreventingRecursion(o, true, (NullableComputable<GoType>)() -> {
      GoConstSpec prev = PsiTreeUtil.getPrevSiblingOfType(o.getParent(), GoConstSpec.class);
      while (prev != null) {
        GoType type = prev.getType();
        if (type != null) return type;
        GoExpression expr = ContainerUtil.getFirstItem(prev.getExpressionList()); // not sure about first
        if (expr != null) return expr.getGoType(context);
        prev = PsiTreeUtil.getPrevSiblingOfType(prev, GoConstSpec.class);
      }
      return null;
    });
  }

  @Nullable
  private static GoType findTypeInConstSpec(@NotNull GoConstDefinition o) {
    GoConstDefinitionStub stub = o.getStub();
    PsiElement parent = PsiTreeUtil.getStubOrPsiParent(o);
    if (!(parent instanceof GoConstSpec)) return null;
    GoConstSpec spec = (GoConstSpec)parent;
    GoType commonType = spec.getType();
    if (commonType != null) return commonType;
    List<GoConstDefinition> varList = spec.getConstDefinitionList();
    int i = Math.max(varList.indexOf(o), 0);
    if (stub != null) return null;
    GoConstSpecStub specStub = spec.getStub();
    List<GoExpression> es = specStub != null ? specStub.getExpressionList() : spec.getExpressionList(); // todo: move to constant spec
    if (es.size() <= i) return null;
    return es.get(i).getGoType(null);
  }

  @Nullable
  private static GoType unwrapParType(@NotNull GoExpression o, @Nullable ResolveState c) {
    GoType inner = getGoTypeInner(o, c);
    return inner instanceof GoParType ? ((GoParType)inner).getActualType() : inner;
  }

  @Nullable
  public static GoType getGoType(@NotNull GoExpression o, @Nullable ResolveState context) {
    return RecursionManager.doPreventingRecursion(o, true, () -> {
      if (context != null) return unwrapParType(o, context);
      return CachedValuesManager.getCachedValue(o, () -> CachedValueProvider.Result
        .create(unwrapParType(o, createContextOnElement(o)), PsiModificationTracker.MODIFICATION_COUNT));
    });
  }

  @Nullable
  private static GoType getGoTypeInner(@NotNull GoExpression o, @Nullable ResolveState context) {
    if (o instanceof GoUnaryExpr) {
      GoUnaryExpr u = (GoUnaryExpr)o;
      GoExpression e = u.getExpression();
      if (e == null) return null;
      GoType type = e.getGoType(context);
      GoType base = type == null || type.getTypeReferenceExpression() == null ? type : type.getUnderlyingType();
      if (u.getBitAnd() != null) return type != null ? new LightPointerType(type) : null;
      if (u.getSendChannel() != null) return base instanceof GoChannelType ? ((GoChannelType)base).getType() : type;
      if (u.getMul() != null) return base instanceof GoPointerType ? ((GoPointerType)base).getType() : type;
      return type;
    }
    if (o instanceof GoAddExpr) {
      return ((GoAddExpr)o).getLeft().getGoType(context);
    }
    if (o instanceof GoMulExpr) {
      GoExpression left = ((GoMulExpr)o).getLeft();
      if (!(left instanceof GoLiteral)) return left.getGoType(context);
      GoExpression right = ((GoBinaryExpr)o).getRight();
      if (right != null) return right.getGoType(context);
    }
    else if (o instanceof GoCompositeLit) {
      GoType type = ((GoCompositeLit)o).getType();
      if (type != null) return type;
      GoTypeReferenceExpression expression = ((GoCompositeLit)o).getTypeReferenceExpression();
      return expression != null ? expression.resolveType() : null;
    }
    else if (o instanceof GoFunctionLit) {
      return new LightFunctionType((GoFunctionLit)o);
    }
    else if (o instanceof GoBuiltinCallExpr) {
      String text = ((GoBuiltinCallExpr)o).getReferenceExpression().getText();
      boolean isNew = "new".equals(text);
      boolean isMake = "make".equals(text);
      if (isNew || isMake) {
        GoBuiltinArgumentList args = ((GoBuiltinCallExpr)o).getBuiltinArgumentList();
        GoType type = args != null ? args.getType() : null;
        return isNew ? type == null ? null : new LightPointerType(type) : type;
      }
    }
    else if (o instanceof GoCallExpr) {
      GoExpression e = ((GoCallExpr)o).getExpression();
      if (e instanceof GoReferenceExpression) { // todo: unify Type processing
        PsiReference ref = e.getReference();
        PsiElement resolve = ref != null ? ref.resolve() : null;
        if (((GoReferenceExpression)e).getQualifier() == null && "append".equals(((GoReferenceExpression)e).getIdentifier().getText())) {
          if (resolve instanceof GoFunctionDeclaration && isBuiltinFile(resolve.getContainingFile())) {
            List<GoExpression> l = ((GoCallExpr)o).getArgumentList().getExpressionList();
            GoExpression f = ContainerUtil.getFirstItem(l);
            return f == null ? null : getGoType(f, context);
          }
        }
        else if (resolve == e) { // C.call()
          return new GoCType(e);
        }
      }
      GoType type = ((GoCallExpr)o).getExpression().getGoType(context);
      if (type instanceof GoFunctionType) {
        return funcType(type);
      }
      GoType byRef = type != null && type.getTypeReferenceExpression() != null ? type.getUnderlyingType() : null;
      if (byRef instanceof GoFunctionType) {
        return funcType(byRef);
      }
      return type;
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
      GoType referenceType = getIndexedExpressionReferenceType((GoIndexOrSliceExpr)o, context);
      if (o.getNode().findChildByType(GoTypes.COLON) != null) return referenceType; // means slice expression, todo: extract if needed
      GoType type = referenceType != null ? referenceType.getUnderlyingType() : null;
      if (type instanceof GoMapType) {
        List<GoType> list = ((GoMapType)type).getTypeList();
        if (list.size() == 2) {
          return list.get(1);
        }
      }
      else if (type instanceof GoArrayOrSliceType) {
        return ((GoArrayOrSliceType)type).getType();
      }
      else if (GoTypeUtil.isString(type)) {
        return getBuiltinType("byte", o);
      }
    }
    else if (o instanceof GoTypeAssertionExpr) {
      return ((GoTypeAssertionExpr)o).getType();
    }
    else if (o instanceof GoConversionExpr) {
      return ((GoConversionExpr)o).getType();
    }
    else if (o instanceof GoStringLiteral) {
      return getBuiltinType("string", o);
    }
    else if (o instanceof GoLiteral) {
      GoLiteral l = (GoLiteral)o;
      if (l.getChar() != null) return getBuiltinType("rune", o);
      if (l.getInt() != null || l.getHex() != null || ((GoLiteral)o).getOct() != null) return getBuiltinType("int", o);
      if (l.getFloat() != null) return getBuiltinType("float64", o);
      if (l.getFloati() != null) return getBuiltinType("complex64", o);
      if (l.getDecimali() != null) return getBuiltinType("complex128", o);
    }
    else if (o instanceof GoConditionalExpr) {
      return getBuiltinType("bool", o);
    }
    return null;
  }

  @Nullable
  public static GoType getIndexedExpressionReferenceType(@NotNull GoIndexOrSliceExpr o, @Nullable ResolveState context) {
    GoExpression first = ContainerUtil.getFirstItem(o.getExpressionList());
    // todo: calculate type for indexed expressions only
    // https://golang.org/ref/spec#Index_expressions – a[x] is shorthand for (*a)[x]
    return unwrapPointerIfNeeded(first != null ? first.getGoType(context) : null);
  }

  @Nullable
  public static GoType unwrapPointerIfNeeded(@Nullable GoType type) {
    return type instanceof GoPointerType ? ((GoPointerType)type).getType() : type;
  }

  @Nullable
  public static GoType getBuiltinType(@NotNull String name, @NotNull PsiElement context) {
    GoFile builtin = GoSdkUtil.findBuiltinFile(context);
    if (builtin != null) {
      GoTypeSpec spec = ContainerUtil.find(builtin.getTypes(), spec1 -> name.equals(spec1.getName()));
      if (spec != null) {
        return spec.getSpecType().getType(); // todo
      }
    }
    return null;
  }

  @Nullable
  private static GoType typeFromRefOrType(@Nullable GoType t) {
    if (t == null) return null;
    GoTypeReferenceExpression tr = t.getTypeReferenceExpression();
    return tr != null ? tr.resolveType() : t;
  }

  @Nullable
  public static GoType typeOrParameterType(@NotNull GoTypeOwner resolve, @Nullable ResolveState context) {
    GoType type = resolve.getGoType(context);
    if (resolve instanceof GoParamDefinition && ((GoParamDefinition)resolve).isVariadic()) {
      return type == null ? null : new LightArrayType(type);
    }
    if (resolve instanceof GoSignatureOwner) {
      return new LightFunctionType((GoSignatureOwner)resolve);
    }
    return type;
  }

  @Nullable
  public static PsiElement resolve(@NotNull GoReferenceExpression o) { // todo: replace with default method in GoReferenceExpressionBase
    return o.getReference().resolve();
  }

  @Nullable
  public static PsiElement resolve(@NotNull GoTypeReferenceExpression o) { // todo: replace with default method in GoReferenceExpressionBase
    return o.getReference().resolve();
  }

  @Nullable
  public static PsiElement resolve(@NotNull GoFieldName o) { // todo: replace with default method in GoReferenceExpressionBase
    return o.getReference().resolve();
  }

  @Nullable
  public static GoType getLiteralType(@Nullable PsiElement context, boolean considerLiteralValue) {
    GoCompositeLit lit = PsiTreeUtil.getNonStrictParentOfType(context, GoCompositeLit.class);
    if (lit == null) {
      return null;
    }
    GoType type = lit.getType();
    if (type == null) {
      GoTypeReferenceExpression ref = lit.getTypeReferenceExpression();
      GoType resolve = ref != null ? ref.resolveType() : null;
      type = resolve != null ? resolve.getUnderlyingType() : null;
    }
    if (!considerLiteralValue) {
      return type;
    }
    GoValue parentGoValue = getParentGoValue(context);
    PsiElement literalValue = PsiTreeUtil.getParentOfType(context, GoLiteralValue.class);
    while (literalValue != null) {
      if (literalValue == lit) break;
      if (literalValue instanceof GoLiteralValue) {
        type = calcLiteralType(parentGoValue, type);
      }
      literalValue = literalValue.getParent();
    }
    return type;
  }

  @Nullable
  public static GoValue getParentGoValue(@NotNull PsiElement element) {
    PsiElement place = element;
    while ((place = PsiTreeUtil.getParentOfType(place, GoLiteralValue.class)) != null) {
      if (place.getParent() instanceof GoValue) {
        return (GoValue)place.getParent();
      }
    }
    return null;
  }

  // todo: rethink and unify this algorithm
  @Nullable
  private static GoType calcLiteralType(@Nullable GoValue parentGoValue, @Nullable GoType type) {
    if (type == null) return null;
    type = findLiteralType(parentGoValue, type);

    if (type instanceof GoParType) {
      type = ((GoParType)type).getActualType();
    }

    if (type != null && type.getTypeReferenceExpression() != null) {
      type = type.getUnderlyingType();
    }

    if (type instanceof GoPointerType) {
      GoType inner = ((GoPointerType)type).getType();
      if (inner != null && inner.getTypeReferenceExpression() != null) {
        type = inner.getUnderlyingType();
      }
    }

    return type instanceof GoSpecType ? ((GoSpecType)type).getType() : type;
  }

  private static GoType findLiteralType(@Nullable GoValue parentGoValue, @Nullable GoType type) {
    boolean inValue = parentGoValue != null;
    if (inValue && type instanceof GoArrayOrSliceType) {
      type = ((GoArrayOrSliceType)type).getType();
    }
    else if (type instanceof GoMapType) {
      type = inValue ? ((GoMapType)type).getValueType() : ((GoMapType)type).getKeyType();
    }
    else if (inValue && type instanceof GoSpecType) {
      GoType inner = ((GoSpecType)type).getType();
      if (inner instanceof GoArrayOrSliceType) {
        type = ((GoArrayOrSliceType)inner).getType();
      }
      else if (inner instanceof GoStructType) {
        GoKey key = PsiTreeUtil.getPrevSiblingOfType(parentGoValue, GoKey.class);
        GoFieldName field = key != null ? key.getFieldName() : null;
        PsiElement resolve = field != null ? field.resolve() : null;
        if (resolve instanceof GoFieldDefinition) {
          type = PsiTreeUtil.getNextSiblingOfType(resolve, GoType.class);
        }
      }
    }
    return type;
  }

  @Nullable
  public static GoType resolveType(@NotNull GoTypeReferenceExpression expression) {
    PsiElement resolve = expression.resolve();
    if (resolve instanceof GoTypeSpec) return ((GoTypeSpec)resolve).getSpecType();
    // hacky C resolve
    return resolve == expression ? new GoCType(expression) : null;
  }

  public static boolean isVariadic(@NotNull GoParamDefinition o) {
    PsiElement parent = o.getParent();
    return parent instanceof GoParameterDeclaration && ((GoParameterDeclaration)parent).isVariadic();
  }

  public static boolean isVariadic(@NotNull GoParameterDeclaration o) {
    GoParameterDeclarationStub stub = o.getStub();
    return stub != null ? stub.isVariadic() : o.getTripleDot() != null;
  }

  public static boolean hasVariadic(@NotNull GoArgumentList argumentList) {
    return argumentList.getTripleDot() != null;
  }

  @Nullable
  public static GoType getGoTypeInner(@NotNull GoTypeSpec o, @SuppressWarnings("UnusedParameters") @Nullable ResolveState context) {
    return o.getSpecType();
  }

  @Nullable
  public static GoType getGoTypeInner(@NotNull GoVarDefinition o, @Nullable ResolveState context) {
    // see http://golang.org/ref/spec#RangeClause
    PsiElement parent = PsiTreeUtil.getStubOrPsiParent(o);
    if (parent instanceof GoRangeClause) {
      return processRangeClause(o, (GoRangeClause)parent, context);
    }
    if (parent instanceof GoVarSpec) {
      return findTypeInVarSpec(o, context);
    }
    GoCompositeLit literal = PsiTreeUtil.getNextSiblingOfType(o, GoCompositeLit.class);
    if (literal != null) {
      return literal.getType();
    }
    GoType siblingType = o.findSiblingType();
    if (siblingType != null) return siblingType;

    if (parent instanceof GoTypeSwitchGuard) {
      GoTypeSwitchStatement switchStatement = ObjectUtils.tryCast(parent.getParent(), GoTypeSwitchStatement.class);
      if (switchStatement != null) {
        GoTypeCaseClause typeCase = getTypeCaseClause(getContextElement(context), switchStatement);
        if (typeCase != null) {
          return typeCase.getDefault() != null ? ((GoTypeSwitchGuard)parent).getExpression().getGoType(context) : typeCase.getType();
        }
        return ((GoTypeSwitchGuard)parent).getExpression().getGoType(null);
      }
    }
    return null;
  }

  public static boolean isVoid(@NotNull GoResult result) {
    GoType type = result.getType();
    if (type != null) return false;
    GoParameters parameters = result.getParameters();
    return parameters == null || parameters.getParameterDeclarationList().isEmpty();
  }

  @Nullable
  private static GoTypeCaseClause getTypeCaseClause(@Nullable PsiElement context, @NotNull GoTypeSwitchStatement switchStatement) {
    return SyntaxTraverser.psiApi().parents(context).takeWhile(Conditions.notEqualTo(switchStatement))
      .filter(GoTypeCaseClause.class).last();
  }

  @Nullable
  private static GoType findTypeInVarSpec(@NotNull GoVarDefinition o, @Nullable ResolveState context) {
    GoVarSpec parent = (GoVarSpec)PsiTreeUtil.getStubOrPsiParent(o);
    if (parent == null) return null;
    GoType commonType = parent.getType();
    if (commonType != null) return commonType;
    List<GoVarDefinition> varList = parent.getVarDefinitionList();
    int i = varList.indexOf(o);
    i = i == -1 ? 0 : i;
    List<GoExpression> exprs = parent.getRightExpressionsList();
    if (exprs.size() == 1 && exprs.get(0) instanceof GoCallExpr) {
      GoExpression call = exprs.get(0);
      GoType fromCall = call.getGoType(context);
      boolean canDecouple = varList.size() > 1;
      GoType underlyingType = canDecouple && fromCall instanceof GoSpecType ? ((GoSpecType)fromCall).getType() : fromCall;
      GoType byRef = typeFromRefOrType(underlyingType);
      GoType type = funcType(canDecouple && byRef instanceof GoSpecType ? ((GoSpecType)byRef).getType() : byRef);
      if (type == null) return fromCall;
      if (type instanceof GoTypeList) {
        if (((GoTypeList)type).getTypeList().size() > i) {
          return ((GoTypeList)type).getTypeList().get(i);
        }
      }
      return type;
    }
    if (exprs.size() <= i) return null;
    return exprs.get(i).getGoType(context);
  }

  @Nullable
  private static GoType funcType(@Nullable GoType type) {
    if (type instanceof GoFunctionType) {
      GoSignature signature = ((GoFunctionType)type).getSignature();
      GoResult result = signature != null ? signature.getResult() : null;
      if (result != null) {
        GoType rt = result.getType();
        if (rt != null) return rt;
        GoParameters parameters = result.getParameters();
        if (parameters != null) {
          List<GoParameterDeclaration> list = parameters.getParameterDeclarationList();
          List<GoType> types = ContainerUtil.newArrayListWithCapacity(list.size());
          for (GoParameterDeclaration declaration : list) {
            GoType declarationType = declaration.getType();
            for (GoParamDefinition ignored : declaration.getParamDefinitionList()) {
              types.add(declarationType);
            }
          }
          if (!types.isEmpty()) {
            return types.size() == 1 ? types.get(0) : new LightTypeList(parameters, types);
          }
        }
      }
      return null;
    }
    return type;
  }

  /**
   * https://golang.org/ref/spec#RangeClause
   */
  @Nullable
  private static GoType processRangeClause(@NotNull GoVarDefinition o, @NotNull GoRangeClause parent, @Nullable ResolveState context) {
    GoExpression rangeExpression = parent.getRangeExpression();
    if (rangeExpression != null) {
      List<GoVarDefinition> varList = parent.getVarDefinitionList();
      GoType type = unwrapIfNeeded(rangeExpression.getGoType(context));
      if (type instanceof GoChannelType) return ((GoChannelType)type).getType();
      int i = varList.indexOf(o);
      i = i == -1 ? 0 : i;
      if (type instanceof GoArrayOrSliceType && i == 1) return ((GoArrayOrSliceType)type).getType();
      if (type instanceof GoMapType) {
        List<GoType> list = ((GoMapType)type).getTypeList();
        if (i == 0) return ContainerUtil.getFirstItem(list);
        if (i == 1) return ContainerUtil.getLastItem(list);
      }
      if (GoTypeUtil.isString(type)) {
        return getBuiltinType("int32", o);
      }
    }
    return null;
  }

  @Nullable
  private static GoType unwrapIfNeeded(@Nullable GoType type) {
    type = unwrapPointerIfNeeded(type);
    return type != null ? type.getUnderlyingType() : null;
  }

  @NotNull
  public static GoType getActualType(@NotNull GoParType o) {
    return ObjectUtils.notNull(SyntaxTraverser.psiTraverser(o).filter(Conditions.notInstanceOf(GoParType.class))
                                 .filter(GoType.class).first(), o.getType());
  }

  @NotNull
  public static String getText(@Nullable GoType o) {
    if (o == null) return "";
    if (o instanceof GoPointerType && ((GoPointerType)o).getType() instanceof GoSpecType) {
      return "*" + getText(((GoPointerType)o).getType());
    }
    if (o instanceof GoSpecType) {
      String fqn = getFqn(getTypeSpecSafe(o));
      if (fqn != null) {
        return fqn;
      }
    }
    else if (o instanceof GoStructType) {
      return ((GoStructType)o).getFieldDeclarationList().isEmpty() ? "struct{}" : "struct {...}";
    }
    else if (o instanceof GoInterfaceType) {
      return ((GoInterfaceType)o).getMethodSpecList().isEmpty() ? "interface{}" : "interface {...}";
    }
    String text = o.getText();
    if (text == null) return "";
    return text.replaceAll("\\s+", " ");
  }

  @Nullable
  private static String getFqn(@Nullable GoTypeSpec typeSpec) {
    if (typeSpec == null) return null;
    String name = typeSpec.getName();
    GoFile file = typeSpec.getContainingFile();
    String packageName = file.getPackageName();
    if (name != null) {
      return !isBuiltinFile(file) ? getFqn(packageName, name) : name;
    }
    return null;
  }

  public static String getFqn(@Nullable String packageName, @NotNull String elementName) {
    return StringUtil.isNotEmpty(packageName) ? packageName + "." + elementName : elementName;
  }

  @NotNull
  public static List<GoMethodSpec> getMethods(@NotNull GoInterfaceType o) {
    return ContainerUtil.filter(o.getMethodSpecList(), spec -> spec.getIdentifier() != null);
  }

  @NotNull
  public static List<GoTypeReferenceExpression> getBaseTypesReferences(@NotNull GoInterfaceType o) {
    List<GoTypeReferenceExpression> refs = ContainerUtil.newArrayList();
    o.accept(new GoRecursiveVisitor() {
      @Override
      public void visitMethodSpec(@NotNull GoMethodSpec o) {
        ContainerUtil.addIfNotNull(refs, o.getTypeReferenceExpression());
      }
    });
    return refs;
  }

  @NotNull
  public static List<GoMethodDeclaration> getMethods(@NotNull GoTypeSpec o) {
    return CachedValuesManager.getCachedValue(o, () -> {
      // todo[zolotov]: implement package modification tracker
      return CachedValueProvider.Result.create(calcMethods(o), PsiModificationTracker.MODIFICATION_COUNT);
    });
  }

  public static boolean allowed(@NotNull PsiFile declarationFile, @Nullable PsiFile referenceFile, @Nullable Module contextModule) {
    if (!(declarationFile instanceof GoFile)) {
      return false;
    }
    VirtualFile referenceVirtualFile = referenceFile != null ? referenceFile.getOriginalFile().getVirtualFile() : null;
    if (!allowed(declarationFile.getVirtualFile(), referenceVirtualFile)) {
      return false;
    }
    if (GoConstants.DOCUMENTATION.equals(((GoFile)declarationFile).getPackageName())) {
      return false;
    }
    return GoUtil.matchedForModuleBuildTarget(declarationFile, contextModule);
  }

  public static boolean allowed(@Nullable VirtualFile declarationFile, @Nullable VirtualFile referenceFile) {
    if (declarationFile == null) {
      return true;
    }
    if (GoUtil.fileToIgnore(declarationFile.getName())) {
      return false;
    }
    // it's not a test or context file is also test from the same package
    return referenceFile == null
           || !GoTestFinder.isTestFile(declarationFile)
           || GoTestFinder.isTestFile(referenceFile) && Comparing.equal(referenceFile.getParent(), declarationFile.getParent());
  }

  static boolean processNamedElements(@NotNull PsiScopeProcessor processor,
                                      @NotNull ResolveState state,
                                      @NotNull Collection<? extends GoNamedElement> elements,
                                      boolean localResolve) {
    //noinspection unchecked
    return processNamedElements(processor, state, elements, Condition.TRUE, localResolve, false);
  }

  static boolean processNamedElements(@NotNull PsiScopeProcessor processor,
                                      @NotNull ResolveState state,
                                      @NotNull Collection<? extends GoNamedElement> elements,
                                      boolean localResolve,
                                      boolean checkContainingFile) {
    //noinspection unchecked
    return processNamedElements(processor, state, elements, Condition.TRUE, localResolve, checkContainingFile);
  }

  static boolean processNamedElements(@NotNull PsiScopeProcessor processor,
                                      @NotNull ResolveState state,
                                      @NotNull Collection<? extends GoNamedElement> elements,
                                      @NotNull Condition<GoNamedElement> condition,
                                      boolean localResolve,
                                      boolean checkContainingFile) {
    PsiFile contextFile = checkContainingFile ? GoReference.getContextFile(state) : null;
    Module module = contextFile != null ? ModuleUtilCore.findModuleForPsiElement(contextFile) : null;
    for (GoNamedElement definition : elements) {
      if (!condition.value(definition)) continue;
      if (!definition.isValid() || checkContainingFile && !allowed(definition.getContainingFile(), contextFile, module)) continue;
      if ((localResolve || definition.isPublic()) && !processor.execute(definition, state)) return false;
    }
    return true;
  }

  public static boolean processSignatureOwner(@NotNull GoSignatureOwner o, @NotNull GoScopeProcessorBase processor) {
    GoSignature signature = o.getSignature();
    if (signature == null) return true;
    if (!processParameters(processor, signature.getParameters())) return false;
    GoResult result = signature.getResult();
    GoParameters resultParameters = result != null ? result.getParameters() : null;
    return resultParameters == null || processParameters(processor, resultParameters);
  }

  private static boolean processParameters(@NotNull GoScopeProcessorBase processor, @NotNull GoParameters parameters) {
    for (GoParameterDeclaration declaration : parameters.getParameterDeclarationList()) {
      if (!processNamedElements(processor, ResolveState.initial(), declaration.getParamDefinitionList(), true)) return false;
    }
    return true;
  }

  @NotNull
  public static String joinPsiElementText(List<? extends PsiElement> items) {
    return StringUtil.join(items, PsiElement::getText, ", ");
  }

  @Nullable
  public static PsiElement getBreakStatementOwner(@NotNull PsiElement breakStatement) {
    GoCompositeElement owner = PsiTreeUtil.getParentOfType(breakStatement, GoSwitchStatement.class, GoForStatement.class,
                                                           GoSelectStatement.class, GoFunctionLit.class);
    return owner instanceof GoFunctionLit ? null : owner;
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
      GlobalSearchScope scope = GoPackageUtil.packageScope((GoFile)file);
      Collection<GoMethodDeclaration> declarations = GoMethodIndex.find(key, project, scope, GoIdFilter.getFilesFilter(scope));
      return ContainerUtil.newArrayList(declarations);
    }
    return Collections.emptyList();
  }

  @NotNull
  public static GoType getUnderlyingType(@NotNull GoType o) {
    GoType type = RecursionManager.doPreventingRecursion(o, true, () -> getTypeInner(o));
    return ObjectUtils.notNull(type, o);
  }

  @NotNull
  private static GoType getTypeInner(@NotNull GoType o) {
    if (o instanceof GoArrayOrSliceType
        | o instanceof GoStructType
        | o instanceof GoPointerType
        | o instanceof GoFunctionType
        | o instanceof GoInterfaceType
        | o instanceof GoMapType
        | o instanceof GoChannelType) {
      return o;
    }

    if (o instanceof GoParType) return ((GoParType)o).getActualType();

    if (o instanceof GoSpecType) {
      GoType type = ((GoSpecType)o).getType();
      return type != null ? type.getUnderlyingType() : o;
    }

    if (builtin(o)) return o;

    GoTypeReferenceExpression e = o.getTypeReferenceExpression();
    GoType byRef = e == null ? null : e.resolveType();
    if (byRef != null) {
      return byRef.getUnderlyingType();
    }

    return o;
  }

  @Nullable
  public static GoType getGoTypeInner(@NotNull GoSignatureOwner o, @SuppressWarnings("UnusedParameters") @Nullable ResolveState context) {
    GoSignature signature = o.getSignature();
    GoResult result = signature != null ? signature.getResult() : null;
    if (result != null) {
      GoType type = result.getType();
      if (type instanceof GoTypeList && ((GoTypeList)type).getTypeList().size() == 1) {
        return ((GoTypeList)type).getTypeList().get(0);
      }
      if (type != null) return type;
      GoParameters parameters = result.getParameters();
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
        return new LightTypeList(parameters, composite);
      }
    }
    return null;
  }

  @NotNull
  public static GoImportSpec addImport(@NotNull GoImportList importList, @NotNull String packagePath, @Nullable String alias) {
    Project project = importList.getProject();
    GoImportDeclaration newDeclaration = GoElementFactory.createImportDeclaration(project, packagePath, alias, false);
    List<GoImportDeclaration> existingImports = importList.getImportDeclarationList();
    for (int i = existingImports.size() - 1; i >= 0; i--) {
      GoImportDeclaration existingImport = existingImports.get(i);
      List<GoImportSpec> importSpecList = existingImport.getImportSpecList();
      if (importSpecList.isEmpty()) {
        continue;
      }
      if (existingImport.getRparen() == null && importSpecList.get(0).isCImport()) {
        continue;
      }
      return existingImport.addImportSpec(packagePath, alias);
    }
    return addImportDeclaration(importList, newDeclaration);
  }

  @NotNull
  private static GoImportSpec addImportDeclaration(@NotNull GoImportList importList, @NotNull GoImportDeclaration newImportDeclaration) {
    GoImportDeclaration lastImport = ContainerUtil.getLastItem(importList.getImportDeclarationList());
    GoImportDeclaration importDeclaration = (GoImportDeclaration)importList.addAfter(newImportDeclaration, lastImport);
    PsiElement importListNextSibling = importList.getNextSibling();
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
    if (rParen == null) {
      GoImportDeclaration newDeclaration = GoElementFactory.createEmptyImportDeclaration(declaration.getProject());
      for (GoImportSpec spec : declaration.getImportSpecList()) {
        newDeclaration.addImportSpec(spec.getPath(), spec.getAlias());
      }
      declaration = (GoImportDeclaration)declaration.replace(newDeclaration);
      LOG.assertTrue(declaration.getRparen() != null);
      return declaration.addImportSpec(packagePath, alias);
    }
    declaration.addBefore(GoElementFactory.createNewLine(declaration.getProject()), rParen);
    GoImportSpec newImportSpace = GoElementFactory.createImportSpec(declaration.getProject(), packagePath, alias);
    GoImportSpec spec = (GoImportSpec)declaration.addBefore(newImportSpace, rParen);
    declaration.addBefore(GoElementFactory.createNewLine(declaration.getProject()), rParen);
    return spec;
  }

  public static String getLocalPackageName(@NotNull String importPath) {
    String fileName = !StringUtil.endsWithChar(importPath, '/') && !StringUtil.endsWithChar(importPath, '\\')
                      ? PathUtil.getFileName(importPath)
                      : "";
    StringBuilder name = null;
    for (int i = 0; i < fileName.length(); i++) {
      char c = fileName.charAt(i);
      if (!(Character.isLetter(c) || c == '_' || i != 0 && Character.isDigit(c))) {
        if (name == null) {
          name = new StringBuilder(fileName.length());
          name.append(fileName, 0, i);
        }
        name.append('_');
      }
      else if (name != null) {
        name.append(c);
      }
    }
    return name == null ? fileName : name.toString();
  }

  public static String getLocalPackageName(@NotNull GoImportSpec importSpec) {
    return getLocalPackageName(importSpec.getPath());
  }

  public static boolean isCImport(@NotNull GoImportSpec importSpec) {
    return GoConstants.C_PATH.equals(importSpec.getPath());
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

  public static String getName(@NotNull GoImportSpec importSpec) {
    return getAlias(importSpec);
  }

  public static String getAlias(@NotNull GoImportSpec importSpec) {
    GoImportSpecStub stub = importSpec.getStub();
    if (stub != null) {
      return stub.getAlias();
    }

    PsiElement identifier = importSpec.getIdentifier();
    if (identifier != null) {
      return identifier.getText();
    }
    return importSpec.isDot() ? "." : null;
  }

  @Nullable
  public static String getImportQualifierToUseInFile(@Nullable GoImportSpec importSpec, @Nullable String defaultValue) {
    if (importSpec == null || importSpec.isForSideEffects()) {
      return null;
    }
    if (importSpec.isDot()) {
      return "";
    }
    String alias = importSpec.getAlias();
    if (alias != null) {
      return alias;
    }
    return defaultValue != null ? defaultValue : importSpec.getLocalPackageName();
  }

  public static boolean shouldGoDeeper(@SuppressWarnings("UnusedParameters") GoImportSpec o) {
    return false;
  }

  public static boolean shouldGoDeeper(@SuppressWarnings("UnusedParameters") GoTypeSpec o) {
    return false;
  }

  public static boolean shouldGoDeeper(@NotNull GoType o) {
    return o instanceof GoInterfaceType || o instanceof GoStructType;
  }

  public static boolean isForSideEffects(@NotNull GoImportSpec o) {
    return "_".equals(o.getAlias());
  }

  @NotNull
  public static String getPath(@NotNull GoImportString o) {
    return o.getStringLiteral().getDecodedText();
  }

  @NotNull
  public static String unquote(@Nullable String s) {
    if (StringUtil.isEmpty(s)) return "";
    char quote = s.charAt(0);
    int startOffset = isQuote(quote) ? 1 : 0;
    int endOffset = s.length();
    if (s.length() > 1) {
      char lastChar = s.charAt(s.length() - 1);
      if (isQuote(quote) && lastChar == quote) {
        endOffset = s.length() - 1;
      }
      if (!isQuote(quote) && isQuote(lastChar)) {
        endOffset = s.length() - 1;
      }
    }
    return s.substring(startOffset, endOffset);
  }

  @NotNull
  public static TextRange getPathTextRange(@NotNull GoImportString importString) {
    String text = importString.getText();
    return !text.isEmpty() && isQuote(text.charAt(0)) ? TextRange.create(1, text.length() - 1) : TextRange.EMPTY_RANGE;
  }

  public static boolean isQuotedImportString(@NotNull String s) {
    return s.length() > 1 && isQuote(s.charAt(0)) && s.charAt(0) == s.charAt(s.length() - 1);
  }

  private static boolean isQuote(char ch) {
    return ch == '"' || ch == '\'' || ch == '`';
  }

  public static boolean isValidHost(@NotNull GoStringLiteral o) {
    return PsiTreeUtil.getParentOfType(o, GoImportString.class) == null;
  }

  @NotNull
  public static GoStringLiteralImpl updateText(@NotNull GoStringLiteral o, @NotNull String text) {
    if (text.length() > 2) {
      if (o.getString() != null) {
        StringBuilder outChars = new StringBuilder();
        GoStringLiteralEscaper.escapeString(text.substring(1, text.length() - 1), outChars);
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

  public static boolean prevDot(@Nullable PsiElement e) {
    PsiElement prev = e == null ? null : PsiTreeUtil.prevVisibleLeaf(e);
    return prev instanceof LeafElement && ((LeafElement)prev).getElementType() == GoTypes.DOT;
  }

  @Nullable
  public static GoSignatureOwner resolveCall(@Nullable GoExpression call) {
    return ObjectUtils.tryCast(resolveCallRaw(call), GoSignatureOwner.class);
  }

  public static PsiElement resolveCallRaw(@Nullable GoExpression call) {
    if (!(call instanceof GoCallExpr)) return null;
    GoExpression e = ((GoCallExpr)call).getExpression();
    if (e instanceof GoSelectorExpr) {
      GoExpression right = ((GoSelectorExpr)e).getRight();
      PsiReference reference = right instanceof GoReferenceExpression ? right.getReference() : null;
      return reference != null ? reference.resolve() : null;
    }
    if (e instanceof GoCallExpr) {
      GoSignatureOwner resolve = resolveCall(e);
      if (resolve != null) {
        GoSignature signature = resolve.getSignature();
        GoResult result = signature != null ? signature.getResult() : null;
        return result != null ? result.getType() : null;
      }
      return null;
    }
    if (e instanceof GoFunctionLit) {
      return e;
    }
    GoReferenceExpression r = e instanceof GoReferenceExpression
                              ? (GoReferenceExpression)e
                              : PsiTreeUtil.getChildOfType(e, GoReferenceExpression.class);
    PsiReference reference = (r != null ? r : e).getReference();
    return reference != null ? reference.resolve() : null;
  }

  public static boolean isUnaryBitAndExpression(@Nullable PsiElement parent) {
    PsiElement grandParent = parent != null ? parent.getParent() : null;
    return grandParent instanceof GoUnaryExpr && ((GoUnaryExpr)grandParent).getBitAnd() != null;
  }

  @NotNull
  public static GoVarSpec addSpec(@NotNull GoVarDeclaration declaration,
                                  @NotNull String name,
                                  @Nullable String type,
                                  @Nullable String value,
                                  @Nullable GoVarSpec specAnchor) {
    Project project = declaration.getProject();
    GoVarSpec newSpec = GoElementFactory.createVarSpec(project, name, type, value);
    PsiElement rParen = declaration.getRparen();
    if (rParen == null) {
      GoVarSpec item = ContainerUtil.getFirstItem(declaration.getVarSpecList());
      assert item != null;
      boolean updateAnchor = specAnchor == item;
      declaration = (GoVarDeclaration)declaration.replace(GoElementFactory.createVarDeclaration(project, "(" + item.getText() + ")"));
      rParen = declaration.getRparen();
      if (updateAnchor) {
        specAnchor = ContainerUtil.getFirstItem(declaration.getVarSpecList());
      }
    }

    assert rParen != null;
    PsiElement anchor = ObjectUtils.notNull(specAnchor, rParen);
    if (!hasNewLineBefore(anchor)) {
      declaration.addBefore(GoElementFactory.createNewLine(declaration.getProject()), anchor);
    }
    GoVarSpec spec = (GoVarSpec)declaration.addBefore(newSpec, anchor);
    declaration.addBefore(GoElementFactory.createNewLine(declaration.getProject()), anchor);
    return spec;
  }

  @NotNull
  public static GoConstSpec addSpec(@NotNull GoConstDeclaration declaration,
                                    @NotNull String name,
                                    @Nullable String type,
                                    @Nullable String value,
                                    @Nullable GoConstSpec specAnchor) {
    Project project = declaration.getProject();
    GoConstSpec newSpec = GoElementFactory.createConstSpec(project, name, type, value);
    PsiElement rParen = declaration.getRparen();
    if (rParen == null) {
      GoConstSpec item = ContainerUtil.getFirstItem(declaration.getConstSpecList());
      assert item != null;
      boolean updateAnchor = specAnchor == item;
      declaration = (GoConstDeclaration)declaration.replace(GoElementFactory.createConstDeclaration(project, "(" + item.getText() + ")"));
      rParen = declaration.getRparen();
      if (updateAnchor) {
        specAnchor = ContainerUtil.getFirstItem(declaration.getConstSpecList());
      }
    }

    assert rParen != null;
    PsiElement anchor = ObjectUtils.notNull(specAnchor, rParen);
    if (!hasNewLineBefore(anchor)) {
      declaration.addBefore(GoElementFactory.createNewLine(declaration.getProject()), anchor);
    }
    GoConstSpec spec = (GoConstSpec)declaration.addBefore(newSpec, anchor);
    declaration.addBefore(GoElementFactory.createNewLine(declaration.getProject()), anchor);
    return spec;
  }

  public static void deleteSpec(@NotNull GoVarDeclaration declaration, @NotNull GoVarSpec specToDelete) {
    List<GoVarSpec> specList = declaration.getVarSpecList();
    int index = specList.indexOf(specToDelete);
    assert index >= 0;
    if (specList.size() == 1) {
      declaration.delete();
      return;
    }
    specToDelete.delete();
  }

  public static void deleteSpec(@NotNull GoConstDeclaration declaration, @NotNull GoConstSpec specToDelete) {
    List<GoConstSpec> specList = declaration.getConstSpecList();
    int index = specList.indexOf(specToDelete);
    assert index >= 0;
    if (specList.size() == 1) {
      declaration.delete();
      return;
    }
    specToDelete.delete();
  }

  public static void deleteExpressionFromAssignment(@NotNull GoAssignmentStatement assignment,
                                                    @NotNull GoExpression expressionToDelete) {
    GoExpression expressionValue = getRightExpression(assignment, expressionToDelete);
    if (expressionValue != null) {
      if (assignment.getExpressionList().size() == 1) {
        assignment.delete();
      }
      else {
        deleteElementFromCommaSeparatedList(expressionToDelete);
        deleteElementFromCommaSeparatedList(expressionValue);
      }
    }
  }

  public static void deleteDefinition(@NotNull GoVarSpec spec, @NotNull GoVarDefinition definitionToDelete) {
    List<GoVarDefinition> definitionList = spec.getVarDefinitionList();
    int index = definitionList.indexOf(definitionToDelete);
    assert index >= 0;
    if (definitionList.size() == 1) {
      PsiElement parent = spec.getParent();
      if (parent instanceof GoVarDeclaration) {
        ((GoVarDeclaration)parent).deleteSpec(spec);
      }
      else {
        spec.delete();
      }
      return;
    }

    GoExpression value = definitionToDelete.getValue();
    if (value != null && spec.getRightExpressionsList().size() <= 1) {
      PsiElement assign = spec.getAssign();
      if (assign != null) {
        assign.delete();
      }
    }
    deleteElementFromCommaSeparatedList(value);
    deleteElementFromCommaSeparatedList(definitionToDelete);
  }

  public static void deleteDefinition(@NotNull GoConstSpec spec, @NotNull GoConstDefinition definitionToDelete) {
    List<GoConstDefinition> definitionList = spec.getConstDefinitionList();
    int index = definitionList.indexOf(definitionToDelete);
    assert index >= 0;
    if (definitionList.size() == 1) {
      PsiElement parent = spec.getParent();
      if (parent instanceof GoConstDeclaration) {
        ((GoConstDeclaration)parent).deleteSpec(spec);
      }
      else {
        spec.delete();
      }
      return;
    }
    GoExpression value = definitionToDelete.getValue();
    if (value != null && spec.getExpressionList().size() <= 1) {
      PsiElement assign = spec.getAssign();
      if (assign != null) {
        assign.delete();
      }
    }
    deleteElementFromCommaSeparatedList(value);
    deleteElementFromCommaSeparatedList(definitionToDelete);
  }

  private static void deleteElementFromCommaSeparatedList(@Nullable PsiElement element) {
    if (element == null) {
      return;
    }
    PsiElement prevVisibleLeaf = PsiTreeUtil.prevVisibleLeaf(element);
    PsiElement nextVisibleLeaf = PsiTreeUtil.nextVisibleLeaf(element);
    if (prevVisibleLeaf != null && prevVisibleLeaf.textMatches(",")) {
      prevVisibleLeaf.delete();
    }
    else if (nextVisibleLeaf != null && nextVisibleLeaf.textMatches(",")) {
      nextVisibleLeaf.delete();
    }
    element.delete();
  }

  private static boolean hasNewLineBefore(@NotNull PsiElement anchor) {
    PsiElement prevSibling = anchor.getPrevSibling();
    while (prevSibling instanceof PsiWhiteSpace) {
      if (prevSibling.textContains('\n')) {
        return true;
      }
      prevSibling = prevSibling.getPrevSibling();
    }
    return false;
  }

  @Nullable
  public static GoExpression getValue(@NotNull GoVarDefinition definition) {
    PsiElement parent = definition.getParent();
    if (parent instanceof GoVarSpec) {
      int index = ((GoVarSpec)parent).getVarDefinitionList().indexOf(definition);
      return getByIndex(((GoVarSpec)parent).getRightExpressionsList(), index);
    }
    if (parent instanceof GoTypeSwitchGuard) {
      return ((GoTypeSwitchGuard)parent).getExpression();
    }
    LOG.error("Cannot find value for variable definition: " + definition.getText(),
              AttachmentFactory.createAttachment(definition.getContainingFile().getVirtualFile()));
    return null;
  }

  @Nullable
  public static GoExpression getValue(@NotNull GoConstDefinition definition) {
    PsiElement parent = definition.getParent();
    assert parent instanceof GoConstSpec;
    int index = ((GoConstSpec)parent).getConstDefinitionList().indexOf(definition);
    return getByIndex(((GoConstSpec)parent).getExpressionList(), index);
  }

  private static <T> T getByIndex(@NotNull List<T> list, int index) {
    return 0 <= index && index < list.size() ? list.get(index) : null;
  }

  @Nullable
  public static GoTypeSpec getTypeSpecSafe(@NotNull GoType type) {
    GoTypeStub stub = type.getStub();
    PsiElement parent = stub == null ? type.getParent() : stub.getParentStub().getPsi();
    return ObjectUtils.tryCast(parent, GoTypeSpec.class);
  }

  public static boolean canBeAutoImported(@NotNull GoFile file, boolean allowMain, @Nullable Module module) {
    if (isBuiltinFile(file) || !allowMain && StringUtil.equals(file.getPackageName(), GoConstants.MAIN)) {
      return false;
    }
    return allowed(file, null, module) && !GoUtil.isExcludedFile(file);
  }

  @Nullable
  @Contract("null, _ -> null")
  public static <T extends PsiElement> T getNonStrictTopmostParentOfType(@Nullable PsiElement element, @NotNull Class<T> aClass) {
    T first = PsiTreeUtil.getNonStrictParentOfType(element, aClass);
    T topMost = PsiTreeUtil.getTopmostParentOfType(first, aClass);
    return ObjectUtils.chooseNotNull(topMost, first);
  }

  @Nullable
  public static GoExpression getExpression(@NotNull GoIndexOrSliceExpr slice) {
    return ContainerUtil.getFirstItem(getExpressionsBefore(slice.getExpressionList(), slice.getLbrack()));
  }

  @NotNull
  public static List<GoExpression> getLeftExpressionsList(@NotNull GoRangeClause rangeClause) {
    return getExpressionsBefore(rangeClause.getExpressionList(), rangeClause.getRange());
  }

  @NotNull
  public static List<GoExpression> getLeftExpressionsList(@NotNull GoRecvStatement recvStatement) {
    return getExpressionsBefore(recvStatement.getExpressionList(),
                                ObjectUtils.chooseNotNull(recvStatement.getAssign(), recvStatement.getVarAssign()));
  }

  @NotNull
  public static Trinity<GoExpression, GoExpression, GoExpression> getIndices(@NotNull GoIndexOrSliceExpr slice) {
    GoExpression start;
    GoExpression end = null;
    GoExpression max = null;
    ASTNode[] colons = slice.getNode().getChildren(TokenSet.create(GoTypes.COLON));
    List<GoExpression> exprList = slice.getExpressionList();

    start = ContainerUtil.getFirstItem(getExpressionsInRange(exprList, slice.getLbrack(), colons.length > 0 ? colons[0].getPsi() : null));
    if (colons.length == 1) {
      end = ContainerUtil.getFirstItem(getExpressionsInRange(exprList, colons[0].getPsi(), slice.getRbrack()));
    }
    if (colons.length == 2) {
      end = ContainerUtil.getFirstItem(getExpressionsInRange(exprList, colons[0].getPsi(), colons[1].getPsi()));
      max = ContainerUtil.getFirstItem(getExpressionsInRange(exprList, colons[1].getPsi(), slice.getRbrack()));
    }

    return Trinity.create(start, end, max);
  }

  @NotNull
  public static List<GoExpression> getRightExpressionsList(@NotNull GoVarSpec varSpec) {
    return varSpec.getExpressionList();
  }

  @NotNull
  public static List<GoExpression> getRightExpressionsList(@NotNull GoRangeClause rangeClause) {
    return ContainerUtil.createMaybeSingletonList(rangeClause.getRangeExpression());
  }

  @NotNull
  public static List<GoExpression> getRightExpressionsList(@NotNull GoRecvStatement recvStatement) {
    return ContainerUtil.createMaybeSingletonList(recvStatement.getRecvExpression());
  }

  @Nullable
  public static GoExpression getRangeExpression(@NotNull GoRangeClause rangeClause) {
    return getLastExpressionAfter(rangeClause.getExpressionList(), rangeClause.getRange());
  }

  @Nullable
  public static GoExpression getRecvExpression(@NotNull GoRecvStatement recvStatement) {
    return getLastExpressionAfter(recvStatement.getExpressionList(),
                                  ObjectUtils.chooseNotNull(recvStatement.getAssign(), recvStatement.getVarAssign()));
  }

  @Nullable
  public static GoExpression getSendExpression(@NotNull GoSendStatement sendStatement) {
    return getLastExpressionAfter(sendStatement.getExpressionList(), sendStatement.getSendChannel());
  }

  @Nullable
  private static GoExpression getLastExpressionAfter(@NotNull List<GoExpression> list, @Nullable PsiElement anchor) {
    if (anchor == null) return null;
    GoExpression last = ContainerUtil.getLastItem(list);
    return last != null && last.getTextRange().getStartOffset() >= anchor.getTextRange().getEndOffset() ? last : null;
  }

  @NotNull
  private static List<GoExpression> getExpressionsInRange(@NotNull List<GoExpression> list,
                                                          @Nullable PsiElement start,
                                                          @Nullable PsiElement end) {
    if (start == null && end == null) {
      return list;
    }
    return ContainerUtil.filter(list, expression -> (end == null || expression.getTextRange().getEndOffset() <= end.getTextRange().getStartOffset()) &&
                                                (start == null || expression.getTextRange().getStartOffset() >= start.getTextRange().getEndOffset()));
  }

  @NotNull
  private static List<GoExpression> getExpressionsBefore(@NotNull List<GoExpression> list, @Nullable PsiElement anchor) {
    return getExpressionsInRange(list, null, anchor);
  }

  @NotNull
  public static ReadWriteAccessDetector.Access getReadWriteAccess(@NotNull GoReferenceExpression referenceExpression) {
    GoExpression expression = getConsiderableExpression(referenceExpression);
    PsiElement parent = expression.getParent();
    if (parent instanceof GoSelectorExpr) {
      if (expression.equals(((GoSelectorExpr)parent).getRight())) {
        expression = getConsiderableExpression((GoSelectorExpr)parent);
        parent = expression.getParent();
      }
      else {
        return Read;
      }
    }
    if (parent instanceof GoIncDecStatement) {
      return Write;
    }
    if (parent instanceof GoLeftHandExprList) {
      PsiElement grandParent = parent.getParent();
      if (grandParent instanceof GoAssignmentStatement) {
        return ((GoAssignmentStatement)grandParent).getAssignOp().getAssign() == null ? ReadWrite : Write;
      }
      if (grandParent instanceof GoSendStatement) {
        return Write;
      }
      return Read;
    }
    if (parent instanceof GoSendStatement && parent.getParent() instanceof GoCommCase) {
      return expression.equals(((GoSendStatement)parent).getSendExpression()) ? Read : ReadWrite;
    }
    if (parent instanceof GoRangeClause) {
      return expression.equals(((GoRangeClause)parent).getRangeExpression()) ? Read : Write;
    }
    if (parent instanceof GoRecvStatement) {
      return expression.equals(((GoRecvStatement)parent).getRecvExpression()) ? Read : Write;
    }
    return Read;
  }

  @NotNull
  private static GoExpression getConsiderableExpression(@NotNull GoExpression element) {
    GoExpression result = element;
    while (true) {
      PsiElement parent = result.getParent();
      if (parent == null) {
        return result;
      }
      if (parent instanceof GoParenthesesExpr) {
        result = (GoParenthesesExpr)parent;
        continue;
      }
      if (parent instanceof GoUnaryExpr) {
        GoUnaryExpr unaryExpr = (GoUnaryExpr)parent;
        if (unaryExpr.getMul() != null || unaryExpr.getBitAnd() != null || unaryExpr.getSendChannel() != null) {
          result = (GoUnaryExpr)parent;
          continue;
        }
      }
      return result;
    }
  }

  @NotNull
  public static String getDecodedText(@NotNull GoStringLiteral o) {
    StringBuilder builder = new StringBuilder();
    TextRange range = ElementManipulators.getManipulator(o).getRangeInElement(o);
    o.createLiteralTextEscaper().decode(range, builder);
    return builder.toString();
  }

  @Nullable
  public static PsiElement getOperator(@NotNull GoUnaryExpr o) {
    return getNotNullElement(o.getNot(), o.getMinus(), o.getPlus(), o.getBitAnd(), o.getBitXor(), o.getMul(), o.getSendChannel());
  }

  @Nullable
  public static PsiElement getOperator(@NotNull GoBinaryExpr o) {
    if (o instanceof GoAndExpr) return ((GoAndExpr)o).getCondAnd();
    if (o instanceof GoOrExpr) return ((GoOrExpr)o).getCondOr();
    if (o instanceof GoSelectorExpr) return ((GoSelectorExpr)o).getDot();
    if (o instanceof GoConversionExpr) return ((GoConversionExpr)o).getComma();

    if (o instanceof GoMulExpr) {
      GoMulExpr m = (GoMulExpr)o;
      return getNotNullElement(m.getMul(), m.getQuotient(), m.getRemainder(), m.getShiftRight(), m.getShiftLeft(), m.getBitAnd(),
                               m.getBitClear());
    }
    if (o instanceof GoAddExpr) {
      GoAddExpr a = (GoAddExpr)o;
      return getNotNullElement(a.getBitXor(), a.getBitOr(), a.getMinus(), a.getPlus());
    }
    if (o instanceof GoConditionalExpr) {
      GoConditionalExpr c = (GoConditionalExpr)o;
      return getNotNullElement(c.getEq(), c.getNotEq(), c.getGreater(), c.getGreaterOrEqual(), c.getLess(), c.getLessOrEqual());
    }
    return null;
  }

  @Nullable
  private static PsiElement getNotNullElement(@Nullable PsiElement... elements) {
    if (elements == null) return null;
    for (PsiElement e : elements) {
      if (e != null) return e;
    }
    return null;
  }

  public static boolean isSingleCharLiteral(@NotNull GoStringLiteral literal) {
    return literal.getDecodedText().length() == 1;
  }

  @Nullable
  public static GoExpression getRightExpression(@NotNull GoAssignmentStatement assignment, @NotNull GoExpression leftExpression) {
    int fieldIndex = assignment.getLeftHandExprList().getExpressionList().indexOf(leftExpression);
    return getByIndex(assignment.getExpressionList(), fieldIndex);
  }
}
