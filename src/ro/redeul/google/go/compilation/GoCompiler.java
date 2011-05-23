package ro.redeul.google.go.compilation;

import com.intellij.compiler.impl.CompilerUtil;
import com.intellij.compiler.make.MakeUtil;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.CapturingProcessHandler;
import com.intellij.execution.process.ProcessOutput;
import com.intellij.ide.errorTreeView.ErrorTreeElementKind;
import com.intellij.ide.errorTreeView.GroupingElement;
import com.intellij.ide.errorTreeView.NavigatableMessageElement;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.compiler.CompileContext;
import com.intellij.openapi.compiler.CompileScope;
import com.intellij.openapi.compiler.CompilerMessageCategory;
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
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.Trinity;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.pom.Navigatable;
import com.intellij.pom.NavigatableWithText;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.util.Chunk;
import com.intellij.util.CommonProcessors;
import com.intellij.util.EnvironmentUtil;
import com.intellij.util.StringBuilderSpinAllocator;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.GoBundle;
import ro.redeul.google.go.GoFileType;
import ro.redeul.google.go.config.sdk.GoSdkData;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.toplevel.GoImportDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoImportSpec;
import ro.redeul.google.go.sdk.GoSdkTool;
import ro.redeul.google.go.sdk.GoSdkUtil;
import ro.redeul.google.go.util.ProcessUtil;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
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

        VirtualFile allFiles[] = findAllFiles(moduleChunk);
        Map<Module, List<VirtualFile>> mapToFiles = CompilerUtil.buildModuleToFilesMap(context, allFiles);

        for (Module module : mapToFiles.keySet()) {
            compileFilesInsideModule(module, mapToFiles.get(module), context, sink);
        }
    }

    private VirtualFile[] findAllFiles(Chunk<Module> moduleChunk) {

        final CommonProcessors.CollectUniquesProcessor<VirtualFile> collector = new CommonProcessors.CollectUniquesProcessor<VirtualFile>() {
            @Override
            public boolean process(VirtualFile virtualFile) {
                if (virtualFile.getFileType() == GoFileType.GO_FILE_TYPE) {
                    super.process(virtualFile);
                }

                return true;
            }
        };

        for (Module module : moduleChunk.getNodes()) {

            final VirtualFile sourceRoots[] = ModuleRootManager.getInstance(module).getSourceRoots();

            ApplicationManager.getApplication().runReadAction(new Runnable() {
                public void run() {
                    for (final VirtualFile sourceRoot : sourceRoots) {
                        VfsUtil.processFilesRecursively(sourceRoot, collector);
                    }
                }
            });
        }

        return collector.toArray(VirtualFile.EMPTY_ARRAY);
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

            if (mainFiles.size() > 0 && !compileApplication(sourceRoot, baseOutputFile, sdk, context, sink, relativePath, currentPackage, mainFiles)) {
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

                    if (child.getFileType() != GoFileType.GO_FILE_TYPE)
                        continue;

                    PsiFile psiFile = PsiManager.getInstance(project).findFile(child);

                    if (!(psiFile instanceof GoFile)) {
                        continue;
                    }

                    GoFile file = (GoFile) psiFile;

                    String packageName = file.getPackage().getPackageName();
                    if (packageName.equals(desiredPackageName)) {
                        if (desiredPackageName.equals("main") && file.getMainFunction() == null) {
                            newFiles.add(child);
                        } else {
                            newFiles.add(child);
                        }
                    }
                }
            }
        });

        return newFiles;
    }

    private boolean doCompilePackage(VirtualFile sourceRoot, String packageName, String relativePath, Collection<VirtualFile> files, String baseOutputPath, final Sdk sdk, CompileContext context, OutputSink sink) {

        if (LOG.isDebugEnabled()) {
            LOG.debug("[compiling] Compiling package \"" + packageName + "\" from path [" + relativePath + "] - " + files);
        }

        File outputFolder = new File(baseOutputPath + "/" + relativePath);

        if (outputFolder.getName().equals(packageName)
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
        command.setEnvParams(new HashMap<String, String>() {{
            put("GOROOT", sdk.getHomePath());
        }});

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
        libraryPackCommand.setEnvParams(new HashMap<String, String>() {{
            put("GOROOT", sdk.getHomePath());
        }});


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


    private boolean doCompileApplication(VirtualFile sourceRoot, String baseOutputPath, final Sdk sdk, CompileContext context, OutputSink sink, String relativePath, String currentPackage, Collection<VirtualFile> files) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("[compiling] Compiling application in folder [" + relativePath + "] - " + files);
        }

        File outputFolder = new File(baseOutputPath + "/" + relativePath);

        outputFolder.mkdirs();

        String targetApplication = findTargetApplicationName(files, context);

        if (targetApplication == null) {
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
        command.setEnvParams(new HashMap<String, String>() {{
            put("GOROOT", sdk.getHomePath());
        }});

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

        GeneralCommandLine applicationLinkCommand = new GeneralCommandLine();
        applicationLinkCommand.setExePath(getLinkerBinary(sdk));
        applicationLinkCommand.addParameter("-o");
        applicationLinkCommand.addParameter(fixApplicationExtension(outputApplication, sdk));
        applicationLinkCommand.addParameter(outputBinary);
        applicationLinkCommand.setEnvParams(new HashMap<String, String>() {{
            put("GOROOT", sdk.getHomePath());
        }});

        output = executeCommandInFolder(applicationLinkCommand, sourceRoot.getPath(), context);

        VirtualFile libraryFile = LocalFileSystem.getInstance().refreshAndFindFileByPath(outputApplication);
        if (libraryFile == null) {
            return false;
        }

        Collection<OutputItem> items = new ArrayList<OutputItem>();
        items.add(new MyOutputItem(outputApplication, intermediateFile));
        sink.add(outputFolder.getAbsolutePath(), items, VirtualFile.EMPTY_ARRAY);

        return output != null && output.getExitCode() == 0;
    }

    private String fixApplicationExtension(String outputApplication, Sdk sdk) {

        GoSdkData goSdkData = goSdkData(sdk);

        if ( goSdkData.TARGET_OS.equals("windows") ){
            return outputApplication + ".exe";
        }

        return outputApplication;
    }

    private String findTargetApplicationName(final Collection<VirtualFile> files, final CompileContext context) {

        return ApplicationManager.getApplication().runReadAction(new Computable<String>() {
            public String compute() {
                String applicationName = null;
                VirtualFile applicationFile = null;

                for (final VirtualFile child : files) {

                    if (child.getFileType() != GoFileType.GO_FILE_TYPE)
                        continue;

                    PsiFile psiFile = PsiManager.getInstance(project).findFile(child);

                    if (!(psiFile instanceof GoFile)) {
                        continue;
                    }

                    GoFile file = (GoFile) psiFile;

                    if (file.getMainFunction() != null) {
                        if (applicationName != null) {
                            context.addMessage(CompilerMessageCategory.ERROR, GoBundle.message("compiler.multiple.files.with.main.method", applicationFile.getPath(), child.getPath()), null, -1, -1);
                            return null;
                        }

                        applicationName = child.getNameWithoutExtension();
                        applicationFile = child;
                    }
                }

                if (applicationName == null) {
                    context.addMessage(CompilerMessageCategory.ERROR, GoBundle.message("compiler.no.main.method.found"), null, -1, -1);
                }

                return applicationName;
            }
        });
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

            GoCompilerOutputStreamParser outputStreamParser = new GoCompilerOutputStreamParser();
            if (output.getExitCode() != 0) {
                List<CompilerMessage> compilerMessages = outputStreamParser.parseStream(output.getStderr());

                for (CompilerMessage compilerMessage : compilerMessages) {
                    String url = generateFileUrl(path, compilerMessage);
                    context.addMessage(CompilerMessageCategory.ERROR, compilerMessage.message, url, compilerMessage.row, compilerMessage.column);
                }

                compilerMessages = outputStreamParser.parseStream(output.getStdout());

                for (CompilerMessage compilerMessage : compilerMessages) {
                    String url = generateFileUrl(path, compilerMessage);
                    context.addMessage(CompilerMessageCategory.ERROR, compilerMessage.message, url, compilerMessage.row, compilerMessage.column);
                }

                context.addMessage(CompilerMessageCategory.WARNING, "process exited with code: " + output.getExitCode(), null, -1, -1);
            }

            return output;
        } catch (ExecutionException ex) {
            context.addMessage(CompilerMessageCategory.WARNING, ex.getMessage(), null, -1, -1);
            return null;
        }
    }

    private String generateFileUrl(String workingDirectory, CompilerMessage compilerMessage) {
        // Using this method instead of File.toURI().toUrl() because it doesn't use the two slashes after ':'
        // and IDEA doesn't recognises this as a valid URL (even though it seems to be spec compliant)

        File sourceFile = new File(workingDirectory, compilerMessage.fileName);
        String url = null;
        try {
            url = "file://" + sourceFile.getCanonicalPath();
        } catch (IOException e) {
            LOG.error("Cannot create url for compiler message: " + compilerMessage, e);
        }
        return url;
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

                    list.add(Trinity.create(virtualFile, packageName, imports));
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
                VfsUtil.createDirectoryIfMissing(new File(baseOutputFile.getPath() + goOutputRelativePath).getAbsolutePath());
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


    private String getCompilerBinary(Sdk sdk) {
        GoSdkData goSdkData = goSdkData(sdk);
        return goSdkData.BINARY_PATH + "/" + GoSdkUtil.getToolName(goSdkData.TARGET_OS, goSdkData.TARGET_ARCH, GoSdkTool.GoCompiler);
    }

    private String getPackerBinary(Sdk sdk) {
        GoSdkData goSdkData = goSdkData(sdk);
        return goSdkData.BINARY_PATH + "/" + GoSdkUtil.getToolName(goSdkData.TARGET_OS, goSdkData.TARGET_ARCH, GoSdkTool.GoArchivePacker);
    }

    private String getLinkerBinary(Sdk sdk) {
        GoSdkData goSdkData = goSdkData(sdk);
        return goSdkData.BINARY_PATH + "/" + GoSdkUtil.getToolName(goSdkData.TARGET_OS, goSdkData.TARGET_ARCH, GoSdkTool.GoLinker);
    }

    private String getTargetExtension(Sdk sdk) {
        GoSdkData goSdkData = goSdkData(sdk);
        return GoSdkUtil.getBinariesDesignation(goSdkData.TARGET_OS, goSdkData.TARGET_ARCH);
    }

    private GoSdkData goSdkData(Sdk sdk) {
        return (GoSdkData) sdk.getSdkAdditionalData();
    }

    private Sdk findGoSdkForModule(Module module) {
        return GoSdkUtil.getGoogleGoSdkForModule(module);
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

        Pattern pattern = Pattern.compile("([^:]+):(\\d+): ((?:(?:.)|(?:\\n(?!/)))+)", Pattern.UNIX_LINES);

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
