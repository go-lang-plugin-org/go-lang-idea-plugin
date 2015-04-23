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
import com.goide.util.GoUtil;
import com.intellij.ProjectTopics;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationListener;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.progress.ProgressIndicatorProvider;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.*;
import com.intellij.openapi.roots.impl.OrderEntryUtil;
import com.intellij.openapi.roots.impl.libraries.LibraryEx;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.libraries.LibraryTable;
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.vfs.*;
import com.intellij.util.Alarm;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.messages.MessageBusConnection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;

import javax.swing.event.HyperlinkEvent;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

public class GoModuleLibrariesInitializer implements ModuleComponent {
  private static final String GO_LIB_NAME = "GOPATH";
  private static final Logger LOG = Logger.getInstance(GoModuleLibrariesInitializer.class);
  private static final String GO_LIBRARIES_NOTIFICATION_HAD_BEEN_SHOWN = "go.libraries.notification.had.been.shown";
  private static final int UPDATE_DELAY = 300;
  private static boolean isTestingMode = false;

  private final Set<VirtualFile> myFilesToWatch = ContainerUtil.newConcurrentSet();
  private final Alarm myAlarm;
  private final MessageBusConnection myConnection;

  @NotNull private final Set<VirtualFile> myLastHandledRoots = ContainerUtil.newHashSet();
  @NotNull private final Set<VirtualFile> myLastHandledExclusions = ContainerUtil.newHashSet();

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
  
  @TestOnly
  public static void setTestingMode(@NotNull Disposable disposable) {
    isTestingMode = true;
    Disposer.register(disposable, new Disposable() {
      @Override
      public void dispose() {
        //noinspection AssignmentToStaticFieldFromInstanceMethod
        isTestingMode = false;
      }
    });
  }

  public GoModuleLibrariesInitializer(@NotNull Module module) {
    myModule = module;
    myAlarm = ApplicationManager.getApplication().isUnitTestMode() ? new Alarm() : new Alarm(Alarm.ThreadToUse.POOLED_THREAD, myModule);
    myConnection = myModule.getMessageBus().connect();
  }

