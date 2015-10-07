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

import com.goide.GoConstants;
import com.goide.GoLibrariesState;
import com.intellij.util.xmlb.XmlSerializer;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.model.JpsGlobal;
import org.jetbrains.jps.model.serialization.JpsGlobalExtensionSerializer;

public class JpsGoApplicationLibrariesSerializer extends JpsGlobalExtensionSerializer {
  public JpsGoApplicationLibrariesSerializer() {
    super(GoConstants.GO_LIBRARIES_CONFIG_FILE, GoConstants.GO_LIBRARIES_SERVICE_NAME);
  }

  @Override
  public void loadExtension(@NotNull JpsGlobal global, @NotNull Element componentTag) {
    GoLibrariesState librariesState = XmlSerializer.deserialize(componentTag, GoLibrariesState.class);
    JpsGoLibrariesExtensionService.getInstance().setApplicationLibrariesState(global, librariesState);
  }

  @Override
  public void saveExtension(@NotNull JpsGlobal global, @NotNull Element componentTag) {
    XmlSerializer.serializeInto(JpsGoLibrariesExtensionService.getInstance().getApplicationLibrariesState(global), componentTag);
  }
}
