/*
 * Copyright 2013-2014 Sergey Ignatov, Alexander Zolotov, Mihai Toader, Florin Patan
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

import com.goide.GoLibrariesState;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.util.Function;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Set;

public abstract class GoLibrariesService implements PersistentStateComponent<GoLibrariesState> {
  private GoLibrariesState myState = new GoLibrariesState();

  @NotNull
  @Override
  public GoLibrariesState getState() {
    return myState;
  }

  @Override
  public void loadState(GoLibrariesState state) {
    XmlSerializerUtil.copyBean(state, myState);
  }

  @NotNull
  public static Collection<? extends VirtualFile> getUserDefinedLibraries(@NotNull Module module) {
    final Set<VirtualFile> result = ContainerUtil.newLinkedHashSet();
    result.addAll(filesFromUrls(GoModuleLibrariesService.getInstance(module).getLibraryRootUrls()));
    result.addAll(getUserDefinedLibraries(module.getProject()));
    result.addAll(getUserDefinedLibraries());
    return result;
  }

  @NotNull
  public static Collection<? extends VirtualFile> getUserDefinedLibraries(@NotNull Project project) {
    return filesFromUrls(GoProjectLibrariesService.getInstance(project).getLibraryRootUrls());
  }

  @NotNull
  public static Collection<? extends VirtualFile> getUserDefinedLibraries() {
    return filesFromUrls(GoApplicationLibrariesService.getInstance().getLibraryRootUrls());
  }

  public void setLibraryRootUrls(@NotNull Collection<String> libraryRootUrl) {
    myState.setUrls(libraryRootUrl);
  }

  @NotNull
  public Collection<String> getLibraryRootUrls() {
    return myState.getUrls();
  }

  @NotNull
  public static Collection<? extends VirtualFile> filesFromUrls(@NotNull Collection<String> urls) {
    return ContainerUtil.skipNulls(ContainerUtil.map(urls, new Function<String, VirtualFile>() {
      @Override
      public VirtualFile fun(String url) {
        return VirtualFileManager.getInstance().findFileByUrl(url);
      }
    }));
  }
}
