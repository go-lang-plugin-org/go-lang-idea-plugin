package com.goide;

import com.goide.sdk.GoSdkService;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.testFramework.UsefulTestCase;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class GoSdkServiceTest extends UsefulTestCase {
  public void testRegularSdkPath() {
    setIsWindows(false);
    assertEquals("/path/to/sdk/bin/go", getExecutablePath("/path/to/sdk"));
  }
  
  public void testSingletonAppEngineSdkPath() {
    setIsWindows(false);
    assertEquals("/path/to/sdk/goapp", getExecutablePath("/path/to/sdk/goroot"));
  }
  
  public void testGcloudAppEngineSdkPath() {
    setIsWindows(false);
    assertEquals("/path/to/sdk/bin/goapp", getExecutablePath("/path/to/sdk/platform/google_appengine/goroot"));
  }
  
  public void testRegularSdkPathWindows() {
    setIsWindows(true);
    assertEquals("/path/to/sdk/bin/go.exe", getExecutablePath("/path/to/sdk"));
  }
  
  public void testSingletonAppEngineSdkPathWindows() {
    setIsWindows(true);
    assertEquals("/path/to/sdk/goapp.bat", getExecutablePath("/path/to/sdk/goroot"));
  }
  
  public void testGcloudAppEngineSdkPathWindows() {
    setIsWindows(true);
    assertEquals("/path/to/sdk/bin/goapp.cmd", getExecutablePath("/path/to/sdk/platform/google_appengine/goroot"));
  }

  private static String getExecutablePath(@NotNull String sdkPath) {
    return FileUtil.toSystemIndependentName(GoSdkService.getGoExecutablePath(sdkPath));
  }

  private void setIsWindows(boolean value) {
    try {
      final boolean oldValue = SystemInfo.isWindows;
      final Field field = SystemInfo.class.getField("isWindows");
      field.setAccessible(true);
      Field modifiersField = Field.class.getDeclaredField("modifiers");
      modifiersField.setAccessible(true);
      modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
      field.set(null, value);
      
      Disposer.register(getTestRootDisposable(), new Disposable() {
        @Override
        public void dispose() {
          try {
            field.set(null, oldValue);
          }
          catch (IllegalAccessException e) {
            throw new RuntimeException(e);
          }
        }
      });
    }
    catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
    catch (NoSuchFieldException e) {
      throw new RuntimeException(e);
    }
  }  
}
