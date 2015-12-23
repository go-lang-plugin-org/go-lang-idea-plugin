/*
 * Copyright 2013-2015 Sergey Ignatov, Alexander Zolotov, Florin Patan, Stuart Carnie
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

package com.plan9.intel.lang.core.psi;

import com.plan9.intel.AsmIntelFileType;
import com.plan9.intel.lang.AsmIntelLanguage;
import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class AsmIntelFile extends PsiFileBase {
  public AsmIntelFile(@NotNull FileViewProvider viewProvider) {
    super(viewProvider, AsmIntelLanguage.INSTANCE);
  }

  @NotNull
  @Override
  public FileType getFileType() {
    return AsmIntelFileType.INSTANCE;
  }

  @Override
  public String toString() {
    return "x86 File";
  }

  @Override
  public Icon getIcon(int flags) {
    return super.getIcon(flags);
  }
}
