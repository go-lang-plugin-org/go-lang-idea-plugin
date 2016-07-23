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

package com.goide.completion;

import com.goide.psi.*;
import com.goide.psi.impl.GoPsiImplUtil;
import com.intellij.psi.PsiElement;
import com.intellij.util.ObjectUtils;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Set;

class GoStructLiteralCompletion {
  /**
   * Describes struct literal completion variants that should be suggested.
   */
  enum Variants {
    /**
     * Only struct field names should be suggested.
     * Indicates that field:value initializers are used in this struct literal.
     * For example, {@code Struct{field1: "", caret}}.
     */
    FIELD_NAME_ONLY,
    /**
     * Only values should be suggested.
     * Indicates that value initializers are used in this struct literal.
     * For example, {@code Struct{"", caret}}.
     */
    VALUE_ONLY,
    /**
     * Both struct field names and values should be suggested.
     * Indicates that there's no reliable way to determine whether field:value or value initializers are used.
     * Example 1: {@code Struct{caret}}.
     * Example 2: {@code Struct{field1:"", "", caret}}
     */
    BOTH,
    /**
     * Indicates that struct literal completion should not be available.
     */
    NONE
  }

  @NotNull
  static Variants allowedVariants(@Nullable GoReferenceExpression structFieldReference) {
    GoValue value = parent(structFieldReference, GoValue.class);
    GoElement element = parent(value, GoElement.class);
    if (element != null && element.getKey() != null) {
      return Variants.NONE;
    }

    GoType type = GoPsiImplUtil.getLiteralType(element, true);
    if (!(type instanceof GoStructType)) {
      return Variants.NONE;
    }

    boolean hasValueInitializers = false;
    boolean hasFieldValueInitializers = false;

    GoLiteralValue literalValue = parent(element, GoLiteralValue.class);
    List<GoElement> fieldInitializers = literalValue != null ? literalValue.getElementList() : Collections.emptyList();
    for (GoElement initializer : fieldInitializers) {
      if (initializer == element) {
        continue;
      }

      PsiElement colon = initializer.getColon();
      hasFieldValueInitializers |= colon != null;
      hasValueInitializers |= colon == null;
    }

    return hasFieldValueInitializers && !hasValueInitializers ? Variants.FIELD_NAME_ONLY :
           !hasFieldValueInitializers && hasValueInitializers ? Variants.VALUE_ONLY :
           Variants.BOTH;
  }

  @NotNull
  static Set<String> alreadyAssignedFields(@Nullable GoLiteralValue literal) {
    if (literal == null) {
      return Collections.emptySet();
    }

    return ContainerUtil.map2SetNotNull(literal.getElementList(), element -> {
      GoKey key = element.getKey();
      GoFieldName fieldName = key != null ? key.getFieldName() : null;
      PsiElement identifier = fieldName != null ? fieldName.getIdentifier() : null;
      return identifier != null ? identifier.getText() : null;
    });
  }

  @Contract("null,_->null")
  private static <T> T parent(@Nullable PsiElement of, @NotNull Class<T> parentClass) {
    return ObjectUtils.tryCast(of != null ? of.getParent() : null, parentClass);
  }
}
