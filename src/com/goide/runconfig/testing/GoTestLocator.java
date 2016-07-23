/*
 * Copyright 2013-2016 Sergey Ignatov, Alexander Zolotov, Florin Patan
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

package com.goide.runconfig.testing;

import com.goide.psi.GoMethodDeclaration;
import com.goide.psi.GoTypeSpec;
import com.goide.stubs.index.GoFunctionIndex;
import com.goide.stubs.index.GoIdFilter;
import com.goide.stubs.index.GoTypesIndex;
import com.intellij.execution.Location;
import com.intellij.execution.PsiLocation;
import com.intellij.execution.testframework.sm.runner.SMTestLocator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.indexing.IdFilter;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class GoTestLocator implements SMTestLocator {
  public static final String SUITE_PROTOCOL = "gosuite";
  public static final String PROTOCOL = "gotest";

  public static final SMTestLocator INSTANCE = new GoTestLocator();

  private GoTestLocator() {}

  @NotNull
  @Override
  public List<Location> getLocation(@NotNull String protocolId,
                                    @NotNull String path,
                                    @NotNull Project project,
                                    @NotNull GlobalSearchScope scope) {
    if (PROTOCOL.equals(protocolId)) {
      IdFilter idFilter = GoIdFilter.getTestsFilter(project);
      List<String> locationDataItems = StringUtil.split(path, ".");
      // Location is a function name, e.g. `TestCheckItOut`
      if (locationDataItems.size() == 1) {
        return ContainerUtil.mapNotNull(GoFunctionIndex.find(path, project, scope, idFilter),
                                        function -> PsiLocation.fromPsiElement(project, function));
      }

      // Location is a method name, e.g. `FooSuite.TestCheckItOut`
      if (locationDataItems.size() == 2) {
        List<Location> locations = ContainerUtil.newArrayList();
        for (GoTypeSpec typeSpec : GoTypesIndex.find(locationDataItems.get(0), project, scope, idFilter)) {
          for (GoMethodDeclaration method : typeSpec.getMethods()) {
            if (locationDataItems.get(1).equals(method.getName())) {
              ContainerUtil.addIfNotNull(locations, PsiLocation.fromPsiElement(method));
            }
          }
        }
        return locations;
      }
    }
    else if (SUITE_PROTOCOL.equals(protocolId)) {
      IdFilter idFilter = GoIdFilter.getTestsFilter(project);
      return ContainerUtil.mapNotNull(GoTypesIndex.find(path, project, scope, idFilter),
                                      spec -> PsiLocation.fromPsiElement(project, spec));
    }
    else {
      return Collections.emptyList();
    }

    throw new RuntimeException("Unsupported location: " + path);
  }
}
