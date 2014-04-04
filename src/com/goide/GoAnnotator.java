package com.goide;

import com.goide.psi.GoImportSpec;
import com.goide.psi.GoReferenceExpression;
import com.goide.psi.GoTypeReferenceExpression;
import com.goide.psi.impl.GoReference;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;

public class GoAnnotator implements Annotator {

  @Override
  public void annotate(@NotNull PsiElement o, @NotNull AnnotationHolder holder) {
    if (o instanceof GoImportSpec) {
      o.putUserData(GoReference.IMPORT_USERS, ContainerUtil.<PsiElement>newArrayListWithCapacity(0));
    }
    else if (o instanceof GoReferenceExpression) {
      ((GoReferenceExpression)o).getReference().resolve();
    }
    else if (o instanceof GoTypeReferenceExpression) {
      ((GoTypeReferenceExpression)o).getReference().resolve();
    }
  }
}
