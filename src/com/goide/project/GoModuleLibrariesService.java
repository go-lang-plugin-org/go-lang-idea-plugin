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

package com.goide.project;

import com.goide.GoConstants;
import com.goide.GoLibrariesState;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.components.StoragePathMacros;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleServiceManager;
import org.jetbrains.annotations.NotNull;

@State(
  name = GoConstants.GO_LIBRARIES_SERVICE_NAME,
  storages = @Storage(file = StoragePathMacros.MODULE_FILE)
)
public class GoModuleLibrariesService extends GoLibrariesService<GoLibrariesState> {
  public static GoModuleLibrariesService getInstance(@NotNull Module module) {
    return ModuleServiceManager.getService(module, GoModuleLibrariesService.class);
  }
}
