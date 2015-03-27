/*
 * Copyright 2013-2014 Sergey Ignatov, Alexander Zolotov, Mihai Toader
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
import com.goide.stubs.GoFieldDefinitionStub;
import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.completion.PrioritizedLookupElement;
import com.intellij.codeInsight.completion.util.ParenthesesInsertHandler;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ObjectUtils;
import com.intellij.util.PlatformIcons;
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
  
  private static class Lazy {
    private static final SingleCharInsertHandler DIR_INSERT_HANDLER = new SingleCharInsertHandler('/');
    private static final SingleCharInsertHandler PACKAGE_INSERT_HANDLER = new SingleCharInsertHandler('.');
  }

  private GoCompletionUtil() {
    
  }

  @NotNull
  public static LookupElement createFunctionOrMethodLookupElement(@NotNull GoNamedSignatureOwner f) {
    return createFunctionOrMethodLookupElement(f, f.getName(), false, null, FUNCTION_PRIORITY);
  }

  @NotNull
  public static LookupElement createFunctionOrMethodLookupElement(@NotNull GoNamedSignatureOwner f,
                                                                  @Nullable String name, // for performance
                                                                  boolean showPackage,
                                                                  @Nullable InsertHandler<LookupElement> h, 
                                                                  double priority) {
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
    String pkg = showPackage ? StringUtil.notNullize(f.getContainingFile().getPackageName()) : "";
    pkg = pkg.isEmpty() ? pkg : pkg + ".";
    name = StringUtil.notNullize(name);
    return PrioritizedLookupElement.withPriority(
      LookupElementBuilder
        .create(f, name)
        .withIcon(icon)
        .withInsertHandler(handler)
        .withTypeText(typeText, true)
        .withTailText(calcTailText(f), true)
        .withLookupString(name.toLowerCase())
        .withLookupString((pkg + name).toLowerCase())
        .withPresentableText(pkg + name + paramText), priority);
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
    return createTypeLookupElement(t, StringUtil.notNullize(t.getName()), false, null, null, TYPE_PRIORITY);
  }

  @NotNull
  public static LookupElement createTypeLookupElement(@NotNull GoTypeSpec t,
                                                      @NotNull String name,
                                                      boolean showPackage,
                                                      @Nullable InsertHandler<LookupElement> handler,
                                                      @Nullable String importPath,
                                                      double priority) {
    String pkg = showPackage ? StringUtil.notNullize(t.getContainingFile().getPackageName()) : "";
    pkg = pkg.isEmpty() ? pkg : pkg + ".";
    LookupElementBuilder builder = LookupElementBuilder.create(t, name)
      .withLookupString(name.toLowerCase())
      .withLookupString((pkg + name).toLowerCase())
      .withPresentableText(pkg + name)
      .withInsertHandler(handler)
      .withIcon(GoIcons.TYPE);
    if (importPath != null) builder = builder.withTailText(" " + importPath, true);
    return PrioritizedLookupElement.withPriority(builder, priority);
  }

  @NotNull
  public static LookupElement createLabelLookupElement(@NotNull GoLabelDefinition l) {
    return PrioritizedLookupElement.withPriority(LookupElementBuilder.create(l).withIcon(GoIcons.LABEL), LABEL_PRIORITY);
  }

  @NotNull
  public static LookupElement createTypeConversionLookupElement(@NotNull GoTypeSpec t) {
    return createTypeConversionLookupElement(t, StringUtil.notNullize(t.getName()), false, null, null, TYPE_CONVERSION);
  }

  @NotNull
  public static LookupElement createTypeConversionLookupElement(@NotNull GoTypeSpec t, 
                                                                @NotNull String name,
                                                                boolean showPackage,
                                                                @Nullable InsertHandler<LookupElement> insertHandler,
                                                                @Nullable String importPath,
                                                                double priority) {
    // todo: check context and place caret in or outside {}
    InsertHandler<LookupElement> handler = ObjectUtils.notNull(insertHandler, getTypeConversionInsertHandler(t));
    return createTypeLookupElement(t, name, showPackage, handler, importPath, priority);
  }

  @NotNull
  public static InsertHandler<LookupElement> getTypeConversionInsertHandler(@NotNull GoTypeSpec t) {
    return t.getType() instanceof GoStructType ? BracesInsertHandler.ONE_LINER : ParenthesesInsertHandler.WITH_PARAMETERS;
  }

  @NotNull
  public static LookupElement createVariableLikeLookupElement(@NotNull GoNamedElement v) {
    Icon icon = v instanceof GoVarDefinition ? GoIcons.VARIABLE :
                v instanceof GoParamDefinition ? GoIcons.PARAMETER :
                v instanceof GoFieldDefinition ? GoIcons.FIELD :
                v instanceof GoReceiver ? GoIcons.RECEIVER :
                v instanceof GoConstDefinition ? GoIcons.CONSTANT :
                v instanceof GoAnonymousFieldDefinition ? GoIcons.FIELD :
                null;
    GoType type = v.getGoType(null);
    String text = GoPsiImplUtil.getText(type);
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
          if (PsiTreeUtil.getParentOfType(at, GoValue.class) == null) return;
          super.handleInsert(context, item);
        }
      } : null;
    return PrioritizedLookupElement.withPriority(
      LookupElementBuilder.create(v, name)
        .withLookupString(name.toLowerCase())
        .withIcon(icon)
        .withTailText(calcTailTextForFields(v), true)
        .withTypeText(text, true)
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
    return createPackageLookupElement(name, true);
  }

  @NotNull
  public static LookupElement createPackageLookupElement(@NotNull String str, boolean forType) {
    return PrioritizedLookupElement.withPriority(
      LookupElementBuilder.create(str).withIcon(GoIcons.PACKAGE).withInsertHandler(forType ? Lazy.PACKAGE_INSERT_HANDLER : null),
      PACKAGE_PRIORITY);
  }

  @NotNull
  public static LookupElementBuilder createDirectoryLookupElement(@NotNull PsiDirectory dir) {
    int files = dir.getFiles().length;
    return LookupElementBuilder.create(dir).withIcon(PlatformIcons.DIRECTORY_CLOSED_ICON)
      .withInsertHandler(files == 0 ? Lazy.DIR_INSERT_HANDLER : null);
  }
}
