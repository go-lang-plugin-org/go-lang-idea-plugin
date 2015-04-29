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

package com.goide.util;

import com.goide.GoConstants;
import com.goide.psi.*;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.text.CharSequenceHashingStrategy;
import gnu.trove.THashSet;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GoUtil {
  public static final String PLUGIN_VERSION = getPlugin().getVersion();

  private static final String PLUGIN_ID = "ro.redeul.google.go";
  private static final Pattern FULL = Pattern.compile("\\w+_(\\w+)_(\\w+)");
  private static final Pattern SHORT = Pattern.compile("\\w+_(\\w+)");
  private static final Set<CharSequence> LINUX = set("linux", "no", "unix", "posix", "notwin");
  private static final Set<CharSequence> MAC = set("darwin", "no", "unix", "posix", "notwin");
  private static final Set<CharSequence> WINDOWS = set("windows", "no");
  private static final Set<CharSequence> OS = set("openbsd", "plan9", "unix", "linux", "netbsd", "darwin", "dragonfly", "bsd", "windows",
                                                  "posix", "freebsd", "notwin");

  public static boolean allowed(@NotNull PsiFile file) {
    String name = StringUtil.trimEnd(FileUtil.getNameWithoutExtension(file.getName()), GoConstants.TEST_SUFFIX);
    Matcher matcher = FULL.matcher(name);
    if (matcher.matches()) {
      String os = matcher.group(1);
      String arch = matcher.group(2);
      if (!OS.contains(os)) return true;
      return os(os) && (SystemInfo.is64Bit && arch.contains("64") || SystemInfo.is32Bit && arch.contains("386"));
    }
    matcher = SHORT.matcher(name);
    if (matcher.matches()) {
      String os = matcher.group(1);
      if (!OS.contains(os)) return true;
      return os(os);
    }

    return file instanceof GoFile;
  }

  @Contract("null -> true")
  public static boolean libraryImportPathToIgnore(@Nullable String importPath) {
    if (importPath != null) {
      for (String part : StringUtil.split(importPath, "/")) {
        if (libraryDirectoryToIgnore(part)) return false;
      }
    }
    return true;
  }
  
  public static boolean libraryDirectoryToIgnore(@NotNull String name) {
    return StringUtil.startsWithChar(name, '.') || StringUtil.startsWithChar(name, '_') || GoConstants.TESTDATA_NAME.equals(name);
  }

  private static boolean os(@NotNull String os) {
    return SystemInfo.isLinux ? LINUX.contains(os) :
           SystemInfo.isMac ? MAC.contains(os) :
           !SystemInfo.isWindows || WINDOWS.contains(os);
  }

  @NotNull
  private static THashSet<CharSequence> set(@NotNull String... strings) {
    return ContainerUtil.newTroveSet(CharSequenceHashingStrategy.CASE_INSENSITIVE, strings);
  }

  public static void installFileChooser(@NotNull Project project,
                                        @NotNull TextFieldWithBrowseButton field,
                                        boolean directory) {
    installFileChooser(project, field, directory, null);
  }

  public static void installFileChooser(@NotNull Project project,
                                        @NotNull TextFieldWithBrowseButton field,
                                        boolean directory,
                                        @Nullable Condition<VirtualFile> fileFilter) {
    FileChooserDescriptor chooseDirectoryDescriptor = directory
                                                      ? FileChooserDescriptorFactory.createSingleFolderDescriptor()
                                                      : FileChooserDescriptorFactory.createSingleLocalFileDescriptor();
    chooseDirectoryDescriptor.setRoots(project.getBaseDir());
    chooseDirectoryDescriptor.setShowFileSystemRoots(false);
    chooseDirectoryDescriptor.withFileFilter(fileFilter);
    field.addBrowseFolderListener(new TextBrowseFolderListener(chooseDirectoryDescriptor));
  }

  @NotNull
  public static GlobalSearchScope moduleScope(@NotNull PsiElement element) {
    Module module = ModuleUtilCore.findModuleForPsiElement(element);
    return module != null ? moduleScope(module) : GlobalSearchScope.projectScope(element.getProject());
  }

  @NotNull
  public static GlobalSearchScope moduleScope(@NotNull Module module) {
    return GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(module).uniteWith(module.getModuleContentWithDependenciesScope());
  }

  public static IdeaPluginDescriptor getPlugin() {
    return PluginManager.getPlugin(PluginId.getId(PLUGIN_ID));
  }

  /**
   * isReferenceTo optimization. Before complex checking via resolve we can say for sure that element
   * can't be a reference to given declaration in following cases:<br/>
   * – GoLabelRef can't be resolved to anything but GoLabelDefinition<br/>
   * – GoTypeReferenceExpression (not from receiver type) can't be resolved to anything but GoTypeSpec<br/>
   * – Definition is private and reference in different package<br/>
   * – Definition is public, reference in different package and reference containing file doesn't have an import of definition package
   */
  public static boolean couldBeReferenceTo(@NotNull PsiElement definition, @NotNull PsiElement reference) {
    if (definition instanceof PsiDirectory && reference instanceof GoReferenceExpressionBase) return true;
    if (reference instanceof GoLabelRef && !(definition instanceof GoLabelDefinition)) return false;
    if (reference instanceof GoTypeReferenceExpression &&
        !(reference.getParent() instanceof GoReceiverType) &&
        !(definition instanceof GoTypeSpec)) {
      return false;
    }

    PsiFile definitionFile = definition.getContainingFile();
    PsiFile referenceFile = reference.getContainingFile();
    if (!(definitionFile instanceof GoFile) || !(referenceFile instanceof GoFile)) return false; // todo: zolotov, are you sure? cross refs, for instance?

    boolean inSameFile = definitionFile.isEquivalentTo(referenceFile);
    if (!inSameFile) {
      String referencePackage = ((GoFile)referenceFile).getPackageName();
      String definitionPackage = ((GoFile)definitionFile).getPackageName();
      boolean inSamePackage = referencePackage != null && referencePackage.equals(definitionPackage);

      if (!inSamePackage) {
        if (reference instanceof GoNamedElement && !((GoNamedElement)reference).isPublic()) {
          return false;
        }
        String path = ((GoFile)definitionFile).getImportPath();
        if (!((GoFile)referenceFile).getImportedPackagesMap().containsKey(path)) {
          return GoConstants.BUILTIN_PACKAGE_NAME.equals(path);
        }
      }
    }
    return true;
  }

  @NotNull
  public static Collection<String> getAllPackagesInDirectory(@Nullable final PsiDirectory dir) {
    if (dir == null) {
      return Collections.emptyList();
    }
    return CachedValuesManager.getCachedValue(dir, new CachedValueProvider<Collection<String>>() {
      @Nullable
      @Override
      public Result<Collection<String>> compute() {
        Collection<String> set = ContainerUtil.newLinkedHashSet();
        for (PsiFile file : dir.getFiles()) {
          if (file instanceof GoFile) {
            String name = ((GoFile)file).getPackageName();
            if (name != null && !GoConstants.MAIN.equals(name)) {
              set.add(name);
            }
          }
        }
        return Result.create(set, dir);
      }
    });
  }
}