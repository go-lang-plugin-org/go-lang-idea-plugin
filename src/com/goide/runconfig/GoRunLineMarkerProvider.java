package com.goide.runconfig;

import com.goide.GoConstants;
import com.goide.GoTypes;
import com.goide.psi.GoFunctionDeclaration;
import com.goide.runconfig.testing.GoTestFinder;
import com.intellij.execution.lineMarker.ExecutorAction;
import com.intellij.execution.lineMarker.RunLineMarkerContributor;
import com.intellij.icons.AllIcons;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.Function;
import org.jetbrains.annotations.Nullable;

public class GoRunLineMarkerProvider extends RunLineMarkerContributor {
  private static final Function<PsiElement, String> TOOLTIP_PROVIDER = new Function<PsiElement, String>() {
    @Override
    public String fun(PsiElement element) {
      return "Run Application";
    }
  };

  @Nullable
  @Override
  public Info getInfo(PsiElement e) {
    if (e != null && e.getNode().getElementType() == GoTypes.IDENTIFIER) {
      PsiElement parent = e.getParent();
      PsiFile file = e.getContainingFile();
      if (GoTestFinder.isTestFile(file) || !GoRunUtil.isMainGoFile(file)) {
        return null;
      }
      if (parent instanceof GoFunctionDeclaration) {
        if (GoConstants.MAIN.equals(((GoFunctionDeclaration)parent).getName())) {
          return new Info(AllIcons.RunConfigurations.TestState.Run, TOOLTIP_PROVIDER, ExecutorAction.getActions(0));
        }
      }
    }
    return null;
  }
}
