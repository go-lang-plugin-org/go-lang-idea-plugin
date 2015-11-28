/*
 * Copyright 2013-2015 Sergey Ignatov, Alexander Zolotov, Florin Patan
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

package com.goide;

import com.goide.psi.*;
import com.goide.psi.impl.GoPsiImplUtil;
import com.goide.psi.impl.GoReference;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.goide.highlighting.GoSyntaxHighlightingColors.*;

public class GoHighlightingAnnotator implements Annotator {
  @Override
  public void annotate(@NotNull PsiElement o, @NotNull AnnotationHolder holder) {
    if (!o.isValid()) return;
    if (o instanceof GoImportSpec) {
      if (((GoImportSpec)o).isDot()) {
        o.putUserData(GoReference.IMPORT_USERS, ContainerUtil.<PsiElement>newArrayListWithCapacity(0));
      }
    }
    else if (o instanceof GoLiteral) {
      if (((GoLiteral)o).getHex() != null || ((GoLiteral)o).getOct() != null) {
        setHighlighting(o, holder, NUMBER, "hex_oct");
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
      setHighlighting(((GoTypeSpec)o).getIdentifier(), holder, getColor((GoTypeSpec)o), "type");
    }
    else if (o instanceof GoConstDefinition) {
      setHighlighting(o, holder, getColor((GoConstDefinition)o), "const");
    }
    else if (o instanceof GoVarDefinition) {
      setHighlighting(o, holder, getColor((GoVarDefinition)o), "var");
    }
    else if (o instanceof GoFieldDefinition) {
      setHighlighting(o, holder, getColor((GoFieldDefinition)o), "field");
    }
    else if (o instanceof GoParamDefinition) {
      setHighlighting(o, holder, FUNCTION_PARAMETER, "param");
    }
    else if (o instanceof GoReceiver) {
      PsiElement identifier = ((GoReceiver)o).getIdentifier();
      if (identifier != null) {
        setHighlighting(identifier, holder, METHOD_RECEIVER, "receiver");
      }
    }
    else if (o instanceof GoLabelDefinition || o instanceof GoLabelRef) {
      setHighlighting(o, holder, LABEL, "label");
    }
    else if (o instanceof GoNamedSignatureOwner) {
      PsiElement identifier = ((GoNamedSignatureOwner)o).getIdentifier();
      if (identifier != null) {
        setHighlighting(identifier, holder, getColor((GoNamedSignatureOwner)o), "signature_owner");
      }
    }
  }

  private static void highlightRefIfNeeded(@NotNull GoReferenceExpressionBase o,
                                           @Nullable PsiElement resolve,
                                           @NotNull AnnotationHolder holder) {

    if (resolve instanceof GoTypeSpec) {
      TextAttributesKey key = GoPsiImplUtil.builtin(resolve)
                              ? BUILTIN_TYPE_REFERENCE
                              : getColor((GoTypeSpec)resolve);
      if (o.getParent() instanceof GoType && o.getParent().getParent() instanceof GoReceiver) {
        key = TYPE_REFERENCE;
      }
      setHighlighting(o.getIdentifier(), holder, key, "type");
    }
    else if (resolve instanceof GoConstDefinition) {
      TextAttributesKey color = GoPsiImplUtil.builtin(resolve) ? BUILTIN_TYPE_REFERENCE : getColor((GoConstDefinition)resolve);
      setHighlighting(o.getIdentifier(), holder, color, "const");
    }
    else if (resolve instanceof GoVarDefinition) {
      TextAttributesKey color = GoPsiImplUtil.builtin(resolve) ? BUILTIN_TYPE_REFERENCE : getColor((GoVarDefinition)resolve);
      setHighlighting(o.getIdentifier(), holder, color, "var");
    }
    else if (resolve instanceof GoFieldDefinition) {
      setHighlighting(o.getIdentifier(), holder, getColor((GoFieldDefinition)resolve), "field");
    }
    else if (resolve instanceof GoFunctionOrMethodDeclaration || resolve instanceof GoMethodSpec) {
      setHighlighting(o.getIdentifier(), holder, getColor((GoNamedSignatureOwner)resolve), "func");
    }
    else if (resolve instanceof GoReceiver) {
      setHighlighting(o.getIdentifier(), holder, METHOD_RECEIVER, "receiver");
    }
    else if (resolve instanceof GoParamDefinition) {
      setHighlighting(o.getIdentifier(), holder, FUNCTION_PARAMETER, "param");
    }
  }

  private static TextAttributesKey getColor(GoConstDefinition o) {
    if (isPackageWide(o)) {
      return o.isPublic() ? PACKAGE_EXPORTED_CONSTANT : PACKAGE_LOCAL_CONSTANT;
    }
    return LOCAL_CONSTANT;
  }

  private static TextAttributesKey getColor(GoFieldDefinition o) {
    return o.isPublic() ? STRUCT_EXPORTED_MEMBER : STRUCT_LOCAL_MEMBER;
  }

  private static TextAttributesKey getColor(GoVarDefinition o) {
    if (PsiTreeUtil.getParentOfType(o, GoForStatement.class) != null ||
        PsiTreeUtil.getParentOfType(o, GoIfStatement.class) != null ||
         PsiTreeUtil.getParentOfType(o, GoSwitchStatement.class) != null) {
      return SCOPE_VARIABLE;
    }

    if (isPackageWide(o)) {
      return o.isPublic() ? PACKAGE_EXPORTED_VARIABLE : PACKAGE_LOCAL_VARIABLE;
    }

    return LOCAL_VARIABLE;
  }

  private static TextAttributesKey getColor(GoNamedSignatureOwner o) {
    return o.isPublic() ? EXPORTED_FUNCTION : LOCAL_FUNCTION;
  }

  private static TextAttributesKey getColor(@Nullable GoTypeSpec o) {
    GoType type = o == null ? null : o.getGoType(null);
    if (type == null) {
      return TYPE_SPECIFICATION;
    }

    boolean isPublic = o.isPublic();
    if (type instanceof GoInterfaceType) {
      return isPublic ? PACKAGE_EXPORTED_INTERFACE : PACKAGE_LOCAL_INTERFACE;
    }
    else if (type instanceof GoStructType) {
      return isPublic ? PACKAGE_EXPORTED_STRUCT : PACKAGE_LOCAL_STRUCT;
    }

    return TYPE_SPECIFICATION;
  }

  private static void setHighlighting(@NotNull PsiElement element,
                                      @NotNull AnnotationHolder holder,
                                      @NotNull TextAttributesKey key,
                                      @Nullable String description) {
    holder.createInfoAnnotation(element, null).setEnforcedTextAttributes(TextAttributes.ERASE_MARKER);
    holder.createInfoAnnotation(element, ApplicationManager.getApplication().isUnitTestMode() ? description : null).setTextAttributes(key);
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
