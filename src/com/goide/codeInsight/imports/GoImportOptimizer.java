package com.goide.codeInsight.imports;

import com.goide.psi.*;
import com.intellij.lang.ImportOptimizer;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.PathUtil;
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
    final MultiMap<String, PsiElement> importMap = ((GoFile)file).getImportMap();

    final List<PsiElement> importEntriesToDelete = ContainerUtil.newArrayList();
    final List<PsiElement> importIdentifiersToDelete = findRedundantImportIdentifiers(importMap);

    importEntriesToDelete.addAll(findDuplicatedEntries(importMap));

    filterUnusedImports(file, importMap);
    importEntriesToDelete.addAll(importMap.values());

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
  public static List<PsiElement> findRedundantImportIdentifiers(@NotNull MultiMap<String, PsiElement> importMap) {
    final List<PsiElement> importIdentifiersToDelete = ContainerUtil.newArrayList();
    for (PsiElement importEntry : importMap.values()) {
      GoImportSpec importSpec = getImportSpec(importEntry);
      if (importSpec != null) {
        String importText = importSpec.getImportString().getText();
        String localPackageName = PathUtil.getFileName(StringUtil.unquoteString(importText));
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

  public static void filterUnusedImports(@NotNull PsiFile file, @NotNull final MultiMap<String, PsiElement> importMap) {
    file.accept(new GoRecursiveVisitor() {
      private final MultiMap<String, PsiElement> myImportMap = importMap;

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
        myImportMap.remove(qualifier.getText());
      }
    });
  }

  @NotNull
  public static Collection<GoImportSpec> findDuplicatedEntries(@NotNull MultiMap<String, PsiElement> importMap) {
    List<GoImportSpec> duplicatedEntries = ContainerUtil.newArrayList();
    for (Map.Entry<String, Collection<PsiElement>> imports : importMap.entrySet()) {
      Collection<PsiElement> importsWithSameName = imports.getValue();
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
      if (importDeclaration.getImportSpecList().size() == 1) {
        importDeclaration.delete();
      }
      else {
        importSpec.delete();
      }
    }
  }

  @NotNull
  private static MultiMap<String, GoImportSpec> collectImportsWithSameString(@NotNull Collection<PsiElement> importsWithSameName) {
    MultiMap<String, GoImportSpec> importsWithSameString = MultiMap.create();
    for (PsiElement duplicateCandidate : importsWithSameName) {
      GoImportSpec importSpec = getImportSpec(duplicateCandidate);
      if (importSpec != null) {
        importsWithSameString.putValue(importSpec.getImportString().getText(), importSpec);
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
