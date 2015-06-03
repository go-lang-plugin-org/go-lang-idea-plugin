/*
 * Copyright 2013-2015 Sergey Ignatov, Alexander Zolotov, Mihai Toader, Florin Patan
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

import com.goide.psi.GoFunctionDeclaration;
import com.goide.psi.GoMethodDeclaration;
import com.goide.psi.GoTypeSpec;
import com.goide.stubs.index.GoFunctionIndex;
import com.goide.stubs.index.GoTypesIndex;
import com.intellij.execution.Location;
import com.intellij.execution.PsiLocation;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.testIntegration.TestLocationProvider;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

// todo: deprecated, remove in IDEA 15.1
public class GoTestLocationProvider implements TestLocationProvider {
  public static final String PROTOCOL = "gotest";

  @NotNull
  @Override
  public List<Location> getLocation(@NotNull String protocolId, @NotNull String locationData, @NotNull final Project project) {
    if (!PROTOCOL.equals(protocolId)) {
      return Collections.emptyList();
    }

    List<Location> locations = ContainerUtil.newArrayList();
    String[] locationDataItems = locationData.split("\\.");

    // Location is a function name, e.g. `TestCheckItOut`
    if (locationDataItems.length == 1) {
      Collection<GoFunctionDeclaration> goFunctionDeclarations = GoFunctionIndex.find(
        locationData, project, GlobalSearchScope.projectScope(project));
      for (GoFunctionDeclaration goFunctionDeclaration : goFunctionDeclarations) {
        Location<GoFunctionDeclaration> functionLocation = PsiLocation.fromPsiElement(project, goFunctionDeclaration);
        ContainerUtil.addIfNotNull(locations, functionLocation);
      }
      return locations;
    }

    // Location is a method name, e.g. `FooSuite.TestCheckItOut`
    if (locationDataItems.length == 2) {
      Collection<GoTypeSpec> goTypeSpecs = GoTypesIndex.find(
        locationDataItems[0], project, GlobalSearchScope.projectScope(project));
      for (GoTypeSpec goTypeSpec : goTypeSpecs) {
        for (GoMethodDeclaration method : goTypeSpec.getMethods()) {
          if (locationDataItems[1].equals(method.getName())) {
            Location<GoMethodDeclaration> methodLocation = PsiLocation.fromPsiElement(method);
            ContainerUtil.addIfNotNull(locations, methodLocation);
          }
        }
      }
      return locations;
    }

    throw new RuntimeException("Unsupported location: " + locationData);
  }

}
