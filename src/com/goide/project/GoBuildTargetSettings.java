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

package com.goide.project;

import com.goide.GoConstants;
import com.goide.sdk.GoSdkService;
import com.goide.util.GoTargetSystem;
import com.goide.util.GoUtil;
import com.intellij.openapi.components.*;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.util.ArrayUtil;
import com.intellij.util.ThreeState;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
  name = GoConstants.GO_BUILD_FLAGS_SERVICE_NAME,
  storages = {
    @Storage(id = "default", file = StoragePathMacros.PROJECT_FILE),
    @Storage(id = "dir", file = StoragePathMacros.PROJECT_CONFIG_DIR + "/" +
                                GoConstants.GO_BUILD_FLAGS_CONFIG_FILE, scheme = StorageScheme.DIRECTORY_BASED)
  })
public class GoBuildTargetSettings implements PersistentStateComponent<GoBuildTargetSettings.GoBuildTargetSettingsState> {
  public static final String DEFAULT = "default";
  public static final String ANY_COMPILER = "Any";
  private static final String GAE_BUILD_FLAG = "appengine";

  @NotNull private final Project myProject;
  @NotNull private final GoBuildTargetSettingsState myState = new GoBuildTargetSettingsState();

  public GoBuildTargetSettings(@NotNull Project project) {
    myProject = project;
  }

  public static GoBuildTargetSettings getInstance(@NotNull Project project) {
    return ServiceManager.getService(project, GoBuildTargetSettings.class);
  }

  @NotNull
  public String getOS() {
    return myState.os;
  }

  public void setOS(@NotNull String OS) {
    myState.os = OS;
  }

  @NotNull
  public String getArch() {
    return myState.arch;
  }

  public void setArch(@NotNull String arch) {
    myState.arch = arch;
  }

  @NotNull
  public ThreeState getCgo() {
    return myState.cgo;
  }

  public void setCgo(@NotNull ThreeState cgo) {
    myState.cgo = cgo;
  }

  @NotNull
  public String getCompiler() {
    return myState.compiler;
  }

  public void setCompiler(@NotNull String compiler) {
    myState.compiler = compiler;
  }

  @NotNull
  public String[] getCustomFlags() {
    return myState.customFlags;
  }

  public void setCustomFlags(@NotNull String... customFlags) {
    myState.customFlags = customFlags;
  }

  @NotNull
  public String getGoVersion() {
    return myState.goVersion;
  }

  public void setGoVersion(@NotNull String goVersion) {
    myState.goVersion = goVersion;
  }

  public GoTargetSystem getTargetSystemDescriptor(@Nullable Module module) {
    String os = realValue(myState.os, GoUtil.systemOS());
    String arch = realValue(myState.arch, GoUtil.systemArch());
    ThreeState cgo = myState.cgo == ThreeState.UNSURE ? GoUtil.systemCgo(os, arch) : myState.cgo;
    String moduleSdkVersion = GoSdkService.getInstance(myProject).getSdkVersion(module);
    String[] customFlags = GoSdkService.getInstance(myProject).isAppEngineSdk(module)
                           ? ArrayUtil.prepend(GAE_BUILD_FLAG, myState.customFlags)
                           : myState.customFlags;
    String compiler = ANY_COMPILER.equals(myState.compiler) ? null : myState.compiler;
    return new GoTargetSystem(os, arch, realValue(myState.goVersion, moduleSdkVersion), compiler, cgo, customFlags);
  }

  @Contract("_,null->!null")
  private static String realValue(@NotNull String value, @Nullable String defaultValue) {
    return DEFAULT.equals(value) ? defaultValue : value;
  }

  @NotNull
  @Override
  public GoBuildTargetSettingsState getState() {
    return myState;
  }

  @Override
  public void loadState(GoBuildTargetSettingsState state) {
    XmlSerializerUtil.copyBean(state, myState);
  }

  static class GoBuildTargetSettingsState {
    @NotNull private String os = DEFAULT;
    @NotNull private String arch = DEFAULT;
    @NotNull private ThreeState cgo = ThreeState.UNSURE;
    @NotNull private String compiler = ANY_COMPILER;
    @NotNull private String goVersion = DEFAULT;
    @NotNull private String[] customFlags = ArrayUtil.EMPTY_STRING_ARRAY;
  }
}
