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

package com.goide.sdk;

import com.goide.GoConstants;
import com.goide.configuration.GoSdkConfigurable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ComponentManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.libraries.LibraryTable;
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.util.ObjectUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GoSmallIDEsSdkService extends GoSdkService {
  public static final String LIBRARY_NAME = "Go SDK";

  public GoSmallIDEsSdkService(@NotNull Project project) {
    super(project);
  }

  @Nullable
  @Override
  public String getSdkHomePath(@Nullable Module module) {
    ComponentManager holder = ObjectUtils.notNull(module, myProject);
    return CachedValuesManager.getManager(myProject).getCachedValue(holder, () -> CachedValueProvider.Result
      .create(ApplicationManager.getApplication().runReadAction(new Computable<String>() {
      @Nullable
      @Override
      public String compute() {
        LibraryTable table = LibraryTablesRegistrar.getInstance().getLibraryTable(myProject);
        for (Library library : table.getLibraries()) {
          String libraryName = library.getName();
          if (libraryName != null && libraryName.startsWith(LIBRARY_NAME)) {
            for (VirtualFile root : library.getFiles(OrderRootType.CLASSES)) {
              if (isGoSdkLibRoot(root)) {
                return libraryRootToSdkPath(root);
              }
            }
          }
        }
        return null;
      }
    }), this));
  }

  @Nullable
  @Override
  public String getSdkVersion(@Nullable Module module) {
    String parentVersion = super.getSdkVersion(module);
    if (parentVersion != null) {
      return parentVersion;
    }

    ComponentManager holder = ObjectUtils.notNull(module, myProject);
    return CachedValuesManager.getManager(myProject).getCachedValue(holder, () -> {
      String result = null;
      String sdkHomePath = getSdkHomePath(module);
      if (sdkHomePath != null) {
        result = GoSdkUtil.retrieveGoVersion(sdkHomePath);
      }
      return CachedValueProvider.Result.create(result, this);
    });
  }

  @Override
  public void chooseAndSetSdk(@Nullable Module module) {
    ShowSettingsUtil.getInstance().editConfigurable(myProject, new GoSdkConfigurable(myProject, true));
  }

  @Nullable
  @Override
  public Configurable createSdkConfigurable() {
    return !myProject.isDefault() ? new GoSdkConfigurable(myProject, false) : null;
  }

  @Override
  public boolean isGoModule(@Nullable Module module) {
    return super.isGoModule(module) && getSdkHomePath(module) != null;
  }

  public static boolean isGoSdkLibRoot(@NotNull VirtualFile root) {
    return root.isInLocalFileSystem() &&
           root.isDirectory() &&
           (VfsUtilCore.findRelativeFile(GoConstants.GO_VERSION_FILE_PATH, root) != null ||
            VfsUtilCore.findRelativeFile(GoConstants.GO_VERSION_NEW_FILE_PATH, root) != null
           );
  }
}
