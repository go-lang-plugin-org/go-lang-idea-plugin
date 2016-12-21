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

import com.goide.GoCodeInsightFixtureTestCase;
import com.goide.SdkAware;
import com.goide.project.GoApplicationLibrariesService;
import com.goide.project.GoModuleLibrariesInitializer;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.roots.*;
import com.intellij.openapi.roots.impl.OrderEntryUtil;
import com.intellij.openapi.roots.impl.libraries.LibraryEx;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

@SdkAware
@SuppressWarnings("ConstantConditions")
public class GoPathLibraryTest extends GoCodeInsightFixtureTestCase {
  private final Collection<VirtualFile> contentRootsToClean = ContainerUtil.newHashSet();
  private final Collection<VirtualFile> tempRootsToClean = ContainerUtil.newHashSet();

  @Override
  protected void tearDown() throws Exception {
    try {
      for (VirtualFile file : tempRootsToClean) {
        ApplicationManager.getApplication().runWriteAction(() -> {
          try {
            file.delete(this);
          }
          catch (IOException e) {
            e.printStackTrace();
          }
        });
      }
    }
    finally {
      tempRootsToClean.clear();
    }
    ModifiableRootModel model = ModuleRootManager.getInstance(myModule).getModifiableModel();
    try {
      for (ContentEntry entry : model.getContentEntries()) {
        if (contentRootsToClean.contains(entry.getFile())) {
          model.removeContentEntry(entry);
        }
      }
      ApplicationManager.getApplication().runWriteAction(model::commit);
    }
    finally {
      contentRootsToClean.clear();
      if (!model.isDisposed()) model.dispose();
    }
    super.tearDown();
  }

  /**
   * src <content root>
   * goPath <gopath>
   * - src
   * -- test
   */
  public void testAddGoPathAsLibrary() throws IOException {
    Ref<VirtualFile> goPathContent = Ref.create(); 
    WriteAction.run(() -> {
      VirtualFile goPath = createGoPath();
      goPathContent.set(goPath.createChildDirectory(this, "src").createChildDirectory(this, "test"));
      GoApplicationLibrariesService.getInstance().setLibraryRootUrls(goPath.getUrl());
    });
    assertLibrary(Collections.singletonList(goPathContent.get().getUrl()), "temp:///src");
  }

  /**
   * src <content root>
   * goPath <gopath>
   * - src
   * -- contentRoot <content root>
   * -- notContentRoot
   */
  public void testExcludeChildContentRootFromLibrary() throws IOException {
    Ref<VirtualFile> goPath = Ref.create();
    Ref<VirtualFile> contentRoot = Ref.create();
    Ref<VirtualFile> notContentRoot = Ref.create();
    WriteAction.run(() -> {
      goPath.set(createGoPath());
      VirtualFile src = goPath.get().createChildDirectory(this, "src");
      contentRoot.set(src.createChildDirectory(this, "contentRoot"));
      notContentRoot.set(src.createChildDirectory(this, "notContentRoot"));
      addContentRoot(contentRoot.get());
    });
    GoApplicationLibrariesService.getInstance().setLibraryRootUrls(goPath.get().getUrl());
    assertLibrary(Collections.singletonList(notContentRoot.get().getUrl()), "temp:///src", contentRoot.get().getUrl());
  }

  /**
   * src <content root>
   * contentRoot <content root>
   * - gopath <gopath>
   * -- src
   * --- test
   * otherGoPath <gopath>
   * -- src
   * --- test
   */
  public void testExcludeParentContentRootFromLibrary() throws IOException {
    Ref<VirtualFile> contentRoot = Ref.create();
    Ref<VirtualFile> goPathContent = Ref.create();
    Ref<VirtualFile> otherGoPathContent = Ref.create();
    WriteAction.run(() -> {
      contentRoot.set(createGoPath());
      VirtualFile goPath = contentRoot.get().createChildDirectory(this, "gopath");
      goPathContent.set(goPath.createChildDirectory(this, "src").createChildDirectory(this, "test"));

      VirtualFile otherGoPath = createGoPath();
      otherGoPathContent.set(otherGoPath.createChildDirectory(this, "src").createChildDirectory(this, "test"));
      GoApplicationLibrariesService.getInstance().setLibraryRootUrls(goPath.getUrl(), otherGoPath.getUrl());
    });

    assertLibrary(ContainerUtil.newHashSet(goPathContent.get().getUrl(), otherGoPathContent.get().getUrl()), "temp:///src");

    WriteAction.run(() -> addContentRoot(contentRoot.get()));
    assertLibrary(Collections.singletonList(otherGoPathContent.get().getUrl()), "temp:///src", contentRoot.get().getUrl());
  }

  /**
   * src <content root>
   * gopath <gopath>
   * - src
   * -- subdir
   * --- contentRoot <content root>
   */
  public void testUpdateLibraryOnAddingContentRoot() throws IOException {
    Ref<VirtualFile> goPathContent = Ref.create();
    WriteAction.run(() -> {
      VirtualFile goPath = createGoPath();
      goPathContent.set(goPath.createChildDirectory(this, "src").createChildDirectory(this, "subdir"));
      GoApplicationLibrariesService.getInstance().setLibraryRootUrls(goPath.getUrl());
    });
    assertLibrary(Collections.singletonList(goPathContent.get().getUrl()), "temp:///src");

    Ref<VirtualFile> contentRoot = Ref.create();
    WriteAction.run(() -> {
      contentRoot.set(goPathContent.get().createChildDirectory(this, "contentRoot"));
      addContentRoot(contentRoot.get());
    });
    assertLibrary(Collections.singletonList(goPathContent.get().getUrl()), "temp:///src", contentRoot.get().getUrl());
  }

  private void addContentRoot(@NotNull VirtualFile contentRoot) {
    ModifiableRootModel model = ModuleRootManager.getInstance(myModule).getModifiableModel();
    try {
      model.addContentEntry(contentRoot);
      contentRootsToClean.add(contentRoot);
      model.commit();
    }
    finally {
      if (!model.isDisposed()) model.dispose();
    }
  }

  private void assertLibrary(@NotNull Collection<String> libUrls, String... exclusionUrls) {
    UIUtil.dispatchAllInvocationEvents();

    GoModuleLibrariesInitializer initializer = myModule.getComponent(GoModuleLibrariesInitializer.class);
    ModuleRootManager model = ModuleRootManager.getInstance(myModule);
    LibraryOrderEntry libraryOrderEntry = OrderEntryUtil.findLibraryOrderEntry(model, initializer.getLibraryName());
    if (libUrls.isEmpty()) {
      assertNull(libraryOrderEntry);
      return;
    }
    LibraryEx library = (LibraryEx)libraryOrderEntry.getLibrary();
    assertNotNull(library);
    assertSameElements(Arrays.asList(library.getUrls(OrderRootType.CLASSES)), libUrls);
    assertSameElements(library.getExcludedRootUrls(), exclusionUrls);
  }

  private VirtualFile createGoPath() throws IOException {
    VirtualFile goPath = VfsUtil.findFileByIoFile(FileUtil.createTempDirectory("go", "path"), true);
    tempRootsToClean.add(goPath);
    return goPath;
  }
}
