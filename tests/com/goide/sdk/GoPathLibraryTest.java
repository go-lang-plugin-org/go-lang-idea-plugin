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
import com.goide.GoModuleType;
import com.goide.project.GoApplicationLibrariesService;
import com.goide.project.GoModuleLibrariesInitializer;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.roots.*;
import com.intellij.openapi.roots.impl.OrderEntryUtil;
import com.intellij.openapi.roots.impl.libraries.LibraryEx;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.fixtures.DefaultLightProjectDescriptor;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

@SuppressWarnings("ConstantConditions")
public class GoPathLibraryTest extends GoCodeInsightFixtureTestCase {
  @NotNull
  @Override
  protected LightProjectDescriptor getProjectDescriptor() {
    return new DefaultLightProjectDescriptor() {
      @NotNull
      @Override
      public ModuleType getModuleType() {
        return GoModuleType.getInstance();
      }
    };
  }
  private final Collection<VirtualFile> contentRootsToClean = ContainerUtil.newHashSet();
  private final Collection<VirtualFile> tempRootsToClean = ContainerUtil.newHashSet();

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    GoModuleLibrariesInitializer.setTestingMode(getTestRootDisposable());
  }

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
    VirtualFile goPath = createGoPath();
    VirtualFile goPathContent = goPath.createChildDirectory(this, "src").createChildDirectory(this, "test");
    GoApplicationLibrariesService.getInstance().setLibraryRootUrls(goPath.getUrl());
    assertLibrary(Collections.singletonList(goPathContent.getUrl()), "temp:///src");
  }

  /**
   * src <content root>
   * goPath <gopath>
   * - src
   * -- contentRoot <content root>
   * -- notContentRoot
   */
  public void testExcludeChildContentRootFromLibrary() throws IOException {
    VirtualFile goPath = createGoPath();
    VirtualFile src = goPath.createChildDirectory(this, "src");
    VirtualFile contentRoot = src.createChildDirectory(this, "contentRoot");
    VirtualFile notContentRoot = src.createChildDirectory(this, "notContentRoot");
    addContentRoot(contentRoot);
    GoApplicationLibrariesService.getInstance().setLibraryRootUrls(goPath.getUrl());
    assertLibrary(Collections.singletonList(notContentRoot.getUrl()), "temp:///src", contentRoot.getUrl());
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
    VirtualFile contentRoot = createGoPath();
    VirtualFile goPath = contentRoot.createChildDirectory(this, "gopath");
    VirtualFile goPathContent = goPath.createChildDirectory(this, "src").createChildDirectory(this, "test");

    VirtualFile otherGoPath = createGoPath();
    VirtualFile otherGoPathContent = otherGoPath.createChildDirectory(this, "src").createChildDirectory(this, "test");
    GoApplicationLibrariesService.getInstance().setLibraryRootUrls(goPath.getUrl(), otherGoPath.getUrl());
    assertLibrary(ContainerUtil.newHashSet(goPathContent.getUrl(), otherGoPathContent.getUrl()), "temp:///src");

    addContentRoot(contentRoot);
    assertLibrary(Collections.singletonList(otherGoPathContent.getUrl()), "temp:///src", contentRoot.getUrl());
  }

  /**
   * src <content root>
   * gopath <gopath>
   * - src
   * -- subdir
   * --- contentRoot <content root>
   */
  public void testUpdateLibraryOnAddingContentRoot() throws IOException {
    VirtualFile goPath = createGoPath();
    VirtualFile goPathContent = goPath.createChildDirectory(this, "src").createChildDirectory(this, "subdir");

    GoApplicationLibrariesService.getInstance().setLibraryRootUrls(goPath.getUrl());
    assertLibrary(Collections.singletonList(goPathContent.getUrl()), "temp:///src");

    VirtualFile contentRoot = goPathContent.createChildDirectory(this, "contentRoot");
    addContentRoot(contentRoot);
    assertLibrary(Collections.singletonList(goPathContent.getUrl()), "temp:///src", contentRoot.getUrl());
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

  @Override
  protected boolean isWriteActionRequired() {
    return true;
  }
}
