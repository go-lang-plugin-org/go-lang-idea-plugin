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

package com.goide.inspections;

import com.goide.psi.*;
import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

import static com.intellij.codeInspection.ProblemHighlightType.GENERIC_ERROR_OR_WARNING;

public class GoDuplicateFieldsOrMethodsInspection extends GoInspectionBase {
  @NotNull
  @Override
  protected GoVisitor buildGoVisitor(@NotNull ProblemsHolder holder,
                                     @SuppressWarnings({"UnusedParameters", "For future"}) @NotNull LocalInspectionToolSession session) {
    return new GoVisitor() {
      @Override
      public void visitStructType(@NotNull GoStructType type) {
        List<GoNamedElement> fields = ContainerUtil.newArrayList();
        type.accept(new GoRecursiveVisitor() {
          @Override
          public void visitFieldDefinition(@NotNull GoFieldDefinition o) {
            addField(o);
          }

          @Override
          public void visitAnonymousFieldDefinition(@NotNull GoAnonymousFieldDefinition o) {
            addField(o);
          }

          private void addField(@NotNull GoNamedElement o) {
            if (!o.isBlank()) fields.add(o);
          }

          @Override
          public void visitType(@NotNull GoType o) {
            if (o == type) super.visitType(o);
          }
        });
        check(fields, holder, "field");
        super.visitStructType(type);
      }

      @Override
      public void visitInterfaceType(@NotNull GoInterfaceType o) {
        check(o.getMethodSpecList(), holder, "method");
        super.visitInterfaceType(o);
      }
    };
  }

  private static void check(@NotNull List<? extends GoNamedElement> fields, @NotNull ProblemsHolder problemsHolder, @NotNull String what) {
    Set<String> names = ContainerUtil.newHashSet();
    for (GoCompositeElement field : fields) {
      if (field instanceof GoMethodSpec && ((GoMethodSpec) field).getSignature() == null) {
        // It's an embedded type, not a method or a field.
        continue;
      }
      if (field instanceof GoNamedElement) {
        String name = ((GoNamedElement)field).getName();
        if (names.contains(name)) {
          PsiElement id = ((GoNamedElement)field).getIdentifier();
          problemsHolder.registerProblem(id != null ? id : field, "Duplicate " + what + " <code>#ref</code> #loc",
                                         GENERIC_ERROR_OR_WARNING);
        }
        else {
          ContainerUtil.addIfNotNull(names, name);
        }
      }
    }
  }
}
