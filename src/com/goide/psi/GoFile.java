package com.goide.psi;

import com.goide.GoFileType;
import com.goide.GoLanguage;
import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.lang.parser.GeneratedParserUtilBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.CachedValue;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.util.Processor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class GoFile extends PsiFileBase {
  public GoFile(@NotNull FileViewProvider viewProvider) {
    super(viewProvider, GoLanguage.INSTANCE);
  }

  private CachedValue<List<GoTopLevelDeclaration>> myDeclarationsValue;

  @NotNull
  public List<GoTopLevelDeclaration> getDeclarations() {
    if (myDeclarationsValue == null) {
      myDeclarationsValue =
        CachedValuesManager.getManager(getProject()).createCachedValue(new CachedValueProvider<List<GoTopLevelDeclaration>>() {
          @Override
          public Result<List<GoTopLevelDeclaration>> compute() {
            return Result.create(calcDeclarations(), GoFile.this);
          }
        }, false);
    }
    return myDeclarationsValue.getValue();
  }

  private List<GoTopLevelDeclaration> calcDeclarations() {
    final List<GoTopLevelDeclaration> result = new ArrayList<GoTopLevelDeclaration>();
    processChildrenDummyAware(this, new Processor<PsiElement>() {
      @Override
      public boolean process(PsiElement psiElement) {
        if (psiElement instanceof GoTopLevelDeclaration) {
          result.add((GoTopLevelDeclaration)psiElement);
        }
        return true;
      }
    });
    return result;
  }

  @NotNull
  @Override
  public FileType getFileType() {
    return GoFileType.INSTANCE;
  }

  private static boolean processChildrenDummyAware(GoFile file, final Processor<PsiElement> processor) {
    return new Processor<PsiElement>() {
      @Override
      public boolean process(PsiElement psiElement) {
        for (PsiElement child = psiElement.getFirstChild(); child != null; child = child.getNextSibling()) {
          if (child instanceof GeneratedParserUtilBase.DummyBlock) {
            if (!process(child)) return false;
          }
          else if (!processor.process(child)) return false;
        }
        return true;
      }
    }.process(file);
  }
}
