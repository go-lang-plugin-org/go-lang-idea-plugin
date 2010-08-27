package ro.redeul.google.go.compilation;

import com.intellij.compiler.impl.CompilerUtil;
import com.intellij.compiler.make.MakeUtil;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.CapturingProcessHandler;
import com.intellij.execution.process.ProcessOutput;
import com.intellij.facet.FacetManager;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.compiler.CompileContext;
import com.intellij.openapi.compiler.CompileScope;
import com.intellij.openapi.compiler.CompilerMessageCategory;
import com.intellij.openapi.compiler.TranslatingCompiler;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileChooser.ex.FileTextFieldImpl;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.roots.ui.configuration.ClasspathEditor;
import com.intellij.openapi.roots.ui.configuration.ModulesConfigurator;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.Trinity;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.util.Chunk;
import com.intellij.util.EnvironmentUtil;
import com.intellij.util.StringBuilderSpinAllocator;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.GoBundle;
import ro.redeul.google.go.GoFileType;
import ro.redeul.google.go.config.facet.GoFacet;
import ro.redeul.google.go.config.facet.GoFacetType;
import ro.redeul.google.go.config.sdk.GoSdkData;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.toplevel.GoImportDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoImportSpec;
import ro.redeul.google.go.util.GoSdkUtil;
import ro.redeul.google.go.util.ProcessUtil;

