/*
 * Copyright 2013-2014 Sergey Ignatov, Alexander Zolotov
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

package com.goide;

import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class GoFileType extends LanguageFileType {
  public static final LanguageFileType INSTANCE = new GoFileType();

  protected GoFileType() {
    super(GoLanguage.INSTANCE);
  }

  @NotNull
  @Override
  public String getName() {
    return "Go";
  }

  @NotNull
  @Override
  public String getDescription() {
    return "Go files";
  }

  @NotNull
  @Override
  public String getDefaultExtension() {
    return "go";
  }

  @Nullable
  @Override
  public Icon getIcon() {
    return GoIcons.ICON;
  }
}
