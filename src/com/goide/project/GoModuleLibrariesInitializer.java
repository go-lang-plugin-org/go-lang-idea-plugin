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
import com.goide.sdk.GoSdkUtil;
import com.intellij.ProjectTopics;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationListener;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleComponent;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.*;
import com.intellij.openapi.roots.impl.OrderEntryUtil;
import com.intellij.openapi.roots.impl.libraries.LibraryEx;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.libraries.LibraryTable;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.event.HyperlinkEvent;
import java.util.List;
import java.util.Set;

public class GoModuleLibrariesInitializer implements ModuleComponent {
  private static final String GO_LIB_NAME = "GOPATH";
  private static final Logger LOG = Logger.getInstance(GoModuleLibrariesInitializer.class);
  private static final String GO_LIBRARIES_NOTIFICATION_HAD_BEEN_SHOWN = "go.libraries.notification.had.been.shown";
  
  @NotNull private final Set<String> myLastHandledRoots = ContainerUtil.newHashSet();
  @NotNull private final Module myModule;
  @NotNull private final GoLibrariesService myLibrariesProvider;

  public GoModuleLibrariesInitializer(@NotNull Module module, @NotNull GoLibrariesService userDefinedLibrariesProvider) {
    myModule = module;
    myLibrariesProvider = userDefinedLibrariesProvider;
  }

  @Override
  public void moduleAdded() {
    if (ModuleUtil.getModuleType(myModule) == GoModuleType.getInstance()) {
      discoverAndAttachGoLibraries();

      myModule.getMessageBus().connect().subscribe(ProjectTopics.PROJECT_ROOTS, new ModuleRootAdapter() {
        public void rootsChanged(final ModuleRootEvent event) {
          discoverAndAttachGoLibraries();
        }
      });
    }
  }

  private void discoverAndAttachGoLibraries() {
    ApplicationManager.getApplication().assertIsDispatchThread();

    final Set<String> libraryRootUrls = ContainerUtil.newLinkedHashSet();
    VirtualFile[] contentRoots = ProjectRootManager.getInstance(myModule.getProject()).getContentRoots();
    
    final List<VirtualFile> candidates = GoSdkUtil.getGoPathsSources();
    candidates.addAll(myLibrariesProvider.getUserDefinedLibraries());
    
    for (VirtualFile file : candidates) {
      addRootUrlsForGoPathFile(libraryRootUrls, contentRoots, file);
    }

    synchronized (myLastHandledRoots) {
      if (!myLastHandledRoots.equals(libraryRootUrls)) {
        myLastHandledRoots.clear();
        myLastHandledRoots.addAll(libraryRootUrls);
      }
      else {
        return;
      }
    }

    if (!libraryRootUrls.isEmpty()) {
      final ModifiableModelsProvider modelsProvider = ModifiableModelsProvider.SERVICE.getInstance();
      final ModifiableRootModel model = modelsProvider.getModuleModifiableModel(myModule);
      final LibraryOrderEntry goLibraryEntry = OrderEntryUtil.findLibraryOrderEntry(model, GO_LIB_NAME);

      ApplicationManager.getApplication().runWriteAction(new Runnable() {
        @Override
        public void run() {
          if (goLibraryEntry != null && goLibraryEntry.isValid()) {
            final Library goLibrary = goLibraryEntry.getLibrary();
            if (goLibrary != null && !((LibraryEx)goLibrary).isDisposed()) {
              fillLibrary(goLibrary, libraryRootUrls);
            }
            else {
              model.removeOrderEntry(goLibraryEntry);
              createAndFillLibrary(model, libraryRootUrls);
            }
          }
          else {
            createAndFillLibrary(model, libraryRootUrls);
          }
          modelsProvider.commitModuleModifiableModel(model);
        }
      });
      showNotification(myModule.getProject());
    }
    else {
      removeLibraryIfNeeded();
    }
  }

  private static void createAndFillLibrary(@NotNull ModifiableRootModel modifiableRootModel, @NotNull Set<String> libraryRootUrls) {
    ApplicationManager.getApplication().assertWriteAccessAllowed();

    final LibraryTable libraryTable = modifiableRootModel.getModuleLibraryTable();
    final Library library = libraryTable.createLibrary(GO_LIB_NAME);
    fillLibrary(library, libraryRootUrls);
  }

