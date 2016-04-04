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
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.Conditions;
import com.intellij.psi.PsiElement;
import com.intellij.util.Function;
import com.intellij.util.ObjectUtils;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

class GoStructLiteralCompletion {
  enum Variants {FIELD_NAME_ONLY, VALUE_ONLY, BOTH, NONE}

  @NotNull
  static Variants allowedVariants(@Nullable GoReferenceExpression structFieldReference) {
    GoValue value = parent(structFieldReference, GoValue.class);
    GoElement element = parent(value, GoElement.class);
    if (element != null && element.getKey() != null) {
      return Variants.NONE;
    }

    GoLiteralValue literalValue = parent(element, GoLiteralValue.class);
    GoCompositeLit structLiteral = parent(literalValue, GoCompositeLit.class);
    GoType type = GoPsiImplUtil.getLiteralType(structLiteral);
    if (!(type instanceof GoStructType)) {
      return Variants.NONE;
    }

    boolean hasValueInitializers = false;
    boolean hasFieldValueInitializers = false;

    List<GoElement> fieldInitializers = literalValue.getElementList();
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
  static Condition<String> newIsFieldAssignedPredicate(@Nullable GoLiteralValue literal) {
    if (literal == null) {
      return Conditions.alwaysFalse();
    }

    final Set<String> assignedFields = ContainerUtil.map2SetNotNull(literal.getElementList(), new Function<GoElement, String>() {
      @Override
      public String fun(GoElement element) {
        GoKey key = element.getKey();
        GoFieldName fieldName = key != null ? key.getFieldName() : null;
        PsiElement identifier = fieldName != null ? fieldName.getIdentifier() : null;
        return identifier != null ? identifier.getText() : null;
      }
    });

    return new Condition<String>() {
      @Override
      public boolean value(String fieldName) {
        return assignedFields.contains(fieldName);
      }
    };
  }

  @Contract("null,_->null")
  private static <T> T parent(@Nullable PsiElement of, @NotNull Class<T> parentClass) {
    return ObjectUtils.tryCast(of != null ? of.getParent() : null, parentClass);
  }
}
