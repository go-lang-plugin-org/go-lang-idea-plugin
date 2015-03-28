package com.goide.appengine;

import com.goide.sdk.GoSdkService;
import com.goide.sdk.GoSmallIDEsSdkService;
import com.intellij.openapi.roots.libraries.DummyLibraryProperties;
import com.intellij.openapi.roots.libraries.LibraryKind;
import com.intellij.openapi.roots.libraries.LibraryPresentationProvider;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;

public class GoAppEngineLibraryPresentationProvider extends LibraryPresentationProvider<DummyLibraryProperties> {
  private static final LibraryKind KIND = LibraryKind.create("go_gae");

  public GoAppEngineLibraryPresentationProvider() {
    super(KIND);
  }

  @Nullable
  @Override
  public Icon getIcon() {
    return GoAppEngineIcons.ICON;
  }

  @Nullable
  @Override
  public DummyLibraryProperties detect(@NotNull List<VirtualFile> classesRoots) {
    for (VirtualFile root : classesRoots) {
      if (GoSmallIDEsSdkService.isGoSdkLibRoot(root) && GoSdkService.isAppEngineSdkPath(GoSdkService.libraryRootToSdkPath(root))) {
        return DummyLibraryProperties.INSTANCE;
      }
    }
    return null;
  }
}
