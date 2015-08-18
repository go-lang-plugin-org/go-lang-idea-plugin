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

import com.goide.project.GoApplicationLibrariesService;
import com.goide.project.GoLibrariesService;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.fileChooser.FileChooserDialog;
import com.intellij.openapi.fileChooser.FileChooserFactory;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.util.Iconable;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.ui.*;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBList;
import com.intellij.util.IconUtil;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Set;

import static com.intellij.openapi.fileChooser.FileChooserDescriptorFactory.createMultipleFoldersDescriptor;

public class GoLibrariesConfigurable implements SearchableConfigurable, Configurable.NoScroll {
  @NotNull private final String myDisplayName;
  private final GoLibrariesService<?> myLibrariesService;
  private final String[] myReadOnlyPaths;
  private final JBCheckBox myUseEnvGoPathCheckBox = new JBCheckBox("Use GOPATH that's defined in system environment");
  private final JPanel myPanel = new JPanel(new BorderLayout());
  private final CollectionListModel<ListItem> myListModel = new CollectionListModel<ListItem>();

  public GoLibrariesConfigurable(@NotNull String displayName, @NotNull GoLibrariesService librariesService, String... readOnlyPaths) {
    myDisplayName = displayName;
    myLibrariesService = librariesService;
    myReadOnlyPaths = readOnlyPaths;

    final JBList filesList = new JBList(myListModel);
    filesList.setCellRenderer(new ColoredListCellRenderer() {
      @Override
      protected void customizeCellRenderer(JList list, Object value, int index, boolean selected, boolean hasFocus) {
        final ListItem item = (ListItem)value;
        final String url = item.url;
        if (item.readOnly) {
          append("[GOPATH] ", SimpleTextAttributes.GRAY_ATTRIBUTES);
        }
        final VirtualFile file = VirtualFileManager.getInstance().findFileByUrl(url);
        if (file != null) {
          append(file.getPresentableUrl(), item.readOnly ? SimpleTextAttributes.GRAY_ATTRIBUTES : SimpleTextAttributes.REGULAR_ATTRIBUTES);
          setIcon(IconUtil.getIcon(file, Iconable.ICON_FLAG_READ_STATUS, null));
        }
        else {
          append(VfsUtilCore.urlToPath(url), SimpleTextAttributes.ERROR_ATTRIBUTES);
        }
      }
    });

    final ToolbarDecorator decorator = ToolbarDecorator.createDecorator(filesList)
      .setAddAction(new AnActionButtonRunnable() {
        @Override
        public void run(AnActionButton button) {
          final FileChooserDialog fileChooser = FileChooserFactory.getInstance()
            .createFileChooser(createMultipleFoldersDescriptor(), null, filesList);

          VirtualFile fileToSelect = null;
          final ListItem lastItem = ContainerUtil.getLastItem(myListModel.getItems());
          if (lastItem != null) {
            fileToSelect = VirtualFileManager.getInstance().findFileByUrl(lastItem.url);
          }

          final VirtualFile[] newDirectories = fileChooser.choose(null, fileToSelect);
          if (newDirectories.length > 0) {
            for (final VirtualFile newDirectory : newDirectories) {
              final String newDirectoryUrl = newDirectory.getUrl();
              boolean alreadyAdded = false;
              for (ListItem item : myListModel.getItems()) {
                if (newDirectoryUrl.equals(item.url) && !item.readOnly) {
                  filesList.clearSelection();
                  filesList.setSelectedValue(item, true);
                  scrollToSelection(filesList);
                  alreadyAdded = true;
                  break;
                }
              }
              if (!alreadyAdded) {
                myListModel.add(new ListItem(newDirectoryUrl, false));
              }
            }
          }
        }
      })
      .setRemoveActionUpdater(new AnActionButtonUpdater() {
        @Override
        public boolean isEnabled(AnActionEvent event) {
          for (Object selectedValue : filesList.getSelectedValues()) {
            if (((ListItem)selectedValue).readOnly) {
              return false;
            }
          }
          return true;
        }
      })
      .setRemoveAction(new AnActionButtonRunnable() {
        @Override
        public void run(AnActionButton button) {
          for (Object selectedValue : filesList.getSelectedValues()) {
            myListModel.remove(((ListItem)selectedValue));
          }
        }
      })
      .disableUpDownActions();
    myPanel.add(decorator.createPanel(), BorderLayout.CENTER);
    if (librariesService instanceof GoApplicationLibrariesService) {
      myUseEnvGoPathCheckBox.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(@NotNull ActionEvent event) {
          if (myUseEnvGoPathCheckBox.isSelected()) {
            addReadOnlyPaths();
          }
          else {
            removeReadOnlyPaths();
          }
        }
      });
      myPanel.add(myUseEnvGoPathCheckBox, BorderLayout.SOUTH);
    }
  }

  private static void scrollToSelection(JList list) {
    int selectedRow = list.getSelectedIndex();
    if (selectedRow >= 0) {
      list.scrollRectToVisible(list.getCellBounds(selectedRow, 0));
    }
  }

  @Nullable
  @Override
  public JComponent createComponent() {
    return myPanel;
  }

  @Override
  public boolean isModified() {
    return !getUserDefinedUrls().equals(ContainerUtil.newHashSet(myLibrariesService.getLibraryRootUrls())) ||
           ((myLibrariesService instanceof GoApplicationLibrariesService) &&
            (((GoApplicationLibrariesService)myLibrariesService).isUseGoPathFromSystemEnvironment() !=
             myUseEnvGoPathCheckBox.isSelected()));
  }

  @Override
  public void apply() throws ConfigurationException {
    myLibrariesService.setLibraryRootUrls(getUserDefinedUrls());
    if (myLibrariesService instanceof GoApplicationLibrariesService) {
      ((GoApplicationLibrariesService)myLibrariesService).setUseGoPathFromSystemEnvironment(myUseEnvGoPathCheckBox.isSelected());
    }
  }

  @Override
  public void reset() {
    myListModel.removeAll();
    resetLibrariesFromEnvironment();
    for (String url : myLibrariesService.getLibraryRootUrls()) {
      myListModel.add(new ListItem(url, false));
    }
  }

  private void resetLibrariesFromEnvironment() {
    if (myLibrariesService instanceof GoApplicationLibrariesService) {
      myUseEnvGoPathCheckBox.setSelected(((GoApplicationLibrariesService)myLibrariesService).isUseGoPathFromSystemEnvironment());
      if (((GoApplicationLibrariesService)myLibrariesService).isUseGoPathFromSystemEnvironment()) {
        addReadOnlyPaths();
      }
      else {
        removeReadOnlyPaths();
      }
    }
  }

  private void addReadOnlyPaths() {
    for (String url : myReadOnlyPaths) {
      myListModel.add(new ListItem(url, true));
    }
  }

  private void removeReadOnlyPaths() {
    Collection<ListItem> toRemove = ContainerUtil.newArrayList();
    for (ListItem item : myListModel.getItems()) {
      if (item.readOnly) toRemove.add(item);
    }
    for (ListItem item : toRemove) {
      myListModel.remove(item);
    }
  }

  @Override
  public void disposeUIResources() {

  }

  @NotNull
  @Nls
  @Override
  public String getDisplayName() {
    return myDisplayName;
  }

  @Nullable
  @Override
  public String getHelpTopic() {
    return null;
  }

  @NotNull
  private Set<String> getUserDefinedUrls() {
    final Set<String> libraryUrls = ContainerUtil.newHashSet();
    for (ListItem item : myListModel.getItems()) {
      if (!item.readOnly) {
        libraryUrls.add(item.url);
      }
    }
    return libraryUrls;
  }

  @NotNull
  @Override
  public String getId() {
    return "go.libraries";
  }

  @Nullable
  @Override
  public Runnable enableSearch(String option) {
    return null;
  }

  private static class ListItem {
    final boolean readOnly;
    final String url;

    public ListItem(String url, boolean readOnly) {
      this.readOnly = readOnly;
      this.url = url;
    }
  }
}
