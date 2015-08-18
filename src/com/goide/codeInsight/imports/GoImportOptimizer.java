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

package com.goide.codeInsight.imports;

import com.goide.GoTypes;
import com.goide.psi.*;
import com.goide.psi.impl.GoReference;
import com.intellij.lang.ImportOptimizer;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.Comparing;
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
import java.util.Set;

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
    final Set<PsiElement> importEntriesToDelete = ContainerUtil.newLinkedHashSet();
    final Set<PsiElement> importIdentifiersToDelete = findRedundantImportIdentifiers(importMap);

    importEntriesToDelete.addAll(findDuplicatedEntries(importMap));
    importEntriesToDelete.addAll(filterUnusedImports(file, importMap).values());

    return new CollectingInfoRunnable() {
      @Nullable
      @Override
      public String getUserNotificationInfo() {
        int entriesToDelete = importEntriesToDelete.size();
        int identifiersToDelete = importIdentifiersToDelete.size();
        String result = "";
        if (entriesToDelete > 0) {
          result = "Removed " + entriesToDelete + " import" + (entriesToDelete > 1 ? "s" : "");
        } 
        if (identifiersToDelete > 0) {
          result += result.isEmpty() ? "Removed " : " and ";
          result += identifiersToDelete + " alias" + (identifiersToDelete > 1 ? "es" : "");
        }
        return StringUtil.nullize(result);
      }

      @Override
      public void run() {
        if (!importEntriesToDelete.isEmpty() && !importIdentifiersToDelete.isEmpty()) {
          commit(file);
        }

        for (PsiElement importEntry : importEntriesToDelete) {
          if (importEntry != null && importEntry.isValid()) {
            deleteImportSpec(getImportSpec(importEntry));
          }
        }

        for (PsiElement identifier : importIdentifiersToDelete) {
          if (identifier != null && identifier.isValid()) {
            identifier.delete();
          }
        }
      }
    };
  }

  @NotNull
  public static Set<PsiElement> findRedundantImportIdentifiers(@NotNull MultiMap<String, GoImportSpec> importMap) {
    Set<PsiElement> importIdentifiersToDelete = ContainerUtil.newLinkedHashSet();
    for (PsiElement importEntry : importMap.values()) {
      GoImportSpec importSpec = getImportSpec(importEntry);
      if (importSpec != null) {
        String localPackageName = importSpec.getLocalPackageName();
        if (!StringUtil.isEmpty(localPackageName)) {
          if (Comparing.equal(importSpec.getAlias(), localPackageName)) {
            importIdentifiersToDelete.add(importSpec.getIdentifier());
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
    result.remove("_"); // imports for side effects are always used
    
    Collection<GoImportSpec> implicitImports = ContainerUtil.newArrayList(result.get("."));
    for (GoImportSpec importEntry : implicitImports) {
      GoImportSpec spec = getImportSpec(importEntry);
      if (spec != null && spec.isDot()) {
        List<? extends PsiElement> list = spec.getUserData(GoReference.IMPORT_USERS);
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
        Collection<String> qualifiersToDelete = ContainerUtil.newHashSet();
        for (GoImportSpec spec : result.get(qualifier.getText())) {
          for (Map.Entry<String, Collection<GoImportSpec>> entry : result.entrySet()) {
            for (GoImportSpec importSpec : entry.getValue()) {
              if (importSpec == spec) {
                qualifiersToDelete.add(entry.getKey());
              }
            }
          }
        }
        for (String qualifierToDelete : qualifiersToDelete) {
          result.remove(qualifierToDelete);
        }
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
        importsWithSameString.putValue(importSpec.getPath(), importSpec);
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
