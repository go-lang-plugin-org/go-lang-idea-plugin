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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jps.model.JpsGlobal;
import org.jetbrains.jps.model.JpsProject;
import org.jetbrains.jps.model.JpsSimpleElement;
import org.jetbrains.jps.model.module.JpsTypedModule;
import org.jetbrains.jps.service.JpsServiceManager;

public abstract class JpsGoLibrariesExtensionService {
  public static JpsGoLibrariesExtensionService getInstance() {
    return JpsServiceManager.getInstance().getService(JpsGoLibrariesExtensionService.class);
  }

  public abstract void setModuleLibrariesState(@NotNull JpsGoModuleProperties properties, @Nullable GoLibrariesState state);
  
  @NotNull
  public abstract GoLibrariesState getModuleLibrariesState(@NotNull JpsSimpleElement<JpsGoModuleProperties> properties);

  public abstract void setProjectLibrariesState(@NotNull JpsProject project, @Nullable GoLibrariesState state);

  @NotNull
  public abstract GoLibrariesState getProjectLibrariesState(@NotNull JpsProject project);

  public abstract void setApplicationLibrariesState(@NotNull JpsGlobal global, @Nullable GoLibrariesState state);

  @NotNull
  public abstract GoLibrariesState getApplicationLibrariesState(@NotNull JpsGlobal global);

  @NotNull
  public abstract String retrieveGoPath(@NotNull JpsTypedModule<JpsSimpleElement<JpsGoModuleProperties>> module);
}
