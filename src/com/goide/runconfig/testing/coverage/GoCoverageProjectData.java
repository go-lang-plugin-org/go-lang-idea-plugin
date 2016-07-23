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

package com.goide.runconfig.testing.coverage;

import com.intellij.openapi.util.Factory;
import com.intellij.rt.coverage.data.CoverageData;
import com.intellij.rt.coverage.data.ProjectData;
import com.intellij.util.Processor;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class GoCoverageProjectData extends ProjectData {
  @NotNull
  private final Map<String, FileData> myFilesData = ContainerUtil.newHashMap();

  public void processFiles(@NotNull Processor<FileData> processor) {
    for (FileData fileData : myFilesData.values()) {
      if (!processor.process(fileData)) {
        return;
      }
    }
  }

  public void processFile(@NotNull String filePath, @NotNull Processor<RangeData> processor) {
    FileData fileData = myFilesData.get(filePath);
    if (fileData != null) {
      for (RangeData rangeData : fileData.myRangesData.values()) {
        if (!processor.process(rangeData)) {
          return;
        }
      }
    }
  }

  public void addData(String filePath, int startLine, int startColumn, int endLine, int endColumn, int statements, int hits) {
    FileData fileData = ContainerUtil.getOrCreate(myFilesData, filePath, new Factory<FileData>() {
      @Override
      public FileData create() {
        return new FileData(filePath);
      }
    });
    fileData.add(startLine, startColumn, endLine, endColumn, statements, hits);
  }

  @Override
  public void merge(CoverageData data) {
    super.merge(data);
    if (data instanceof GoCoverageProjectData) {
      for (Map.Entry<String, FileData> entry : ((GoCoverageProjectData)data).myFilesData.entrySet()) {
        String filePath = entry.getKey();
        FileData fileData = myFilesData.get(filePath);
        FileData fileDataToMerge = entry.getValue();
        if (fileData != null) {
          for (Map.Entry<String, RangeData> dataEntry : fileDataToMerge.myRangesData.entrySet()) {
            RangeData existingRangeData = fileData.myRangesData.get(dataEntry.getKey());
            if (existingRangeData != null) {
              fileData.myRangesData.put(dataEntry.getKey(),
                                        new RangeData(existingRangeData.startLine, existingRangeData.startColumn, existingRangeData.endLine,
                                                      existingRangeData.endColumn, existingRangeData.statements,
                                                      existingRangeData.hits + dataEntry.getValue().hits));
            }
            else {
              fileData.myRangesData.put(dataEntry.getKey(), dataEntry.getValue());
            }
          }
        }
        else {
          myFilesData.put(filePath, fileDataToMerge);
        }
      }
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof GoCoverageProjectData)) return false;

    GoCoverageProjectData data = (GoCoverageProjectData)o;
    return myFilesData.equals(data.myFilesData);
  }

  @Override
  public int hashCode() {
    return myFilesData.hashCode();
  }

  public static class FileData {
    @NotNull
    public final String myFilePath;
    @NotNull
    public final Map<String, RangeData> myRangesData = ContainerUtil.newHashMap();

    public FileData(@NotNull String filePath) {
      myFilePath = filePath;
    }

    public void add(int startLine, int startColumn, int endLine, int endColumn, int statements, int hits) {
      myRangesData.put(rangeKey(startLine, startColumn, endLine, endColumn),
                       new RangeData(startLine, startColumn, endLine, endColumn, statements, hits));
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof FileData)) return false;

      FileData fileData = (FileData)o;

      if (!myFilePath.equals(fileData.myFilePath)) return false;
      return myRangesData.equals(fileData.myRangesData);
    }

    @Override
    public int hashCode() {
      int result = myFilePath.hashCode();
      result = 31 * result + myRangesData.hashCode();
      return result;
    }
  }

  public static class RangeData {
    public final int startLine;
    public final int startColumn;
    public final int endLine;
    public final int endColumn;
    public final int hits;
    public final int statements;

    public RangeData(int startLine, int startColumn, int endLine, int endColumn, int statements, int hits) {
      this.startLine = startLine;
      this.startColumn = startColumn;
      this.endLine = endLine;
      this.endColumn = endColumn;
      this.hits = hits;
      this.statements = statements;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof RangeData)) return false;

      RangeData data = (RangeData)o;

      if (startLine != data.startLine) return false;
      if (startColumn != data.startColumn) return false;
      if (endLine != data.endLine) return false;
      if (endColumn != data.endColumn) return false;
      if (hits != data.hits) return false;
      return statements == data.statements;
    }

    @Override
    public int hashCode() {
      int result = startLine;
      result = 31 * result + startColumn;
      result = 31 * result + endLine;
      result = 31 * result + endColumn;
      result = 31 * result + hits;
      result = 31 * result + statements;
      return result;
    }

    @Override
    public String toString() {
      return rangeKey(startLine, startColumn, endLine, endColumn) + "; hits: " + hits + "; statements: " + statements;
    }
  }

  private static String rangeKey(int startLine, int startColumn, int endLine, int endColumn) {
    return startLine + ":" + startColumn + "-" + endLine + ":" + endColumn;
  }
}
