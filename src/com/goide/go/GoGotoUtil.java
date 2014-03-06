package com.goide.go;

import com.goide.GoStructureViewFactory;
import com.goide.psi.GoNamedElement;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.stubs.StubIndexKey;
import com.intellij.util.containers.ContainerUtil;

import java.util.Collection;
import java.util.List;

public class GoGotoUtil {
  public static NavigationItem[] getItemsByName(String name, Project project, boolean includeNonProjectItems, StubIndexKey<String, ? extends GoNamedElement> key) {
    GlobalSearchScope scope = includeNonProjectItems ? GlobalSearchScope.allScope(project) : GlobalSearchScope.projectScope(project);
    Collection<? extends GoNamedElement> result = StubIndex.getInstance().get(key, name, project, scope);
    List<NavigationItem> items = ContainerUtil.newArrayListWithExpectedSize(result.size());
    for (final GoNamedElement element : result) {
      items.add(new GoStructureViewFactory.Element(element) {
        @Override
        public String getLocationString() {
          return "(in " + element.getContainingFile().getFullPackageName() + ")";
        }
      });
    }
    return items.toArray(new NavigationItem[items.size()]);
  }
}
