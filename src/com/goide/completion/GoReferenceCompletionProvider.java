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

import com.goide.project.GoVendoringUtil;
import com.goide.psi.*;
import com.goide.psi.impl.*;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.resolve.reference.impl.PsiMultiReference;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ArrayUtil;
import com.intellij.util.ObjectUtils;
import com.intellij.util.ProcessingContext;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

import static com.goide.completion.GoCompletionUtil.createPrefixMatcher;

public class GoReferenceCompletionProvider extends CompletionProvider<CompletionParameters> {
  @Override
  protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet set) {
    GoReferenceExpressionBase expression = PsiTreeUtil.getParentOfType(parameters.getPosition(), GoReferenceExpressionBase.class);
    PsiFile originalFile = parameters.getOriginalFile();
    if (expression != null) {
      fillVariantsByReference(expression.getReference(), originalFile, set.withPrefixMatcher(createPrefixMatcher(set.getPrefixMatcher())));
    }
    PsiElement parent = parameters.getPosition().getParent();
    if (parent != null) {
      fillVariantsByReference(parent.getReference(), originalFile, set.withPrefixMatcher(createPrefixMatcher(set.getPrefixMatcher())));
    }
  }

  private static void fillVariantsByReference(@Nullable PsiReference reference,
                                              @NotNull PsiFile file,
                                              @NotNull CompletionResultSet result) {
    if (reference == null) return;
    if (reference instanceof PsiMultiReference) {
      PsiReference[] references = ((PsiMultiReference)reference).getReferences();
      ContainerUtil.sort(references, PsiMultiReference.COMPARATOR);
      fillVariantsByReference(ArrayUtil.getFirstElement(references), file, result);
    }
    else if (reference instanceof GoReference) {
      GoReferenceExpression refExpression = ObjectUtils.tryCast(reference.getElement(), GoReferenceExpression.class);
      GoStructLiteralCompletion.Variants variants = GoStructLiteralCompletion.allowedVariants(refExpression);

      fillStructFieldNameVariants(file, result, variants, refExpression);

      if (variants != GoStructLiteralCompletion.Variants.FIELD_NAME_ONLY) {
        ((GoReference)reference).processResolveVariants(new MyGoScopeProcessor(result, file, false));
      }
    }
    else if (reference instanceof GoTypeReference) {
      PsiElement element = reference.getElement();
      PsiElement spec = PsiTreeUtil.getParentOfType(element, GoFieldDeclaration.class, GoTypeSpec.class);
      boolean insideParameter = PsiTreeUtil.getParentOfType(element, GoParameterDeclaration.class) != null;
      ((GoTypeReference)reference).processResolveVariants(new MyGoScopeProcessor(result, file, true) {
        @Override
        protected boolean accept(@NotNull PsiElement e) {
          return e != spec && !(insideParameter &&
                                (e instanceof GoNamedSignatureOwner || e instanceof GoVarDefinition || e instanceof GoConstDefinition));
        }
      });
    }
    else if (reference instanceof GoCachedReference) {
      ((GoCachedReference)reference).processResolveVariants(new MyGoScopeProcessor(result, file, false));
    }
  }

  private static void fillStructFieldNameVariants(@NotNull PsiFile file,
                                                  @NotNull CompletionResultSet result,
                                                  @NotNull GoStructLiteralCompletion.Variants variants,
                                                  @Nullable GoReferenceExpression refExpression) {
    if (refExpression == null ||
        variants != GoStructLiteralCompletion.Variants.FIELD_NAME_ONLY &&
        variants != GoStructLiteralCompletion.Variants.BOTH) {
      return;
    }

    GoLiteralValue literal = PsiTreeUtil.getParentOfType(refExpression, GoLiteralValue.class);
    new GoFieldNameReference(refExpression).processResolveVariants(new MyGoScopeProcessor(result, file, false) {
      final Set<String> alreadyAssignedFields = GoStructLiteralCompletion.alreadyAssignedFields(literal);

      @Override
      public boolean execute(@NotNull PsiElement o, @NotNull ResolveState state) {
        String structFieldName = o instanceof GoFieldDefinition ? ((GoFieldDefinition)o).getName() :
                                 o instanceof GoAnonymousFieldDefinition ? ((GoAnonymousFieldDefinition)o).getName() : null;
        if (structFieldName != null && alreadyAssignedFields.contains(structFieldName)) {
          return true;
        }
        return super.execute(o, state);
      }
    });
  }

  private static void addElement(@NotNull PsiElement o,
                                 @NotNull ResolveState state,
                                 boolean forTypes,
                                 boolean vendoringEnabled,
                                 @NotNull Set<String> processedNames,
                                 @NotNull CompletionResultSet set) {
    LookupElement lookup = createLookupElement(o, state, forTypes, vendoringEnabled);
    if (lookup != null) {
      String lookupString = lookup.getLookupString();
      if (!processedNames.contains(lookupString)) {
        set.addElement(lookup);
        processedNames.add(lookupString);
      }
    }
  }

  @Nullable
  private static LookupElement createLookupElement(@NotNull PsiElement o,
                                                   @NotNull ResolveState state,
                                                   boolean forTypes,
                                                   boolean vendoringEnabled) {
    if (o instanceof GoNamedElement && !((GoNamedElement)o).isBlank() || o instanceof GoImportSpec && !((GoImportSpec)o).isDot()) {
      if (o instanceof GoImportSpec) {
        return GoCompletionUtil.createPackageLookupElement((GoImportSpec)o, state.get(GoReferenceBase.ACTUAL_NAME), vendoringEnabled);
      }
      else if (o instanceof GoNamedSignatureOwner && ((GoNamedSignatureOwner)o).getName() != null) {
        String name = ((GoNamedSignatureOwner)o).getName();
        if (name != null) {
          return GoCompletionUtil.createFunctionOrMethodLookupElement((GoNamedSignatureOwner)o, name, null,
                                                                      GoCompletionUtil.FUNCTION_PRIORITY);
        }
      }
      else if (o instanceof GoTypeSpec) {
        return forTypes
               ? GoCompletionUtil.createTypeLookupElement((GoTypeSpec)o)
               : GoCompletionUtil.createTypeConversionLookupElement((GoTypeSpec)o);
      }
      else if (o instanceof PsiDirectory) {
        return GoCompletionUtil.createPackageLookupElement(((PsiDirectory)o).getName(), (PsiDirectory)o, o, vendoringEnabled, true);
      }
      else if (o instanceof GoLabelDefinition) {
        String name = ((GoLabelDefinition)o).getName();
        if (name != null) return GoCompletionUtil.createLabelLookupElement((GoLabelDefinition)o, name);
      }
      else if (o instanceof GoFieldDefinition) {
        return GoCompletionUtil.createFieldLookupElement((GoFieldDefinition)o);
      }
      else {
        return GoCompletionUtil.createVariableLikeLookupElement((GoNamedElement)o);
      }
    }
    return null;
  }

  private static class MyGoScopeProcessor extends GoScopeProcessor {
    @NotNull private final CompletionResultSet myResult;
    private final boolean myForTypes;
    private final boolean myVendoringEnabled;
    private final Set<String> myProcessedNames = ContainerUtil.newHashSet();

    public MyGoScopeProcessor(@NotNull CompletionResultSet result, @NotNull PsiFile originalFile, boolean forTypes) {
      myResult = result;
      myForTypes = forTypes;
      myVendoringEnabled = GoVendoringUtil.isVendoringEnabled(ModuleUtilCore.findModuleForPsiElement(originalFile));
    }

    @Override
    public boolean execute(@NotNull PsiElement o, @NotNull ResolveState state) {
      if (accept(o)) {
        addElement(o, state, myForTypes, myVendoringEnabled, myProcessedNames, myResult);
      }
      return true;
    }

    protected boolean accept(@NotNull PsiElement e) {
      return true;
    }

    @Override
    public boolean isCompletion() {
      return true;
    }
  }
}
                                                      