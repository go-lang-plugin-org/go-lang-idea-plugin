package ro.redeul.google.go.compilation;

import com.intellij.compiler.impl.CompilerUtil;
import com.intellij.compiler.make.MakeUtil;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.compiler.CompileContext;
import com.intellij.openapi.compiler.CompileScope;
import com.intellij.openapi.compiler.TranslatingCompiler;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.roots.ui.configuration.ClasspathEditor;
import com.intellij.openapi.roots.ui.configuration.ModulesConfigurator;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.Trinity;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.util.Chunk;
import com.intellij.util.CommonProcessors;
import com.intellij.util.FilteringProcessor;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.GoBundle;
import ro.redeul.google.go.GoFileType;
import ro.redeul.google.go.config.sdk.GoSdkData;
import ro.redeul.google.go.config.sdk.GoTargetOs;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.sdk.GoSdkTool;
import ro.redeul.google.go.sdk.GoSdkUtil;
import ro.redeul.google.go.util.GoUtil;

import java.io.File;
import java.util.*;

public class GoCompiler implements TranslatingCompiler {

    private static final Logger LOG = Logger.getInstance(
        "#ro.redeul.google.go.compilation.GoCompiler");

    private final Project project;
    private CompileContext context;
    private OutputSink sink;

    public GoCompiler(Project project) {
        this.project = project;
    }

    @NotNull
    public String getDescription() {
        return "Go Compiler";
    }

    public boolean validateConfiguration(CompileScope scope) {

        final VirtualFile files[] = scope.getFiles(GoFileType.INSTANCE, true);

        final Set<Module> affectedModules = new HashSet<>();

        ApplicationManager.getApplication().runReadAction(new Runnable() {
            public void run() {
                ProjectFileIndex projectFileIndex = ProjectRootManager.getInstance(
                    project).getFileIndex();
                for (VirtualFile file : files) {
                    affectedModules.add(
                        projectFileIndex.getModuleForFile(file));
                }
            }
        });

        if (LOG.isDebugEnabled()) {
            LOG.debug(
                "[validating] Found affected modules: " + affectedModules);
        }

        for (Module affectedModule : affectedModules) {
            if (findGoSdkForModule(affectedModule) == null) {

                Messages.showErrorDialog(
                    affectedModule.getProject(),
                    GoBundle.message("cannot.compile.go.files.no.facet",
                                     affectedModule.getName()),
                    GoBundle.message("cannot.compile"));

                ModulesConfigurator.showDialog(affectedModule.getProject(),
                                               affectedModule.getName(),
                                               ClasspathEditor.NAME);

                return false;
            }
        }

        return true;
    }

    public boolean isCompilableFile(VirtualFile file, CompileContext context) {
        return file.getFileType() == GoFileType.INSTANCE;
    }

    public void compile(CompileContext context, Chunk<Module> moduleChunk, VirtualFile[] files, OutputSink sink) {
        this.context = context;
        this.sink = sink;

        VirtualFile allFiles[] = findAllFiles(moduleChunk);

        Map<Module, List<VirtualFile>> mapToFiles = CompilerUtil.buildModuleToFilesMap(
            context, allFiles);

        for (Module module : mapToFiles.keySet()) {
            compileFilesInsideModule(module, mapToFiles.get(module));
        }
    }

    private VirtualFile[] findAllFiles(Chunk<Module> moduleChunk) {

        final CommonProcessors.CollectUniquesProcessor<VirtualFile> collector = new CommonProcessors.CollectUniquesProcessor<>();

        for (Module module : moduleChunk.getNodes()) {

            final VirtualFile sourceRoots[] = ModuleRootManager.getInstance(
                module).getSourceRoots();

            ApplicationManager.getApplication().runReadAction(new Runnable() {
                public void run() {
                    for (final VirtualFile sourceRoot : sourceRoots) {
                        VfsUtil.processFilesRecursively(sourceRoot,
                                                        new FilteringProcessor<>(
                                                            new Condition<VirtualFile>() {
                                                                @Override
                                                                public boolean value(VirtualFile virtualFile) {
                                                                    return virtualFile
                                                                        .getFileType() == GoFileType.INSTANCE &&
                                                                        !virtualFile
                                                                            .getNameWithoutExtension()
                                                                            .matches(
                                                                                ".*_test$");
                                                                }
                                                            }, collector));
                    }
                }
            });
        }

        return collector.toArray(
            new VirtualFile[collector.getResults().size()]);
    }

