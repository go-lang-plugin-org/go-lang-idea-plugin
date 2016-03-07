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

import com.goide.project.GoVendoringSettings;
import com.goide.sdk.GoSdkService;
import com.intellij.ProjectTopics;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ModuleRootAdapter;
import com.intellij.openapi.roots.ModuleRootEvent;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.MutableCollectionComboBoxModel;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ThreeState;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.messages.MessageBusConnection;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class GoVendoringUI implements Disposable {
  private static final String ENABLED = "Enabled";
  private static final String DISABLED = "Disabled";

  @NotNull
  private final MutableCollectionComboBoxModel myVendoringEnabledComboModel = new MutableCollectionComboBoxModel<String>();
  @NotNull
  private String myDefaultComboText = "";

  private JPanel myPanel;
  private JBLabel myErrorMessageLabel;
  private ComboBox myVendoringEnabledCombo;
  private JTextPane myDescriptionPane;

  public void initPanel(@NotNull Module module) {
    if (!module.isDisposed()) {
      MessageBusConnection connection = module.getMessageBus().connect(this);
      //noinspection unchecked
      myVendoringEnabledCombo.setModel(myVendoringEnabledComboModel);
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
      if (!GoVendoringSettings.supportsVendoring(sdkVersion) && sdkVersion != null) {
        myErrorMessageLabel.setIcon(AllIcons.General.BalloonWarning);
        myErrorMessageLabel.setText("Go " + sdkVersion + " doesn't support vendor experiment");
        myErrorMessageLabel.setVisible(true);
      }
      else {
        myErrorMessageLabel.setVisible(false);
      }
      myDefaultComboText = "Default for SDK (" + (GoVendoringSettings.supportsVendoringByDefault(sdkVersion) ? ENABLED : DISABLED) + ")";
      //noinspection unchecked
      myVendoringEnabledComboModel.update(ContainerUtil.newArrayList(myDefaultComboText, ENABLED, DISABLED));
    }
  }

  public void reset(@NotNull GoVendoringSettings settings) {
    switch (settings.vendorSupportEnabled) {
      case YES:
        myVendoringEnabledComboModel.setSelectedItem(ENABLED);
        break;
      case NO:
        myVendoringEnabledComboModel.setSelectedItem(DISABLED);
        break;
      case UNSURE:
        myVendoringEnabledComboModel.setSelectedItem(myDefaultComboText);
        break;
    }
  }

  public boolean isModified(@NotNull GoVendoringSettings settings) {
    Object item = myVendoringEnabledComboModel.getSelectedItem();
    switch (settings.vendorSupportEnabled) {
      case YES:
        return !ENABLED.equals(item);
      case NO:
        return !DISABLED.equals(item);
      case UNSURE:
        return !myDefaultComboText.equals(item);
    }
    return true;
  }


  public void apply(@NotNull GoVendoringSettings settings) {
    Object item = myVendoringEnabledComboModel.getSelectedItem();
    if (ENABLED.equals(item)) {
      settings.vendorSupportEnabled = ThreeState.YES;
    }
    else if (DISABLED.equals(item)) {
      settings.vendorSupportEnabled = ThreeState.NO;
    }
    else {
      settings.vendorSupportEnabled = ThreeState.UNSURE;
    }
  }

  public JPanel getPanel() {
    return myPanel;
  }

  @Override
  public void dispose() {
    UIUtil.dispose(myPanel);
    UIUtil.dispose(myVendoringEnabledCombo);
  }

  private void createUIComponents() {
    myDescriptionPane = GoUIUtil.createDescriptionPane(); 
  }
}
