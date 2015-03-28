package com.goide.sdk;

import com.goide.GoConstants;
import com.goide.configuration.GoSdkConfigurable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.libraries.LibraryTable;
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GoSmallIDEsSdkService extends GoSdkService {
  public static final String LIBRARY_NAME = "Go SDK";

  public GoSmallIDEsSdkService(@NotNull Project project) {
    super(project);
  }

  @Nullable
  @Override
  public String getSdkHomePath(@Nullable final Module module) {
    return ApplicationManager.getApplication().runReadAction(new Computable<String>() {
      @Nullable
      @Override
      public String compute() {
        LibraryTable table = LibraryTablesRegistrar.getInstance().getLibraryTable(myProject);
        for (Library library : table.getLibraries()) {
          final String libraryName = library.getName();
          if (libraryName != null && libraryName.startsWith(LIBRARY_NAME)) {
            for (final VirtualFile root : library.getFiles(OrderRootType.CLASSES)) {
              if (isGoSdkLibRoot(root)) {
                return libraryRootToSdkPath(root);
              }
            }
          }
        }
        return null;
      }
    });
  }

  @Nullable
  @Override
  public String getSdkVersion(@Nullable final Module module) {
    return CachedValuesManager.getManager(myProject).getCachedValue(myProject, new CachedValueProvider<String>() {
      @Nullable
      @Override
      public Result<String> compute() {
        String result = null;
        final String sdkHomePath = getSdkHomePath(module);
        if (sdkHomePath != null) {
          result = GoSdkUtil.retrieveGoVersion(sdkHomePath);
        }
        return Result.create(result, GoSmallIDEsSdkService.this);
      }
    });
  }

  @Override
  public void chooseAndSetSdk(@Nullable Module module) {
    ShowSettingsUtil.getInstance().editConfigurable(myProject, new GoSdkConfigurable(myProject, true));
  }

  @Nullable
  @Override
  public Configurable createSdkConfigurable() {
    return !myProject.isDefault() ? new GoSdkConfigurable(myProject, false) : null;
  }

  @Override
  public boolean isGoModule(@Nullable Module module) {
    return super.isGoModule(module) && getSdkHomePath(module) != null;
  }

  public static boolean isGoSdkLibRoot(@NotNull VirtualFile root) {
    return root.isInLocalFileSystem() && root.isDirectory() && VfsUtilCore.findRelativeFile(GoConstants.GO_VERSION_FILE_PATH, root) != null;
  }
}