    private void compileFilesInsideModule(final Module module, List<VirtualFile> files) {

        final String goOutputRelativePath = "/go-bins";

        context.getProgressIndicator()
               .setText(GoBundle.message("compiler.compiling.module",
                                         module.getName()));

        if (LOG.isDebugEnabled()) {
            LOG.debug("[compiling] Compiling module: " + module.getName());
        }

        final VirtualFile baseOutputFile = context.getModuleOutputDirectory(
            module);
        if (baseOutputFile == null) {
            return;
        }

        String baseOutputPath = findOrCreateGoOutputPath(module,
                                                         goOutputRelativePath,
                                                         baseOutputFile);

        final Map<VirtualFile, Map<String, List<Pair<VirtualFile, GoFileMetadata>>>> sourceRootsMap;

        sourceRootsMap = sortFilesBySourceRoots(context, module, files);

        final Sdk sdk = findGoSdkForModule(module);

        for (VirtualFile sourceRoot : sourceRootsMap.keySet()) {
            compileForSourceRoot(sourceRoot, sourceRootsMap.get(sourceRoot),
                                 baseOutputPath, sdk);
        }
    }

    private void compileForSourceRoot(VirtualFile sourceRoot,
                                      Map<String, List<Pair<VirtualFile, GoFileMetadata>>> filesToCompile,
                                      String baseOutputPath, Sdk sdk) {

        if (LOG.isDebugEnabled()) {
            LOG.debug(
                "[compiling] Compiling source root: " + sourceRoot.getPath());
        }

        // sorting reverse by relative paths.
        List<String> relativePaths = new ArrayList<>(
            filesToCompile.keySet());
        Collections.sort(relativePaths, new Comparator<String>() {
            public int compare(String o1, String o2) {
                return o2.length() - o1.length();
            }
        });

        if (LOG.isDebugEnabled()) {
            LOG.debug("[compiling] Sorted file folders: " + relativePaths);
        }

        // packageName | applicationName -> Package | Application, dependencies, files
        Map<String, Trinity<TargetType, Set<String>, List<VirtualFile>>> targets = new HashMap<>();

        for (String relativePath : relativePaths) {
            List<Pair<VirtualFile, GoFileMetadata>> list = filesToCompile.get(
                relativePath);

            if (LOG.isDebugEnabled()) {
                LOG.debug("[compiling] " + relativePath + " => " + list);
            }

            // sort by declaredPackageName
            Collections.sort(list,
                             new Comparator<Pair<VirtualFile, GoFileMetadata>>() {
                                 public int compare(Pair<VirtualFile, GoFileMetadata> o1, Pair<VirtualFile, GoFileMetadata> o2) {
                                     return o1.getSecond()
                                              .getPackageName()
                                              .compareTo(o2.getSecond()
                                                           .getPackageName());
                                 }
                             });

            if (LOG.isDebugEnabled()) {
                LOG.debug(
                    "[compiling] after sorting by packages: " + relativePath + " => " + list);
            }

            String currentPackage = "";
            List<Pair<VirtualFile, GoFileMetadata>> targetFiles = new ArrayList<>();
            for (Pair<VirtualFile, GoFileMetadata> fileMetadata : list) {
                // file, declaredPackage, imports

                if (!currentPackage.equals(
                    fileMetadata.getSecond().getPackageName())) {
                    convertToTarget(targets, sourceRoot, relativePath,
                                    currentPackage, targetFiles);
                    currentPackage = fileMetadata.getSecond().getPackageName();
                    targetFiles.clear();
                }

                targetFiles.add(new Pair<>(
                    fileMetadata.getFirst(), fileMetadata.getSecond()));
            }

            if (targetFiles.size() != 0) {
                convertToTarget(targets, sourceRoot, relativePath,
                                currentPackage, targetFiles);
            }
        }

        if (LOG.isDebugEnabled()) {
            for (Map.Entry<String, Trinity<TargetType, Set<String>, List<VirtualFile>>> targetEntry : targets
                .entrySet()) {
                LOG.debug(
                    String.format("[structure]: %s: \"%s\" => %s %d files",
                                  targetEntry.getValue().getFirst(),
                                  targetEntry.getKey(),
                                  targetEntry.getValue().getSecond(),
                                  targetEntry.getValue().getThird().size()));
            }
        }

        buildTargets(sourceRoot, targets, baseOutputPath, sdk);
    }

