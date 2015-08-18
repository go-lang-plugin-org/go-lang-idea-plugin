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

package com.goide.configuration;

import com.goide.GoConstants;
import com.goide.project.GoBuildTargetSettings;
import com.goide.sdk.GoSdkService;
import com.goide.util.GoUtil;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.MutableCollectionComboBoxModel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ArrayUtil;
import com.intellij.util.ThreeState;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class GoBuildTargetConfigurable implements SearchableConfigurable, Configurable.NoScroll {
  private static final String ENABLED = "Enabled";
  private static final String DISABLED = "Disabled";

  @NotNull private final GoBuildTargetSettings myBuildTargetSettings;

  private JPanel myPanel;
  private ComboBox myOSCombo;
  private ComboBox myArchCombo;
  private ComboBox myGoVersionCombo;
  private ComboBox myCompilerCombo;
  private ComboBox myCgoCombo;
  private JBTextField myCustomFlagsField;

  @NotNull private final MutableCollectionComboBoxModel<String> myCgoComboModel;

  @NotNull private final String myDefaultOSValue;
  @NotNull private final String myDefaultArchValue;
  @NotNull private String myDefaultCgo;
  @NotNull private final String myDefaultGoVersion;

  public GoBuildTargetConfigurable(@NotNull Project project, boolean dialogMode) {
    if (dialogMode) {
      myPanel.setPreferredSize(new Dimension(400, -1));
    }
    
    myBuildTargetSettings = GoBuildTargetSettings.getInstance(project);

    myDefaultOSValue = "Default (" + GoUtil.systemOS() + ")";
    myDefaultArchValue = "Default (" + GoUtil.systemArch() + ")";
    myDefaultGoVersion = "Project SDK (" + StringUtil.notNullize(GoSdkService.getInstance(project).getSdkVersion(null), "any") + ")";
    myDefaultCgo = "Default (" + cgo(GoUtil.systemCgo(myDefaultOSValue, myDefaultArchValue)) + ")";

    myOSCombo.setModel(createModel(GoConstants.KNOWN_OS, myDefaultOSValue));
    myArchCombo.setModel(createModel(GoConstants.KNOWN_ARCH, myDefaultArchValue));
    myCgoComboModel = createModel(ContainerUtil.newArrayList(ENABLED, DISABLED), myDefaultCgo);
    myCgoCombo.setModel(myCgoComboModel);
    myCompilerCombo.setModel(createModel(GoConstants.KNOWN_COMPILERS, GoBuildTargetSettings.ANY_COMPILER));
    myGoVersionCombo.setModel(createModel(GoConstants.KNOWN_VERSIONS, myDefaultGoVersion));

    ActionListener updateCgoListener = new ActionListener() {
      @Override
      public void actionPerformed(@NotNull ActionEvent event) {
        String selected = StringUtil.notNullize(myCgoComboModel.getSelected(), myDefaultCgo);
        String oldDefault = myDefaultCgo;
        String os = expandDefault(selected(myOSCombo, myDefaultOSValue), GoUtil.systemOS());
        String arch = expandDefault(selected(myArchCombo, myDefaultArchValue), GoUtil.systemArch());

        myDefaultCgo = "Default (" + cgo(GoUtil.systemCgo(os, arch)) + ")";
        myCgoComboModel.update(ContainerUtil.newArrayList(myDefaultCgo, ENABLED, DISABLED));
        myCgoComboModel.setSelectedItem(oldDefault.equals(selected) ? myDefaultCgo : selected);
      }
    };
    myOSCombo.addActionListener(updateCgoListener);
    myArchCombo.addActionListener(updateCgoListener);
  }

  @NotNull
  private String selectedCompiler() {
    final Object item = myCompilerCombo.getSelectedItem();
    return item != null && item instanceof String ? (String)item : GoBuildTargetSettings.ANY_COMPILER;
  }

  @NotNull
  private String[] selectedCustomFlags() {
    return ArrayUtil.toStringArray(StringUtil.split(myCustomFlagsField.getText(), " "));
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
    final Object item = comboBox.getSelectedItem();
    if (item != null && item instanceof String) {
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
    return new MutableCollectionComboBoxModel<String>(items, defaultValue);
  }

  @Override
  public boolean isModified() {
    return !myBuildTargetSettings.getOS().equals(selected(myOSCombo, myDefaultOSValue)) ||
           !myBuildTargetSettings.getArch().equals(selected(myArchCombo, myDefaultArchValue)) ||
           !myBuildTargetSettings.getGoVersion().equals(selected(myGoVersionCombo, myDefaultGoVersion)) ||
           !myBuildTargetSettings.getCgo().equals(selectedCgo()) ||
           !myBuildTargetSettings.getCompiler().equals(selectedCompiler()) ||
           !Arrays.equals(myBuildTargetSettings.getCustomFlags(), selectedCustomFlags());
  }

  @Override
  public void apply() throws ConfigurationException {
    myBuildTargetSettings.setOS(selected(myOSCombo, myDefaultOSValue));
    myBuildTargetSettings.setArch(selected(myArchCombo, myDefaultArchValue));
    myBuildTargetSettings.setGoVersion(selected(myGoVersionCombo, myDefaultGoVersion));
    myBuildTargetSettings.setCompiler(selectedCompiler());
    myBuildTargetSettings.setCgo(selectedCgo());
    myBuildTargetSettings.setCustomFlags(selectedCustomFlags());
  }

  @Override
  public void reset() {
    myOSCombo.setSelectedItem(expandDefault(myBuildTargetSettings.getOS(), myDefaultOSValue));
    myArchCombo.setSelectedItem(expandDefault(myBuildTargetSettings.getArch(), myDefaultArchValue));
    myGoVersionCombo.setSelectedItem(expandDefault(myBuildTargetSettings.getGoVersion(), myDefaultGoVersion));
    myCgoCombo.setSelectedItem(expandDefault(cgo(myBuildTargetSettings.getCgo()), myDefaultCgo));
    myCompilerCombo.setSelectedItem(myBuildTargetSettings.getCompiler());
    myCustomFlagsField.setText(StringUtil.join(myBuildTargetSettings.getCustomFlags(), " "));
  }

  @Nullable
  @Override
  public JComponent createComponent() {
    return myPanel;
  }

  @Override
  public void disposeUIResources() {

  }

  @Nls
  @Override
  public String getDisplayName() {
    return "Build Flags";
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

  @NotNull
  @Override
  public String getId() {
    return "go.build.flags";
  }

  @Nullable
  @Override
  public Runnable enableSearch(String s) {
    return null;
  }

  @Nullable
  @Override
  public String getHelpTopic() {
    return null;
  }
}
