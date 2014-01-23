package com.goide.codeInsight.imports;

import com.goide.psi.*;
import com.intellij.lang.ImportOptimizer;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
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
    final MultiMap<String, PsiElement> importMap = ((GoFile)file).getImportMap();

    final List<PsiElement> importEntriesToDelete = ContainerUtil.newArrayList();
    final List<PsiElement> importIdentifiersToDelete = ContainerUtil.newArrayList();

    for (PsiElement importEntry : importMap.values()) {
      GoImportSpec importSpec = getImportSpec(importEntry);
      if (importSpec != null) {
        PsiDirectory resolve = importSpec.getImportString().resolve();
        if (resolve != null) {
          PsiElement identifier = importSpec.getIdentifier();
          if (identifier != null) {
            String identifierName = identifier.getText();
            if (identifierName.equals(resolve.getName())) {
              importIdentifiersToDelete.add(identifier);
            }
          }
        }
        else {
          if (!ApplicationManager.getApplication().isUnitTestMode()) {
            importEntriesToDelete.add(importSpec); //todo test sdk require
          }
        }
      }
    }

    importEntriesToDelete.addAll(findDuplicatedEntries(importMap));

    GoRecursiveVisitor visitor = new GoRecursiveVisitor() {
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
        importMap.remove(qualifier.getText());
      }
    };
    visitor.visitFile(file);
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
  private static Collection<GoImportSpec> findDuplicatedEntries(@NotNull MultiMap<String, PsiElement> importMap) {
    Collection<GoImportSpec> duplicatedEntries = ContainerUtil.newArrayList();
    for (Map.Entry<String, Collection<PsiElement>> imports : importMap.entrySet()) {
      Collection<PsiElement> importsWithSameName = imports.getValue();
      if (importsWithSameName.size() > 1) {
        MultiMap<String, GoImportSpec> importsWithSameString = collectImportsWithSameString(importsWithSameName);

        for (Map.Entry<String, Collection<GoImportSpec>> importWithSameString : importsWithSameString.entrySet()) {
          List<GoImportSpec> duplicates = ContainerUtil.newArrayList(importWithSameString.getValue());
          if (duplicates.size() > 1) {
            duplicatedEntries.addAll(ContainerUtil.getFirstItems(duplicates, duplicates.size() - 1));
          }
        }
      }
    }
    return duplicatedEntries;
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
  private static GoImportSpec getImportSpec(@NotNull PsiElement importEntry) {
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
