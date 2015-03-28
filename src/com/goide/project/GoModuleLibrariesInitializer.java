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

import com.goide.GoConstants;
import com.goide.configuration.GoLibrariesConfigurableProvider;
import com.goide.sdk.GoSdkService;
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
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.*;
import com.intellij.openapi.roots.impl.OrderEntryUtil;
import com.intellij.openapi.roots.impl.libraries.LibraryEx;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.libraries.LibraryTable;
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar;
import com.intellij.openapi.vfs.*;
import com.intellij.util.Alarm;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.event.HyperlinkEvent;
import java.util.Collection;
import java.util.Set;

public class GoModuleLibrariesInitializer implements ModuleComponent {
  private static final String GO_LIB_NAME = "GOPATH";
  private static final Logger LOG = Logger.getInstance(GoModuleLibrariesInitializer.class);
  private static final String GO_LIBRARIES_NOTIFICATION_HAD_BEEN_SHOWN = "go.libraries.notification.had.been.shown";
  private static final int UPDATE_DELAY = 300;

  private final Set<VirtualFile> myFilesToWatch = ContainerUtil.newConcurrentSet();
  private final Alarm myAlarm;

  @NotNull private final Set<String> myLastHandledRoots = ContainerUtil.newHashSet();
  @NotNull private final Module myModule;
  @NotNull private final VirtualFileAdapter myFilesListener = new VirtualFileAdapter() {
    @Override
    public void fileCreated(@NotNull VirtualFileEvent event) {
      handleEvent(event);
    }

    @Override
    public void fileDeleted(@NotNull VirtualFileEvent event) {
      handleEvent(event);
    }

    @Override
    public void fileMoved(@NotNull VirtualFileMoveEvent event) {
      handleEvent(event);
    }

    private void handleEvent(VirtualFileEvent event) {
      if (myFilesToWatch.contains(event.getFile())) {
        scheduleUpdate();
      }
      else {
        for (VirtualFile file : myFilesToWatch) {
          if (VfsUtilCore.isAncestor(file, event.getFile(), true)) {
            scheduleUpdate();
          }
        }
      }
    }
  };

  public GoModuleLibrariesInitializer(@NotNull Module module) {
    myModule = module;
    myAlarm = new Alarm(Alarm.ThreadToUse.POOLED_THREAD, myModule);
  }

  @Override
  public void moduleAdded() {
    scheduleUpdate(0);

    myModule.getMessageBus().connect().subscribe(ProjectTopics.PROJECT_ROOTS, new ModuleRootAdapter() {
      public void rootsChanged(final ModuleRootEvent event) {
        scheduleUpdate();
      }
    });
    myModule.getMessageBus().connect().subscribe(GoLibrariesService.LIBRARIES_TOPIC, new GoLibrariesService.LibrariesListener() {
      @Override
      public void librariesChanged(@NotNull Collection<String> newRootUrls) {
        scheduleUpdate();
      }
    });

    VirtualFileManager.getInstance().addVirtualFileListener(myFilesListener);
  }

  private void scheduleUpdate() {
    scheduleUpdate(UPDATE_DELAY);
  }

  private void scheduleUpdate(int delay) {
    myAlarm.addRequest(new Runnable() {
      public void run() {
        final Project project = myModule.getProject();
        if (GoSdkService.getInstance(project).isGoModule(GoModuleLibrariesInitializer.this.myModule)) {
          final Set<String> libraryRootUrls = ContainerUtil.newLinkedHashSet();
          VirtualFile[] contentRoots = ProjectRootManager.getInstance(project).getContentRoots();

          final Collection<VirtualFile> candidates = GoSdkUtil.getGoPathsSources(project, myModule);
          myFilesToWatch.clear();
          for (VirtualFile file : candidates) {
            addRootUrlsForGoPathFile(libraryRootUrls, contentRoots, file);
          }
          myFilesToWatch.addAll(candidates);

          synchronized (myLastHandledRoots) {
            if (!myLastHandledRoots.equals(libraryRootUrls)) {
              myLastHandledRoots.clear();
              myLastHandledRoots.addAll(libraryRootUrls);

              ApplicationManager.getApplication().invokeLater(new Runnable() {
                @Override
                public void run() {
                  if (!project.isDisposed() && GoSdkService.getInstance(project).isGoModule(GoModuleLibrariesInitializer.this.myModule)) {
                    attachLibraries(libraryRootUrls);
                  }
                }
              });
            }
          }
        }
        else {
          synchronized (myLastHandledRoots) {
            myLastHandledRoots.clear();
            ApplicationManager.getApplication().invokeLater(new Runnable() {
              @Override
              public void run() {
                if (!project.isDisposed() && !GoSdkService.getInstance(project).isGoModule(GoModuleLibrariesInitializer.this.myModule)) {
                  removeLibraryIfNeeded();
                }
              }
            });
          }
        }
      }
    }, delay);
  }

