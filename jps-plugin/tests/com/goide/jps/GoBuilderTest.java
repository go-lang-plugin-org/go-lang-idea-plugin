/*
 * Copyright 2012-2013 Sergey Ignatov
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

package com.goide.jps;

import com.goide.jps.model.JpsGoModuleType;
import com.goide.jps.model.JpsGoSdkType;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.util.PathUtilRt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jps.model.JpsDummyElement;
import org.jetbrains.jps.model.JpsElement;
import org.jetbrains.jps.model.library.JpsOrderRootType;
import org.jetbrains.jps.model.library.JpsTypedLibrary;
import org.jetbrains.jps.model.library.sdk.JpsSdk;
import org.jetbrains.jps.model.module.JpsModule;
import org.jetbrains.jps.util.JpsPathUtil;

import java.io.File;
import java.util.Arrays;

public class GoBuilderTest extends JpsBuildTestCase {
  public static final String GO_LINUX_SDK_PATH = "/usr/lib/go";
  public static final String GO_MAC_SDK_PATH = "/usr/local/go";

  public void testSimple() throws Exception {
    String depFile = createFile("src/simple.go", "package main\nimport \"fmt\"\nfunc main() {\n\tfmt.Printf(\"Hello\\n\");\n}");
    String moduleName = "m";
    addModule(moduleName, PathUtilRt.getParentPath(depFile));
    rebuildAll();
    assertCompiled(moduleName, "simple");
  }

  private void assertCompiled(@NotNull String moduleName, @NotNull String fileName) {
    String absolutePath = getAbsolutePath("out/production/" + moduleName);
    String outDirContent = Arrays.toString(new File(absolutePath).list());
    assertNotNull("File '" + fileName + "' not found in " + outDirContent, FileUtil.findFileInProvidedPath(absolutePath, fileName));
  }

  @NotNull
  @Override
  protected JpsSdk<JpsDummyElement> addJdk(@NotNull String name, String path) {
    String homePath = getGoSdkPath();
    String versionString = "1.2";
    JpsTypedLibrary<JpsSdk<JpsDummyElement>> jdk = myModel.getGlobal().addSdk(versionString, homePath, versionString, JpsGoSdkType.INSTANCE);
    jdk.addRoot(JpsPathUtil.pathToUrl(homePath), JpsOrderRootType.COMPILED);
    return jdk.getProperties();
  }

  @NotNull
  private static String getGoSdkPath() {
    if (SystemInfo.isLinux) {
      return GO_LINUX_SDK_PATH;
    }
    else if (SystemInfo.isMac) {
      return GO_MAC_SDK_PATH;
    }
    throw new RuntimeException("Only mac & linux supported");
  }

  @Override
  protected <T extends JpsElement> JpsModule addModule(@NotNull String moduleName,
                                                       @NotNull String[] srcPaths,
                                                       @Nullable String outputPath,
                                                       @Nullable String testOutputPath,
                                                       @NotNull JpsSdk<T> sdk) {
    return addModule(moduleName, srcPaths, outputPath, testOutputPath, sdk, JpsGoModuleType.INSTANCE);
  }
}
