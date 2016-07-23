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

package com.goide.configuration;

import com.goide.GoConstants;
import com.goide.project.GoBuildTargetSettings;
import com.goide.sdk.GoSdkService;
import com.goide.util.GoUtil;
import com.intellij.ProjectTopics;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ModuleRootAdapter;
import com.intellij.openapi.roots.ModuleRootEvent;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.IdeBorderFactory;
import com.intellij.ui.MutableCollectionComboBoxModel;
import com.intellij.ui.RawCommandLineEditor;
import com.intellij.util.ArrayUtil;
import com.intellij.util.ThreeState;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.messages.MessageBusConnection;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class GoBuildTagsUI implements Disposable {
  private static final String ENABLED = "Enabled";
  private static final String DISABLED = "Disabled";

  private JPanel myPanel;
  private ComboBox myOSCombo;
  private ComboBox myArchCombo;
  private ComboBox myGoVersionCombo;
  private ComboBox myCompilerCombo;
  private ComboBox myCgoCombo;
  private RawCommandLineEditor myCustomTagsField;
  @SuppressWarnings("unused") 
  private JTextPane myDescriptionPane;

  @NotNull private final MutableCollectionComboBoxModel<String> myCgoComboModel;

  @NotNull private final String myDefaultOSValue;
  @NotNull private final String myDefaultArchValue;
  @NotNull private String myDefaultCgo;
  @NotNull private String myDefaultGoVersion = "";

  @SuppressWarnings("unchecked")
  public GoBuildTagsUI() {
    myPanel.setBorder(IdeBorderFactory.createTitledBorder("Build tags"));
    
    myDefaultOSValue = "Default (" + GoUtil.systemOS() + ")";
    myDefaultArchValue = "Default (" + GoUtil.systemArch() + ")";
    myDefaultCgo = "Default (" + cgo(GoUtil.systemCgo(myDefaultOSValue, myDefaultArchValue)) + ")";
    myCustomTagsField.setDialogCaption("Custom Build Tags");

    myOSCombo.setModel(createModel(GoConstants.KNOWN_OS, myDefaultOSValue));
    myArchCombo.setModel(createModel(GoConstants.KNOWN_ARCH, myDefaultArchValue));
    myCgoComboModel = createModel(ContainerUtil.newArrayList(ENABLED, DISABLED), myDefaultCgo);
    myCgoCombo.setModel(myCgoComboModel);
    myCompilerCombo.setModel(createModel(GoConstants.KNOWN_COMPILERS, GoBuildTargetSettings.ANY_COMPILER));

    ActionListener updateCgoListener = event -> {
      String selected = StringUtil.notNullize(myCgoComboModel.getSelected(), myDefaultCgo);
      String oldDefault = myDefaultCgo;
      String os = expandDefault(selected(myOSCombo, myDefaultOSValue), GoUtil.systemOS());
      String arch = expandDefault(selected(myArchCombo, myDefaultArchValue), GoUtil.systemArch());

      myDefaultCgo = "Default (" + cgo(GoUtil.systemCgo(os, arch)) + ")";
      myCgoComboModel.update(ContainerUtil.newArrayList(myDefaultCgo, ENABLED, DISABLED));
      myCgoComboModel.setSelectedItem(oldDefault.equals(selected) ? myDefaultCgo : selected);
    };
    myOSCombo.addActionListener(updateCgoListener);
    myArchCombo.addActionListener(updateCgoListener);
  }

  public void initPanel(@NotNull Module module) {
    if (!module.isDisposed()) {
      MessageBusConnection connection = module.getMessageBus().connect(this);
      connection.subscribe(ProjectTopics.PROJECT_ROOTS, new ModuleRootAdapter() {
        @Override
        public void rootsChanged(ModuleRootEvent event) {
          initComboValues(module);
        }
      });
      initComboValues(module);
    }
  }

  private void initComboValues(@NotNull Module module) {
    if (!module.isDisposed()) {
      String sdkVersion = GoSdkService.getInstance(module.getProject()).getSdkVersion(module);
      myDefaultGoVersion = "Project SDK (" + StringUtil.notNullize(sdkVersion, "any") + ")";
      //noinspection unchecked
      myGoVersionCombo.setModel(createModel(GoConstants.KNOWN_VERSIONS, myDefaultGoVersion));
    }
  }

  @NotNull
  private String selectedCompiler() {
    Object item = myCompilerCombo.getSelectedItem();
    return item instanceof String ? (String)item : GoBuildTargetSettings.ANY_COMPILER;
  }

  @NotNull
  private String[] selectedCustomTags() {
    return ArrayUtil.toStringArray(StringUtil.split(myCustomTagsField.getText(), " "));
  }

  @NotNull
  private ThreeState selectedCgo() {
    String string = myCgoComboModel.getSelected();
    if (ENABLED.equals(string)) {
      return ThreeState.YES;
    }
    if (DISABLED.equals(string)) {
      return ThreeState.NO;
    }
    return ThreeState.UNSURE;
  }

  @NotNull
  private static String selected(@NotNull ComboBox comboBox, @NotNull String defaultValue) {
    Object item = comboBox.getSelectedItem();
    if (item instanceof String) {
      return defaultValue.equals(item) ? GoBuildTargetSettings.DEFAULT : (String)item;
    }
    return GoBuildTargetSettings.DEFAULT;
  }

  @NotNull
  private static String expandDefault(@NotNull String value, @NotNull String defaultValue) {
    return GoBuildTargetSettings.DEFAULT.equals(value) ? defaultValue : value;
  }

  @NotNull
  private static MutableCollectionComboBoxModel<String> createModel(@NotNull Collection<String> values, @NotNull String defaultValue) {
    List<String> items = ContainerUtil.newArrayList(defaultValue);
    items.addAll(ContainerUtil.sorted(values));
    return new MutableCollectionComboBoxModel<>(items, defaultValue);
  }

  public boolean isModified(@NotNull GoBuildTargetSettings buildTargetSettings) {
    return !buildTargetSettings.os.equals(selected(myOSCombo, myDefaultOSValue)) ||
           !buildTargetSettings.arch.equals(selected(myArchCombo, myDefaultArchValue)) ||
           !buildTargetSettings.goVersion.equals(selected(myGoVersionCombo, myDefaultGoVersion)) ||
           buildTargetSettings.cgo != selectedCgo() ||
           !buildTargetSettings.compiler.equals(selectedCompiler()) ||
           !Arrays.equals(buildTargetSettings.customFlags, selectedCustomTags());
  }

  public void apply(@NotNull GoBuildTargetSettings buildTargetSettings) {
    buildTargetSettings.os = selected(myOSCombo, myDefaultOSValue);
    buildTargetSettings.arch = selected(myArchCombo, myDefaultArchValue);
    buildTargetSettings.goVersion = selected(myGoVersionCombo, myDefaultGoVersion);
    buildTargetSettings.compiler = selectedCompiler();
    buildTargetSettings.cgo = selectedCgo();
    buildTargetSettings.customFlags = selectedCustomTags();
  }

  public void reset(@NotNull GoBuildTargetSettings buildTargetSettings) {
    myOSCombo.setSelectedItem(expandDefault(buildTargetSettings.os, myDefaultOSValue));
    myArchCombo.setSelectedItem(expandDefault(buildTargetSettings.arch, myDefaultArchValue));
    myGoVersionCombo.setSelectedItem(expandDefault(buildTargetSettings.goVersion, myDefaultGoVersion));
    myCgoCombo.setSelectedItem(expandDefault(cgo(buildTargetSettings.cgo), myDefaultCgo));
    myCompilerCombo.setSelectedItem(buildTargetSettings.compiler);
    myCustomTagsField.setText(StringUtil.join(buildTargetSettings.customFlags, " "));
  }

  @NotNull
  public JPanel getPanel() {
    return myPanel;
  }

  @Override
  public void dispose() {
    UIUtil.dispose(myPanel);
    UIUtil.dispose(myOSCombo);
    UIUtil.dispose(myArchCombo);
    UIUtil.dispose(myGoVersionCombo);
    UIUtil.dispose(myCompilerCombo);
    UIUtil.dispose(myCgoCombo);
    UIUtil.dispose(myCustomTagsField);
  }

  @NotNull
  private static String cgo(@NotNull ThreeState threeState) {
    if (threeState == ThreeState.YES) {
      return ENABLED;
    }
    if (threeState == ThreeState.NO) {
      return DISABLED;
    }
    return GoBuildTargetSettings.DEFAULT;
  }

  private void createUIComponents() {
    myDescriptionPane = GoUIUtil.createDescriptionPane();
  }
}
