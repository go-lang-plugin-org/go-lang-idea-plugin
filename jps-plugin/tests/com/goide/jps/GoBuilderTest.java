package com.goide.jps;

import com.goide.jps.model.JpsGoSdkType;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.util.PathUtilRt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.incremental.messages.BuildMessage;
import org.jetbrains.jps.model.JpsDummyElement;
import org.jetbrains.jps.model.library.JpsOrderRootType;
import org.jetbrains.jps.model.library.JpsTypedLibrary;
import org.jetbrains.jps.model.library.sdk.JpsSdk;
import org.jetbrains.jps.util.JpsPathUtil;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class GoBuilderTest extends JpsBuildTestCase {
  public static final String GO_LINUX_SDK_PATH = "/usr/local/go";
  public static final String GO_MAC_SDK_PATH = "/usr/local/go";

  public void testSimple() throws Exception {
    if (!SystemInfo.isMac && !SystemInfo.isLinux) return;

    String depFile = createFile("simple/simple.go", "package main\nimport \"fmt\"\nfunc main() {\n\tfmt.Printf(\"Hello\\n\");\n}");
    String moduleName = "m";
    addModule(moduleName, PathUtilRt.getParentPath(depFile));
    rebuildAll();
    assertCompiled(moduleName, "simple");
  }

  public void testDependentFiles() throws Exception {
    if (!SystemInfo.isMac && !SystemInfo.isLinux) return;

    String mainFile = createFile("simple/simple.go", "package main\nfunc main() {\n\tSayHello();\n}");
    createFile("simple/depFile.go", "package main\nimport \"fmt\"\nfunc SayHello() {\n\tfmt.Printf(\"Hello\\n\");\n}");
    String moduleName = "m";
    addModule(moduleName, PathUtilRt.getParentPath(mainFile));
    rebuildAll();
    assertCompiled(moduleName, "simple");
  }

  public void testCompilerErrors() {
    if (!SystemInfo.isMac && !SystemInfo.isLinux) return;

    String depFile = createFile("simple/errors.go", "package main\nimport \"fmt\"\nfunc main() {\n\tfmt.Printf(\"Hello\\n);\n}");
    String moduleName = "m";
    addModule(moduleName, PathUtilRt.getParentPath(depFile));
    BuildResult result = doBuild(CompileScopeTestBuilder.rebuild().all());
    result.assertFailed();

    List<BuildMessage> errors = result.getMessages(BuildMessage.Kind.ERROR);
    assertEquals(2, errors.size());

    assertEquals("newline in string", errors.get(0).getMessageText());
    assertEquals(BuildMessage.Kind.ERROR, errors.get(0).getKind());

    assertEquals("syntax error: unexpected }, expecting )", errors.get(1).getMessageText());
    assertEquals(BuildMessage.Kind.ERROR, errors.get(1).getKind());
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
    if (SystemInfo.isLinux) return GO_LINUX_SDK_PATH;
    if (SystemInfo.isMac) return GO_MAC_SDK_PATH;
    throw new RuntimeException("Only mac & linux supported");
  }
}
