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

package com.goide.project;

import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.SimpleModificationTracker;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.util.ArrayUtil;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
  name = "GoExcludedPaths",
  storages = {
    @Storage(id = "default", file = StoragePathMacros.PROJECT_FILE),
    @Storage(id = "dir", file = StoragePathMacros.PROJECT_CONFIG_DIR + "/goExcludedPaths.xml", scheme = StorageScheme.DIRECTORY_BASED)
  }
)
public class GoExcludedPathsSettings extends SimpleModificationTracker implements PersistentStateComponent<GoExcludedPathsSettings> {
  private String[] myExcludedPackages = ArrayUtil.EMPTY_STRING_ARRAY;

  public static GoExcludedPathsSettings getInstance(Project project) {
    return ServiceManager.getService(project, GoExcludedPathsSettings.class);
  }

  @Nullable
  @Override
  public GoExcludedPathsSettings getState() {
    return this;
  }

  @Override
  public void loadState(GoExcludedPathsSettings state) {
    XmlSerializerUtil.copyBean(state, this);
  }

  public String[] getExcludedPackages() {
    return myExcludedPackages;
  }

  public void setExcludedPackages(String... excludedPackages) {
    myExcludedPackages = excludedPackages;
    incModificationCount();
  }

  public boolean isExcluded(@NotNull String importPath) {
    for (String excludedPath : myExcludedPackages) {
      if (FileUtil.isAncestor(excludedPath, importPath, false)) return true;
    }
    return false;
  }

  public void excludePath(@NotNull String importPath) {
    setExcludedPackages(ArrayUtil.append(myExcludedPackages, importPath));
  }
}
