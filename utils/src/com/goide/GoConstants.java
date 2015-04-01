package com.goide;

import com.intellij.notification.NotificationGroup;
import com.intellij.openapi.wm.ToolWindowId;
import org.jetbrains.annotations.NonNls;

public class GoConstants {
  public static final String MODULE_TYPE_ID = "GO_MODULE";
  public static final String SDK_TYPE_ID = "Go SDK";
  public static final String GO_PATH = "GOPATH";
  public static final String GO_ROOT = "GOROOT";
  public static final String GO_LIBRARIES_SERVICE_NAME = "GoLibraries";
  public static final String GO_LIBRARIES_CONFIG_FILE = "goLibraries.xml";

  public static final String TEST_SUFFIX = "_test";
  public static final String TEST_SUFFIX_WITH_EXTENSION = "_test.go";
  public static final String MAIN = "main";
  public static final String INIT = "init";

  public static final String GO_NOTIFICATION_GROUP = "Go plugin notifications";
  public static NotificationGroup GO_EXECUTION_NOTIFICATION_GROUP = NotificationGroup.toolWindowGroup("Go Execution", ToolWindowId.RUN);
  
  @NonNls public static final String LIB_EXEC_DIRECTORY = "libexec";
  @NonNls public static final String GO_VERSION_FILE_PATH = "runtime/zversion.go";
  public static final String BUILTIN_FILE_PATH = "builtin/builtin.go";
  
  @NonNls public static final String APP_ENGINE_MARKER_FILE = "appcfg.py";
  @NonNls public static final String APP_ENGINE_GO_ROOT_DIRECTORY_PATH = "/goroot";
  @NonNls public static final String GCLOUD_APP_ENGINE_DIRECTORY_PATH = "/platform/google_appengine";
  @NonNls public static final String GAE_EXECUTABLE_NAME = "goapp";
  @NonNls public static final String GAE_BAT_EXECUTABLE_NAME = "goapp.bat";
  @NonNls public static final String GAE_CMD_EXECUTABLE_NAME = "goapp.cmd";

  @NonNls public static final String GO_EXECUTABLE_NAME = "go";

  private GoConstants() {
    
  }
}
