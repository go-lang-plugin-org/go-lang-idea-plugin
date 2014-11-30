/*
 * Copyright 2013-2014 Sergey Ignatov, Alexander Zolotov, Mihai Toader
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

import com.goide.GoModuleType;
import com.intellij.application.options.ModuleAwareProjectConfigurable;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.options.CompositeConfigurable;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurableProvider;
import com.intellij.openapi.options.UnnamedConfigurable;
import com.intellij.openapi.project.Project;
import com.intellij.ui.IdeBorderFactory;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class GoLibrariesConfigurableProvider extends ConfigurableProvider {
  public static final String DISPLAY_NAME = "Go Libraries";
  @NotNull private final Project myProject;

  public GoLibrariesConfigurableProvider(@NotNull Project project) {
    myProject = project;
  }

  @Nullable
  @Override
  public Configurable createConfigurable() {
    return new CompositeConfigurable<UnnamedConfigurable>() {

      @Nullable
      @Override
      public JComponent createComponent() {
        final List<UnnamedConfigurable> configurables = getConfigurables();
        JPanel panel = new JPanel(new GridLayoutManager(configurables.size(), 1, new Insets(0, 0, 10, 0), -1, -1));
        for (int i = 0; i < configurables.size(); i++) {
          UnnamedConfigurable configurable = configurables.get(i);
          final JComponent component = configurable.createComponent();
          assert component != null;
          final JPanel componentPanel = new JPanel(new BorderLayout());
          componentPanel.add(component, BorderLayout.CENTER);
          if (configurable instanceof ModuleAwareProjectConfigurable) {
            componentPanel.setBorder(IdeBorderFactory.createTitledBorder("Module-specific Libraries"));
          }
          else {
            componentPanel.setBorder(IdeBorderFactory.createTitledBorder("Application-wide Libraries"));
          }
          panel.add(componentPanel, new GridConstraints(i, 0, 1, 1, 0, GridConstraints.FILL_BOTH,
                                                        GridConstraints.SIZEPOLICY_CAN_GROW |
                                                        GridConstraints.SIZEPOLICY_WANT_GROW |
                                                        GridConstraints.SIZEPOLICY_CAN_SHRINK,
                                                        GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_CAN_SHRINK,
                                                        new Dimension(-1, -1), new Dimension(-1, -1), new Dimension(-1, -1)));
        }
        panel.revalidate();
        return panel;
      }

      @Nls
      @Override
      public String getDisplayName() {
        return DISPLAY_NAME;
      }

      @Nullable
      @Override
      public String getHelpTopic() {
        return null;
      }

      @Override
      protected List<UnnamedConfigurable> createConfigurables() {
        final List<UnnamedConfigurable> result = ContainerUtil.newArrayList();
        result.add(new GoLibrariesConfigurable(GoApplicationLibrariesService.getInstance()));
        result.add(new ModuleAwareProjectConfigurable(myProject, DISPLAY_NAME, DISPLAY_NAME) {

          @Override
          protected boolean isSuitableForModule(@NotNull Module module) {
            return ModuleUtil.getModuleType(module) == GoModuleType.getInstance();
          }

          @NotNull
          @Override
          protected UnnamedConfigurable createModuleConfigurable(Module module) {
            return new GoLibrariesConfigurable(GoModuleLibrariesService.getInstance(module));
          }
        });
        return result;
      }
    };
  }
}
