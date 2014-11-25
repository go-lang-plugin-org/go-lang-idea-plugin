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

import com.goide.GoIcons;
import com.goide.completion.GoCompletionContributor;
import com.goide.psi.*;
import com.goide.psi.impl.imports.GoImportReferenceSet;
import com.goide.stubs.GoNamedStub;
import com.goide.stubs.index.GoMethodIndex;
import com.goide.util.SingleCharInsertHandler;
import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.PrioritizedLookupElement;
import com.intellij.codeInsight.completion.util.ParenthesesInsertHandler;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.*;
import com.intellij.psi.impl.light.LightElement;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReferenceOwner;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.PsiFileReference;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.GlobalSearchScopesCore;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class GoPsiImplUtil {
  public static boolean builtin(@NotNull PsiElement resolve) {
    PsiFile file = resolve.getContainingFile();
    if (!(file instanceof GoFile)) return false;
    return "builtin".equals(((GoFile)file).getPackageName()) && file.getName().equals("builtin.go");
  }

  public static boolean isPanic(@NotNull GoCallExpr o) {
    GoExpression e = o.getExpression();
    if (StringUtil.equals("panic", e.getText()) && e instanceof GoReferenceExpression) {
      PsiReference reference = e.getReference();
      PsiElement resolve = reference != null ? reference.resolve() : null;
      if (!(resolve instanceof GoFunctionDeclaration)) return false;
      GoFile file = ((GoFunctionDeclaration)resolve).getContainingFile();
      return StringUtil.equals(file.getPackageName(), "builtin") && StringUtil.equals(file.getName(), "builtin.go");
    }
    return false;
  }

  private static class Lazy {
    private static final SingleCharInsertHandler DIR_INSERT_HANDLER = new SingleCharInsertHandler('/');
    private static final SingleCharInsertHandler PACKAGE_INSERT_HANDLER = new SingleCharInsertHandler('.');
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
    return PsiTreeUtil.getParentOfType(o, GoShortVarDeclaration.class) != null ? new GoVarReference(o) : null;
  }

  @NotNull
  public static GoReference getReference(@NotNull final GoReferenceExpression o) {
    return new GoReference(o);
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
    if (o instanceof GoVarSpec) return isAncestor || GoCompositeElementImpl.precessDeclarationDefault(o, processor, state, lastParent, place);

    if (isAncestor) return GoCompositeElementImpl.precessDeclarationDefault(o, processor, state, lastParent, place);

    if (o instanceof GoBlock ||
        o instanceof GoIfStatement ||
        o instanceof GoSwitchStatement ||
        o instanceof GoForStatement ||
        o instanceof GoCommClause ||
        o instanceof GoFunctionLit ||
        o instanceof GoTypeCaseClause ||
        o instanceof GoExprCaseClause) {
      return processor.execute(o, state);
    }
    return GoCompositeElementImpl.precessDeclarationDefault(o, processor, state, lastParent, place);
  }

  @NotNull
  public static LookupElement createFunctionOrMethodLookupElement(@NotNull GoNamedSignatureOwner f) {
    return createFunctionOrMethodLookupElement(f, false, null);
  }

  @NotNull
  public static LookupElement createFunctionOrMethodLookupElement(@NotNull GoNamedSignatureOwner f,
                                                                  boolean showPkg,
                                                                  @Nullable InsertHandler<LookupElement> h) {
    Icon icon = f instanceof GoMethodDeclaration || f instanceof GoMethodSpec ? GoIcons.METHOD : GoIcons.FUNCTION;
    GoSignature signature = f.getSignature();
    int paramsCount = 0;
    String typeText = "";
    String paramText = "";
    if (signature != null) {
      paramsCount = signature.getParameters().getParameterDeclarationList().size();
      GoResult result = signature.getResult();
      paramText = signature.getParameters().getText();
      if (result != null) typeText = result.getText();
    }

    InsertHandler<LookupElement> handler = h != null ? h :
                                           paramsCount == 0
                                           ? ParenthesesInsertHandler.NO_PARAMETERS
                                           : ParenthesesInsertHandler.WITH_PARAMETERS;
    String pkg = showPkg ? StringUtil.notNullize(f.getContainingFile().getPackageName()) : "";
    pkg = pkg.isEmpty() ? pkg : pkg + ".";
    return PrioritizedLookupElement.withPriority(
      LookupElementBuilder
        .create(f)
        .withIcon(icon)
        .withInsertHandler(handler)
        .withTypeText(typeText, true)
        .withTailText(calcTailText(f), true)
        .withLookupString(pkg)
        .withLookupString(StringUtil.notNullize(f.getName(), "").toLowerCase())
        .withLookupString(pkg + f.getName())
        .withPresentableText(pkg + f.getName() + paramText),
      showPkg ? GoCompletionContributor.FUNCTION_WITH_PACKAGE_PRIORITY : GoCompletionContributor.FUNCTION_PRIORITY
    );
  }

  @Nullable
  private static String calcTailText(GoSignatureOwner m) {
    String text = "";
    if (m instanceof GoMethodDeclaration) {
      text = getText(((GoMethodDeclaration)m).getReceiver().getType());
    }
    else if (m instanceof GoMethodSpec) {
      PsiElement parent = m.getParent();
      if (parent instanceof GoInterfaceType) {
        text = getText((GoInterfaceType)parent);
      }
    }
    if (!StringUtil.isEmpty(text)) return " " + UIUtil.rightArrow() + " " + text;
    return null;
  }

  @NotNull
  public static LookupElement createTypeLookupElement(@NotNull GoTypeSpec t) {
    return createTypeLookupElement(t, false, null);
  }

  @NotNull
  public static LookupElement createTypeLookupElement(@NotNull GoTypeSpec t,
                                                      boolean showPkg,
                                                      @Nullable InsertHandler<LookupElement> handler) {
    String pkg = showPkg ? StringUtil.notNullize(t.getContainingFile().getPackageName()) : "";
    pkg = pkg.isEmpty() ? pkg : pkg + ".";
    return PrioritizedLookupElement.withPriority(
      LookupElementBuilder.
        create(t)
        .withLookupString(pkg)
        .withLookupString(StringUtil.notNullize(t.getName(), "").toLowerCase())
        .withLookupString(pkg + t.getName())
        .withPresentableText(pkg + t.getName())
        .withInsertHandler(handler)
        .withIcon(GoIcons.TYPE),
      GoCompletionContributor.TYPE_PRIORITY);
  }

  @NotNull
  public static LookupElement createLabelLookupElement(@NotNull GoLabelDefinition l) {
    return PrioritizedLookupElement.withPriority(LookupElementBuilder.create(l).withIcon(GoIcons.LABEL),
                                                 GoCompletionContributor.LABEL_PRIORITY);
  }

  @NotNull
  public static LookupElement createTypeConversionLookupElement(@NotNull GoTypeSpec t) {
    return PrioritizedLookupElement.withPriority(
      LookupElementBuilder
        .create(t)
        .withLookupString(StringUtil.notNullize(t.getName(), "").toLowerCase())
        .withInsertHandler(ParenthesesInsertHandler.WITH_PARAMETERS).withIcon(GoIcons.TYPE),
      GoCompletionContributor.TYPE_CONVERSION);
  }

  @NotNull
  public static LookupElement createVariableLikeLookupElement(@NotNull GoNamedElement v) {
    Icon icon = v instanceof GoVarDefinition ? GoIcons.VARIABLE :
                v instanceof GoParamDefinition ? GoIcons.PARAMETER :
                v instanceof GoFieldDefinition ? GoIcons.FIELD :
                v instanceof GoReceiver ? GoIcons.RECEIVER :
                v instanceof GoConstDefinition ? GoIcons.CONST :
                v instanceof GoAnonymousFieldDefinition ? GoIcons.FIELD :
                null;
    GoType type = v.getGoType();
    String text = getText(type);
    return PrioritizedLookupElement.withPriority(
      LookupElementBuilder
        .create(v)
        .withLookupString(StringUtil.notNullize(v.getName(), "").toLowerCase())
        .withIcon(icon)
        .withTypeText(text, true),
      GoCompletionContributor.VAR_PRIORITY);
  }

  @Nullable
  public static LookupElement createPackageLookupElement(@NotNull GoImportSpec spec) {
    PsiElement id = spec.getIdentifier();
    return id != null ? createPackageLookupElement(id.getText(), true) : null;
  }

  @NotNull
  public static LookupElement createPackageLookupElement(@NotNull String str, boolean forType) {
    return PrioritizedLookupElement.withPriority(
      LookupElementBuilder.create(str).withIcon(GoIcons.PACKAGE).withInsertHandler(forType ? Lazy.PACKAGE_INSERT_HANDLER : null),
      GoCompletionContributor.PACKAGE_PRIORITY);
  }

  @NotNull
  public static LookupElementBuilder createDirectoryLookupElement(@NotNull PsiDirectory dir) {
    int files = dir.getFiles().length;
    return LookupElementBuilder.create(dir).withIcon(GoIcons.PACKAGE).withInsertHandler(files == 0 ? Lazy.DIR_INSERT_HANDLER : null);
  }

  @Nullable
  public static GoType getGoType(@NotNull GoReceiver o) {
    return o.getType();
  }

  @Nullable
  public static GoType getGoType(@NotNull GoAnonymousFieldDefinition o) {
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
      GoType type = o.getType();
      return type != null ? type.getTypeReferenceExpression() : null;
    }
    return o.getTypeReferenceExpression();
  }

  @Nullable
  public static GoType getGoType(@NotNull GoExpression o) {
    if (o instanceof GoUnaryExpr) {
      GoExpression expression = ((GoUnaryExpr)o).getExpression();
      if (expression != null) {
        GoType type = expression.getGoType();
        if (type instanceof GoChannelType && ((GoUnaryExpr)o).getSendChannel() != null) return type.getType();
        return type;
      }
      return null;
    }
    else if (o instanceof GoCompositeLit) {
      GoType type = ((GoCompositeLit)o).getLiteralTypeExpr().getType();
      if (type != null) return type;
      GoTypeReferenceExpression expression = ((GoCompositeLit)o).getLiteralTypeExpr().getTypeReferenceExpression();
      return getType(expression);
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
      return ((GoCallExpr)o).getExpression().getGoType();
    }
    else if (o instanceof GoReferenceExpression) {
      PsiReference reference = o.getReference();
      PsiElement resolve = reference != null ? reference.resolve() : null;
      if (resolve instanceof GoTypeOwner) {
        return ((GoTypeOwner)resolve).getGoType();
      }
    }
    else if (o instanceof GoParenthesesExpr) {
      GoExpression expression = ((GoParenthesesExpr)o).getExpression();
      return expression != null ? expression.getGoType() : null;
    }
    else if (o instanceof GoSelectorExpr) {
      GoExpression item = ContainerUtil.getLastItem(((GoSelectorExpr)o).getExpressionList());
      return item != null ? item.getGoType() : null;
    }
    else if (o instanceof GoIndexExpr) {
      GoExpression first = ContainerUtil.getFirstItem(((GoIndexExpr)o).getExpressionList());
      GoType type = first == null ? null : getGoType(first);
      if (type instanceof GoMapType) {
        List<GoType> list = ((GoMapType)type).getTypeList();
        if (list.size() == 2) {
          return list.get(1);
        }
      }
      else if (type instanceof GoArrayOrSliceType) {
        return type.getType();
      }
    }
    else if (o instanceof GoSliceExpr) {
      GoExpression first = ContainerUtil.getFirstItem(((GoSliceExpr)o).getExpressionList());
      return first == null ? null : getGoType(first);
    }
    else if (o instanceof GoTypeAssertionExpr) {
      return ((GoTypeAssertionExpr)o).getType();
    }
    return null;
  }

  @Nullable
  private static GoType getType(@Nullable GoTypeReferenceExpression expression) {
    PsiReference reference = expression != null ? expression.getReference() : null;
    PsiElement resolve = reference != null ? reference.resolve() : null;
    if (resolve instanceof GoTypeSpec) return ((GoTypeSpec)resolve).getType();
    return null;
  }

  @Nullable
  public static GoType getGoType(@NotNull GoTypeSpec o) {
    return o.getType();
  }

  @Nullable
  public static GoType getGoType(@NotNull GoVarDefinition o) {
    // see http://golang.org/ref/spec#RangeClause
    PsiElement parent = o.getParent();
    if (parent instanceof GoRangeClause) {
      return processRangeClause(o, (GoRangeClause)parent);
    }
    if (parent instanceof GoVarSpec) {
      return processVarSpec(o, (GoVarSpec)parent);
    }
    GoCompositeLit literal = PsiTreeUtil.getNextSiblingOfType(o, GoCompositeLit.class);
    if (literal != null) {
      return literal.getLiteralTypeExpr().getType();
    }
    return GoNamedElementImpl.getType(o);
  }

  @Nullable
  private static GoType processVarSpec(GoVarDefinition o, @NotNull GoVarSpec parent) {
    GoType commonType = parent.getType();
    if (commonType != null) return commonType;
    List<GoVarDefinition> varList = parent.getVarDefinitionList();
    int i = varList.indexOf(o);
    i = i == -1 ? 0 : i;
    List<GoExpression> exprs = parent.getExpressionList();
    if (exprs.size() == 1 && exprs.get(0) instanceof GoCallExpr) {
      GoExpression call = exprs.get(0);
      GoType type = call.getGoType();
      if (type instanceof GoTypeList) {
        if (((GoTypeList)type).getTypeList().size() > i) {
          return ((GoTypeList)type).getTypeList().get(i);
        }
      }
      return type;
    }
    if (exprs.size() <= i) return null;
    return exprs.get(i).getGoType();
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
      GoType type = last.getGoType();
      GoTypeReferenceExpression typeRef = type != null ? type.getTypeReferenceExpression() : null;
      if (typeRef != null) {
        PsiElement resolve = typeRef.getReference().resolve();
        if (resolve instanceof GoTypeSpec) {
          type = ((GoTypeSpec)resolve).getType();
        }
      }
      if (type instanceof GoArrayOrSliceType && i == 1) return type.getType();
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
    if (o instanceof GoStructType || o instanceof GoInterfaceType) {
      PsiElement parent = o.getParent();
      if (parent instanceof GoTypeSpec) {
        String n = ((GoTypeSpec)parent).getName();
        String p = ((GoTypeSpec)parent).getContainingFile().getPackageName();
        if (n != null && p != null) return p + "." + n;
      }
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
    PsiElement identifier = o.getIdentifier();
    PsiFile file = o.getContainingFile().getOriginalFile();
    if (file instanceof GoFile) {
      String packageName = ((GoFile)file).getPackageName();
      String typeName = identifier.getText();
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
  public static GoType getGoType(@NotNull GoSignatureOwner o) {
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
          composite.add(p.getType());
        }
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
          public GoType getType() {
            return null;
          }

          @Nullable
          @Override
          public GoTypeReferenceExpression getTypeReferenceExpression() {
            return null;
          }

          @Nullable
          @Override
          public PsiElement getLparen() {
            return null;
          }

          @Nullable
          @Override
          public PsiElement getRparen() {
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
        return addImportDeclaration(importList, newDeclaration, lastImportDeclaration);
      }
      else {
        return lastImportDeclaration.addImportSpec(packagePath, alias);
      }
    }
    else {
      return addImportDeclaration(importList, newDeclaration, null);
    }
  }

  @NotNull
  private static GoImportSpec addImportDeclaration(@NotNull GoImportList importList,
                                                   @NotNull GoImportDeclaration newImportDeclaration,
                                                   @Nullable PsiElement anchor) {
    GoImportDeclaration importDeclaration = (GoImportDeclaration)importList.addAfter(newImportDeclaration, anchor);
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
    return (GoImportSpec)declaration.addBefore(GoElementFactory.createImportSpec(declaration.getProject(), packagePath, alias), rParen);
  }
  
  public static boolean isBlank(@NotNull PsiElement o) {
    return StringUtil.equals(o.getText(), "_");
  }
}
