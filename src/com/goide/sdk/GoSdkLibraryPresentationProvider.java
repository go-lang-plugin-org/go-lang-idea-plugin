package com.goide.sdk;

import com.goide.GoIcons;
import com.intellij.openapi.roots.libraries.DummyLibraryProperties;
import com.intellij.openapi.roots.libraries.LibraryKind;
import com.intellij.openapi.roots.libraries.LibraryPresentationProvider;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;

public class GoSdkLibraryPresentationProvider extends LibraryPresentationProvider<DummyLibraryProperties> {
  private static final LibraryKind KIND = LibraryKind.create("go");

  public GoSdkLibraryPresentationProvider() {
    super(KIND);
  }

  @Nullable
  public Icon getIcon() {
    return GoIcons.ICON;
  }

  @Nullable
  public DummyLibraryProperties detect(@NotNull List<VirtualFile> classesRoots) {
    for (VirtualFile root : classesRoots) {
      if (GoSmallIDEsSdkService.isGoSdkLibRoot(root)) {
        return DummyLibraryProperties.INSTANCE;
      }
    }
    return null;
  }
}
