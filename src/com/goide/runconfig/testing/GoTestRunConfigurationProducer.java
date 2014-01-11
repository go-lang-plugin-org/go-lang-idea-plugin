package com.goide.runconfig.testing;

import com.goide.psi.GoFile;
import com.goide.psi.GoPackageClause;
import com.goide.psi.impl.GoFunctionDeclarationImpl;
import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.actions.RunConfigurationProducer;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Nullable;

public class GoTestRunConfigurationProducer extends RunConfigurationProducer<GoTestRunConfiguration> implements Cloneable {

  public GoTestRunConfigurationProducer() {
    super(GoTestRunConfigurationType.getInstance());
  }

  @Override
  protected boolean setupConfigurationFromContext(GoTestRunConfiguration configuration, ConfigurationContext context, Ref sourceElement) {
    PsiElement contextElement = getContextElement(context);
    if (contextElement == null) {
      return false;
    }

    configuration.setModule(ModuleUtilCore.findModuleForPsiElement(contextElement));
    if (contextElement instanceof PsiDirectory) {
      configuration.setName("All in '" + ((PsiDirectory)contextElement).getName() + "'");
      configuration.setKind(GoTestRunConfiguration.Kind.DIRECTORY);
      String directoryPath = ((PsiDirectory)contextElement).getVirtualFile().getPath();
      configuration.setDirectoryPath(directoryPath);
      configuration.setWorkingDirectory(directoryPath);
      return true;
    }
    else {
      PsiFile file = contextElement.getContainingFile();
      if (GoTestFinder.isTestFile(file)) {
        if (PsiTreeUtil.getNonStrictParentOfType(contextElement, GoPackageClause.class) != null) {
          String packageName = StringUtil.notNullize(((GoFile)file).getPackageName());
          configuration.setKind(GoTestRunConfiguration.Kind.PACKAGE);
          configuration.setPackage(packageName);
          configuration.setName("All in '" + packageName + "'");
        }
        else {
          configuration.setName(file.getName());
          configuration.setKind(GoTestRunConfiguration.Kind.FILE);
          configuration.setFilePath(file.getVirtualFile().getPath());
          GoFunctionDeclarationImpl function = PsiTreeUtil.getNonStrictParentOfType(contextElement, GoFunctionDeclarationImpl.class);
          if (function != null) {
            String functionName = StringUtil.notNullize(function.getName());
            if (functionName.startsWith("Test")) {
              configuration.setName(functionName + " in " + file.getName());
              configuration.setPattern("^" + functionName + "$");
            }
          }
        }
        return true;
      }
    }

    return false;
  }

  @Override
  public boolean isConfigurationFromContext(GoTestRunConfiguration configuration, ConfigurationContext context) {
    return false;
  }

  @Nullable
  private static PsiElement getContextElement(@Nullable ConfigurationContext context) {
    if (context == null) {
      return null;
    }
    PsiElement psiElement = context.getPsiLocation();
    if (psiElement == null || !psiElement.isValid()) {
      return null;
    }
    return psiElement;
  }
}
