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

package com.goide.stubs.index;

import com.goide.runconfig.testing.GoTestFinder;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ContentIterator;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.VirtualFileWithId;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.CachedValue;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.util.indexing.FileBasedIndex;
import com.intellij.util.indexing.IdFilter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.BitSet;

public class GoIdFilter extends IdFilter {
  public static final Logger LOG = Logger.getInstance("#com.intellij.ide.util.gotoByName.DefaultFileNavigationContributor");
  private static final Key<CachedValue<IdFilter>> PRODUCTION_FILTER = Key.create("PRODUCTION");
  private static final Key<CachedValue<IdFilter>> TESTS_FILTER = Key.create("TESTS");

  private final BitSet myIdSet;

  private GoIdFilter(@NotNull BitSet idSet) {
    myIdSet = idSet;
  }

  @Override
  public boolean containsFileId(int id) {
    return id >= 0 && myIdSet.get(id);
  }

  public static IdFilter getProductionFilter(@NotNull Project project) {
    return createIdFilter(project, PRODUCTION_FILTER, file -> !file.isDirectory() && !GoTestFinder.isTestFile(file));
  }

  public static IdFilter getTestsFilter(@NotNull Project project) {
    return createIdFilter(project, TESTS_FILTER, file -> !file.isDirectory() && GoTestFinder.isTestFile(file));
  }

  private static IdFilter createIdFilter(@NotNull Project project,
                                         @NotNull Key<CachedValue<IdFilter>> cacheKey,
                                         @NotNull Condition<VirtualFile> filterCondition) {
    return CachedValuesManager.getManager(project).getCachedValue(project, cacheKey, () -> {
      BitSet bitSet = new BitSet();
      ContentIterator iterator = fileOrDir -> {
        if (filterCondition.value(fileOrDir)) {
          addToBitSet(bitSet, fileOrDir);
        }
        ProgressManager.checkCanceled();
        return true;
      };
      FileBasedIndex.getInstance().iterateIndexableFiles(iterator, project, null);
      return CachedValueProvider.Result.create(new GoIdFilter(bitSet), ProjectRootManager.getInstance(project),
                                               VirtualFileManager.VFS_STRUCTURE_MODIFICATIONS);
    }, false);
  }

  @Nullable
  public static IdFilter getFilesFilter(@NotNull GlobalSearchScope scope) {
    if (scope instanceof GlobalSearchScope.FilesScope) {
      BitSet bitSet = new BitSet();
      for (VirtualFile file : (GlobalSearchScope.FilesScope)scope) {
        addToBitSet(bitSet, file);
      }
      return new GoIdFilter(bitSet);
    }
    return null;
  }

  private static void addToBitSet(@NotNull BitSet set, @NotNull VirtualFile file) {
    if (file instanceof VirtualFileWithId) {
      int id = ((VirtualFileWithId)file).getId();
      if (id < 0) id = -id; // workaround for encountering invalid files, see EA-49915, EA-50599
      set.set(id);
    }
  }
}
