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

package com.goide.codeInsight.imports;

import com.goide.GoTypes;
import com.goide.psi.*;
import com.goide.psi.impl.GoReference;
import com.intellij.lang.ImportOptimizer;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.containers.MultiMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class GoImportOptimizer implements ImportOptimizer {
  @Override
  public boolean supports(PsiFile file) {
    return file instanceof GoFile;
  }

  @NotNull
  @Override
  public Runnable processFile(@NotNull final PsiFile file) {
    commit(file);
    assert file instanceof GoFile;
    MultiMap<String, GoImportSpec> importMap = ((GoFile)file).getImportMap();
    final List<PsiElement> importEntriesToDelete = ContainerUtil.newArrayList();
    final List<PsiElement> importIdentifiersToDelete = findRedundantImportIdentifiers(importMap);

    importEntriesToDelete.addAll(findDuplicatedEntries(importMap));
    importEntriesToDelete.addAll(filterUnusedImports(file, importMap).values());

    return new Runnable() {
      @Override
      public void run() {
        commit(file);

        for (PsiElement importEntry : importEntriesToDelete) {
          if (importEntry.isValid()) {
            deleteImportSpec(getImportSpec(importEntry));
          }
        }

        for (PsiElement identifier : importIdentifiersToDelete) {
          if (identifier.isValid()) {
            identifier.delete();
          }
        }
      }
    };
  }

  @NotNull
  public static List<PsiElement> findRedundantImportIdentifiers(@NotNull MultiMap<String, GoImportSpec> importMap) {
    final List<PsiElement> importIdentifiersToDelete = ContainerUtil.newArrayList();
    for (PsiElement importEntry : importMap.values()) {
      GoImportSpec importSpec = getImportSpec(importEntry);
      if (importSpec != null) {
        String localPackageName = importSpec.getLocalPackageName();
        if (!StringUtil.isEmpty(localPackageName)) {
          PsiElement identifier = importSpec.getIdentifier();
          if (identifier != null) {
            String identifierName = identifier.getText();
            if (identifierName.equals(localPackageName)) {
              importIdentifiersToDelete.add(identifier);
            }
          }
        }
      }
    }
    return importIdentifiersToDelete;
  }

  public static MultiMap<String, GoImportSpec> filterUnusedImports(@NotNull PsiFile file, 
                                                                   @NotNull final MultiMap<String, GoImportSpec> importMap) {
    final MultiMap<String, GoImportSpec> result = MultiMap.create();
    result.putAllValues(importMap);
    
    Collection<GoImportSpec> implicitImports = ContainerUtil.newArrayList(result.get("."));
    for (GoImportSpec importEntry : implicitImports) {
      GoImportSpec spec = getImportSpec(importEntry);
      if (spec != null && spec.getDot() != null) {
        List<PsiElement> list = spec.getUserData(GoReference.IMPORT_USERS);
        if (list != null) {
          for (PsiElement e : list) {
            if (e.isValid()) {
              result.remove(".", importEntry);
            }
          }
        }
      }
    }
    
    file.accept(new GoRecursiveVisitor() {
      @Override
      public void visitTypeReferenceExpression(@NotNull GoTypeReferenceExpression o) {
        GoTypeReferenceExpression lastQualifier = o.getQualifier();
        if (lastQualifier != null) {
          GoTypeReferenceExpression previousQualifier;
          while ((previousQualifier = lastQualifier.getQualifier()) != null) {
            lastQualifier = previousQualifier;
          }
          markAsUsed(lastQualifier.getIdentifier());
        }
      }

      @Override
      public void visitReferenceExpression(@NotNull GoReferenceExpression o) {
        GoReferenceExpression lastQualifier = o.getQualifier();
        if (lastQualifier != null) {
          GoReferenceExpression previousQualifier;
          while ((previousQualifier = lastQualifier.getQualifier()) != null) {
            lastQualifier = previousQualifier;
          }
          markAsUsed(lastQualifier.getIdentifier());
        }
      }

      private void markAsUsed(@NotNull PsiElement qualifier) {
        result.remove(qualifier.getText());
      }
    });
    return result;
  }

  @NotNull
  public static Collection<GoImportSpec> findDuplicatedEntries(@NotNull MultiMap<String, GoImportSpec> importMap) {
    List<GoImportSpec> duplicatedEntries = ContainerUtil.newArrayList();
    for (Map.Entry<String, Collection<GoImportSpec>> imports : importMap.entrySet()) {
      Collection<GoImportSpec> importsWithSameName = imports.getValue();
      if (importsWithSameName.size() > 1) {
        MultiMap<String, GoImportSpec> importsWithSameString = collectImportsWithSameString(importsWithSameName);

        for (Map.Entry<String, Collection<GoImportSpec>> importWithSameString : importsWithSameString.entrySet()) {
          List<GoImportSpec> duplicates = ContainerUtil.newArrayList(importWithSameString.getValue());
          if (duplicates.size() > 1) {
            duplicatedEntries.addAll(duplicates.subList(1, duplicates.size()));
          }
        }
      }
    }
    return duplicatedEntries;
  }

  private static void deleteImportSpec(@Nullable GoImportSpec importSpec) {
    GoImportDeclaration importDeclaration = PsiTreeUtil.getParentOfType(importSpec, GoImportDeclaration.class);
    if (importSpec != null && importDeclaration != null) {
      PsiElement startElementToDelete = importSpec;
      PsiElement endElementToDelete = importSpec;
      if (importDeclaration.getImportSpecList().size() == 1) {
        startElementToDelete = importDeclaration;
        endElementToDelete = importDeclaration;
        
        PsiElement nextSibling = endElementToDelete.getNextSibling();
        if (nextSibling != null && nextSibling.getNode().getElementType() == GoTypes.SEMICOLON) {
          endElementToDelete = nextSibling;
        }
      }
      // todo: delete after proper formatter implementation
      PsiElement nextSibling = endElementToDelete.getNextSibling();
      if (nextSibling instanceof PsiWhiteSpace && nextSibling.textContains('\n')) {
        endElementToDelete = nextSibling;
      }
      startElementToDelete.getParent().deleteChildRange(startElementToDelete, endElementToDelete);
    }
  }

  @NotNull
  private static MultiMap<String, GoImportSpec> collectImportsWithSameString(@NotNull Collection<GoImportSpec> importsWithSameName) {
    MultiMap<String, GoImportSpec> importsWithSameString = MultiMap.create();
    for (PsiElement duplicateCandidate : importsWithSameName) {
      GoImportSpec importSpec = getImportSpec(duplicateCandidate);
      if (importSpec != null) {
        importsWithSameString.putValue(importSpec.getImportString().getPath(), importSpec);
      }
    }
    return importsWithSameString;
  }

  @Nullable
  public static GoImportSpec getImportSpec(@NotNull PsiElement importEntry) {
    return PsiTreeUtil.getNonStrictParentOfType(importEntry, GoImportSpec.class);
  }

  private static void commit(@NotNull PsiFile file) {
    PsiDocumentManager manager = PsiDocumentManager.getInstance(file.getProject());
    Document document = manager.getDocument(file);
    if (document != null) {
      manager.commitDocument(document);
    }
  }
}