    private void buildTargets(VirtualFile sourceRoot,
                              Map<String, Trinity<TargetType, Set<String>, List<VirtualFile>>> targets,
                              String baseOutputPath, Sdk sdk) {
        // collect only library targets
        Set<String> allLibraries = new HashSet<>();
        for (Map.Entry<String, Trinity<TargetType, Set<String>, List<VirtualFile>>> targetEntry : targets
            .entrySet()) {
            if (targetEntry.getValue().getFirst() == TargetType.Library) {
                allLibraries.add(targetEntry.getKey());
            }
        }

        // extract the local dependency graph and also initialize the starting set for the topological sort
        Set<String> startSet = new HashSet<>();
        Map<String, Set<String>> mutableDependenciesGraph = new HashMap<>();

        for (Map.Entry<String, Trinity<TargetType, Set<String>, List<VirtualFile>>> targetEntry : targets
            .entrySet()) {
            Set<String> properDependencies = new HashSet<>();

            for (String dependency : targetEntry.getValue().getSecond()) {
                if (allLibraries.contains(dependency)) {
                    properDependencies.add(dependency);
                }
            }

            if (properDependencies.size() == 0) {
                startSet.add(targetEntry.getKey());
            } else {
                mutableDependenciesGraph.put(targetEntry.getKey(),
                                             properDependencies);
            }
        }

        // do a topological sorting
        List<String> buildOrder = new ArrayList<>();
        while (startSet.size() > 0) {

            for (String node : startSet) {
                buildOrder.add(node);

                for (Map.Entry<String, Set<String>> dependency : mutableDependenciesGraph
                    .entrySet()) {
                    dependency.getValue().remove(node);
                }
            }

            startSet.clear();

            for (Map.Entry<String, Set<String>> dependency : mutableDependenciesGraph
                .entrySet()) {
                if (dependency.getValue().size() == 0) {
                    startSet.add(dependency.getKey());
                }
            }

            for (String node : startSet) {
                mutableDependenciesGraph.remove(node);
            }
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("[sorting]: Build order: " + buildOrder);
        }

        for (String targetToBuild : buildOrder) {
            buildTarget(sourceRoot, targetToBuild, targets.get(targetToBuild),
                        baseOutputPath, sdk);
        }
    }

    private void buildTarget(VirtualFile sourceRoot, String target, Trinity<TargetType, Set<String>, List<VirtualFile>> targetDescription,
                             String baseOutputPath, Sdk sdk) {
        switch (targetDescription.getFirst()) {
            case Application:
                buildApplication(sourceRoot, target, targetDescription,
                                 baseOutputPath, sdk);
                break;
            case Library:
                buildLibrary(sourceRoot, target, targetDescription,
                             baseOutputPath, sdk);
                break;
        }
    }

