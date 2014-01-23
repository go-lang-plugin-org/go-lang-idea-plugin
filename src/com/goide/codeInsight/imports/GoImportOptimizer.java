package com.goide.codeInsight.imports;

import com.goide.psi.*;
import com.intellij.lang.ImportOptimizer;
import com.intellij.openapi.editor.Document;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class GoImportOptimizer implements ImportOptimizer {
  @Override
  public boolean supports(PsiFile file) {
    return file instanceof GoFile;
  }

  @NotNull
  @Override
  public Runnable processFile(final PsiFile file) {
    commit(file);
    assert file instanceof GoFile;
    final Map<String, Object> importMap = ((GoFile)file).getImportMap();
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

    return new Runnable() {
      @Override
      public void run() {
        commit(file);
        for (Object importEntry : importMap.values()) {
          if (importEntry instanceof PsiElement && ((PsiElement)importEntry).isValid()) {
            GoImportDeclaration importDeclaration = PsiTreeUtil.getParentOfType((PsiElement)importEntry, GoImportDeclaration.class);
            if (importDeclaration != null) {
              importDeclaration.delete();
            }
          }
        }
      }
    };
  }

  private static void commit(PsiFile file) {
    PsiDocumentManager manager = PsiDocumentManager.getInstance(file.getProject());
    Document document = manager.getDocument(file);
    if (document != null) {
      manager.commitDocument(document);
    }
  }
}
