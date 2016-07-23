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
import com.goide.configuration.GoConfigurableProvider;
import com.goide.configuration.GoModuleSettingsConfigurable;
import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.components.StoragePathMacros;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleServiceManager;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import com.intellij.util.ThreeState;
import com.intellij.util.messages.Topic;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.intellij.util.xmlb.annotations.OptionTag;
import com.intellij.util.xmlb.annotations.Property;
import org.jetbrains.annotations.NotNull;

@State(name = GoConstants.GO_MODULE_SESTTINGS_SERVICE_NAME, storages = @Storage(file = StoragePathMacros.MODULE_FILE))
public class GoModuleSettings implements PersistentStateComponent<GoModuleSettings.GoModuleSettingsState> {
  public static final Topic<BuildTargetListener> TOPIC = Topic.create("build target changed", BuildTargetListener.class);

  @NotNull
  private final GoModuleSettingsState myState = new GoModuleSettingsState();
  @NotNull
  private final Module myModule;

  public GoModuleSettings(@NotNull Module module) {
    myModule = module;
  }

  public static GoModuleSettings getInstance(@NotNull Module module) {
    return ModuleServiceManager.getService(module, GoModuleSettings.class);
  }

  @NotNull
  public ThreeState getVendoringEnabled() {
    return myState.vendoring;
  }

  public void setVendoringEnabled(@NotNull ThreeState vendoringEnabled) {
    if (vendoringEnabled != myState.vendoring) {
      cleanResolveCaches();
    }
    myState.vendoring = vendoringEnabled;
  }

  @NotNull
  public GoBuildTargetSettings getBuildTargetSettings() {
    return myState.buildTargetSettings;
  }

  public void setBuildTargetSettings(@NotNull GoBuildTargetSettings buildTargetSettings) {
    if (!buildTargetSettings.equals(myState.buildTargetSettings)) {
      XmlSerializerUtil.copyBean(buildTargetSettings, myState.buildTargetSettings);
      if (!myModule.isDisposed()) {
        myModule.getProject().getMessageBus().syncPublisher(TOPIC).changed(myModule);
      }
      cleanResolveCaches();
      myState.buildTargetSettings.incModificationCount();
    }
  }

  private void cleanResolveCaches() {
    Project project = myModule.getProject();
    if (!project.isDisposed()) {
      ResolveCache.getInstance(project).clearCache(true);
      DaemonCodeAnalyzer.getInstance(project).restart();
    }
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
    void changed(@NotNull Module module);
  }

  static class GoModuleSettingsState {
    @OptionTag
    @NotNull
    private ThreeState vendoring = ThreeState.UNSURE;

    @Property(surroundWithTag = false)
    @NotNull
    private GoBuildTargetSettings buildTargetSettings = new GoBuildTargetSettings();
  }

  public static void showModulesConfigurable(@NotNull Project project) {
    ApplicationManager.getApplication().assertIsDispatchThread();
    if (!project.isDisposed()) {
      ShowSettingsUtil.getInstance().editConfigurable(project, new GoConfigurableProvider.GoProjectSettingsConfigurable(project));
    }
  }

  public static void showModulesConfigurable(@NotNull Module module) {
    ApplicationManager.getApplication().assertIsDispatchThread();
    if (!module.isDisposed()) {
      ShowSettingsUtil.getInstance().editConfigurable(module.getProject(), new GoModuleSettingsConfigurable(module, true));
    }
  }
}
