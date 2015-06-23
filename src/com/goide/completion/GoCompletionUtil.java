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

package com.goide.completion;

import com.goide.GoIcons;
import com.goide.psi.*;
import com.goide.psi.impl.GoPsiImplUtil;
import com.goide.sdk.GoSdkUtil;
import com.goide.stubs.GoFieldDefinitionStub;
import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.completion.PrefixMatcher;
import com.intellij.codeInsight.completion.PrioritizedLookupElement;
import com.intellij.codeInsight.completion.impl.CamelHumpMatcher;
import com.intellij.codeInsight.completion.util.ParenthesesInsertHandler;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.lookup.LookupElementPresentation;
import com.intellij.codeInsight.lookup.LookupElementRenderer;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ObjectUtils;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class GoCompletionUtil {
  public static final int KEYWORD_PRIORITY = 20;
  public static final int CONTEXT_KEYWORD_PRIORITY = 25;
  public static final int NOT_IMPORTED_FUNCTION_PRIORITY = 3;
  public static final int FUNCTION_PRIORITY = NOT_IMPORTED_FUNCTION_PRIORITY + 10;
  public static final int NOT_IMPORTED_TYPE_PRIORITY = 5;
  public static final int TYPE_PRIORITY = NOT_IMPORTED_TYPE_PRIORITY + 10;
  public static final int NOT_IMPORTED_TYPE_CONVERSION = 1;
  public static final int TYPE_CONVERSION = NOT_IMPORTED_TYPE_CONVERSION + 10;
  public static final int VAR_PRIORITY = 15;
  public static final int LABEL_PRIORITY = 15;
  public static final int PACKAGE_PRIORITY = 5;
  public static final InsertHandler<LookupElement> FUNCTION_INSERT_HANDLER = new InsertHandler<LookupElement>() {
    @Override
    public void handleInsert(InsertionContext context, LookupElement item) {
      PsiElement element = item.getPsiElement();
      if (!(element instanceof GoSignatureOwner)) return;
      GoSignatureOwner f = (GoSignatureOwner)element;
      GoSignature signature = f.getSignature();
      int paramsCount = signature != null ? signature.getParameters().getParameterDeclarationList().size() : 0;
      InsertHandler<LookupElement> handler =
        paramsCount == 0 ? ParenthesesInsertHandler.NO_PARAMETERS : ParenthesesInsertHandler.WITH_PARAMETERS;
      handler.handleInsert(context, item);
    }
  };
  public static final LookupElementRenderer<LookupElement> FUNCTION_RENDERER = new LookupElementRenderer<LookupElement>() {
    @Override
    public void renderElement(LookupElement element, LookupElementPresentation p) {
      PsiElement o = element.getPsiElement();
      if (!(o instanceof GoNamedSignatureOwner)) return;
      GoNamedSignatureOwner f = (GoNamedSignatureOwner)o;
      Icon icon = f instanceof GoMethodDeclaration || f instanceof GoMethodSpec ? GoIcons.METHOD : GoIcons.FUNCTION;
      String typeText = "";
      GoSignature signature = f.getSignature();
      String paramText = "";
      if (signature != null) {
        GoResult result = signature.getResult();
        paramText = signature.getParameters().getText();
        if (result != null) typeText = result.getText();
      }

      p.setIcon(icon);
      p.setTypeText(typeText);
      p.setTypeGrayed(true);
      p.setTailText(calcTailText(f), true);
      p.setItemText(element.getLookupString() + paramText);
    }
  };
  public static final LookupElementRenderer<LookupElement> VARIABLE_RENDERER = new LookupElementRenderer<LookupElement>() {
    @Override
    public void renderElement(LookupElement element, LookupElementPresentation p) {
      PsiElement o = element.getPsiElement();
      if (!(o instanceof GoNamedElement)) return;
      GoNamedElement v = (GoNamedElement)o;
      GoType type = v.getGoType(null);
      String text = GoPsiImplUtil.getText(type);
      Icon icon = v instanceof GoVarDefinition ? GoIcons.VARIABLE :
                  v instanceof GoParamDefinition ? GoIcons.PARAMETER :
                  v instanceof GoFieldDefinition ? GoIcons.FIELD :
                  v instanceof GoReceiver ? GoIcons.RECEIVER :
                  v instanceof GoConstDefinition ? GoIcons.CONSTANT :
                  v instanceof GoAnonymousFieldDefinition ? GoIcons.FIELD :
                  null;

      p.setIcon(icon);
      p.setTailText(calcTailTextForFields(v), true);
      p.setTypeText(text);
      p.setTypeGrayed(true);
    }
  };

  private static class Lazy {
    private static final SingleCharInsertHandler DIR_INSERT_HANDLER = new SingleCharInsertHandler('/');
    private static final SingleCharInsertHandler PACKAGE_INSERT_HANDLER = new SingleCharInsertHandler('.');
  }

  private GoCompletionUtil() {

  }

  @NotNull
  public static CamelHumpMatcher createPrefixMatcher(@NotNull PrefixMatcher original) {
    return createPrefixMatcher(original.getPrefix());
  }

  @NotNull
  public static CamelHumpMatcher createPrefixMatcher(@NotNull String prefix) {
    return new CamelHumpMatcher(prefix, false);
  }

  @NotNull
  public static LookupElement createFunctionOrMethodLookupElement(@NotNull final GoNamedSignatureOwner f,
                                                                  @NotNull final String lookupString,
                                                                  @Nullable final InsertHandler<LookupElement> h,
                                                                  final double priority) {
    return PrioritizedLookupElement.withPriority(LookupElementBuilder
                                                   .createWithSmartPointer(lookupString, f)
                                                   .withRenderer(FUNCTION_RENDERER)
                                                   .withInsertHandler(h != null ? h : FUNCTION_INSERT_HANDLER), priority);
  }

  @Nullable
  private static String calcTailText(GoSignatureOwner m) {
    String text = "";
    if (m instanceof GoMethodDeclaration) {
      text = GoPsiImplUtil.getText(((GoMethodDeclaration)m).getReceiver().getType());
    }
    else if (m instanceof GoMethodSpec) {
      PsiElement parent = m.getParent();
      if (parent instanceof GoInterfaceType) {
        text = GoPsiImplUtil.getText((GoInterfaceType)parent);
      }
    }
    return StringUtil.isNotEmpty(text) ? " " + UIUtil.rightArrow() + " " + text : null;
  }

  @NotNull
  public static LookupElement createTypeLookupElement(@NotNull GoTypeSpec t) {
    return createTypeLookupElement(t, StringUtil.notNullize(t.getName()), null, null, TYPE_PRIORITY);
  }

  @NotNull
  public static LookupElement createTypeLookupElement(@NotNull GoTypeSpec t,
                                                      @NotNull String lookupString,
                                                      @Nullable InsertHandler<LookupElement> handler,
                                                      @Nullable String importPath,
                                                      double priority) {
    LookupElementBuilder builder = LookupElementBuilder.createWithSmartPointer(lookupString, t)
      .withInsertHandler(handler).withIcon(GoIcons.TYPE);
    if (importPath != null) builder = builder.withTailText(" " + importPath, true);
    return PrioritizedLookupElement.withPriority(builder, priority);
  }

  @NotNull
  public static LookupElement createLabelLookupElement(@NotNull GoLabelDefinition l, @NotNull String lookupString) {
    return PrioritizedLookupElement.withPriority(LookupElementBuilder.createWithSmartPointer(lookupString, l)
                                                   .withIcon(GoIcons.LABEL), LABEL_PRIORITY);
  }

  @NotNull
  public static LookupElement createTypeConversionLookupElement(@NotNull GoTypeSpec t) {
    return createTypeConversionLookupElement(t, StringUtil.notNullize(t.getName()), null, null, TYPE_CONVERSION);
  }

  @NotNull
  public static LookupElement createTypeConversionLookupElement(@NotNull GoTypeSpec t,
                                                                @NotNull String lookupString,
                                                                @Nullable InsertHandler<LookupElement> insertHandler,
                                                                @Nullable String importPath,
                                                                double priority) {
    // todo: check context and place caret in or outside {}
    InsertHandler<LookupElement> handler = ObjectUtils.notNull(insertHandler, getTypeConversionInsertHandler(t));
    return createTypeLookupElement(t, lookupString, handler, importPath, priority);
  }

  @NotNull
  public static InsertHandler<LookupElement> getTypeConversionInsertHandler(@NotNull GoTypeSpec t) {
    GoType type = t.getType();
    return type instanceof GoStructType || type instanceof GoArrayOrSliceType || type instanceof GoMapType
           ? BracesInsertHandler.ONE_LINER
           : ParenthesesInsertHandler.WITH_PARAMETERS;
  }

  @NotNull
  public static LookupElement createVariableLikeLookupElement(@NotNull GoNamedElement v) {
    String name = StringUtil.notNullize(v.getName());
    SingleCharInsertHandler handler =
      v instanceof GoFieldDefinition ?
      new SingleCharInsertHandler(':') {
        @Override
        public void handleInsert(@NotNull InsertionContext context, LookupElement item) {
          PsiFile file = context.getFile();
          if (!(file instanceof GoFile)) return;
          context.commitDocument();
          int offset = context.getStartOffset();
          PsiElement at = file.findElementAt(offset);
          GoCompositeElement ref = PsiTreeUtil.getParentOfType(at, GoValue.class, GoReferenceExpression.class);
          if (ref instanceof GoReferenceExpression && (((GoReferenceExpression)ref).getQualifier() != null || GoPsiImplUtil.prevDot(ref))) {
            return;
          }
          GoValue value = PsiTreeUtil.getParentOfType(at, GoValue.class);
          if (value == null || PsiTreeUtil.getPrevSiblingOfType(value, GoKey.class) != null) return;
          super.handleInsert(context, item);
        }
      } : null;
    return PrioritizedLookupElement.withPriority(
      LookupElementBuilder.createWithSmartPointer(name, v)
        .withLookupString(name.toLowerCase()).withRenderer(VARIABLE_RENDERER)
        .withInsertHandler(handler)
      , VAR_PRIORITY);
  }

  @Nullable
  private static String calcTailTextForFields(@NotNull GoNamedElement v) {
    String name = null;
    if (v instanceof GoFieldDefinition) {
      GoFieldDefinitionStub stub = ((GoFieldDefinition)v).getStub();
      GoTypeSpec spec = stub != null ? stub.getParentStubOfType(GoTypeSpec.class) : PsiTreeUtil.getParentOfType(v, GoTypeSpec.class);
      name = spec != null ? spec.getName() : null;
    }
    return StringUtil.isNotEmpty(name) ? " " + UIUtil.rightArrow() + " " + name : null;
  }

  @Nullable
  public static LookupElement createPackageLookupElement(@NotNull GoImportSpec spec, @Nullable String name) {
    name = name != null ? name : ObjectUtils.notNull(spec.getAlias(), spec.getLocalPackageName());
    return createPackageLookupElement(name, spec, true);
  }

  @NotNull
  public static LookupElement createPackageLookupElement(@NotNull String importPath,
                                                         @Nullable PsiElement context,
                                                         boolean forType) {
    return createPackageLookupElement(importPath, getContextImportPath(context), forType);
  }

  @NotNull
  public static LookupElement createPackageLookupElement(@NotNull String importPath, @Nullable String contextImportPath, boolean forType) {
    return PrioritizedLookupElement.withPriority(
      LookupElementBuilder.create(importPath)
        .withLookupString(importPath.substring(Math.max(0, importPath.lastIndexOf('/'))))
        .withIcon(GoIcons.PACKAGE).withInsertHandler(forType ? Lazy.PACKAGE_INSERT_HANDLER : null),
      calculatePackagePriority(importPath, contextImportPath));
  }

  public static int calculatePackagePriority(@NotNull String importPath, @Nullable String currentPath) {
    int priority = PACKAGE_PRIORITY;
    if (StringUtil.isNotEmpty(currentPath)) {
      String[] givenSplit = importPath.split("/");
      String[] contextSplit = currentPath.split("/");
      for (int i = 0; i < contextSplit.length && i < givenSplit.length; i++) {
        if (contextSplit[i].equals(givenSplit[i])) {
          priority++;
        }
        else {
          break;
        }
      }
    }
    return priority - StringUtil.countChars(importPath, '/') - StringUtil.countChars(importPath, '.');
  }

  @Nullable
  public static String getContextImportPath(@Nullable PsiElement context) {
    if (context == null) return null;
    String currentPath = null;
    if (context instanceof PsiDirectory) {
      currentPath = GoSdkUtil.getImportPath((PsiDirectory)context);
    }
    else {
      PsiFile file = context.getContainingFile();
      if (file instanceof GoFile) {
        currentPath = ((GoFile)file).getImportPath();
      }
    }
    return currentPath;
  }

  @NotNull
  public static LookupElementBuilder createDirectoryLookupElement(@NotNull PsiDirectory dir) {
    int files = dir.getFiles().length;
    return LookupElementBuilder.createWithSmartPointer(dir.getName(), dir).withIcon(GoIcons.DIRECTORY)
      .withInsertHandler(files == 0 ? Lazy.DIR_INSERT_HANDLER : null);
  }
}
