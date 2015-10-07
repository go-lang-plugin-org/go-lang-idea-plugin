/*
 * Copyright 2013-2015 Sergey Ignatov, Alexander Zolotov, Florin Patan
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
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.roots.LibraryOrderEntry;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.impl.OrderEntryUtil;
import com.intellij.openapi.roots.impl.libraries.LibraryEx;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.fixtures.DefaultLightProjectDescriptor;
import com.intellij.util.ui.UIUtil;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

@SuppressWarnings("ConstantConditions")
public class GoPathLibraryTest extends GoCodeInsightFixtureTestCase {
  @Override
  protected LightProjectDescriptor getProjectDescriptor() {
    return new DefaultLightProjectDescriptor() {
      @Override
      public ModuleType getModuleType() {
        return GoModuleType.getInstance();
      }
    };
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    GoModuleLibrariesInitializer.setTestingMode(getTestRootDisposable());
  }

  /**
   * src <content root>
   * goPath <gopath>
   * - src
   * -- test.go
   */
  public void testAddGoPathAsLibrary() throws IOException {
    VirtualFile goPath = VfsUtil.findFileByIoFile(FileUtil.createTempDirectory("go", "path"), true);
    VirtualFile goPathContent = goPath.createChildDirectory(this, "src").createChildData(this, "test.go");
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
    VirtualFile goPath = VfsUtil.findFileByIoFile(FileUtil.createTempDirectory("go", "path"), true);
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
   * --- test.go
   * - otherGoPath <gopath>
   * -- src
   * --- test.go
   */
  public void testExcludeParentContentRootFromLibrary() throws IOException {
    VirtualFile contentRoot = VfsUtil.findFileByIoFile(FileUtil.createTempDirectory("go", "contentRoot"), true);
    VirtualFile goPath = contentRoot.createChildDirectory(this, "gopath");
    goPath.createChildDirectory(this, "src").createChildData(this, "test.go");

    VirtualFile otherGoPath = VfsUtil.findFileByIoFile(FileUtil.createTempDirectory("go", "otherGoPath"), true);
    VirtualFile otherGoPathContent = otherGoPath.createChildDirectory(this, "src").createChildData(this, "test.go");

    addContentRoot(contentRoot);
    GoApplicationLibrariesService.getInstance().setLibraryRootUrls(goPath.getUrl(), otherGoPath.getUrl());
    assertLibrary(Collections.singletonList(otherGoPathContent.getUrl()), "temp:///src", contentRoot.getUrl());
  }

  /**
   * src <content root>
   * gopath <gopath>
   * - src
   * -- notTestData
   * -- testdata
   */
  public void testExcludeTestDataDirectories() throws IOException {
    VirtualFile goPath = VfsUtil.findFileByIoFile(FileUtil.createTempDirectory("go", "path"), true);
    VirtualFile src = goPath.createChildDirectory(this, "src");
    VirtualFile testData = src.createChildDirectory(this, "testdata");
    VirtualFile notTestData = src.createChildDirectory(this, "notTestdata");
    GoApplicationLibrariesService.getInstance().setLibraryRootUrls(goPath.getUrl());
    assertLibrary(Arrays.asList(testData.getUrl(), notTestData.getUrl()), "temp:///src", testData.getUrl());
  }

  /**
   * src <content root>
   * gopath <gopath>
   * - src
   * -- subdir
   * --- contentRoot <content root>
   */
  public void testUpdateLibraryOnAddingContentRoot() throws IOException {
    VirtualFile goPath = VfsUtil.findFileByIoFile(FileUtil.createTempDirectory("go", "path"), true);
    VirtualFile goPathContent = goPath.createChildDirectory(this, "src").createChildDirectory(this, "subdir");

    GoApplicationLibrariesService.getInstance().setLibraryRootUrls(goPath.getUrl());
    assertLibrary(Collections.singletonList(goPathContent.getUrl()), "temp:///src");

    VirtualFile contentRoot = goPathContent.createChildDirectory(this, "contentRoot");
    addContentRoot(contentRoot);
    assertLibrary(Collections.singletonList(goPathContent.getUrl()), "temp:///src", contentRoot.getUrl());
  }

  /**
   * src <content root>
   * goPath <gopath>
   * - src
   * -- subdir
   * --- testdata
   */
  public void testUpdateLibraryOnAddingDataToExistingGoPath() throws IOException {
    VirtualFile file = VfsUtil.findFileByIoFile(FileUtil.createTempDirectory("go", "test"), true);
    VirtualFile subdir = file.createChildDirectory(this, "src").createChildDirectory(this, "subdir");
    GoApplicationLibrariesService.getInstance().setLibraryRootUrls(file.getUrl());
    assertLibrary(Collections.singletonList(subdir.getUrl()), "temp:///src");

    VirtualFile newDirectory = subdir.createChildDirectory(this, "testdata");
    assertLibrary(Collections.singletonList(subdir.getUrl()), "temp:///src", newDirectory.getUrl());
  }

  private void addContentRoot(VirtualFile contentRoot) {
    ModifiableRootModel model = ModuleRootManager.getInstance(myModule).getModifiableModel();
    try {
      model.addContentEntry(contentRoot);
      model.commit();
    }
    finally {
      if (!model.isDisposed()) model.dispose();
    }
  }

  private void assertLibrary(Collection<String> libUrls, String... exclusionUrls) {
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
}
