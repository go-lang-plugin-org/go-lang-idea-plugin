/*
 * Copyright 2013-2014 Sergey Ignatov, Alexander Zolotov, Mihai Toader, Florin Patan
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
import com.goide.sdk.GoSdkService;
import com.goide.sdk.GoSdkUtil;
import com.intellij.application.options.ModuleAwareProjectConfigurable;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.options.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.HideableDecorator;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.intellij.util.Function;
import com.intellij.util.PlatformUtils;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

public class GoLibrariesConfigurableProvider extends ConfigurableProvider {
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
        final Collection<HideableDecorator> hideableDecorators = ContainerUtil.newHashSet();

        final GridLayoutManager layoutManager = new GridLayoutManager(configurables.size() + 1, 1, new Insets(0, 0, 0, 0), -1, -1);
        final JPanel rootPanel = new JPanel(layoutManager);
        final Spacer spacer = new Spacer();
        rootPanel.add(spacer, new GridConstraints(configurables.size(), 0, 1, 1, GridConstraints.ANCHOR_SOUTH,
                                                  GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED,
                                                  GridConstraints.SIZEPOLICY_FIXED, null, null, null));

        for (int i = 0; i < configurables.size(); i++) {
          UnnamedConfigurable configurable = configurables.get(i);
          final JComponent configurableComponent = configurable.createComponent();
          assert configurableComponent != null;
          final JPanel hideablePanel = new JPanel(new BorderLayout());

          rootPanel.add(hideablePanel, configurableConstrains(i));

          if (configurable instanceof Configurable) {
            final String displayName = ((Configurable)configurable).getDisplayName();
            ListenableHideableDecorator decorator = new ListenableHideableDecorator(hideablePanel, displayName, configurableComponent);
            decorator.addListener(new MyHideableDecoratorListener(layoutManager, hideablePanel,
                                                                  spacer, hideableDecorators,
                                                                  configurableExpandedPropertyKey(((Configurable)configurable))
            ));
            hideableDecorators.add(decorator);
            decorator.setOn(isConfigurableExpanded(i, ((Configurable)configurable)));
          }
        }
        rootPanel.revalidate();
        return rootPanel;
      }

      @Override
      public void apply() throws ConfigurationException {
        super.apply();
        final Collection<Module> modules = PlatformUtils.isIntelliJ()
                                           ? ModuleUtil.getModulesOfType(myProject, GoModuleType.getInstance())
                                           : ContainerUtil.newArrayList(ModuleManager.getInstance(myProject).getModules());
        for (Module module : modules) {
          module.getComponent(GoModuleLibrariesInitializer.class).scheduleUpdate();
        }
      }

      @NotNull
      @Override
      protected List<UnnamedConfigurable> createConfigurables() {
        final List<UnnamedConfigurable> result = ContainerUtil.newArrayList();

        String[] urlsFromEnv = ContainerUtil.map2Array(GoSdkUtil.getGoPathsSourcesFromEnvironment(), String.class,
                                                             new Function<VirtualFile, String>() {
                                                               @Override
                                                               public String fun(VirtualFile file) {
                                                                 return file.getUrl();
                                                               }
                                                             });
        result.add(new GoLibrariesConfigurable("Global libraries", GoApplicationLibrariesService.getInstance(), urlsFromEnv));
        if (!myProject.isDefault()) {
          result.add(new GoLibrariesConfigurable("Project libraries", GoProjectLibrariesService.getInstance(myProject)));
          result.add(new ModuleAwareProjectConfigurable(myProject, "Module libraries", "Module libraries") {
            @Override
            protected boolean isSuitableForModule(@NotNull Module module) {
              return GoSdkService.getInstance().isGoModule(module);
            }

            @NotNull
            @Override
            protected UnnamedConfigurable createModuleConfigurable(@NotNull Module module) {
              return new GoLibrariesConfigurable("Module libraries", GoModuleLibrariesService.getInstance(module));
            }
          });
        }
        return result;
      }

      @NotNull
      @Nls
      @Override
      public String getDisplayName() {
        return "Go Libraries";
      }

      @Nullable
      @Override
      public String getHelpTopic() {
        return null;
      }

      @NotNull
      private GridConstraints configurableConstrains(int i) {
        return new GridConstraints(i, 0, 1, 1, GridConstraints.ANCHOR_NORTHEAST, GridConstraints.FILL_BOTH,
                                   GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_WANT_GROW |
                                   GridConstraints.SIZEPOLICY_CAN_SHRINK,
                                   GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_CAN_SHRINK,
                                   null, null, null);
      }

      private boolean isConfigurableExpanded(int index, @NotNull Configurable configurable) {
        return PropertiesComponent.getInstance(myProject).getBoolean(configurableExpandedPropertyKey(configurable), index < 2);
      }

      private void storeConfigurableExpandedProperty(@NotNull String storeKey, @NotNull Boolean value) {
        PropertiesComponent.getInstance(myProject).setValue(storeKey, value.toString());
      }

      private String configurableExpandedPropertyKey(@NotNull Configurable configurable) {
        final String keyName = "configurable " + configurable.getDisplayName() + " is expanded".toLowerCase(Locale.US);
        return StringUtil.replaceChar(keyName, ' ', '.');
      }

      class MyHideableDecoratorListener extends ListenableHideableDecorator.MyListener {
        private final GridLayoutManager myLayoutManager;
        private final JPanel myHideablePanel;
        @NotNull private final String myStoreKey;
        private final Spacer mySpacer;
        private final Collection<HideableDecorator> myHideableDecorators;

        public MyHideableDecoratorListener(@NotNull GridLayoutManager layoutManager,
                                           @NotNull JPanel hideablePanel,
                                           @NotNull Spacer spacer,
                                           @NotNull Collection<HideableDecorator> hideableDecorators,
                                           @NotNull String storeKey) {
          myLayoutManager = layoutManager;
          myHideablePanel = hideablePanel;
          myStoreKey = storeKey;
          mySpacer = spacer;
          myHideableDecorators = hideableDecorators;
        }

        @Override
        public void on() {
          final GridConstraints c = myLayoutManager.getConstraintsForComponent(myHideablePanel);
          c.setVSizePolicy(c.getVSizePolicy() | GridConstraints.SIZEPOLICY_WANT_GROW);

          final GridConstraints spacerConstraints = myLayoutManager.getConstraintsForComponent(mySpacer);
          spacerConstraints.setVSizePolicy(spacerConstraints.getVSizePolicy() & ~GridConstraints.SIZEPOLICY_WANT_GROW);

          storeConfigurableExpandedProperty(myStoreKey, Boolean.TRUE);
        }


        @Override
        public void beforeOff() {
          final GridConstraints c = myLayoutManager.getConstraintsForComponent(myHideablePanel);
          c.setVSizePolicy(c.getVSizePolicy() & ~GridConstraints.SIZEPOLICY_WANT_GROW);
        }

        @Override
        public void afterOff() {
          if (isAllDecoratorsCollapsed()) {
            final GridConstraints c = myLayoutManager.getConstraintsForComponent(mySpacer);
            c.setVSizePolicy(c.getVSizePolicy() | GridConstraints.SIZEPOLICY_WANT_GROW);
          }

          storeConfigurableExpandedProperty(myStoreKey, Boolean.FALSE);
        }

        private boolean isAllDecoratorsCollapsed() {
          for (HideableDecorator hideableDecorator : myHideableDecorators) {
            if (hideableDecorator.isExpanded()) {
              return false;
            }
          }
          return true;
        }
      }
    };
  }
}