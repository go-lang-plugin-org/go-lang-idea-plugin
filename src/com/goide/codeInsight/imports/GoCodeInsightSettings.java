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

package com.goide.codeInsight.imports;

import com.intellij.openapi.components.*;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.Nullable;

@State(
  name = "Go",
  storages = @Storage(file = StoragePathMacros.APP_CONFIG + "/editor.codeinsight.xml")
)
public class GoCodeInsightSettings implements PersistentStateComponent<GoCodeInsightSettings> {
  private boolean myShowImportPopup = true;
  private boolean myAddUnambiguousImportsOnTheFly = false;

  public static GoCodeInsightSettings getInstance() {
    return ServiceManager.getService(GoCodeInsightSettings.class);
  }

  @Nullable
  @Override
  public GoCodeInsightSettings getState() {
    return this;
  }

  @Override
  public void loadState(GoCodeInsightSettings state) {
    XmlSerializerUtil.copyBean(state, this);
  }

  public boolean isShowImportPopup() {
    return myShowImportPopup;
  }

  public void setShowImportPopup(boolean showImportPopup) {
    myShowImportPopup = showImportPopup;
  }

  public boolean isAddUnambiguousImportsOnTheFly() {
    return myAddUnambiguousImportsOnTheFly;
  }

  public void setAddUnambiguousImportsOnTheFly(boolean addUnambiguousImportsOnTheFly) {
    myAddUnambiguousImportsOnTheFly = addUnambiguousImportsOnTheFly;
  }
}
