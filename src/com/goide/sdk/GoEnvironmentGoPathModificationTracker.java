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

package com.goide.sdk;

import com.goide.GoEnvironmentUtil;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.*;
import com.intellij.util.SystemProperties;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Collection;
import java.util.Set;

public class GoEnvironmentGoPathModificationTracker {
  private final Set<String> pathsToTrack = ContainerUtil.newHashSet();
  private final Collection<VirtualFile> goPathRoots = ContainerUtil.newLinkedHashSet();

  public GoEnvironmentGoPathModificationTracker() {
    String goPath = GoEnvironmentUtil.retrieveGoPathFromEnvironment();
    if (goPath != null) {
      String home = SystemProperties.getUserHome();
      for (String s : StringUtil.split(goPath, File.pathSeparator)) {
        if (home != null) {
          pathsToTrack.add(s.replaceAll("\\$HOME", home));
        }
      }
    }
    recalculateFiles();
    
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

      private void handleEvent(VirtualFileEvent event) {
        if (pathsToTrack.contains(event.getFile().getPath())) {
          recalculateFiles();
        }
      }
    });
  }

  private Collection<VirtualFile> recalculateFiles() {
    Collection<VirtualFile> result = ContainerUtil.newLinkedHashSet();
    for (String path : pathsToTrack) {
      ContainerUtil.addIfNotNull(result, LocalFileSystem.getInstance().findFileByPath(path));
    }
    updateGoPathRoots(result);
    return result;
  }

  private synchronized void updateGoPathRoots(Collection<VirtualFile> newRoots) {
    goPathRoots.clear();
    goPathRoots.addAll(newRoots);
  }
  
  private synchronized Collection<VirtualFile> getGoPathRoots() {
    return goPathRoots;
  }

  public static Collection<VirtualFile> getGoEnvironmentGoPathRoots() {
    return ServiceManager.getService(GoEnvironmentGoPathModificationTracker.class).getGoPathRoots();
  }
}
