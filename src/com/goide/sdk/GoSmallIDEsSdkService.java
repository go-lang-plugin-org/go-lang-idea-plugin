package com.goide.sdk;

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
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.util.ArrayUtil;
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
        Library lib = table.getLibraryByName(LIBRARY_NAME);
        String[] urls = lib == null ? ArrayUtil.EMPTY_STRING_ARRAY : lib.getUrls(OrderRootType.CLASSES);
        String firstElement = ArrayUtil.getFirstElement(urls);
        if (firstElement != null) {
          firstElement = StringUtil.trimEnd(StringUtil.trimEnd(firstElement, "src/pkg"), "src");
          return VfsUtilCore.urlToPath(firstElement);
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
        String sdkHomePath = getSdkHomePath(module);
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
}
