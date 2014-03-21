package com.goide.editor;

import com.goide.psi.GoNamedElement;
import com.goide.psi.GoType;
import com.goide.psi.GoTypeReferenceExpression;
import com.intellij.codeInsight.navigation.actions.TypeDeclarationProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.Nullable;

public class GoTypeDeclarationProvider implements TypeDeclarationProvider {
  @Nullable
  @Override
  public PsiElement[] getSymbolTypeDeclarations(PsiElement element) {
    if (!(element instanceof GoNamedElement)) return PsiElement.EMPTY_ARRAY;
    GoType type = ((GoNamedElement)element).getGoType();
    GoTypeReferenceExpression ref = type != null ? type.getTypeReferenceExpression() : null;
    PsiReference reference = ref != null ? ref.getReference() : null;
    PsiElement resolve = reference != null ? reference.resolve() : type; // todo: think about better fallback instead of `type`
    return resolve != null ? new PsiElement[]{resolve} : PsiElement.EMPTY_ARRAY;
  }
}