  private void attachLibraries(@NotNull final Set<String> libraryRootUrls) {
    ApplicationManager.getApplication().assertIsDispatchThread();

    if (!libraryRootUrls.isEmpty()) {
      ApplicationManager.getApplication().runWriteAction(new Runnable() {
        @Override
        public void run() {
          final ModuleRootManager model = ModuleRootManager.getInstance(myModule);
          final LibraryOrderEntry goLibraryEntry = OrderEntryUtil.findLibraryOrderEntry(model, getLibraryName());

          if (goLibraryEntry != null && goLibraryEntry.isValid()) {
            final Library goLibrary = goLibraryEntry.getLibrary();
            if (goLibrary != null && !((LibraryEx)goLibrary).isDisposed()) {
              fillLibrary(goLibrary, libraryRootUrls);
            }
          }
          else {
            final LibraryTable libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(myModule.getProject());
            final Library library = libraryTable.createLibrary(getLibraryName());
            fillLibrary(library, libraryRootUrls);
            ModuleRootModificationUtil.addDependency(myModule, library);
          }
        }
      });
      showNotification(myModule.getProject());
    }
    else {
      removeLibraryIfNeeded();
    }
  }

  private String getLibraryName() {
    return GO_LIB_NAME + " <" + myModule.getName() + ">";
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
    final LibraryOrderEntry goLibraryEntry = OrderEntryUtil.findLibraryOrderEntry(model, getLibraryName());
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

  private static void addRootUrlsForGoPathFile(@NotNull final Set<String> libraryRootUrls,
                                               @NotNull final VirtualFile[] contentRoots,
                                               @NotNull VirtualFile file) {

    VfsUtilCore.visitChildrenRecursively(file, new VirtualFileVisitor() {
      @NotNull
      @Override
      public Result visitFileEx(@NotNull VirtualFile file) {
        for (VirtualFile contentRoot : contentRoots) {
          if (VfsUtilCore.isAncestor(contentRoot, file, false)) {
            LOG.info("The directory is child of project root, skipping: " + file.getPath());
            libraryRootUrls.remove(contentRoot.getUrl());
            return SKIP_CHILDREN;
          }
          else if (VfsUtilCore.isAncestor(file, contentRoot, true)) {
            LOG.info("The directory is ancestor of project root, looking deeper: " + file.getPath());
            libraryRootUrls.remove(contentRoot.getUrl());
            return CONTINUE;
          }
          else {
            LOG.info("Add directory to GOPATH library: " + file.getPath());
            libraryRootUrls.add(file.getUrl());
          }
        }
        return SKIP_CHILDREN;
      }
    });
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
      final Notification notification = new Notification(GoConstants.GO_NOTIFICATION_GROUP, "GOPATH was detected",
                                                         "We've been detected some libraries from your GOPATH.\n" +
                                                         "You may want to add extra libraries in <a href='configure'>Go Libraries configuration</a>.",
                                                         NotificationType.INFORMATION, new NotificationListener.Adapter() {
        @Override
        protected void hyperlinkActivated(@NotNull Notification notification, @NotNull HyperlinkEvent event) {
          if (event.getDescription().equals("configure")) {
            showModulesConfigurable(project);
          }
        }
      });
      Notifications.Bus.notify(notification, project);
    }
  }

  public static void showModulesConfigurable(@NotNull Project project) {
    ApplicationManager.getApplication().assertIsDispatchThread();
    if (!project.isDisposed()) {
      Configurable configurable = new GoLibrariesConfigurableProvider(project).createConfigurable(true);
      if (configurable != null) {
        ShowSettingsUtil.getInstance().editConfigurable(project, configurable);
      }
    }
  }

  @Override
  public void initComponent() {

  }

  @Override
  public void disposeComponent() {
    VirtualFileManager.getInstance().removeVirtualFileListener(myFilesListener);
  }

  @Override
  public void projectOpened() {

  }

  @Override
  public void projectClosed() {
    VirtualFileManager.getInstance().removeVirtualFileListener(myFilesListener);
  }

  @NotNull
  @Override
  public String getComponentName() {
    return getClass().getName();
  }
}
