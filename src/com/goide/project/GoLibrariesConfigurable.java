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

import com.intellij.openapi.fileChooser.FileChooserDialog;
import com.intellij.openapi.fileChooser.FileChooserFactory;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.Iconable;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.ui.*;
import com.intellij.ui.components.JBList;
import com.intellij.util.IconUtil;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import static com.intellij.openapi.fileChooser.FileChooserDescriptorFactory.createMultipleFoldersDescriptor;

public class GoLibrariesConfigurable implements Configurable {
  @NotNull private final String myDisplayName;
  private final GoLibrariesService myLibrariesService;
  private final JPanel myPanel = new JPanel(new BorderLayout());
  private final CollectionListModel<String> myListModel = new CollectionListModel<String>();

  public GoLibrariesConfigurable(@NotNull String displayName, @NotNull GoLibrariesService librariesService) {
    myDisplayName = displayName;
    myLibrariesService = librariesService;

    final JBList filesList = new JBList(myListModel);
    filesList.setCellRenderer(new ColoredListCellRenderer() {
      @Override
      protected void customizeCellRenderer(JList list, Object value, int index, boolean selected, boolean hasFocus) {
        final String url = value.toString();
        final VirtualFile file = VirtualFileManager.getInstance().findFileByUrl(url);
        if (file != null) {
          append(file.getPath());
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
          final String lastItem = ContainerUtil.getLastItem(myListModel.getItems());
          if (lastItem != null) {
            fileToSelect = VirtualFileManager.getInstance().findFileByUrl(lastItem);
          }

          final VirtualFile[] newDirectories = fileChooser.choose(null, fileToSelect);
          if (newDirectories.length > 0) {
            for (final VirtualFile newDirectory : newDirectories) {
              final String newDirectoryUrl = newDirectory.getUrl();
              boolean alreadyAdded = false;
              List<String> items = myListModel.getItems();
              for (String item : items) {
                if (newDirectoryUrl.equals(item)) {
                  filesList.clearSelection();
                  filesList.setSelectedValue(item, true);
                  scrollToSelection(filesList);
                  alreadyAdded = true;
                  break;
                }
              }
              if (!alreadyAdded) {
                myListModel.add(newDirectoryUrl);
              }
            }
          }
        }
      })
      .setRemoveAction(new AnActionButtonRunnable() {
        @Override
        public void run(AnActionButton button) {
          for (Object selectedValue : filesList.getSelectedValues()) {
            myListModel.remove(selectedValue.toString());
          }
        }
      })
      .disableUpDownActions();
    myPanel.add(decorator.createPanel(), BorderLayout.CENTER);
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
    return !ContainerUtil.newHashSet(myListModel.getItems()).equals(ContainerUtil.newHashSet(myLibrariesService.getLibraryRootUrls()));
  }

  @Override
  public void apply() throws ConfigurationException {
    myLibrariesService.setLibraryRootUrls(ContainerUtil.newHashSet(myListModel.getItems()));
  }

  @Override
  public void reset() {
    myListModel.removeAll();
    myListModel.add(ContainerUtil.newArrayList(myLibrariesService.getLibraryRootUrls()));
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
}
