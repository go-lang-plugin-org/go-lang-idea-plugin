package com.goide.jps;

import com.goide.jps.model.JpsGoModuleType;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.application.ex.PathManagerEx;
import com.intellij.openapi.util.io.FileSystemUtil;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.testFramework.UsefulTestCase;
import com.intellij.util.TimeoutUtil;
import com.intellij.util.containers.hash.HashMap;
import com.intellij.util.io.TestFileSystemBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jps.api.CanceledStatus;
import org.jetbrains.jps.builders.impl.BuildDataPathsImpl;
import org.jetbrains.jps.builders.impl.BuildRootIndexImpl;
import org.jetbrains.jps.builders.impl.BuildTargetIndexImpl;
import org.jetbrains.jps.builders.impl.BuildTargetRegistryImpl;
import org.jetbrains.jps.builders.logging.BuildLoggingManager;
import org.jetbrains.jps.builders.storage.BuildDataPaths;
import org.jetbrains.jps.cmdline.ClasspathBootstrap;
import org.jetbrains.jps.cmdline.ProjectDescriptor;
import org.jetbrains.jps.incremental.BuilderRegistry;
import org.jetbrains.jps.incremental.IncProjectBuilder;
import org.jetbrains.jps.incremental.RebuildRequestedException;
import org.jetbrains.jps.incremental.fs.BuildFSState;
import org.jetbrains.jps.incremental.storage.BuildDataManager;
import org.jetbrains.jps.incremental.storage.BuildTargetsState;
import org.jetbrains.jps.incremental.storage.ProjectTimestamps;
import org.jetbrains.jps.indices.ModuleExcludeIndex;
import org.jetbrains.jps.indices.impl.IgnoredFileIndexImpl;
import org.jetbrains.jps.indices.impl.ModuleExcludeIndexImpl;
import org.jetbrains.jps.model.*;
import org.jetbrains.jps.model.java.JavaSourceRootType;
import org.jetbrains.jps.model.java.JpsJavaExtensionService;
import org.jetbrains.jps.model.java.JpsJavaModuleExtension;
import org.jetbrains.jps.model.java.JpsJavaSdkType;
import org.jetbrains.jps.model.library.JpsOrderRootType;
import org.jetbrains.jps.model.library.JpsTypedLibrary;
import org.jetbrains.jps.model.library.sdk.JpsSdk;
import org.jetbrains.jps.model.library.sdk.JpsSdkType;
import org.jetbrains.jps.model.module.JpsModule;
import org.jetbrains.jps.model.module.JpsSdkReferencesTable;
import org.jetbrains.jps.model.serialization.JpsProjectLoader;
import org.jetbrains.jps.model.serialization.PathMacroUtil;
import org.jetbrains.jps.util.JpsPathUtil;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

public abstract class JpsBuildTestCase extends UsefulTestCase {
  @Nullable private File myProjectDir;
  protected JpsProject myProject;
  protected JpsModel myModel;
  private JpsSdk<JpsDummyElement> myJdk;
  protected File myDataStorageRoot;
  private TestProjectBuilderLogger myLogger;

  protected Map<String, String> myBuildParams;