import java.io.File;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GoCompiler implements TranslatingCompiler {

    private static final Logger LOG = Logger.getInstance("#ro.redeul.google.go.compilation.GoCompiler");

    Project project;

    public GoCompiler(Project project) {
        this.project = project;
    }

    @NotNull
    public String getDescription() {
        return "Go Compiler";
    }

    public boolean validateConfiguration(CompileScope scope) {

        final VirtualFile files[] = scope.getFiles(GoFileType.GO_FILE_TYPE, true);

        final Set<Module> affectedModules = new HashSet<Module>();


        ApplicationManager.getApplication().runReadAction(new Runnable() {
            public void run() {
                ProjectFileIndex projectFileIndex = ProjectRootManager.getInstance(project).getFileIndex();

                for (VirtualFile file : files) {
                    affectedModules.add(projectFileIndex.getModuleForFile(file));
                }
            }
        });

        if (LOG.isDebugEnabled()) {
            LOG.debug("[validating] Found affected modules: " + affectedModules);
        }

        for (Module affectedModule : affectedModules) {
            if (findGoSdkForModule(affectedModule) == null) {

                Messages.showErrorDialog(
                        affectedModule.getProject(),
                        GoBundle.message("cannot.compile.go.files.no.facet", affectedModule.getName()),
                        GoBundle.message("cannot.compile"));

                ModulesConfigurator.showDialog(affectedModule.getProject(), affectedModule.getName(), ClasspathEditor.NAME, false);

                return false;
            }
        }

        return true;
    }

    public boolean isCompilableFile(VirtualFile file, CompileContext context) {
        return file.getFileType() == GoFileType.GO_FILE_TYPE;
    }

    public void compile(CompileContext context, Chunk<Module> moduleChunk, VirtualFile[] files, OutputSink sink) {

        Map<Module, List<VirtualFile>> mapToFiles = CompilerUtil.buildModuleToFilesMap(context, files);

        for (Module module : mapToFiles.keySet()) {
            compileFilesInsideModule(module, mapToFiles.get(module), context, sink);
        }
    }

    private void compileFilesInsideModule(final Module module, List<VirtualFile> files, CompileContext context, OutputSink sink) {

        final String goOutputRelativePath = "/go-bins";

        context.getProgressIndicator().setText(GoBundle.message("compiler.compiling.module", module.getName()));

        if (LOG.isDebugEnabled()) {
            LOG.debug("[compiling] Compiling module: " + module.getName());
        }

        final VirtualFile baseOutputFile = context.getModuleOutputDirectory(module);
        if (baseOutputFile == null) {
            return;
        }

        String baseOutputPath = findOrCreateGoOutputPath(module, goOutputRelativePath, baseOutputFile);

        final Map<VirtualFile, Map<String, List<Trinity<VirtualFile, String, List<String>>>>> sourceRootsMap;

        sourceRootsMap = sortFilesBySourceRoots(context, module, files);

        final Sdk sdk = findGoSdkForModule(module);

        for (VirtualFile sourceRoot : sourceRootsMap.keySet()) {
            compileForSourceRoot(sourceRoot, sourceRootsMap.get(sourceRoot), baseOutputPath, sdk, context, sink);
        }
    }

    private void compileForSourceRoot(VirtualFile sourceRoot,
                                      Map<String, List<Trinity<VirtualFile, String, List<String>>>> filesToCompile,
                                      String baseOutputFile,
                                      Sdk sdk, CompileContext context, OutputSink sink) {

        if (LOG.isDebugEnabled()) {
            LOG.debug("[compiling] Compiling source root: " + sourceRoot.getPath());
        }

        // sorting reverse by relative paths.
        List<String> relativePaths = new ArrayList<String>(filesToCompile.keySet());
        Collections.sort(relativePaths, new Comparator<String>() {
            public int compare(String o1, String o2) {
                return o2.length() - o1.length();
            }
        });

        if (LOG.isDebugEnabled()) {
            LOG.debug("[compiling] Sorted file folders: " + relativePaths);
        }

        for (String relativePath : relativePaths) {
            List<Trinity<VirtualFile, String, List<String>>> list = filesToCompile.get(relativePath);

            if (LOG.isDebugEnabled()) {
                LOG.debug("[compiling] " + relativePath + " => " + list);
            }

            Collections.sort(list, new Comparator<Trinity<VirtualFile, String, List<String>>>() {
                public int compare(Trinity<VirtualFile, String, List<String>> o1, Trinity<VirtualFile, String, List<String>> o2) {
                    return o1.getSecond().compareTo(o2.getSecond());
                }
            });

            if (LOG.isDebugEnabled()) {
                LOG.debug("[compiling] after sorting by packages: " + relativePath + " => " + list);
            }

            List<VirtualFile> mainFiles = new ArrayList<VirtualFile>();

            String currentPackage = "";
            List<VirtualFile> files = new ArrayList<VirtualFile>();
            for (Trinity<VirtualFile, String, List<String>> compilableFileData : list) {
                if (!currentPackage.equals(compilableFileData.getSecond())) {
                    if (files.size() != 0 && !currentPackage.startsWith("main")) {
                        if (compilePackage(sourceRoot, baseOutputFile, sdk, context, sink, relativePath, currentPackage, files))
                            return;
                    }

                    currentPackage = compilableFileData.getSecond();
                    files.clear();
                }

                if (currentPackage.startsWith("main")) {
                    mainFiles.add(compilableFileData.getFirst());
                } else {
                    files.add(compilableFileData.getFirst());
                }
            }

            if (files.size() != 0) {
                if (compilePackage(sourceRoot, baseOutputFile, sdk, context, sink, relativePath, currentPackage, files))
                    return;
            }

            if (!compileApplication(sourceRoot, baseOutputFile, sdk, context, sink, relativePath, "main", mainFiles)) {
                return;
            }
        }
    }

    private boolean compilePackage(VirtualFile sourceRoot, String baseOutputFile, Sdk sdk, CompileContext context, OutputSink sink, String relativePath, String currentPackage, List<VirtualFile> files) {

        Collection<VirtualFile> updatedFileList = updateFileListForPackage(sourceRoot, relativePath, currentPackage, files);

        if (!doCompilePackage(sourceRoot, currentPackage, relativePath, updatedFileList, baseOutputFile, sdk, context, sink)) {

            if (LOG.isDebugEnabled()) {
                LOG.debug("[compiling] Failed compilation for package: " + (relativePath + "/" + currentPackage) + " " + files);
            }

            return true;
        }
        return false;
    }

    private boolean compileApplication(VirtualFile sourceRoot, String baseOutputPath, Sdk sdk, CompileContext context, OutputSink sink, String relativePath, String currentPackage, List<VirtualFile> files) {

        Collection<VirtualFile> updateFileList = updateFileListForPackage(sourceRoot, relativePath, "main", files);

        return doCompileApplication(sourceRoot, baseOutputPath, sdk, context, sink, relativePath, currentPackage, updateFileList);
    }

    private Collection<VirtualFile> updateFileListForPackage(VirtualFile sourceRoot, String relativePath, final String desiredPackageName, Collection<VirtualFile> files) {
        final Set<VirtualFile> newFiles = new HashSet<VirtualFile>(files);

        final VirtualFile folder = sourceRoot.findFileByRelativePath(relativePath);
        if (folder == null) {
            return files;
        }

        ApplicationManager.getApplication().runReadAction(new Runnable() {
            public void run() {
                VirtualFile children[] = folder.getChildren();
                for (final VirtualFile child : children) {

                    PsiFile psiFile = PsiManager.getInstance(project).findFile(child);

                    GoFile file = (GoFile) psiFile;
                    if (file == null) {
                        continue;
                    }

                    String packageName = file.getPackage().getPackageName();
                    if ( packageName.equals(desiredPackageName) ) {
                        if ( desiredPackageName.equals("main") && file.getMainFunction() == null ) {
                            newFiles.add(child);
                        } else {
                            newFiles.add(child);
                        }
                    }
                }
            }
        });

        return files;
    }

    private boolean doCompilePackage(VirtualFile sourceRoot, String packageName, String relativePath, Collection<VirtualFile> files, String baseOutputPath, Sdk sdk, CompileContext context, OutputSink sink) {

        if (LOG.isDebugEnabled()) {
            LOG.debug("[compiling] Compiling package \"" + packageName + "\" from path [" + relativePath + "] - " + files);
        }

        File outputFolder = new File(baseOutputPath + "/" + relativePath);

        if (files.size() != 1
                && outputFolder.getName().equals(packageName)
                && VfsUtil.isAncestor(new File(baseOutputPath), outputFolder, true)) {
            outputFolder = outputFolder.getParentFile();
        }

        outputFolder.mkdirs();

        String outputBinary = outputFolder.getAbsolutePath() + File.separator + packageName + "." + getTargetExtension(sdk);

        GeneralCommandLine command = new GeneralCommandLine();

        command.setExePath(getCompilerBinary(sdk));
        command.addParameter("-o");
        command.addParameter(outputBinary);
        command.setWorkDirectory(sourceRoot.getPath());

        for (VirtualFile file : files) {
            command.addParameter(VfsUtil.getRelativePath(file, sourceRoot, '/'));
        }

        ProcessOutput output = executeCommandInFolder(command, sourceRoot.getPath(), context);

        if (output == null || output.getExitCode() != 0) {
            return false;
        }

        VirtualFile intermediateFile = LocalFileSystem.getInstance().refreshAndFindFileByPath(outputBinary);
        if (intermediateFile == null) {
            return false;
        }

        Collection<OutputItem> outputItems = new ArrayList<OutputItem>();
        for (VirtualFile file : files) {
            outputItems.add(new MyOutputItem(outputBinary, file));
        }

        sink.add(outputFolder.getAbsolutePath(), outputItems, VirtualFile.EMPTY_ARRAY);

        // begin library packing command
        String outputLibrary = outputFolder.getAbsolutePath() + File.separator + packageName + ".a";

        GeneralCommandLine libraryPackCommand = new GeneralCommandLine();
        libraryPackCommand.setExePath(getPackerBinary(sdk));
        libraryPackCommand.addParameter("grc");
        libraryPackCommand.addParameter(outputLibrary);
        libraryPackCommand.addParameter(outputBinary);

        output = executeCommandInFolder(libraryPackCommand, sourceRoot.getPath(), context);

        VirtualFile libraryFile = LocalFileSystem.getInstance().refreshAndFindFileByPath(outputLibrary);
        if (libraryFile == null) {
            return false;
        }

        Collection<OutputItem> items = new ArrayList<OutputItem>();
        items.add(new MyOutputItem(outputLibrary, intermediateFile));
        sink.add(outputFolder.getAbsolutePath(), items, VirtualFile.EMPTY_ARRAY);

        return output != null && output.getExitCode() == 0;
    }


    private boolean doCompileApplication(VirtualFile sourceRoot, String baseOutputPath, Sdk sdk, CompileContext context, OutputSink sink, String relativePath, String currentPackage, Collection<VirtualFile> files) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("[compiling] Compiling application in folder [" + relativePath + "] - " + files);
        }

        File outputFolder = new File(baseOutputPath + "/" + relativePath);

        outputFolder.mkdirs();

        String targetApplication = currentPackage.replaceFirst("^main\\.", "");

        if (targetApplication.trim().length() == 0 ) {
            context.addMessage(CompilerMessageCategory.INFORMATION, "No main function provided", null, -1, -1);
            return false;
        }

        String outputBinary = outputFolder.getAbsolutePath() + File.separator + targetApplication + "." + getTargetExtension(sdk);
        String executionPath = outputFolder.getAbsolutePath();


        GeneralCommandLine command = new GeneralCommandLine();

        command.setExePath(getCompilerBinary(sdk));
