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

import com.goide.project.GoBuildTargetSettings;
import com.goide.project.GoModuleSettings;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.ConfigurableUi;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.Disposer;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public class GoModuleSettingsUI implements ConfigurableUi<GoModuleSettings>, Disposable {
  private JPanel myPanel;
  private JPanel myBuildTagsPanel;
  private JPanel myVendoringPanel;
  private GoVendoringUI myVendoringUI;
  private GoBuildTagsUI myBuildTagsUI;

  public GoModuleSettingsUI(@NotNull Module module, boolean dialogMode) {
    if (dialogMode) {
      myPanel.setPreferredSize(new Dimension(400, -1));
    }
    
    myVendoringUI.initPanel(module);
    myBuildTagsUI.initPanel(module);
  }

  @Override
  public void reset(@NotNull GoModuleSettings settings) {
    myBuildTagsUI.reset(settings.getBuildTargetSettings());
    myVendoringUI.reset(settings);
  }

  @Override
  public boolean isModified(@NotNull GoModuleSettings settings) {
    return myVendoringUI.isModified(settings) || myBuildTagsUI.isModified(settings.getBuildTargetSettings());
  }

  @Override
  public void apply(@NotNull GoModuleSettings settings) throws ConfigurationException {
    myVendoringUI.apply(settings);

    GoBuildTargetSettings newBuildTargetSettings = new GoBuildTargetSettings();
    myBuildTagsUI.apply(newBuildTargetSettings);
    settings.setBuildTargetSettings(newBuildTargetSettings);
  }

  @NotNull
  @Override
  public JComponent getComponent() {
    return myPanel;
  }

  private void createUIComponents() {
    myVendoringUI = new GoVendoringUI();
    myBuildTagsUI = new GoBuildTagsUI();
    
    myVendoringPanel = myVendoringUI.getPanel();
    myBuildTagsPanel = myBuildTagsUI.getPanel();
  }

  @Override
  public void dispose() {
    Disposer.dispose(myVendoringUI);
    Disposer.dispose(myBuildTagsUI);
    UIUtil.dispose(myPanel);
  }
}
