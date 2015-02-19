/*
 * Copyright 2013-2014 Sergey Ignatov, Alexander Zolotov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.goide.go;

import com.goide.psi.GoFile;
import com.goide.psi.GoNamedElement;
import com.goide.tree.GoStructureViewFactory;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.stubs.StubIndexKey;
import com.intellij.util.ObjectUtils;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public class GoGotoUtil {
  @NotNull
  public static <T extends GoNamedElement> NavigationItem[] getItemsByName(@NotNull String name,
                                                                           @NotNull Project project,
                                                                           boolean includeNonProjectItems,
                                                                           @NotNull StubIndexKey<String, T> key, 
                                                                           @NotNull Class<T> clazz) {
    GlobalSearchScope scope = includeNonProjectItems ? GlobalSearchScope.allScope(project) : GlobalSearchScope.projectScope(project);
    Collection<T> result = StubIndex.getElements(key, name, project, scope, clazz);
    List<NavigationItem> items = ContainerUtil.newArrayListWithCapacity(result.size());
    for (final GoNamedElement element : result) {
      items.add(new GoStructureViewFactory.Element(element) {
        @Override
        public String getLocationString() {
          GoFile file = element.getContainingFile();
          return "(in " + ObjectUtils.notNull(file.getImportPath(), ObjectUtils.notNull(file.getPackageName(), file.getName())) + ")";
        }
      });
    }
    return items.toArray(new NavigationItem[items.size()]);
  }
}