  private static void fillLibrary(@NotNull Library library, @NotNull Set<String> libraryRootUrls) {
    ApplicationManager.getApplication().assertWriteAccessAllowed();

    final Library.ModifiableModel libraryModel = library.getModifiableModel();
    for (String root : libraryModel.getUrls(OrderRootType.CLASSES)) {
      libraryModel.removeRoot(root, OrderRootType.CLASSES);
    }
    for (String libraryRootUrl : libraryRootUrls) {
      libraryModel.addRoot(libraryRootUrl, OrderRootType.CLASSES);
    }
    libraryModel.commit();
  }

  private void removeLibraryIfNeeded() {
    ApplicationManager.getApplication().assertIsDispatchThread();
    
    final ModifiableModelsProvider modelsProvider = ModifiableModelsProvider.SERVICE.getInstance();
    final ModifiableRootModel model = modelsProvider.getModuleModifiableModel(myModule);
    final LibraryOrderEntry goLibraryEntry = OrderEntryUtil.findLibraryOrderEntry(model, GO_LIB_NAME);
    if (goLibraryEntry != null) {
      ApplicationManager.getApplication().runWriteAction(new Runnable() {
        @Override
        public void run() {
          final Library library = goLibraryEntry.getLibrary();
          if (library != null) {
            final LibraryTable table = library.getTable();
            if (table != null) {
              table.removeLibrary(library);
              model.removeOrderEntry(goLibraryEntry);
              modelsProvider.commitModuleModifiableModel(model);
            }
          }
          else {
            modelsProvider.disposeModuleModifiableModel(model);
          }
        }
      });
    }
    else {
      ApplicationManager.getApplication().runWriteAction(new Runnable() {
        @Override
        public void run() {
          modelsProvider.disposeModuleModifiableModel(model);
        }
      });
    }
  }

  private static void addRootUrlsForGoPathFile(@NotNull Set<String> libraryRootUrls,
                                               @NotNull VirtualFile[] contentRoots,
                                               @NotNull VirtualFile file) {
    for (VirtualFile contentRoot : contentRoots) {
      if (file.equals(contentRoot)) {
        LOG.info("The directory is project root, skipping: " + file.getPath());
        libraryRootUrls.remove(contentRoot.getUrl());
        return;
      }
      else if (VfsUtilCore.isAncestor(file, contentRoot, true)) {
        LOG.info("The directory is ancestor of project root, looking deeper: " + file.getPath());
        libraryRootUrls.remove(contentRoot.getUrl());
        final VirtualFile contentRootParent = contentRoot.getParent();
        assert contentRootParent != null;
        //noinspection UnsafeVfsRecursion
        for (VirtualFile virtualFile : contentRootParent.getChildren()) {
          addRootUrlsForGoPathFile(libraryRootUrls, contentRoots, virtualFile);
        }
      }
      else {
        LOG.info("Add directory to GOPATH library: " + file.getPath());
        libraryRootUrls.add(file.getUrl());
      }
    }
  }

  private static void showNotification(@NotNull final Project project) {
    final PropertiesComponent propertiesComponent = PropertiesComponent.getInstance(project);
    boolean shownAlready;
    //noinspection SynchronizationOnLocalVariableOrMethodParameter
    synchronized (propertiesComponent) {
      shownAlready = propertiesComponent.getBoolean(GO_LIBRARIES_NOTIFICATION_HAD_BEEN_SHOWN, false);
      if (!shownAlready) {
        propertiesComponent.setValue(GO_LIBRARIES_NOTIFICATION_HAD_BEEN_SHOWN, String.valueOf(true));
      }
    }

    if (!shownAlready) {
      final Notification notification = new Notification("go", "GOPATH was detected",
                                                         "We've been detected some libraries from your GOPATH.\n" +
                                                         "You may want to add extra libraries in <a href='configure'>Go Libraries configuration</a>.",
                                                         NotificationType.INFORMATION, new NotificationListener.Adapter() {
        @Override
        protected void hyperlinkActivated(@NotNull Notification notification, @NotNull HyperlinkEvent event) {
          if (event.getDescription().equals("configure") && !project.isDisposed()) {
            ShowSettingsUtil.getInstance().showSettingsDialog(project, GoLibrariesConfigurable.DISPLAY_NAME);
          }
        }
      });
      Notifications.Bus.notify(notification, project);
    }
  }

  @Override
  public void initComponent() {

  }

  @Override
  public void disposeComponent() {

  }

  @Override
  public void projectOpened() {

  }

  @Override
  public void projectClosed() {

  }

  @NotNull
  @Override
  public String getComponentName() {
    return getClass().getName();
  }
}
