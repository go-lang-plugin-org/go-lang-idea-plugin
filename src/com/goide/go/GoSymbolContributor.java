package com.goide.go;

import com.goide.stubs.index.GoAllNamesIndex;
import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.Project;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.util.ArrayUtil;
import org.jetbrains.annotations.NotNull;

public class GoSymbolContributor implements ChooseByNameContributor {
  @NotNull
  @Override
  public String[] getNames(@NotNull Project project, boolean includeNonProjectItems) {
    return ArrayUtil.toStringArray(StubIndex.getInstance().getAllKeys(GoAllNamesIndex.ALL_NAMES, project));
  }

  @NotNull
  @Override
  public NavigationItem[] getItemsByName(String name, String pattern, Project project, boolean includeNonProjectItems) {
    return GoGotoUtil.getItemsByName(name, project, includeNonProjectItems, GoAllNamesIndex.ALL_NAMES);
  }
}