  protected static void rename(@NotNull String path, @NotNull String newName) {
    try {
      File file = new File(FileUtil.toSystemDependentName(path));
      assertTrue("File " + file.getAbsolutePath() + " doesn't exist", file.exists());
      final File tempFile = new File(file.getParentFile(), "__" + newName);
      FileUtil.rename(file, tempFile);
      File newFile = new File(file.getParentFile(), newName);
      FileUtil.copyContent(tempFile, newFile);
      FileUtil.delete(tempFile);
      change(newFile.getPath());
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    myModel = JpsElementFactory.getInstance().createModel();
    myProject = myModel.getProject();
    myDataStorageRoot = FileUtil.createTempDirectory("compile-server-" + getProjectName(), null);
    myLogger = new TestProjectBuilderLogger();
    myBuildParams = new HashMap<String, String>();
  }

  @Override
  protected void tearDown() throws Exception {
    myProjectDir = null;
    super.tearDown();
  }

  protected static void assertOutput(@NotNull final String outputPath, @NotNull TestFileSystemBuilder expected) {
    expected.build().assertDirectoryEqual(new File(FileUtil.toSystemDependentName(outputPath)));
  }

  protected static void assertOutput(JpsModule module, @NotNull TestFileSystemBuilder expected) {
    String outputUrl = JpsJavaExtensionService.getInstance().getOutputUrl(module, false);
    assertNotNull(outputUrl);
    assertOutput(JpsPathUtil.urlToPath(outputUrl), expected);
  }

  protected static void change(@NotNull String filePath) {
    change(filePath, null);
  }

  protected static void change(@NotNull String filePath, final @Nullable String newContent) {
    try {
      File file = new File(FileUtil.toSystemDependentName(filePath));
      assertTrue("File " + file.getAbsolutePath() + " doesn't exist", file.exists());
      if (newContent != null) {
        FileUtil.writeToFile(file, newContent);
      }
      long oldTimestamp = FileSystemUtil.lastModified(file);
      long time = System.currentTimeMillis();
      setLastModified(file, time);
      if (FileSystemUtil.lastModified(file) <= oldTimestamp) {
        setLastModified(file, time + 1);
        long newTimeStamp = FileSystemUtil.lastModified(file);
        if (newTimeStamp <= oldTimestamp) {
          //Mac OS and some versions of Linux truncates timestamp to nearest second
          setLastModified(file, time + 1000);
          newTimeStamp = FileSystemUtil.lastModified(file);
          assertTrue("Failed to change timestamp for " + file.getAbsolutePath(), newTimeStamp > oldTimestamp);
        }
        sleepUntil(newTimeStamp);
      }
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  protected static void sleepUntil(long time) {
    //we need this to ensure that the file won't be treated as changed by user during compilation and therefore marked for recompilation
    long delta;
    while ((delta = time - System.currentTimeMillis()) > 0) {
      TimeoutUtil.sleep(delta);
    }
  }

  private static void setLastModified(@NotNull File file, long time) {
    boolean updated = file.setLastModified(time);
    assertTrue("Cannot modify timestamp for " + file.getAbsolutePath(), updated);
  }

  @NotNull
  protected JpsSdk<JpsDummyElement> addJdk(@NotNull final String name) {
    try {
      return addJdk(name, FileUtil.toSystemIndependentName(ClasspathBootstrap.getResourceFile(Object.class).getCanonicalPath()));
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @NotNull
  protected JpsSdk<JpsDummyElement> addJdk(@NotNull final String name, final String path) {
    String homePath = System.getProperty("java.home");
    String versionString = System.getProperty("java.version");
    JpsTypedLibrary<JpsSdk<JpsDummyElement>> jdk = myModel.getGlobal().addSdk(name, homePath, versionString, JpsJavaSdkType.INSTANCE);
    jdk.addRoot(JpsPathUtil.pathToUrl(path), JpsOrderRootType.COMPILED);
    return jdk.getProperties();
  }

  protected String getProjectName() {
    return StringUtil.decapitalize(StringUtil.trimStart(getName(), "test"));
  }

  @NotNull
  protected ProjectDescriptor createProjectDescriptor(final BuildLoggingManager buildLoggingManager) {
    try {
      BuildTargetRegistryImpl targetRegistry = new BuildTargetRegistryImpl(myModel);
      ModuleExcludeIndex index = new ModuleExcludeIndexImpl(myModel);
      IgnoredFileIndexImpl ignoredFileIndex = new IgnoredFileIndexImpl(myModel);
      BuildDataPaths dataPaths = new BuildDataPathsImpl(myDataStorageRoot);
      BuildRootIndexImpl buildRootIndex = new BuildRootIndexImpl(targetRegistry, myModel, index, dataPaths, ignoredFileIndex);
      BuildTargetIndexImpl targetIndex = new BuildTargetIndexImpl(targetRegistry, buildRootIndex);
      BuildTargetsState targetsState = new BuildTargetsState(dataPaths, myModel, buildRootIndex);
      ProjectTimestamps timestamps = new ProjectTimestamps(myDataStorageRoot, targetsState);
      BuildDataManager dataManager = new BuildDataManager(dataPaths, targetsState, true);
      return new ProjectDescriptor(myModel, new BuildFSState(true), timestamps, dataManager, buildLoggingManager, index, targetsState,
                                   targetIndex, buildRootIndex, ignoredFileIndex);
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  protected void loadProject(String projectPath) {
    loadProject(projectPath, Collections.<String, String>emptyMap());
  }

  protected void loadProject(String projectPath,
                             @NotNull Map<String, String> pathVariables) {
    try {
      String testDataRootPath = getTestDataRootPath();
      String fullProjectPath = FileUtil.toSystemDependentName(testDataRootPath != null ? testDataRootPath + "/" + projectPath : projectPath);
      Map<String, String> allPathVariables = new HashMap<String, String>(pathVariables.size() + 1);
      allPathVariables.putAll(pathVariables);
      allPathVariables.put(PathMacroUtil.APPLICATION_HOME_DIR, PathManager.getHomePath());
      allPathVariables.putAll(getAdditionalPathVariables());
      JpsProjectLoader.loadProject(myProject, allPathVariables, fullProjectPath);
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @NotNull
  protected Map<String, String> getAdditionalPathVariables() {
    return Collections.emptyMap();
  }

  @Nullable
  protected String getTestDataRootPath() {
    return null;
  }

  @NotNull
  protected <T extends JpsElement> JpsModule addModule(@NotNull String moduleName,
                                                       @NotNull String[] srcPaths,
                                                       @Nullable String outputPath,
                                                       @Nullable String testOutputPath,
                                                       @NotNull JpsSdk<T> sdk) {
    JpsModule module = myProject.addModule(moduleName, JpsGoModuleType.INSTANCE);
    final JpsSdkType<T> sdkType = sdk.getSdkType();
    final JpsSdkReferencesTable sdkTable = module.getSdkReferencesTable();
    sdkTable.setSdkReference(sdkType, sdk.createReference());

    module.getDependenciesList().addSdkDependency(sdkType);
    if (srcPaths.length > 0 || outputPath != null) {
      for (String srcPath : srcPaths) {
        module.getContentRootsList().addUrl(JpsPathUtil.pathToUrl(srcPath));
        module.addSourceRoot(JpsPathUtil.pathToUrl(srcPath), JavaSourceRootType.SOURCE);
      }
      JpsJavaModuleExtension extension = JpsJavaExtensionService.getInstance().getOrCreateModuleExtension(module);
      if (outputPath != null) {
        extension.setOutputUrl(JpsPathUtil.pathToUrl(outputPath));
        if (!StringUtil.isEmpty(testOutputPath)) {
          extension.setTestOutputUrl(JpsPathUtil.pathToUrl(testOutputPath));
        }
        else {
          extension.setTestOutputUrl(extension.getOutputUrl());
        }
      }
      else {
        extension.setInheritOutput(true);
      }
    }
    return module;
  }

  protected void rebuildAll() {
    doBuild(CompileScopeTestBuilder.rebuild().all()).assertSuccessful();
  }

  @NotNull
  protected BuildResult doBuild(@NotNull CompileScopeTestBuilder scope) {
    ProjectDescriptor descriptor = createProjectDescriptor(new BuildLoggingManager(myLogger));
    try {
      myLogger.clear();
      return doBuild(descriptor, scope);
    }
    finally {
      descriptor.release();
    }
  }

  @NotNull
  protected BuildResult doBuild(@NotNull final ProjectDescriptor descriptor, @NotNull CompileScopeTestBuilder scopeBuilder) {
    IncProjectBuilder builder = new IncProjectBuilder(descriptor, BuilderRegistry.getInstance(), myBuildParams, CanceledStatus.NULL, null, true);
    BuildResult result = new BuildResult();
    builder.addMessageHandler(result);
    try {
      beforeBuildStarted(descriptor);
      builder.build(scopeBuilder.build(), false);
    }
    catch (RebuildRequestedException e) {
      throw new RuntimeException(e);
    }
    return result;
  }

  protected void beforeBuildStarted(@NotNull ProjectDescriptor descriptor) {
  }

  @NotNull
  protected String createFile(@NotNull String relativePath) {
    return createFile(relativePath, "");
  }

  @NotNull
  public String createFile(@NotNull String relativePath, @NotNull final String text) {
    try {
      File file = new File(getOrCreateProjectDir(), relativePath);
      FileUtil.writeToFile(file, text);
      return FileUtil.toSystemIndependentName(file.getAbsolutePath());
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @NotNull
  protected File findFindUnderProjectHome(@NotNull String relativeSourcePath) {
    return PathManagerEx.findFileUnderProjectHome(relativeSourcePath, getClass());
  }

  @Nullable
  public File getOrCreateProjectDir() {
    if (myProjectDir == null) {
      try {
        myProjectDir = doGetProjectDir();
      }
      catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    return myProjectDir;
  }

  @NotNull
  protected File doGetProjectDir() throws IOException {
    return FileUtil.createTempDirectory("prj", null);
  }

  @NotNull
  public String getAbsolutePath(@NotNull final String pathRelativeToProjectRoot) {
    return FileUtil.toSystemIndependentName(new File(getOrCreateProjectDir(), pathRelativeToProjectRoot).getAbsolutePath());
  }

  @NotNull
  public JpsModule addModule(@NotNull String moduleName, @NotNull String... srcPaths) {
    if (myJdk == null) {
      myJdk = addJdk("1.6");
    }
    return addModule(moduleName, srcPaths, getAbsolutePath("out/production/" + moduleName), null, myJdk);
  }
}