    private void buildApplication(VirtualFile sourceRoot, String target,
                                  Trinity<TargetType, Set<String>, List<VirtualFile>> targetDescription,
                                  String baseOutputPath, final Sdk sdk) {
        if (LOG.isDebugEnabled()) {
            LOG.debug(String.format("[building] application [%s]", target));
        }

        File targetFile = new File(baseOutputPath + "/" + target);
        File targetPath = targetFile.getParentFile();

        if (!targetPath.exists() && !targetPath.mkdirs()) {
            LOG.warn("Could not create parent dirs: " + targetPath);
        }

        String outputBinary = targetFile + "." + getTargetExtension(sdk);

        GoSdkData sdkData = goSdkData(sdk);

        GeneralCommandLine command = new GeneralCommandLine();
        command.setExePath(getGoToolBinary(sdk));
        command.addParameter("tool");
        command.addParameter(
            GoSdkUtil.getCompilerName(sdkData.TARGET_ARCH));
        command.addParameter("-I");
        command.addParameter(baseOutputPath);
        command.addParameter("-o");
        command.addParameter(outputBinary);
        command.setEnvParams(new HashMap<String, String>() {{
            put("GOROOT", sdk.getHomePath());
        }});

        for (VirtualFile file : targetDescription.getThird()) {
            String fileRelativePath = VfsUtil.getRelativePath(file, sourceRoot,
                                                              '/');
            if (fileRelativePath != null) {
                command.addParameter(fileRelativePath);
            }
        }

        String executionPath = sourceRoot.getPath();
        CompilationTaskWorker compilationTaskWorker = new CompilationTaskWorker(
            new GoCompilerOutputStreamParser(executionPath));
        if (compilationTaskWorker.executeTask(command, executionPath,
                                              context) == null) {
            return;
        }

        VirtualFile intermediateFile = LocalFileSystem.getInstance()
                                                      .refreshAndFindFileByPath(
                                                          outputBinary);
        if (intermediateFile == null) {
            return;
        }

        Collection<OutputItem> outputItems = new ArrayList<>();
        for (VirtualFile file : targetDescription.getThird()) {
            outputItems.add(new MyOutputItem(outputBinary, file));
        }

        sink.add(targetPath.getAbsolutePath(), outputItems,
                 VirtualFile.EMPTY_ARRAY);

        // begin application linker command
        String outputApplication = targetFile.getAbsolutePath();

        GeneralCommandLine linkCommand = new GeneralCommandLine();
        linkCommand.setExePath(getGoToolBinary(sdk));
        linkCommand.addParameter("tool");
        linkCommand.addParameter(
            GoSdkUtil.getLinkerName(sdkData.TARGET_ARCH));
        linkCommand.addParameter("-L");
        linkCommand.addParameter(baseOutputPath);
        linkCommand.addParameter("-o");
        linkCommand.addParameter(
            fixApplicationExtension(outputApplication, sdk));
        linkCommand.addParameter(outputBinary);
        linkCommand.setWorkDirectory(sourceRoot.getPath());
        linkCommand.setEnvParams(new HashMap<String, String>() {{
            put("GOROOT", sdk.getHomePath());
        }});

        if (compilationTaskWorker.executeTask(linkCommand, sourceRoot.getPath(),
                                              context) == null) {
            return;
        }

        VirtualFile libraryFile = LocalFileSystem.getInstance()
                                                 .refreshAndFindFileByPath(
                                                     outputApplication);
        if (libraryFile == null) {
            return;
        }

        Collection<OutputItem> items = new ArrayList<>();
        items.add(new MyOutputItem(outputApplication, intermediateFile));
        sink.add(targetPath.getAbsolutePath(), items, VirtualFile.EMPTY_ARRAY);
    }

