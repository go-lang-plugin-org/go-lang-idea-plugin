package com.goide.refactor;

import com.goide.psi.GoAnonymousFieldDefinition;
import com.goide.psi.GoTypeSpec;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.refactoring.rename.RenamePsiElementProcessor;
import com.intellij.util.Query;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class GoAnonymousFieldProcessor extends RenamePsiElementProcessor {
  @Override
  public boolean canProcessElement(@NotNull PsiElement element) {
    return
      element instanceof GoTypeSpec ||
      element instanceof GoAnonymousFieldDefinition;
  }

  @Override
  public void prepareRenaming(PsiElement element, String newName, Map<PsiElement, String> allRenames, SearchScope scope) {
    if (element instanceof GoTypeSpec) {
      Query<PsiReference> search = ReferencesSearch.search(element, scope);
      for (PsiReference ref : search) {
        PsiElement refElement = ref.getElement();
        PsiElement parent = refElement == null ? null : refElement.getParent();
        if (parent instanceof GoAnonymousFieldDefinition) {
          allRenames.put(parent, newName);
        }
      }
    }
    else if (element instanceof GoAnonymousFieldDefinition) {
      PsiElement type = ((GoAnonymousFieldDefinition)element).getTypeReferenceExpression().getReference().resolve();
      if (type instanceof GoTypeSpec) {
        allRenames.put(type, newName);
      }
    }
  }
}
