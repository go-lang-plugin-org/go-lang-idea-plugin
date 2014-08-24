package com.goide.jps;

import com.intellij.openapi.util.io.FileUtil;
import com.intellij.util.containers.MultiMap;
import gnu.trove.THashSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.builders.impl.logging.ProjectBuilderLoggerBase;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;

public class TestProjectBuilderLogger extends ProjectBuilderLoggerBase {
  @NotNull private final MultiMap<String, File> myCompiledFiles = new MultiMap<String, File>();
  @NotNull private final Set<File> myDeletedFiles = new THashSet<File>(FileUtil.FILE_HASHING_STRATEGY);
  
  @Override
  public void logDeletedFiles(@NotNull Collection<String> paths) {
    for (String path : paths) {
      myDeletedFiles.add(new File(path));
    }
  }

  @Override
  public void logCompiledFiles(Collection<File> files, String builderName, String description) throws IOException {
    myCompiledFiles.putValues(builderName, files);
  }

  public void clear() {
    myCompiledFiles.clear();
    myDeletedFiles.clear();
  }

  @Override
  protected void logLine(String message) {
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
