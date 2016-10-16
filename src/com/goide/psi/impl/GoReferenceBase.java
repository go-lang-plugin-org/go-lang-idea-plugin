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

package com.goide.psi.impl;

import com.goide.psi.*;
import com.goide.sdk.GoSdkUtil;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.util.ObjectUtils;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.goide.psi.impl.GoPsiImplUtil.allowed;

public abstract class GoReferenceBase<T extends GoReferenceExpressionBase> extends PsiPolyVariantReferenceBase<T> {
  public static final Key<List<PsiElement>> IMPORT_USERS = Key.create("IMPORT_USERS");
  public static final Key<String> ACTUAL_NAME = Key.create("ACTUAL_NAME");

  public GoReferenceBase(T element, TextRange range) {
    super(element, range);
  }

  @Nullable
  protected static String getPath(@Nullable PsiFile file) {
    if (file == null) return null;
    VirtualFile virtualFile = file.getOriginalFile().getVirtualFile();
    return virtualFile == null ? null : virtualFile.getPath();
  }

  private static void putIfAbsent(@NotNull GoImportSpec importSpec, @NotNull PsiElement usage) {
    //noinspection SynchronizationOnLocalVariableOrMethodParameter
    synchronized (importSpec) {
      List<PsiElement> newUsages = ContainerUtil.newSmartList(usage);
      newUsages.addAll(IMPORT_USERS.get(importSpec, ContainerUtil.emptyList()));
      importSpec.putUserData(IMPORT_USERS, newUsages);
    }
  }

  protected boolean processDirectory(@Nullable PsiDirectory dir,
                                     @Nullable GoFile file,
                                     @Nullable String packageName,
                                     @NotNull GoScopeProcessor processor,
                                     @NotNull ResolveState state,
                                     boolean localProcessing) {
    if (dir == null) return true;
    String filePath = getPath(file);
    Module module = file != null ? ModuleUtilCore.findModuleForPsiElement(file) : null;
    for (PsiFile f : dir.getFiles()) {
      if (!(f instanceof GoFile) || Comparing.equal(getPath(f), filePath)) continue;
      if (packageName != null && !packageName.equals(((GoFile)f).getPackageName())) continue;
      if (!allowed(f, file, module)) continue;
      if (!processFileEntities((GoFile)f, processor, state, localProcessing)) return false;
    }
    return true;
  }

  protected boolean processBuiltin(@NotNull GoScopeProcessor processor, @NotNull ResolveState state, @NotNull GoCompositeElement element) {
    GoFile builtin = GoSdkUtil.findBuiltinFile(element);
    return builtin == null || processFileEntities(builtin, processor, state, true);
  }

  protected boolean processImports(@NotNull GoFile file,
                                   @NotNull GoScopeProcessor processor,
                                   @NotNull ResolveState state,
                                   @NotNull GoCompositeElement element) {
    for (Map.Entry<String, Collection<GoImportSpec>> entry : file.getImportMap().entrySet()) {
      for (GoImportSpec o : entry.getValue()) {
        if (o.isForSideEffects()) continue;

        GoImportString importString = o.getImportString();
        if (o.isDot()) {
          PsiDirectory implicitDir = importString.resolve();
          boolean resolved = !processDirectory(implicitDir, file, null, processor, state, false);
          if (resolved && !processor.isCompletion()) {
            putIfAbsent(o, element);
          }
          if (resolved) return false;
        }
        else {
          if (o.getAlias() == null) {
            PsiDirectory resolve = importString.resolve();
            if (resolve != null && !processor.execute(resolve, state.put(ACTUAL_NAME, entry.getKey()))) return false;
          }
          // todo: multi-resolve into appropriate package clauses
          if (!processor.execute(o, state.put(ACTUAL_NAME, entry.getKey()))) return false;
        }
      }
    }
    return true;
  }

  @NotNull
  protected GoScopeProcessor createResolveProcessor(@NotNull Collection<ResolveResult> result,
                                                    @NotNull GoReferenceExpressionBase o) {
    return new GoScopeProcessor() {
      @Override
      public boolean execute(@NotNull PsiElement element, @NotNull ResolveState state) {
        if (element.equals(o)) return !result.add(new PsiElementResolveResult(element));
        String name = ObjectUtils.chooseNotNull(state.get(ACTUAL_NAME),
                                                element instanceof PsiNamedElement ? ((PsiNamedElement)element).getName() : null);
        if (name != null && o.getIdentifier().textMatches(name)) {
          result.add(new PsiElementResolveResult(element));
          return false;
        }
        return true;
      }
    };
  }

  protected abstract boolean processFileEntities(@NotNull GoFile file,
                                                 @NotNull GoScopeProcessor processor,
                                                 @NotNull ResolveState state,
                                                 boolean localProcessing);
}
