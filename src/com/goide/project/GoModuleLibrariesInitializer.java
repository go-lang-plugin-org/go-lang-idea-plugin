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
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleComponent;
import com.intellij.openapi.progress.ProgressIndicatorProvider;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.*;
import com.intellij.openapi.roots.impl.OrderEntryUtil;
import com.intellij.openapi.roots.impl.libraries.LibraryEx;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.libraries.LibraryTable;
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar;
import com.intellij.openapi.startup.StartupManager;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.vfs.*;
import com.intellij.psi.PsiFileSystemItem;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.util.Alarm;
import com.intellij.util.ThreeState;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.messages.MessageBusConnection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;

import javax.swing.event.HyperlinkEvent;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class GoModuleLibrariesInitializer implements ModuleComponent {
  private static final String GO_LIB_NAME = "GOPATH";
  private static final String GO_LIBRARIES_NOTIFICATION_HAD_BEEN_SHOWN = "go.libraries.notification.had.been.shown";
  private static final String GO_VENDORING_NOTIFICATION_HAD_BEEN_SHOWN = "go.vendoring.notification.had.been.shown";
  private static final int UPDATE_DELAY = 300;
  private static boolean isTestingMode;

  private final Alarm myAlarm;
  private final MessageBusConnection myConnection;
  private boolean myModuleInitialized;

  @NotNull private final Set<VirtualFile> myLastHandledGoPathSourcesRoots = ContainerUtil.newHashSet();
  @NotNull private final Set<VirtualFile> myLastHandledExclusions = ContainerUtil.newHashSet();
  @NotNull private final Set<LocalFileSystem.WatchRequest> myWatchedRequests = ContainerUtil.newHashSet();

  @NotNull private final Module myModule;
  @NotNull private final VirtualFileAdapter myFilesListener = new VirtualFileAdapter() {
    @Override
    public void fileCreated(@NotNull VirtualFileEvent event) {
      if (GoConstants.VENDOR.equals(event.getFileName()) && event.getFile().isDirectory()) {
        showVendoringNotification();
      }
    }
  };

  @TestOnly
  public static void setTestingMode(@NotNull Disposable disposable) {
    isTestingMode = true;
    Disposer.register(disposable, () -> {
      //noinspection AssignmentToStaticFieldFromInstanceMethod
      isTestingMode = false;
    });
  }

  public GoModuleLibrariesInitializer(@NotNull Module module) {
    myModule = module;
    myAlarm = ApplicationManager.getApplication().isUnitTestMode() ? new Alarm() : new Alarm(Alarm.ThreadToUse.POOLED_THREAD, myModule);
    myConnection = myModule.getMessageBus().connect();
  }

  @Override
  public void moduleAdded() {
    if (!myModuleInitialized) {
      myConnection.subscribe(ProjectTopics.PROJECT_ROOTS, new ModuleRootAdapter() {
        @Override
        public void rootsChanged(ModuleRootEvent event) {
          scheduleUpdate();
        }
      });
      myConnection.subscribe(GoLibrariesService.LIBRARIES_TOPIC, newRootUrls -> scheduleUpdate());

      Project project = myModule.getProject();
      StartupManager.getInstance(project).runWhenProjectIsInitialized(() -> {
        if (!project.isDisposed() && !myModule.isDisposed()) {
          for (PsiFileSystemItem vendor : FilenameIndex.getFilesByName(project, GoConstants.VENDOR, GoUtil.moduleScope(myModule), true)) {
            if (vendor.isDirectory()) {
              showVendoringNotification();
              break;
            }
          }
        }
      });

      VirtualFileManager.getInstance().addVirtualFileListener(myFilesListener);
    }
    scheduleUpdate(0);
    myModuleInitialized = true;
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

  private void attachLibraries(@NotNull Collection<VirtualFile> libraryRoots, Set<VirtualFile> exclusions) {
    ApplicationManager.getApplication().assertIsDispatchThread();

    if (!libraryRoots.isEmpty()) {
      ApplicationManager.getApplication().runWriteAction(() -> {
        ModuleRootManager model = ModuleRootManager.getInstance(myModule);
        LibraryOrderEntry goLibraryEntry = OrderEntryUtil.findLibraryOrderEntry(model, getLibraryName());

        if (goLibraryEntry != null && goLibraryEntry.isValid()) {
          Library library = goLibraryEntry.getLibrary();
          if (library != null && !((LibraryEx)library).isDisposed()) {
            fillLibrary(library, libraryRoots, exclusions);
          }
        }
        else {
          LibraryTable libraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(myModule.getProject());
          Library library = libraryTable.createLibrary(getLibraryName());
          fillLibrary(library, libraryRoots, exclusions);
          ModuleRootModificationUtil.addDependency(myModule, library);
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

    Library.ModifiableModel libraryModel = library.getModifiableModel();
    for (String root : libraryModel.getUrls(OrderRootType.CLASSES)) {
      libraryModel.removeRoot(root, OrderRootType.CLASSES);
    }
    for (String root : libraryModel.getUrls(OrderRootType.SOURCES)) {
      libraryModel.removeRoot(root, OrderRootType.SOURCES);
    }
    for (VirtualFile libraryRoot : libraryRoots) {
      libraryModel.addRoot(libraryRoot, OrderRootType.CLASSES); // in order to consider GOPATH as library and show it in Ext. Libraries
      libraryModel.addRoot(libraryRoot, OrderRootType.SOURCES); // in order to find usages inside GOPATH
    }
    for (VirtualFile root : exclusions) {
      ((LibraryEx.ModifiableModelEx)libraryModel).addExcludedRoot(root.getUrl());
    }
    libraryModel.commit();
  }

  private void removeLibraryIfNeeded() {
    ApplicationManager.getApplication().assertIsDispatchThread();

    ModifiableModelsProvider modelsProvider = ModifiableModelsProvider.SERVICE.getInstance();
    ModifiableRootModel model = modelsProvider.getModuleModifiableModel(myModule);
    LibraryOrderEntry goLibraryEntry = OrderEntryUtil.findLibraryOrderEntry(model, getLibraryName());
    if (goLibraryEntry != null) {
      ApplicationManager.getApplication().runWriteAction(() -> {
        Library library = goLibraryEntry.getLibrary();
        if (library != null) {
          LibraryTable table = library.getTable();
          if (table != null) {
            table.removeLibrary(library);
            model.removeOrderEntry(goLibraryEntry);
            modelsProvider.commitModuleModifiableModel(model);
          }
        }
        else {
          modelsProvider.disposeModuleModifiableModel(model);
        }
      });
    }
    else {
      ApplicationManager.getApplication().runWriteAction(() -> modelsProvider.disposeModuleModifiableModel(model));
    }
  }

  private static void showNotification(@NotNull Project project) {
    PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();
    PropertiesComponent projectPropertiesComponent = PropertiesComponent.getInstance(project);
    boolean shownAlready;
    //noinspection SynchronizationOnLocalVariableOrMethodParameter
    synchronized (propertiesComponent) {
      shownAlready = propertiesComponent.getBoolean(GO_LIBRARIES_NOTIFICATION_HAD_BEEN_SHOWN, false)
                     || projectPropertiesComponent.getBoolean(GO_LIBRARIES_NOTIFICATION_HAD_BEEN_SHOWN, false);
      if (!shownAlready) {
        propertiesComponent.setValue(GO_LIBRARIES_NOTIFICATION_HAD_BEEN_SHOWN, String.valueOf(true));
      }
    }

    if (!shownAlready) {
      NotificationListener.Adapter notificationListener = new NotificationListener.Adapter() {
        @Override
        protected void hyperlinkActivated(@NotNull Notification notification, @NotNull HyperlinkEvent event) {
          if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED && "configure".equals(event.getDescription())) {
            GoLibrariesConfigurableProvider.showModulesConfigurable(project);
          }
        }
      };
      Notification notification = GoConstants.GO_NOTIFICATION_GROUP.createNotification("GOPATH was detected",
                                                                                       "We've detected some libraries from your GOPATH.\n" +
                                                                                       "You may want to add extra libraries in <a href='configure'>Go Libraries configuration</a>.",
                                                                                       NotificationType.INFORMATION, notificationListener);
      Notifications.Bus.notify(notification, project);
    }
  }

  private void showVendoringNotification() {
    if (!myModuleInitialized || myModule.isDisposed()) {
      return;
    }
    Project project = myModule.getProject();
    String version = GoSdkService.getInstance(project).getSdkVersion(myModule);
    if (!GoVendoringUtil.supportsVendoring(version) || GoVendoringUtil.supportsVendoringByDefault(version)) {
      return;
    }
    if (GoModuleSettings.getInstance(myModule).getVendoringEnabled() != ThreeState.UNSURE) {
      return;
    }

    PropertiesComponent propertiesComponent = PropertiesComponent.getInstance(project);
    boolean shownAlready;
    //noinspection SynchronizationOnLocalVariableOrMethodParameter
    synchronized (propertiesComponent) {
      shownAlready = propertiesComponent.getBoolean(GO_VENDORING_NOTIFICATION_HAD_BEEN_SHOWN, false);
      if (!shownAlready) {
        propertiesComponent.setValue(GO_VENDORING_NOTIFICATION_HAD_BEEN_SHOWN, String.valueOf(true));
      }
    }

    if (!shownAlready) {
      NotificationListener.Adapter notificationListener = new NotificationListener.Adapter() {
        @Override
        protected void hyperlinkActivated(@NotNull Notification notification, @NotNull HyperlinkEvent event) {
          if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED && "configure".equals(event.getDescription())) {
            GoModuleSettings.showModulesConfigurable(project);
          }
        }
      };
      Notification notification = GoConstants.GO_NOTIFICATION_GROUP.createNotification("Vendoring usage is detected",
                                                                                       "<p><strong>vendor</strong> directory usually means that project uses Go Vendor Experiment.</p>\n" +
                                                                                       "<p>Selected Go SDK version support vendoring but it's disabled by default.</p>\n" +
                                                                                       "<p>You may want to explicitly enabled Go Vendor Experiment in the <a href='configure'>project settings</a>.</p>",
                                                                                       NotificationType.INFORMATION, notificationListener);
      Notifications.Bus.notify(notification, project);
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
    myLastHandledGoPathSourcesRoots.clear();
    myLastHandledExclusions.clear();
    LocalFileSystem.getInstance().removeWatchedRoots(myWatchedRequests);
    myWatchedRequests.clear();
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
    @Override
    public void run() {
      Project project = myModule.getProject();
      if (GoSdkService.getInstance(project).isGoModule(myModule)) {
        synchronized (myLastHandledGoPathSourcesRoots) {
          Collection<VirtualFile> goPathSourcesRoots = GoSdkUtil.getGoPathSources(project, myModule);
          Set<VirtualFile> excludeRoots = ContainerUtil.newHashSet(ProjectRootManager.getInstance(project).getContentRoots());
          ProgressIndicatorProvider.checkCanceled();
          if (!myLastHandledGoPathSourcesRoots.equals(goPathSourcesRoots) || !myLastHandledExclusions.equals(excludeRoots)) {
            Collection<VirtualFile> includeRoots = gatherIncludeRoots(goPathSourcesRoots, excludeRoots);
            ApplicationManager.getApplication().invokeLater(() -> {
              if (!myModule.isDisposed() && GoSdkService.getInstance(project).isGoModule(myModule)) {
                attachLibraries(includeRoots, excludeRoots);
              }
            });

            myLastHandledGoPathSourcesRoots.clear();
            myLastHandledGoPathSourcesRoots.addAll(goPathSourcesRoots);

            myLastHandledExclusions.clear();
            myLastHandledExclusions.addAll(excludeRoots);

            List<String> paths = ContainerUtil.map(goPathSourcesRoots, VirtualFile::getPath);
            myWatchedRequests.clear();
            myWatchedRequests.addAll(LocalFileSystem.getInstance().addRootsToWatch(paths, true));
          }
        }
      }
      else {
        synchronized (myLastHandledGoPathSourcesRoots) {
          LocalFileSystem.getInstance().removeWatchedRoots(myWatchedRequests);
          myLastHandledGoPathSourcesRoots.clear();
          myLastHandledExclusions.clear();
          ApplicationManager.getApplication().invokeLater(() -> {
            if (!myModule.isDisposed() && GoSdkService.getInstance(project).isGoModule(myModule)) {
              removeLibraryIfNeeded();
            }
          });
        }
      }
    }
  }

  @NotNull
  private static Collection<VirtualFile> gatherIncludeRoots(Collection<VirtualFile> goPathSourcesRoots, Set<VirtualFile> excludeRoots) {
    Collection<VirtualFile> includeRoots = ContainerUtil.newHashSet();
    for (VirtualFile goPathSourcesDirectory : goPathSourcesRoots) {
      ProgressIndicatorProvider.checkCanceled();
      boolean excludedRootIsAncestor = false;
      for (VirtualFile excludeRoot : excludeRoots) {
        ProgressIndicatorProvider.checkCanceled();
        if (VfsUtilCore.isAncestor(excludeRoot, goPathSourcesDirectory, false)) {
          excludedRootIsAncestor = true;
          break;
        }
      }
      if (excludedRootIsAncestor) {
        continue;
      }
      for (VirtualFile file : goPathSourcesDirectory.getChildren()) {
        ProgressIndicatorProvider.checkCanceled();
        if (file.isDirectory() && !excludeRoots.contains(file)) {
          includeRoots.add(file);
        }
      }
    }
    return includeRoots;
  }
}
