package com.goide.jps.builder;

import com.intellij.openapi.util.io.FileUtilRt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.builders.BuildRootDescriptor;
import org.jetbrains.jps.builders.BuildTarget;

import java.io.File;
import java.io.FileFilter;

public class GoSourceRootDescriptor extends BuildRootDescriptor {
  private File myRoot;
  private final GoTarget myGoTarget;

  public GoSourceRootDescriptor(File root, GoTarget goTarget) {
    myRoot = root;
    myGoTarget = goTarget;
  }

  @NotNull
  @Override
  public String getRootId() {
    return myRoot.getAbsolutePath();
  }

  @Override
  public File getRootFile() {
    return myRoot;
  }

  @Override
  public BuildTarget<?> getTarget() {
    return myGoTarget;
  }

  @NotNull
  @Override
  public FileFilter createFileFilter() {
    return new FileFilter() {
      @Override
      public boolean accept(@NotNull File file) {
        return FileUtilRt.extensionEquals(file.getName(), "go");
      }
    };
  }
}
