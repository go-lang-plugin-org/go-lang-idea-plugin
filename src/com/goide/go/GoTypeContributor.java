package com.goide.go;

import com.goide.psi.GoTypeSpec;
import com.goide.stubs.index.GoTypesIndex;
import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.Project;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.util.ArrayUtil;
import org.jetbrains.annotations.NotNull;

public class GoTypeContributor implements ChooseByNameContributor {
  @NotNull
  @Override
  public String[] getNames(@NotNull Project project, boolean includeNonProjectItems) {
    return ArrayUtil.toStringArray(StubIndex.getInstance().getAllKeys(GoTypesIndex.KEY, project));
  }

  @NotNull
  @Override
  public NavigationItem[] getItemsByName(String name, String pattern, Project project, boolean includeNonProjectItems) {
    return GoGotoUtil.getItemsByName(name, project, includeNonProjectItems, GoTypesIndex.KEY, GoTypeSpec.class);
  }
}
