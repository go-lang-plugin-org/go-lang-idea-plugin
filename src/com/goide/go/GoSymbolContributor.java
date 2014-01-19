package com.goide.go;

import com.goide.GoStructureViewFactory;
import com.goide.psi.GoNamedElement;
import com.goide.stubs.index.GoAllNamesIndex;
import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.util.ArrayUtil;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public class GoSymbolContributor implements ChooseByNameContributor {
  @NotNull
  @Override
  public String[] getNames(Project project, boolean includeNonProjectItems) {
    return ArrayUtil.toStringArray(StubIndex.getInstance().getAllKeys(GoAllNamesIndex.ALL_NAMES, project));
  }

  @NotNull
  @Override
  public NavigationItem[] getItemsByName(String name, String pattern, Project project, boolean includeNonProjectItems) {
    GlobalSearchScope scope = includeNonProjectItems ? GlobalSearchScope.allScope(project) : GlobalSearchScope.projectScope(project);
    Collection<GoNamedElement> result = StubIndex.getInstance().get(GoAllNamesIndex.ALL_NAMES, name, project, scope);
    List<NavigationItem> items = ContainerUtil.newArrayListWithExpectedSize(result.size());
    for (final GoNamedElement element : result) {
      items.add(new GoStructureViewFactory.Element(element) {
        @Override
        public String getLocationString() {
          return "(in " + element.getContainingFile().getName() + ")";
        }
      });
    }
    return items.toArray(new NavigationItem[items.size()]);
  }
}
