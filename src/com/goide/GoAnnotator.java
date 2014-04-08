package com.goide;

import com.goide.highlighting.GoSyntaxHighlightingColors;
import com.goide.psi.*;
import com.goide.psi.impl.GoReference;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.psi.PsiElement;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GoAnnotator implements Annotator {
  @Override
  public void annotate(@NotNull PsiElement o, @NotNull AnnotationHolder holder) {
    if (o instanceof GoImportSpec) {
      if (((GoImportSpec)o).getDot() != null) {
        o.putUserData(GoReference.IMPORT_USERS, ContainerUtil.<PsiElement>newArrayListWithCapacity(0));
      }
    }
    else if (o instanceof GoReferenceExpression) {
      PsiElement resolve = ((GoReferenceExpression)o).getReference().resolve();
      highlightAsTypeRefIfNeeded(o, resolve, holder);      
    }
    else if (o instanceof GoTypeReferenceExpression) {
      PsiElement resolve = ((GoTypeReferenceExpression)o).getReference().resolve();
      highlightAsTypeRefIfNeeded(o, resolve, holder);
    }
    else if (o instanceof GoTypeSpec) {
      setHighlighting(((GoTypeSpec)o).getIdentifier(), holder, GoSyntaxHighlightingColors.TYPE_SPECIFICATION);
    }
  }

  private static void highlightAsTypeRefIfNeeded(@NotNull PsiElement o, @Nullable PsiElement resolve, @NotNull AnnotationHolder holder) {
    if (resolve instanceof GoTypeSpec) {
      setHighlighting(o, holder, GoSyntaxHighlightingColors.TYPE_REFERENCE);
    }
  }

  private static void setHighlighting(@NotNull PsiElement element, @NotNull AnnotationHolder holder, @NotNull TextAttributesKey key) {
    holder.createInfoAnnotation(element, null).setEnforcedTextAttributes(TextAttributes.ERASE_MARKER);
    TextAttributes attributes = EditorColorsManager.getInstance().getGlobalScheme().getAttributes(key);
    holder.createInfoAnnotation(element, null).setEnforcedTextAttributes(attributes);
  }
}