//        command.addParameter("-I");
//        command.addParameter(".");
        command.addParameter("-o");
        command.addParameter(outputBinary);

        VirtualFile outputVirtualFile = VfsUtil.findFileByURL(VfsUtil.convertToURL("file://" + executionPath + "/"));

        for (VirtualFile file : files) {
            command.addParameter("../" + VfsUtil.getPath(outputVirtualFile, file, '/'));
        }

        ProcessOutput output = executeCommandInFolder(command, executionPath, context);

        if (output == null || output.getExitCode() != 0) {
            return false;
        }

        VirtualFile intermediateFile = LocalFileSystem.getInstance().refreshAndFindFileByPath(outputBinary);
        if (intermediateFile == null) {
            return false;
        }

        Collection<OutputItem> outputItems = new ArrayList<OutputItem>();
        for (VirtualFile file : files) {
            outputItems.add(new MyOutputItem(outputBinary, file));
        }

        sink.add(outputFolder.getAbsolutePath(), outputItems, VirtualFile.EMPTY_ARRAY);

        // begin application linker command
        String outputApplication = outputFolder.getAbsolutePath() + File.separator + targetApplication;

        GeneralCommandLine libraryPackCommand = new GeneralCommandLine();
        libraryPackCommand.setExePath(getLinkerBinary(sdk));
        libraryPackCommand.addParameter("-o");
        libraryPackCommand.addParameter(outputApplication);
        libraryPackCommand.addParameter(outputBinary);

        output = executeCommandInFolder(libraryPackCommand, sourceRoot.getPath(), context);

        VirtualFile libraryFile = LocalFileSystem.getInstance().refreshAndFindFileByPath(outputApplication);
        if (libraryFile == null) {
            return false;
        }

        Collection<OutputItem> items = new ArrayList<OutputItem>();
        items.add(new MyOutputItem(outputApplication, intermediateFile));
        sink.add(outputFolder.getAbsolutePath(), items, VirtualFile.EMPTY_ARRAY);

        return output != null && output.getExitCode() == 0;
    }

    private ProcessOutput executeCommandInFolder(GeneralCommandLine command, String path, CompileContext context) {

        if (LOG.isDebugEnabled()) {
            @NonNls final StringBuilder buf = StringBuilderSpinAllocator.alloc();
            try {
                buf.append("\n===== Environment:===========================\n");
                for (String pair : EnvironmentUtil.getEnvironment()) {
                    buf.append("\t").append(pair).append("\n");
                }

                Map<String, String> map = command.getEnvParams();
                if (map != null) {
                    buf.append("===== Custom environment:").append("\n");
                    for (String key : map.keySet()) {
                        buf.append("\t").append(key).append("=").append(map.get(key)).append("\n");
                    }
                }
                buf.append("===== Working folder:===========================\n");
                buf.append("\t").append(path).append("\n");
                buf.append("===== Command: ").append("\n");
                buf.append("\t").append(command.getCommandLineString()).append("\n");
                buf.append("=============================================================================\n");
                LOG.debug(buf.toString());
            } finally {
                StringBuilderSpinAllocator.dispose(buf);
            }
        }

        command.setWorkDirectory(path);

        try {
            ProcessOutput output = new CapturingProcessHandler(
                    command.createProcess(),
                    Charset.defaultCharset(),
                    command.getCommandLineString()).runProcess();

            if (output.getExitCode() != 0) {
                List<String> lines = output.getStderrLines();

                if (lines.size() > 0) {
                    for (String errorLine : lines) {
                        context.addMessage(CompilerMessageCategory.ERROR, errorLine, null, -1, -1);
                    }
                }

                lines = output.getStdoutLines();
                if (lines.size() > 0) {
                    for (String errorLine : lines) {
                        context.addMessage(CompilerMessageCategory.ERROR, errorLine, null, -1, -1);
                    }
                }

                context.addMessage(CompilerMessageCategory.WARNING, "process exited with code: " + output.getExitCode(), null, -1, -1);
            }

            return output;
        } catch (ExecutionException ex) {
            context.addMessage(CompilerMessageCategory.WARNING, ex.getMessage(), null, -1, -1);
            return null;
        }
    }

    private Map<VirtualFile, Map<String, List<Trinity<VirtualFile, String, List<String>>>>> sortFilesBySourceRoots(CompileContext context, final Module module, List<VirtualFile> files) {

        // relative path to source
        final Map<VirtualFile, Map<String, List<Trinity<VirtualFile, String, List<String>>>>>
                sourceRootsMap = new HashMap<VirtualFile, Map<String, List<Trinity<VirtualFile, String, List<String>>>>>();

        // we need to find and sort the files as packages in order to compile them properly
        for (final VirtualFile virtualFile : files) {

            if (virtualFile.getFileType() != GoFileType.GO_FILE_TYPE) {
                continue;
            }

            VirtualFile sourceRoot = MakeUtil.getSourceRoot(context, module, virtualFile);

            Map<String, List<Trinity<VirtualFile, String, List<String>>>> relativePathsMap;

            relativePathsMap = sourceRootsMap.get(sourceRoot);

            if (relativePathsMap == null) {
                relativePathsMap = new HashMap<String, List<Trinity<VirtualFile, String, List<String>>>>();
                sourceRootsMap.put(sourceRoot, relativePathsMap);
            }

            String relativePath = VfsUtil.getRelativePath(virtualFile.getParent(), sourceRoot, '/');

            if (relativePathsMap.get(relativePath) == null) {
                relativePathsMap.put(relativePath, new ArrayList<Trinity<VirtualFile, String, List<String>>>());
            }

            final List<Trinity<VirtualFile, String, List<String>>> list = relativePathsMap.get(relativePath);

            ApplicationManager.getApplication().runReadAction(new Runnable() {
                public void run() {
                    PsiFile psiFile = PsiManager.getInstance(module.getProject()).findFile(virtualFile);

                    GoFile file = (GoFile) psiFile;
                    if (file == null) {
                        return;
                    }

                    String packageName = file.getPackage().getPackageName();
                    GoImportDeclaration[] importDeclarations = file.getImportDeclarations();

                    List<String> imports = new ArrayList<String>();
                    for (GoImportDeclaration importDeclaration : importDeclarations) {
                        GoImportSpec[] importSpecs = importDeclaration.getImports();

                        for (GoImportSpec importSpec : importSpecs) {
                            if (!importSpec.getImportPath().startsWith("\"./")) {
                                continue;
                            }

                            imports.add(importSpec.getImportPath());
                        }
                    }

                    if (LOG.isDebugEnabled()) {
                        LOG.debug(String.format("[compiling] " + virtualFile + " pkg: " + packageName));
                    }


                    if (packageName.equals("main") && file.getMainFunction() != null) {
                        list.add(Trinity.create(virtualFile, packageName + "." + virtualFile.getNameWithoutExtension(), imports));
                    } else {
                        list.add(Trinity.create(virtualFile, packageName, imports));
                    }
                }
            });
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug(String.format("[compiling] " + sourceRootsMap));
        }

        return sourceRootsMap;
    }

    private String findOrCreateGoOutputPath(final Module module, final String goOutputRelativePath, final VirtualFile baseOutputFile) {
        boolean outputBasePathResult = new WriteCommandAction<Boolean>(module.getProject()) {
            @Override
            protected void run(Result<Boolean> boolResult) throws Throwable {
                boolResult.setResult(false);
                VfsUtil.createDirectoryIfMissing(baseOutputFile, goOutputRelativePath);
                boolResult.setResult(true);
            }
        }.execute().getResultObject();

        String baseOutputPath = baseOutputFile.getPath() + goOutputRelativePath;

        if (!outputBasePathResult) {
            LOG.error("[compiling] unable to initialize destination path: " + baseOutputPath);
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("[compiling] Destination path: " + baseOutputPath);
        }
        return baseOutputPath;
    }

    private String cleanUpImportName(String importPath) {
        return importPath.replaceAll("(^\"|\"$)", "").replaceAll("^[^/]*/", "");
    }

    private List<String> findPackagesCompilationOrder(List<Pair<String, String>> importDependencies, Set<String> ourPackages) {

        List<String> sortedPackages = new ArrayList<String>();

        Map<String, Set<String>> outGoingList = new HashMap<String, Set<String>>();
        Map<String, Integer> incomingCounts = new HashMap<String, Integer>();

        for (String ourPackage : ourPackages) {
            incomingCounts.put(ourPackage, 0);
        }

        for (Pair<String, String> importDependency : importDependencies) {
            if (!ourPackages.contains(importDependency.getFirst())) {
                continue;
            }

            Set<String> adiacentNodes = outGoingList.get(importDependency.getFirst());
            if (adiacentNodes == null) {
                adiacentNodes = new HashSet<String>();
                outGoingList.put(importDependency.getFirst(), adiacentNodes);
            }

            adiacentNodes.add(importDependency.getSecond());

            incomingCounts.put(importDependency.second, incomingCounts.get(importDependency.second) + 1);
        }

        boolean canGoFurther = true;

        List<String> unsortedItems = new ArrayList<String>(ourPackages);
        while (canGoFurther) {

            canGoFurther = false;
            List<String> removed = new ArrayList<String>();

            for (String key : incomingCounts.keySet()) {
                if (incomingCounts.get(key) == 0) {
                    removed.add(key);

                    if (outGoingList.containsKey(key)) {
                        for (String outgoing : outGoingList.get(key)) {
                            if (incomingCounts.get(outgoing) != null) {
                                incomingCounts.put(outgoing, incomingCounts.get(outgoing) - 1);
                            }
                        }
                    }
                }
            }

            for (String s : removed) {
                canGoFurther = true;
                incomingCounts.remove(s);
                outGoingList.remove(s);
                sortedPackages.add(s);
            }
        }

        return sortedPackages;
    }


    private List<VirtualFile> getFilesForPackage(String packageName, Map<String, List<VirtualFile>> packagesToFileList) {
        List<VirtualFile> files = packagesToFileList.get(packageName);

        if (files == null) {
            files = new ArrayList<VirtualFile>();
            packagesToFileList.put(packageName, files);
        }

        return files;
    }

    private void doPackLibrary(CompileContext context, Sdk sdk, String outputPath, String compileOutput, String libraryOutput) {
        context.getProgressIndicator().setText("Packing library .. " + libraryOutput);

        List<String> compileCommand = new ArrayList<String>();

        compileCommand.add(getPackerBinary(sdk));
        compileCommand.add("grc");
        compileCommand.add(libraryOutput);
        compileCommand.add(compileOutput);

        Pair<String, String> compilationResult =
                ProcessUtil.executeAndProcessOutput(
                        compileCommand,
                        new File(outputPath),
                        ProcessUtil.NULL_PARSER,
                        ProcessUtil.NULL_PARSER);

        if (compilationResult.getSecond() != null) {
            if (compilationResult.getSecond().length() > 0) {
                context.addMessage(CompilerMessageCategory.INFORMATION, compilationResult.getSecond(), "", -1, -1);
            }
        }

        if (compilationResult.getFirst() != null) {
            if (compilationResult.getFirst().length() > 0) {
                context.addMessage(CompilerMessageCategory.INFORMATION, compilationResult.getFirst(), "", -1, -1);
            }
        }
    }

    private void doCompileFile(CompileContext context, Sdk sdk, String executionFolder, List<VirtualFile> files, String destinationFile) {
        context.getProgressIndicator().setText("Compiling .. " + files);

        List<String> compileCommand = new ArrayList<String>();

        compileCommand.add(getCompilerBinary(sdk));
        compileCommand.add("-I");
        compileCommand.add(executionFolder + "/go-binaries/packages/");
        compileCommand.add("-o");
        compileCommand.add(destinationFile);

        for (VirtualFile file : files) {
            compileCommand.add(file.getPath());
        }

        Pair<List<CompilerMessage>, String> compilationResult =
                ProcessUtil.executeAndProcessOutput(
                        compileCommand,
                        new File(executionFolder),
                        new GoCompilerOutputStreamParser(),
                        ProcessUtil.NULL_PARSER);

        if (compilationResult.getFirst() != null) {
            for (CompilerMessage message : compilationResult.getFirst()) {
                context.addMessage(message.category, message.message, VfsUtil.pathToUrl(FileUtil.toSystemIndependentName(message.fileName)), message.row, -1);
            }
        }

        if (compilationResult.getSecond() != null) {
            if (compilationResult.getSecond().length() > 0) {
                context.addMessage(CompilerMessageCategory.INFORMATION, compilationResult.getSecond(), "", -1, -1);
            }
        }
    }

    private String getCompilerBinary(Sdk sdk) {
        GoSdkData goSdkData = (GoSdkData) sdk.getSdkAdditionalData();
        return goSdkData.BINARY_PATH + "/" + GoSdkUtil.getCompilerName(goSdkData.TARGET_OS, goSdkData.TARGET_ARCH);
    }

    private String getPackerBinary(Sdk sdk) {
        GoSdkData goSdkData = (GoSdkData) sdk.getSdkAdditionalData();
        return goSdkData.BINARY_PATH + "/" + "gopack";
    }

    private String getLinkerBinary(Sdk sdk) {
        GoSdkData goSdkData = (GoSdkData) sdk.getSdkAdditionalData();
        return goSdkData.BINARY_PATH + "/" + GoSdkUtil.getLinkerName(goSdkData.TARGET_OS, goSdkData.TARGET_ARCH);
    }

    private String getTargetExtension(Sdk sdk) {
        GoSdkData goSdkData = (GoSdkData) sdk.getSdkAdditionalData();
        return GoSdkUtil.getBinariesDesignation(goSdkData.TARGET_OS, goSdkData.TARGET_ARCH);
    }

    private Sdk findGoSdkForModule(Module module) {

        GoFacet goFacet = FacetManager.getInstance(module).getFacetByType(GoFacetType.GO_FACET_TYPE_ID);

        if (goFacet == null)
            return null;

        Sdk sdk = goFacet.getGoSdk();
        if (sdk != null) {
            return sdk;
        }

        // other checks (module of type .. etc)
        return null;
    }

static class CompilerMessage {
    public CompilerMessageCategory category;
    public String message;
    public String fileName;
    public int row;
    public int column = -1;

    CompilerMessage(CompilerMessageCategory category, String message, String fileName, int row, int column) {
        this.category = category;
        this.message = message;
        this.fileName = fileName;
        this.row = row;
        this.column = column;
    }
}

private static class GoCompilerOutputStreamParser implements ProcessUtil.StreamParser<List<CompilerMessage>> {

    Pattern pattern = Pattern.compile("((?:/[^/:]+)+):(\\d+): ((?:(?:.)|(?:\\n(?!/)))+)", Pattern.UNIX_LINES);

    public List<CompilerMessage> parseStream(String data) {

        List<CompilerMessage> messages = new ArrayList<CompilerMessage>();

        Matcher matcher = pattern.matcher(data);

        while (matcher.find()) {
            messages.add(
                    new CompilerMessage(
                            CompilerMessageCategory.ERROR, matcher.group(3), matcher.group(1), Integer.parseInt(matcher.group(2)), -1));
        }

        return messages;
    }
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
