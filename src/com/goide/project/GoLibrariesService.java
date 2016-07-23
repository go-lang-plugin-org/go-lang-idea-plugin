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

package com.goide.project;

import com.goide.GoLibrariesState;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.ModificationTracker;
import com.intellij.openapi.util.SimpleModificationTracker;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.messages.Topic;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

public abstract class GoLibrariesService<T extends GoLibrariesState> extends SimpleModificationTracker implements PersistentStateComponent<T> {
  public static final Topic<LibrariesListener> LIBRARIES_TOPIC = Topic.create("libraries changes", LibrariesListener.class);
  protected final T myState = createState();

  @NotNull
  @Override
  public T getState() {
    return myState;
  }

  @Override
  public void loadState(T state) {
    XmlSerializerUtil.copyBean(state, myState);
  }

  @NotNull
  protected T createState() {
    //noinspection unchecked
    return (T)new GoLibrariesState();
  }

  @NotNull
  public static Collection<? extends VirtualFile> getUserDefinedLibraries(@NotNull Module module) {
    Set<VirtualFile> result = ContainerUtil.newLinkedHashSet();
    result.addAll(goRootsFromUrls(GoModuleLibrariesService.getInstance(module).getLibraryRootUrls()));
    result.addAll(getUserDefinedLibraries(module.getProject()));
    return result;
  }

  @NotNull
  public static Collection<? extends VirtualFile> getUserDefinedLibraries(@NotNull Project project) {
    Set<VirtualFile> result = ContainerUtil.newLinkedHashSet();
    result.addAll(goRootsFromUrls(GoProjectLibrariesService.getInstance(project).getLibraryRootUrls()));
    result.addAll(getUserDefinedLibraries());
    return result;
  }

  @NotNull
  public static Collection<? extends VirtualFile> getUserDefinedLibraries() {
    return goRootsFromUrls(GoApplicationLibrariesService.getInstance().getLibraryRootUrls());
  }

  @NotNull
  public static ModificationTracker[] getModificationTrackers(@NotNull Project project, @Nullable Module module) {
    return module != null
           ? new ModificationTracker[]{GoModuleLibrariesService.getInstance(module), GoProjectLibrariesService.getInstance(module.getProject()), GoApplicationLibrariesService.getInstance()}
           : new ModificationTracker[]{GoProjectLibrariesService.getInstance(project), GoApplicationLibrariesService.getInstance()};
  }
  
  public void setLibraryRootUrls(@NotNull String... libraryRootUrls) {
    setLibraryRootUrls(Arrays.asList(libraryRootUrls));
  }
  
  public void setLibraryRootUrls(@NotNull Collection<String> libraryRootUrls) {
    if (!myState.getUrls().equals(libraryRootUrls)) {
      myState.setUrls(libraryRootUrls);
      incModificationCount();
      ApplicationManager.getApplication().getMessageBus().syncPublisher(LIBRARIES_TOPIC).librariesChanged(libraryRootUrls);
    }
  }

  @NotNull
  public Collection<String> getLibraryRootUrls() {
    return myState.getUrls();
  }

  @NotNull
  private static Collection<? extends VirtualFile> goRootsFromUrls(@NotNull Collection<String> urls) {
    return ContainerUtil.mapNotNull(urls, url -> VirtualFileManager.getInstance().findFileByUrl(url));
  }

  public interface LibrariesListener {
    void librariesChanged(@NotNull Collection<String> newRootUrls);
  }
}
