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

package com.goide.appengine;

import com.goide.util.GoUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.SimpleModificationTracker;
import com.intellij.openapi.util.UserDataHolder;
import com.intellij.openapi.vfs.*;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.util.ObjectUtils;
import com.intellij.util.PathUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class YamlFilesModificationTracker extends SimpleModificationTracker {
  public YamlFilesModificationTracker(@NotNull Project project) {
    VirtualFileManager.getInstance().addVirtualFileListener(new VirtualFileAdapter() {
      @Override
      public void fileCreated(@NotNull VirtualFileEvent event) {
        handleEvent(event);
      }

      @Override
      public void fileDeleted(@NotNull VirtualFileEvent event) {
        handleEvent(event);
      }

      @Override
      public void fileMoved(@NotNull VirtualFileMoveEvent event) {
        handleEvent(event);
      }

      @Override
      public void fileCopied(@NotNull VirtualFileCopyEvent event) {
        handleEvent(event);
      }

      private void handleEvent(@NotNull VirtualFileEvent event) {
        if ("yaml".equals(PathUtil.getFileExtension(event.getFileName()))) {
          incModificationCount();
        }
      }
    }, project);
  }

  public static YamlFilesModificationTracker getInstance(@NotNull Project project) {
    return ServiceManager.getService(project, YamlFilesModificationTracker.class);
  }

  @NotNull
  public static Collection<VirtualFile> getYamlFiles(@NotNull Project project, @Nullable Module module) {
    UserDataHolder dataHolder = ObjectUtils.notNull(module, project);
    return CachedValuesManager.getManager(project).getCachedValue(dataHolder, () -> {
      Collection<VirtualFile> yamlFiles = ApplicationManager.getApplication().runReadAction(new Computable<Collection<VirtualFile>>() {
        @Override
        public Collection<VirtualFile> compute() {
          return FilenameIndex.getAllFilesByExt(project, "yaml", GoUtil.moduleScopeWithoutLibraries(project, module));
        }
      });
      return CachedValueProvider.Result.create(yamlFiles, getInstance(project));
    });
  }
}
