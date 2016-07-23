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

import com.goide.GoConstants;
import com.goide.sdk.GoPackageUtil;
import com.intellij.coverage.BaseCoverageSuite;
import com.intellij.coverage.CoverageEngine;
import com.intellij.coverage.CoverageRunner;
import com.intellij.coverage.CoverageSuite;
import com.intellij.execution.configurations.ModuleBasedConfiguration;
import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.rt.coverage.data.ClassData;
import com.intellij.rt.coverage.data.LineData;
import com.intellij.rt.coverage.data.ProjectData;
import gnu.trove.TIntObjectHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.List;

public class GoCoverageRunner extends CoverageRunner {
  private static final Logger LOG = Logger.getInstance(GoCoverageRunner.class);

  private static final String ID = "GoCoverage";
  private static final String DATA_FILE_EXTENSION = "out";
  private static final String PRESENTABLE_NAME = GoConstants.GO;

  @Override
  public ProjectData loadCoverageData(@NotNull File sessionDataFile, @Nullable CoverageSuite baseCoverageSuite) {
    if (!(baseCoverageSuite instanceof BaseCoverageSuite)) {
      return null;
    }
    Project project = baseCoverageSuite.getProject();
    if (project == null) {
      return null;
    }
    RunConfigurationBase configuration = ((BaseCoverageSuite)baseCoverageSuite).getConfiguration();
    Module module = configuration instanceof ModuleBasedConfiguration
                    ? ((ModuleBasedConfiguration)configuration).getConfigurationModule().getModule()
                    : null;

    try {
      BufferedReader reader = new BufferedReader(new FileReader(sessionDataFile.getAbsolutePath()));
      try {
        return parseCoverage(reader, project, module);
      }
      catch (IOException e) {
        LOG.warn(e);
      }
      finally {
        try {
          reader.close();
        }
        catch (IOException e) {
          LOG.warn(e);
        }
      }
    }
    catch (FileNotFoundException e) {
      LOG.warn(e);
    }

    return null;
  }

  @Nullable
  public static GoCoverageProjectData parseCoverage(@NotNull BufferedReader dataReader,
                                                    @NotNull Project project,
                                                    @Nullable Module module) throws IOException {
    GoCoverageProjectData result = new GoCoverageProjectData();
    String line;

    while ((line = dataReader.readLine()) != null) {
      if (line.isEmpty()) continue;
      List<String> fileNameTail = StringUtil.split(line, ":");
      VirtualFile file = GoPackageUtil.findByImportPath(fileNameTail.get(0), project, module);
      if (file == null) continue;
      String filePath = file.getPath();

      List<String> tailParts = StringUtil.split(fileNameTail.get(1), " ");
      if (tailParts.size() != 3) continue;

      int statements = Integer.parseInt(tailParts.get(1));
      int hit = Integer.parseInt(tailParts.get(2));

      String offsets = tailParts.get(0);
      int firstDot = offsets.indexOf('.');
      int comma = offsets.indexOf(',', firstDot);
      int secondDot = offsets.indexOf('.', comma);
      if (firstDot == -1 || comma == -1 || secondDot == -1) continue;

      int lineStart = Integer.parseInt(offsets.substring(0, firstDot));
      int columnStart = Integer.parseInt(offsets.substring(firstDot + 1, comma));
      int lineEnd = Integer.parseInt(offsets.substring(comma + 1, secondDot));
      int columnEnd = Integer.parseInt(offsets.substring(secondDot + 1));

      result.addData(filePath, lineStart, columnStart, lineEnd, columnEnd, statements, hit);
    }

    result.processFiles(fileData -> {
      ClassData classData = result.getOrCreateClassData(fileData.myFilePath);
      int max = -1;
      TIntObjectHashMap<LineData> linesMap = new TIntObjectHashMap<>();
      for (GoCoverageProjectData.RangeData rangeData : fileData.myRangesData.values()) {
        for (int i = rangeData.startLine; i <= rangeData.endLine; i++) {
          LineData existingData = linesMap.get(i);
          if (existingData != null) {
            existingData.setHits(existingData.getHits() + rangeData.hits);
            // emulate partial
            existingData.setFalseHits(0, 0);
            existingData.setTrueHits(0, 0);
          }
          else {
            LineData newData = new LineData(i, null);
            newData.setHits(newData.getHits() + rangeData.hits);
            linesMap.put(i, newData);
          }
        }
        max = Math.max(max, rangeData.endLine);
      }

      LineData[] linesArray = new LineData[max + 1];
      linesMap.forEachValue(data -> {
        data.fillArrays();
        linesArray[data.getLineNumber()] = data;
        return true;
      });
      classData.setLines(linesArray);
      return true;
    });

    return result;
  }

  @Override
  public String getPresentableName() {
    return PRESENTABLE_NAME;
  }

  @Override
  public String getId() {
    return ID;
  }

  @Override
  public String getDataFileExtension() {
    return DATA_FILE_EXTENSION;
  }

  @Override
  public boolean acceptsCoverageEngine(@NotNull CoverageEngine engine) {
    return engine instanceof GoCoverageEngine;
  }
}