    private void buildLibrary(VirtualFile sourceRoot, String target,
                              Trinity<TargetType, Set<String>, List<VirtualFile>> targetDescription,
                              String baseOutputPath, final Sdk sdk) {
        if (LOG.isDebugEnabled()) {
            LOG.debug(String.format("[building] library \"%s\"", target));
        }

        File libraryFile = new File(baseOutputPath + "/" + target + ".a");
        File outputFolder = libraryFile.getParentFile();

        if (!outputFolder.exists() && !outputFolder.mkdirs()) {
            LOG.warn("Could not create parent folders: " + outputFolder);
        }

        String outputBinary = baseOutputPath + File.separator + target + "." + getTargetExtension(
            sdk);

        GoSdkData sdkData = goSdkData(sdk);

        GeneralCommandLine command = new GeneralCommandLine();
        command.setExePath(getGoToolBinary(sdk));
        command.addParameter("tool");
        command.addParameter(
            GoSdkUtil.getCompilerName(sdkData.TARGET_ARCH));
        command.addParameter("-I");
        command.addParameter(baseOutputPath);
        command.addParameter("-o");
        command.addParameter(outputBinary);
        command.setWorkDirectory(sourceRoot.getPath());
        command.setEnvParams(new HashMap<String, String>() {{
            put("GOROOT", sdk.getHomePath());
        }});

        for (VirtualFile file : targetDescription.getThird()) {
            String fileRelativePath = VfsUtil.getRelativePath(file, sourceRoot,
                                                              '/');
            if (fileRelativePath != null) {
                command.addParameter(fileRelativePath);
            }
        }

        CompilationTaskWorker compilationTaskWorker = new CompilationTaskWorker(
            new GoCompilerOutputStreamParser(sourceRoot.getPath()));
        if (compilationTaskWorker.executeTask(command, sourceRoot.getPath(),
                                              context) == null) {
            return;
        }

        VirtualFile intermediateFile = LocalFileSystem.getInstance()
                                                      .refreshAndFindFileByPath(
                                                          outputBinary);
        if (intermediateFile == null) {
            return;
        }

        Collection<OutputItem> outputItems = new ArrayList<>();
        for (VirtualFile file : targetDescription.getThird()) {
            outputItems.add(new MyOutputItem(outputBinary, file));
        }

        sink.add(outputFolder.getAbsolutePath(), outputItems,
                 VirtualFile.EMPTY_ARRAY);

        GeneralCommandLine libraryPackCommand = new GeneralCommandLine();
        libraryPackCommand.setExePath(getGoToolBinary(sdk));
        libraryPackCommand.addParameter("tool");
        libraryPackCommand.addParameter(
            GoSdkUtil.getToolName(sdkData.TARGET_ARCH,
                                  GoSdkTool.GoArchivePacker));
        libraryPackCommand.addParameter("grc");
        libraryPackCommand.addParameter(libraryFile.getPath());
        libraryPackCommand.addParameter(outputBinary);
        libraryPackCommand.setEnvParams(new HashMap<String, String>() {{
            put("GOROOT", sdk.getHomePath());
        }});

        if (compilationTaskWorker.executeTask(libraryPackCommand,
                                              sourceRoot.getPath(),
                                              context) == null) {
            return;
        }

        VirtualFile libraryAsVirtualFile = LocalFileSystem.getInstance()
                                                          .refreshAndFindFileByPath(
                                                              libraryFile.getPath());
        if (libraryAsVirtualFile == null) {
            return;
        }

        Collection<OutputItem> items = new ArrayList<>();
        items.add(new MyOutputItem(libraryFile.getPath(), intermediateFile));
        sink.add(outputFolder.getAbsolutePath(), items,
                 VirtualFile.EMPTY_ARRAY);
    }

    private void convertToTarget(
        Map<String, Trinity<TargetType, Set<String>, List<VirtualFile>>> targets,
        VirtualFile sourceRoot, String path, String currentPackage, List<Pair<VirtualFile, GoFileMetadata>> files) {
        VirtualFile targetFolder = sourceRoot.findFileByRelativePath(path);
        if (targetFolder == null || files.size() == 0) {
            return;
        }

        TargetType targetType = TargetType.Library;
        if (currentPackage.equals("main")) {
            targetType = TargetType.Application;
        }

        String targetName = path;
        if (!targetFolder.getName().equals(currentPackage)) {
            targetName = path + "/" + currentPackage;
        }

        String makefileTarget = GoUtil.getTargetFromMakefile(
            sourceRoot.findFileByRelativePath(path + "/Makefile"));

        if (makefileTarget != null) {
            targetName = makefileTarget;
        }

        List<VirtualFile> targetFiles = new ArrayList<>();
        Set<String> imports = new HashSet<>();

        for (Pair<VirtualFile, GoFileMetadata> file : files) {
            targetFiles.add(file.getFirst());
            imports.addAll(file.getSecond().getImports());

            if (targetType == TargetType.Application && file.getSecond()
                                                            .isMain()) {
                targetName = VfsUtil.getRelativePath(
                    file.getFirst().getParent(), sourceRoot, '/');
                targetName = targetName != null
                    ? targetName + "/" + file.getFirst()
                                             .getNameWithoutExtension()
                    : file.getFirst().getNameWithoutExtension();
            }
        }

        targets.put(targetName,
                    new Trinity<>(
                        targetType, imports, targetFiles));
    }

    private String fixApplicationExtension(String outputApplication, Sdk sdk) {

        GoSdkData goSdkData = goSdkData(sdk);

        if (goSdkData.TARGET_OS == GoTargetOs.Windows) {
            return outputApplication + ".exe";
        }

        return outputApplication;
    }