  @Override
  public void moduleAdded() {
    scheduleUpdate(0);
    myConnection.subscribe(ProjectTopics.PROJECT_ROOTS, new ModuleRootAdapter() {
      public void rootsChanged(final ModuleRootEvent event) {
        scheduleUpdate();
      }
    });
    myConnection.subscribe(GoLibrariesService.LIBRARIES_TOPIC, new GoLibrariesService.LibrariesListener() {
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
    myAlarm.cancelAllRequests();
    UpdateRequest updateRequest = new UpdateRequest();
    if (isTestingMode) {
      ApplicationManager.getApplication().invokeLater(updateRequest);
    }
    else {
      myAlarm.addRequest(updateRequest, delay);
    }
  }

  private void attachLibraries(@NotNull final Collection<VirtualFile> libraryRoots, final Set<VirtualFile> exclusions) {
    ApplicationManager.getApplication().assertIsDispatchThread();

    if (!libraryRoots.isEmpty()) {
      ApplicationManager.getApplication().runWriteAction(new Runnable() {
        @Override
        public void run() {
          final ModuleRootManager model = ModuleRootManager.getInstance(myModule);
          final LibraryOrderEntry goLibraryEntry = OrderEntryUtil.findLibraryOrderEntry(model, getLibraryName());

          if (goLibraryEntry != null && goLibraryEntry.isValid()) {
            final Library library = goLibraryEntry.getLibrary();
            if (library != null && !((LibraryEx)library).isDisposed()) {
              fillLibrary(library, libraryRoots, exclusions);
            }
          }
          else {
            final LibraryTable libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(myModule.getProject());
            final Library library = libraryTable.createLibrary(getLibraryName());
            fillLibrary(library, libraryRoots, exclusions);
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

  public String getLibraryName() {
    return GO_LIB_NAME + " <" + myModule.getName() + ">";
  }

  private static void fillLibrary(@NotNull Library library, @NotNull Collection<VirtualFile> libraryRoots, Set<VirtualFile> exclusions) {
    ApplicationManager.getApplication().assertWriteAccessAllowed();

    final Library.ModifiableModel libraryModel = library.getModifiableModel();
    for (String root : libraryModel.getUrls(OrderRootType.CLASSES)) {
      libraryModel.removeRoot(root, OrderRootType.CLASSES);
    }
    for (VirtualFile libraryRoot : libraryRoots) {
      libraryModel.addRoot(libraryRoot, OrderRootType.CLASSES);
    }
    for (VirtualFile root : exclusions) {
      ((LibraryEx.ModifiableModelEx)libraryModel).addExcludedRoot(root.getUrl());
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

  @NotNull
  private static Set<VirtualFile> gatherExclusions(@NotNull Collection<VirtualFile> roots, @NotNull final VirtualFile... exclusions) {
    final Set<VirtualFile> result = ContainerUtil.newHashSet(exclusions);

    // todo: remove ancestors removing after 15.1
    Iterator<VirtualFile> iterator = roots.iterator();
    while (iterator.hasNext()) {
      VirtualFile file = iterator.next();
      for (VirtualFile exclusion : exclusions) {
        if (VfsUtilCore.isAncestor(exclusion, file, false)) {
          iterator.remove();
          break;
        }
      }
    }

    for (VirtualFile file : roots) {
      VfsUtilCore.visitChildrenRecursively(file, new VirtualFileVisitor() {
        @NotNull
        @Override
        public Result visitFileEx(@NotNull VirtualFile file) {
          if (GoUtil.shouldBeExcluded(file)) {
            result.add(file);
            LOG.info("Excluding part of GOPATH: " + file.getPath());
            return SKIP_CHILDREN;
          }
          return CONTINUE;
        }
      });
    }
    return result;
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
    Disposer.dispose(myConnection);
    Disposer.dispose(myAlarm);
    VirtualFileManager.getInstance().removeVirtualFileListener(myFilesListener);
    myLastHandledRoots.clear();
    myLastHandledExclusions.clear();
    myFilesToWatch.clear();
  }

  @Override
  public void projectOpened() {

  }

  @Override
  public void projectClosed() {
    disposeComponent();
  }

  @NotNull
  @Override
  public String getComponentName() {
    return getClass().getName();
  }

  private class UpdateRequest implements Runnable {
    public void run() {
      final Project project = myModule.getProject();
      if (GoSdkService.getInstance(project).isGoModule(GoModuleLibrariesInitializer.this.myModule)) {
        synchronized (myLastHandledRoots) {
          final Collection<VirtualFile> libraryRoots = ContainerUtil.newHashSet();
          for (VirtualFile packages : GoSdkUtil.getGoPathsSources(project, myModule)) {
            Collections.addAll(libraryRoots, packages.getChildren());
          }
          final Set<VirtualFile> excludedRoots = gatherExclusions(libraryRoots,
                                                                  ProjectRootManager.getInstance(project).getContentRoots());

          ProgressIndicatorProvider.checkCanceled();
          if (!myLastHandledRoots.equals(libraryRoots) || !myLastHandledExclusions.equals(excludedRoots)) {
            myFilesToWatch.clear();
            myFilesToWatch.addAll(libraryRoots);

            ApplicationManager.getApplication().invokeLater(new Runnable() {
              @Override
              public void run() {
                if (!myModule.isDisposed() && GoSdkService.getInstance(project).isGoModule(GoModuleLibrariesInitializer.this.myModule)) {
                  attachLibraries(libraryRoots, excludedRoots);
                }
              }
            });

            myLastHandledRoots.clear();
            myLastHandledRoots.addAll(libraryRoots);

            myLastHandledExclusions.clear();
            myLastHandledExclusions.addAll(excludedRoots);
          }
        }
      }
      else {
        synchronized (myLastHandledRoots) {
          myLastHandledRoots.clear();
          myLastHandledExclusions.clear();
          ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
              if (!myModule.isDisposed() && GoSdkService.getInstance(project).isGoModule(GoModuleLibrariesInitializer.this.myModule)) {
                removeLibraryIfNeeded();
              }
            }
          });
        }
      }
    }
  }
}
