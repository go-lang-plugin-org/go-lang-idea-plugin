/*
 * Copyright 2013-2014 Sergey Ignatov, Alexander Zolotov, Mihai Toader
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

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.text.CharSequenceHashingStrategy;
import gnu.trove.THashSet;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GoUtil {
  public static final Pattern FULL = Pattern.compile("\\w+_(\\w+)_(\\w+)");
  public static final Pattern SHORT = Pattern.compile("\\w+_(\\w+)");
  public static final Set<CharSequence> LINUX = set("linux", "no", "unix", "posix", "notwin");
  public static final Set<CharSequence> MAC = set("darwin", "no", "unix", "posix", "notwin");
  public static final Set<CharSequence> WINDOWS = set("windows", "no");
  public static final Set<CharSequence> OS = set("openbsd", "plan9", "unix", "linux", "netbsd", "darwin", "dragonfly", "bsd", "windows",
                                                 "posix", "freebsd", "notwin");

  public static boolean allowed(@NotNull PsiFile file) {
    String name = StringUtil.trimEnd(FileUtil.getNameWithoutExtension(file.getName()), "_test");
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
    return true;
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

  public static void installFileChooser(@NotNull Project project, @NotNull TextFieldWithBrowseButton field, boolean directory) {
    FileChooserDescriptor chooseDirectoryDescriptor = directory
                                                      ? FileChooserDescriptorFactory.createSingleFolderDescriptor() 
                                                      : FileChooserDescriptorFactory.createSingleLocalFileDescriptor();
    chooseDirectoryDescriptor.setRoots(project.getBaseDir());
    chooseDirectoryDescriptor.setShowFileSystemRoots(false);
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
}