    private Map<VirtualFile, Map<String, List<Pair<VirtualFile, GoFileMetadata>>>> sortFilesBySourceRoots(CompileContext context, final Module module, List<VirtualFile> files) {

        // relative path to source
        final Map<VirtualFile, Map<String, List<Pair<VirtualFile, GoFileMetadata>>>>
            sourceRootsMap = new HashMap<>();

        // we need to find and sort the files as packages in order to compile them properly
        for (final VirtualFile virtualFile : files) {

            if (virtualFile.getFileType() != GoFileType.INSTANCE || virtualFile.getNameWithoutExtension()
                                                                               .matches(
                                                                                   ".*_test$")) {
                continue;
            }

            VirtualFile sourceRoot = MakeUtil.getSourceRoot(context, module,
                                                            virtualFile);

            Map<String, List<Pair<VirtualFile, GoFileMetadata>>> relativePathsMap;

            relativePathsMap = sourceRootsMap.get(sourceRoot);

            if (relativePathsMap == null) {
                relativePathsMap = new HashMap<>();
                sourceRootsMap.put(sourceRoot, relativePathsMap);
            }

            String relativePath = VfsUtil.getRelativePath(
                virtualFile.getParent(), sourceRoot, '/');

            if (relativePathsMap.get(relativePath) == null) {
                relativePathsMap.put(relativePath,
                                     new ArrayList<Pair<VirtualFile, GoFileMetadata>>());
            }

            final List<Pair<VirtualFile, GoFileMetadata>> list = relativePathsMap
                .get(relativePath);

            ApplicationManager.getApplication().runReadAction(new Runnable() {
                public void run() {
                    PsiFile psiFile = PsiManager.getInstance(
                        module.getProject()).findFile(virtualFile);

                    GoFile file = (GoFile) psiFile;
                    if (file == null) {
                        return;
                    }

                    GoFileMetadata fileMetadata = new GoFileMetadata(file);

                    if (LOG.isDebugEnabled()) {
                        LOG.debug(String.format(
                            "[compiling] " + virtualFile + " pkg: " + fileMetadata
                                .getPackageName()));
                    }

                    list.add(Pair.create(virtualFile, fileMetadata));
                }
            });
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug(String.format("[compiling] " + sourceRootsMap));
        }

        return sourceRootsMap;
    }

    private String findOrCreateGoOutputPath(final Module module, final String goOutputRelativePath, final VirtualFile baseOutputFile) {
        boolean outputBasePathResult = new WriteCommandAction<Boolean>(
            module.getProject()) {
            @Override
            protected void run(Result<Boolean> boolResult) throws Throwable {
                boolResult.setResult(false);
                VfsUtil.createDirectoryIfMissing(new File(
                    baseOutputFile.getPath() + goOutputRelativePath).getAbsolutePath());
                boolResult.setResult(true);
            }
        }.execute().getResultObject();

        String baseOutputPath = baseOutputFile.getPath() + goOutputRelativePath;

        if (!outputBasePathResult) {
            LOG.error(
                "[compiling] unable to initialize destination path: " + baseOutputPath);
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("[compiling] Destination path: " + baseOutputPath);
        }
        return baseOutputPath;
    }

    private String getGoToolBinary(Sdk sdk) {
        GoSdkData goSdkData = goSdkData(sdk);
        return goSdkData.GO_BIN_PATH;
    }

    private String getTargetExtension(Sdk sdk) {
        GoSdkData goSdkData = goSdkData(sdk);
        return GoSdkUtil.getBinariesDesignation(
                goSdkData.TARGET_ARCH);
    }

    private GoSdkData goSdkData(Sdk sdk) {
        return (GoSdkData) sdk.getSdkAdditionalData();
    }

    private Sdk findGoSdkForModule(Module module) {
        return GoSdkUtil.getGoogleGoSdkForModule(module);
    }

    private static class MyOutputItem implements OutputItem {
        private final String outputBinary;
        private final VirtualFile file;

        public MyOutputItem(String outputBinary, VirtualFile file) {
            this.outputBinary = outputBinary;
            this.file = file;
        }

        public String getOutputPath() {
            return outputBinary;
        }

        public VirtualFile getSourceFile() {
            return file;
        }
    }
}
