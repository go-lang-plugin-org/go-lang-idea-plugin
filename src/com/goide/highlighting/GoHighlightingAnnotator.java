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

package com.goide.highlighting;

import com.goide.psi.*;
import com.goide.psi.impl.GoPsiImplUtil;
import com.goide.psi.impl.GoReference;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GoHighlightingAnnotator implements Annotator {
  @Override
  public void annotate(@NotNull PsiElement o, @NotNull AnnotationHolder holder) {
    if (o instanceof GoImportSpec) {
      if (((GoImportSpec)o).getDot() != null) {
        o.putUserData(GoReference.IMPORT_USERS, ContainerUtil.<PsiElement>newArrayListWithCapacity(0));
      }
    }
    else if (o instanceof GoLiteral) {
      if (((GoLiteral)o).getHex() != null || ((GoLiteral)o).getOct() != null) {
        setHighlighting(o, holder, GoSyntaxHighlightingColors.NUMBER);
      }
    }
    else if (o instanceof GoReferenceExpression) {
      PsiElement resolve = ((GoReferenceExpression)o).getReference().resolve();
      highlightRefIfNeeded((GoReferenceExpression)o, resolve, holder);      
    }
    else if (o instanceof GoTypeReferenceExpression) {
      PsiElement resolve = ((GoTypeReferenceExpression)o).getReference().resolve();
      highlightRefIfNeeded((GoTypeReferenceExpression)o, resolve, holder);
    }
    else if (o instanceof GoTypeSpec) {
      setHighlighting(((GoTypeSpec)o).getIdentifier(), holder, getColor((GoTypeSpec)o));
    }
    else if (o instanceof GoConstDefinition) {
      setHighlighting(o, holder, getColor((GoConstDefinition)o));
    }
    else if (o instanceof GoVarDefinition) {
      setHighlighting(o, holder, getColor((GoVarDefinition)o));
    }
    else if (o instanceof GoFieldDefinition) {
      setHighlighting(o, holder, getColor((GoFieldDefinition)o));
    }
    else if (o instanceof GoParamDefinition) {
      setHighlighting(o, holder, getColor((GoParamDefinition)o));
    }
    else if (o instanceof GoReceiver) {
      setHighlighting(o, holder, getColor((GoReceiver)o));
    }
    else if (o instanceof GoNamedSignatureOwner) {
      PsiElement identifier = ((GoNamedSignatureOwner)o).getIdentifier();
      if (identifier != null) {
        setHighlighting(identifier, holder, getColor((GoNamedSignatureOwner)o));
      }
    }
  }

  private static void highlightRefIfNeeded(@NotNull GoReferenceExpressionBase o,
                                           @Nullable PsiElement resolve,
                                           @NotNull AnnotationHolder holder) {
    if (resolve instanceof GoTypeSpec) {
      TextAttributesKey key = GoPsiImplUtil.builtin(resolve)
                              ? GoSyntaxHighlightingColors.BUILTIN_TYPE_REFERENCE
                              : getColor((GoTypeSpec)resolve);
      setHighlighting(o.getIdentifier(), holder, key);
    }
    else if (resolve instanceof GoConstDefinition) {
      TextAttributesKey color = GoPsiImplUtil.builtin(resolve)
                              ? GoSyntaxHighlightingColors.BUILTIN_TYPE_REFERENCE
                              : getColor((GoConstDefinition)resolve);
      setHighlighting(o.getIdentifier(), holder, color);
    }
    else if (resolve instanceof GoVarDefinition) {
      TextAttributesKey color = GoPsiImplUtil.builtin(resolve)
                              ? GoSyntaxHighlightingColors.BUILTIN_TYPE_REFERENCE
                              : getColor((GoVarDefinition)resolve);

      setHighlighting(o.getIdentifier(), holder, color);
    }
    else if (resolve instanceof GoFieldDefinition) {
      setHighlighting(o.getIdentifier(), holder, getColor((GoFieldDefinition)resolve));
    }
    else if (resolve instanceof GoFunctionOrMethodDeclaration) {
      setHighlighting(o.getIdentifier(), holder, getColor((GoNamedSignatureOwner)resolve));
    }
    else if (resolve instanceof GoReceiver) {
      setHighlighting(o.getIdentifier(), holder, getColor((GoReceiver)resolve));
    }
    else if (resolve instanceof GoParamDefinition) {
      setHighlighting(o.getIdentifier(), holder, getColor((GoParamDefinition)resolve));
    }
  }

  private static TextAttributesKey getColor(GoConstDefinition o) {
    if (isPackageWide(o)) {
      return o.isPublic()
              ? GoSyntaxHighlightingColors.PACKAGE_EXPORTED_CONSTANT
              : GoSyntaxHighlightingColors.PACKAGE_LOCAL_CONSTANT;
    }

    return GoSyntaxHighlightingColors.LOCAL_CONSTANT;
  }

  private static TextAttributesKey getColor(GoFieldDefinition o) {
    return o.isPublic()
              ? GoSyntaxHighlightingColors.STRUCT_EXPORTED_MEMBER
              : GoSyntaxHighlightingColors.STRUCT_LOCAL_MEMBER;
  }

  private static TextAttributesKey getColor(GoVarDefinition o) {
    if (PsiTreeUtil.getParentOfType(o, GoForStatement.class) != null ||
        PsiTreeUtil.getParentOfType(o, GoIfStatement.class) != null ||
         PsiTreeUtil.getParentOfType(o, GoSwitchStatement.class) != null) {
      return GoSyntaxHighlightingColors.SCOPE_VARIABLE;
    }

    if (isPackageWide(o)) {
      return o.isPublic()
              ? GoSyntaxHighlightingColors.PACKAGE_EXPORTED_VARIABLE
              : GoSyntaxHighlightingColors.PACKAGE_LOCAL_VARIABLE;
    }

    return GoSyntaxHighlightingColors.LOCAL_VARIABLE;
  }

  private static TextAttributesKey getColor(GoNamedSignatureOwner o) {
    boolean isPublic = o.isPublic();

    if (o instanceof GoMethodDeclaration) {
      return isPublic
              ? GoSyntaxHighlightingColors.STRUCT_EXPORTED_METHOD
              : GoSyntaxHighlightingColors.STRUCT_LOCAL_METHOD;
    }

    return isPublic
            ? GoSyntaxHighlightingColors.PACKAGE_EXPORTED_FUNCTION
            : GoSyntaxHighlightingColors.PACKAGE_LOCAL_FUNCTION;
  }

  private static TextAttributesKey getColor(GoParamDefinition o) {
    return GoSyntaxHighlightingColors.FUNCTION_PARAMETER;
  }

  private static TextAttributesKey getColor(GoReceiver o) {
    return GoSyntaxHighlightingColors.METHOD_RECEIVER;
  }

  private static TextAttributesKey getColor(GoTypeSpec o) {
    if (o == null || o.getGoType(null) == null) {
      return GoSyntaxHighlightingColors.TYPE_SPECIFICATION;
    }

    if (o.getGoType(null) instanceof GoInterfaceType) {
      return StringUtil.isCapitalized(o.getText())
             ? GoSyntaxHighlightingColors.PACKAGE_EXPORTED_INTERFACE
             : GoSyntaxHighlightingColors.PACKAGE_LOCAL_INTERFACE;
    }
    else if (o.getGoType(null) instanceof GoStructType) {
      return StringUtil.isCapitalized(o.getText())
             ? GoSyntaxHighlightingColors.PACKAGE_EXPORTED_STRUCT
             : GoSyntaxHighlightingColors.PACKAGE_LOCAL_STRUCT;
    }

    return GoSyntaxHighlightingColors.TYPE_SPECIFICATION;
  }

  private static void setHighlighting(@NotNull PsiElement element, @NotNull AnnotationHolder holder, @NotNull TextAttributesKey key) {
    holder.createInfoAnnotation(element, null).setEnforcedTextAttributes(TextAttributes.ERASE_MARKER);
    TextAttributes attributes = EditorColorsManager.getInstance().getGlobalScheme().getAttributes(key);
    holder.createInfoAnnotation(element, null).setEnforcedTextAttributes(attributes);
  }

  private static boolean isPackageWide(@NotNull GoVarDefinition o) {
    GoVarDeclaration declaration = PsiTreeUtil.getParentOfType(o, GoVarDeclaration.class);
    return declaration != null && declaration.getParent() instanceof GoFile;
  }

  private static boolean isPackageWide(@NotNull GoConstDefinition o) {
    GoConstDeclaration declaration = PsiTreeUtil.getParentOfType(o, GoConstDeclaration.class);
    return declaration != null && declaration.getParent() instanceof GoFile;
  }
}
