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

package com.goide;

import com.goide.sdk.GoSdkService;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.testFramework.PlatformTestUtil;
import com.intellij.testFramework.UsefulTestCase;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class GoSdkServiceTest extends UsefulTestCase {
  public void testRegularSdkPath() {
    setIsWindows(false);
    setIsLinux(false);
    PlatformTestUtil.assertPathsEqual("/path/to/sdk/bin/go", executable("/path/to/sdk"));
  }
  
  public void testRegularSdkPathWithGorootName() {
    setIsWindows(false);
    setIsLinux(false);
    String sdkPath = createDir("goroot/").getAbsolutePath();
    PlatformTestUtil.assertPathsEqual(sdkPath + "/goroot/bin/go", executable(sdkPath + "/goroot"));
  }

  public void testSingletonAppEngineSdkPath() {
    setIsWindows(false);
    String sdkPath = createDir("goroot/", "goapp").getAbsolutePath();
    PlatformTestUtil.assertPathsEqual(sdkPath + "/goapp", executable(sdkPath + "/goroot"));
  }

  public void testGcloudAppEngineSdkPath() {
    setIsWindows(false);
    String sdkPath = createDir("platform/google_appengine/goroot/", "bin/goapp").getAbsolutePath();
    PlatformTestUtil.assertPathsEqual(sdkPath + "/bin/goapp", executable(sdkPath + "/platform/google_appengine/goroot"));
  }

  public void testRegularSdkPathWindows() {
    setIsWindows(true);
    setIsLinux(false);
    String sdkPath = createDir("platform/google_appengine/goroot/", "bin/goapp").getAbsolutePath();
    PlatformTestUtil.assertPathsEqual(sdkPath + "/bin/go.exe", executable(sdkPath));
  }

  public void testSingletonAppEngineSdkPathWindows() {
    setIsWindows(true);
    String sdkPath = createDir("goroot/", "goapp.bat").getAbsolutePath();
    PlatformTestUtil.assertPathsEqual(sdkPath + "/goapp.bat", executable(sdkPath + "/goroot"));
  }

  public void testGcloudAppEngineSdkPathWindows() {
    setIsWindows(true);
    String sdkPath = createDir("platform/google_appengine/goroot/", "bin/goapp.cmd").getAbsolutePath();
    PlatformTestUtil.assertPathsEqual(sdkPath + "/bin/goapp.cmd", executable(sdkPath + "/platform/google_appengine/goroot"));
  }

  private static String executable(@NotNull String sdkPath) {
    return GoSdkService.getGoExecutablePath(sdkPath);
  }

  private void setIsWindows(boolean value) {
    setIsWindows(value, SystemInfo.isWindows, "isWindows");
  }

  private void setIsLinux(boolean value) {
    setIsWindows(value, SystemInfo.isLinux, "isLinux");
  }

  private void setIsWindows(boolean value, boolean oldValue, @NotNull String fieldName) {
    try {
      Field field = SystemInfo.class.getField(fieldName);
      field.setAccessible(true);
      Field modifiersField = Field.class.getDeclaredField("modifiers");
      modifiersField.setAccessible(true);
      modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
      field.set(null, value);

      Disposer.register(getTestRootDisposable(), () -> {
        try {
          field.set(null, oldValue);
        }
        catch (IllegalAccessException e) {
          throw new RuntimeException(e);
        }
      });
    }
    catch (IllegalAccessException | NoSuchFieldException e) {
      throw new RuntimeException(e);
    }
  }

  private static File createDir(String... children) {
    try {
      File dir = FileUtil.createTempDirectory("goSdk", "test");
      for (String child : children) {
        File file = new File(dir, child);
        FileUtil.createParentDirs(file);
        if (StringUtil.endsWithChar(child, '/')) {
          assertTrue(file.mkdir());
        }
        else {
          assertTrue(file.createNewFile());
        }
      }
      return dir;
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
