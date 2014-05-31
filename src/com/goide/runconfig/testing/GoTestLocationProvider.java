package com.goide.runconfig.testing;

import com.goide.psi.GoFunctionDeclaration;
import com.goide.stubs.index.GoFunctionIndex;
import com.intellij.execution.Location;
import com.intellij.execution.PsiLocation;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.testIntegration.TestLocationProvider;
import com.intellij.util.Function;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GoTestLocationProvider implements TestLocationProvider {
  @NotNull
  @Override
  public List<Location> getLocation(@NotNull String protocolId, @NotNull String locationData, @NotNull final Project project) {
    return ContainerUtil.map(GoFunctionIndex.find(locationData, project, GlobalSearchScope.projectScope(project)), new Function<GoFunctionDeclaration, Location>() {
      @Override
      public Location fun(GoFunctionDeclaration declaration) {
        return PsiLocation.fromPsiElement(project, declaration);
      }
    });
  }
}
