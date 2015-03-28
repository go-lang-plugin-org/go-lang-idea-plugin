package com.goide.jps.builder;

import com.goide.GoConstants;
import com.goide.GoEnvironmentUtil;
import com.goide.jps.model.JpsGoLibrariesExtensionService;
import com.goide.jps.model.JpsGoModuleProperties;
import com.goide.jps.model.JpsGoModuleType;
import com.goide.jps.model.JpsGoSdkType;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.BaseOSProcessHandler;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.io.FileUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.builders.BuildOutputConsumer;
import org.jetbrains.jps.builders.DirtyFilesHolder;
import org.jetbrains.jps.incremental.CompileContext;
import org.jetbrains.jps.incremental.ProjectBuildException;
import org.jetbrains.jps.incremental.TargetBuilder;
import org.jetbrains.jps.incremental.messages.BuildMessage;
import org.jetbrains.jps.incremental.messages.CompilerMessage;
import org.jetbrains.jps.incremental.resources.ResourcesBuilder;
import org.jetbrains.jps.incremental.resources.StandardResourceBuilderEnabler;
import org.jetbrains.jps.model.JpsDummyElement;
import org.jetbrains.jps.model.JpsSimpleElement;
import org.jetbrains.jps.model.java.JpsJavaExtensionService;
import org.jetbrains.jps.model.library.sdk.JpsSdk;
import org.jetbrains.jps.model.module.JpsModule;
import org.jetbrains.jps.model.module.JpsTypedModule;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Arrays;

public class GoBuilder extends TargetBuilder<GoSourceRootDescriptor, GoTarget> {
  public static final String NAME = "go";
  private final static Logger LOG = Logger.getInstance(GoBuilder.class);

  public GoBuilder() {
    super(Arrays.asList(GoTargetType.PRODUCTION, GoTargetType.TESTS));
    ResourcesBuilder.registerEnabler(new StandardResourceBuilderEnabler() {
      @Override
      public boolean isResourceProcessingEnabled(@NotNull JpsModule module) {
        return module.getModuleType() != JpsGoModuleType.INSTANCE;
      }
    });
  }

  @Override
  public void build(@NotNull GoTarget target,
                    @NotNull DirtyFilesHolder<GoSourceRootDescriptor, GoTarget> holder,
                    @NotNull BuildOutputConsumer outputConsumer,
                    @NotNull CompileContext context) throws ProjectBuildException, IOException {
    LOG.debug(target.getPresentableName());
    if (!holder.hasDirtyFiles() && !holder.hasRemovedFiles()) return;

    JpsModule jpsModule = target.getModule();
    if (jpsModule.getModuleType() != JpsGoModuleType.INSTANCE) return;
    
    JpsTypedModule<JpsSimpleElement<JpsGoModuleProperties>> module = jpsModule.asTyped(JpsGoModuleType.INSTANCE);
    assert module != null;
    JpsSdk<JpsDummyElement> sdk = getSdk(context, module);
    File executable = new File(sdk.getHomePath(), "bin/" + GoEnvironmentUtil.getBinaryFileNameForPath(GoConstants.GO_EXECUTABLE_NAME));
    File outputDirectory = getBuildOutputDirectory(module, target.isTests(), context);

    for (String contentRootUrl : module.getContentRootsList().getUrls()) {
      String contentRootPath = new URL(contentRootUrl).getPath();
      GeneralCommandLine commandLine = new GeneralCommandLine();
      commandLine.getEnvironment().put(GoConstants.GO_PATH, JpsGoLibrariesExtensionService.getInstance().retrieveGoPath(module));
      commandLine.withWorkDirectory(contentRootPath);
      commandLine.setExePath(executable.getAbsolutePath());
      commandLine.addParameter("build");
      String outExecutable = GoEnvironmentUtil.getExecutableResultForModule(contentRootPath, outputDirectory.getAbsolutePath());
      commandLine.addParameters("-o", FileUtil.toSystemDependentName(outExecutable));
      runBuildProcess(context, commandLine, contentRootPath);
    }
  }

  @NotNull
  @Override
  public String getPresentableName() {
    return NAME;
  }

  @NotNull
  private static File getBuildOutputDirectory(@NotNull JpsModule module,
                                              boolean forTests,
                                              @NotNull CompileContext context) throws ProjectBuildException {
    JpsJavaExtensionService instance = JpsJavaExtensionService.getInstance();
    File outputDirectory = instance.getOutputDirectory(module, forTests);
    if (outputDirectory == null) {
      String errorMessage = "No output dir for module " + module.getName();
      context.processMessage(new CompilerMessage(NAME, BuildMessage.Kind.ERROR, errorMessage));
      throw new ProjectBuildException(errorMessage);
    }
    if (!outputDirectory.exists()) {
      FileUtil.createDirectory(outputDirectory);
    }
    return outputDirectory;
  }

  @NotNull
  private static JpsSdk<JpsDummyElement> getSdk(@NotNull CompileContext context,
                                                @NotNull JpsModule module) throws ProjectBuildException {
    JpsSdk<JpsDummyElement> sdk = module.getSdk(JpsGoSdkType.INSTANCE);
    if (sdk == null) {
      String errorMessage = "No SDK for module " + module.getName();
      context.processMessage(new CompilerMessage(NAME, BuildMessage.Kind.ERROR, errorMessage));
      throw new ProjectBuildException(errorMessage);
    }
    return sdk;
  }

  private static void runBuildProcess(@NotNull CompileContext context, @NotNull GeneralCommandLine commandLine, @NotNull String path)
    throws ProjectBuildException {
    Process process;
    try {
      process = commandLine.createProcess();
    }
    catch (ExecutionException e) {
      throw new ProjectBuildException("Failed to launch go compiler", e);
    }
    BaseOSProcessHandler handler = new BaseOSProcessHandler(process, commandLine.getCommandLineString(), Charset.defaultCharset());
    ProcessAdapter adapter = new GoCompilerProcessAdapter(context, NAME, path);
    handler.addProcessListener(adapter);
    handler.startNotify();
    handler.waitFor();
  }
}
