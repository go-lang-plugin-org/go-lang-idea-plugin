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

package com.goide.jps.model;

import com.goide.GoLibrariesState;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jps.model.ex.JpsElementBase;

public class JpsGoLibraries extends JpsElementBase<JpsGoLibraries> {
  @NotNull
  private GoLibrariesState myState = new GoLibrariesState();

  public JpsGoLibraries(@Nullable GoLibrariesState state) {
    if (state != null) {
      myState = state;
    }
  }

  @NotNull
  public GoLibrariesState getState() {
    return myState;
  }

  @NotNull
  @Override
  public JpsGoLibraries createCopy() {
    GoLibrariesState newState = new GoLibrariesState();
    XmlSerializerUtil.copyBean(myState, newState);
    return new JpsGoLibraries(newState);
  }

  @Override
  public void applyChanges(@NotNull JpsGoLibraries modified) {
    XmlSerializerUtil.copyBean(modified, myState);
  }
}
