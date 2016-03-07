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

package com.goide.project;

import com.goide.GoConstants;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.components.StoragePathMacros;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleServiceManager;
import com.intellij.util.messages.Topic;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;

@State(name = GoConstants.GO_MODULE_SESTTINGS_SERVICE_NAME, storages = {@Storage(StoragePathMacros.MODULE_FILE)})
public class GoModuleSettings implements PersistentStateComponent<GoModuleSettings.GoModuleSettingsState> {
  public static final Topic<BuildTargetListener> TOPIC = new Topic<BuildTargetListener>("build target changed", BuildTargetListener.class);

  @NotNull
  private final GoModuleSettingsState myState = new GoModuleSettingsState();
  @NotNull private final Module myModule;

  public GoModuleSettings(@NotNull Module module) {
    myModule = module;
  }

  public static GoModuleSettings getInstance(@NotNull Module module) {
    return ModuleServiceManager.getService(module, GoModuleSettings.class);
  }

  @NotNull
  public GoVendoringSettings getVendoringSettings() {
    return myState.vendoringSettings;
  }

  public void setVendoringSettings(@NotNull GoVendoringSettings vendorSupportEnabled) {
    myState.vendoringSettings = vendorSupportEnabled;
  }

  @NotNull
  public GoBuildTargetSettings getBuildTargetSettings() {
    return myState.buildTargetSettings;
  }

  public void setBuildTargetSettings(@NotNull GoBuildTargetSettings buildTargetSettings) {
    if (!buildTargetSettings.equals(myState.buildTargetSettings)) {
      myModule.getMessageBus().syncPublisher(TOPIC).changed();
    }
    myState.buildTargetSettings = buildTargetSettings;
  }

  @NotNull
  @Override
  public GoModuleSettingsState getState() {
    return myState;
  }

  @Override
  public void loadState(GoModuleSettingsState state) {
    XmlSerializerUtil.copyBean(state, myState);
  }

  public interface BuildTargetListener {
    void changed();
  }

  static class GoModuleSettingsState {
    @NotNull
    private GoVendoringSettings vendoringSettings = new GoVendoringSettings();
    @NotNull
    private GoBuildTargetSettings buildTargetSettings = new GoBuildTargetSettings();
  }
}
