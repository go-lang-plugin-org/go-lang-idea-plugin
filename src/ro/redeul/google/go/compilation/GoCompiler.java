package ro.redeul.google.go.compilation;

import com.intellij.compiler.impl.CompilerUtil;
import com.intellij.facet.FacetManager;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.compiler.CompileContext;
import com.intellij.openapi.compiler.CompileScope;
import com.intellij.openapi.compiler.CompilerMessageCategory;
import com.intellij.openapi.compiler.TranslatingCompiler;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.roots.ui.configuration.ClasspathEditor;
import com.intellij.openapi.roots.ui.configuration.ModulesConfigurator;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.util.Chunk;
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
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Aug 19, 2010
 * Time: 10:46:14 AM
 * To change this template use File | Settings | File Templates.
 */
public class GoCompiler implements TranslatingCompiler {

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

    public void compile(final CompileContext context, Chunk<Module> moduleChunk, VirtualFile[] files, OutputSink sink) {

        Map<Module, List<VirtualFile>> mapToFiles = CompilerUtil.buildModuleToFilesMap(context, files);

        for (Map.Entry<Module, List<VirtualFile>> moduleFiles : mapToFiles.entrySet()) {

            final Module module = moduleFiles.getKey();

            final Sdk sdk = findGoSdkForModule(module);
            final VirtualFile moduleOutputDirectory = context.getModuleOutputDirectory(module);
            final String outputPath = moduleOutputDirectory.getPath();

            final Map<String, List<VirtualFile>> packagesToFileList = new HashMap<String, List<VirtualFile>>();

            final List<Pair<String, String>> importDependencies = new ArrayList<Pair<String, String>>();
            final Set<String> ourPackages = new HashSet<String>();

            for (final VirtualFile virtualFile : moduleFiles.getValue()) {
                ApplicationManager.getApplication().runReadAction(new Runnable() {
                    public void run() {
                        PsiFile psiFile = PsiManager.getInstance(module.getProject()).findFile(virtualFile);

                        if (psiFile instanceof GoFile) {
                            GoFile goFile = (GoFile) psiFile;

                            String packageName = goFile.getPackage().getPackageName();

                            List<VirtualFile> virtualFiles = getFilesForPackage(packageName, packagesToFileList);
                            virtualFiles.add(virtualFile);
                            ourPackages.add(packageName);

                            for (GoImportDeclaration importDeclaration : goFile.getImportDeclarations()) {
                                for (GoImportSpec importSpec : importDeclaration.getImports()) {
                                    importDependencies.add(Pair.create(cleanUpImportName(importSpec.getImportPath()), packageName));
                                }
                            }
                        }
                    }
                });
            }

            List<String> packagesCompilationOrder = findPackagesCompilationOrder(importDependencies, ourPackages);

            for (final String packageName : packagesCompilationOrder) {
                final String destination = outputPath + "/go-binaries/packages/";

                new WriteCommandAction<Boolean>(module.getProject()) {
                    @Override
                    protected void run(Result<Boolean> boolResult) throws Throwable {
                        boolResult.setResult(false);
                        VfsUtil.createDirectoryIfMissing(moduleOutputDirectory, "/go-binaries/packages");
                        boolResult.setResult(true);
                    }
                }.execute();

                doCompileFile(context, sdk, outputPath, packagesToFileList.get(packageName), destination + packageName + ".6");
                if (LocalFileSystem.getInstance().refreshAndFindFileByPath(destination + packageName + ".6") == null) {
                    continue;
                }

                doPackLibrary(context, sdk, outputPath, destination + packageName + ".6", destination + packageName + ".a");
                if (LocalFileSystem.getInstance().refreshAndFindFileByPath(destination + packageName + ".a") == null) {
                    continue;
                }

                Collection<OutputItem> outputItems = new ArrayList<OutputItem>();

                for (final VirtualFile sourceFile : packagesToFileList.get(packageName)) {
                    outputItems.add(
                            new TranslatingCompiler.OutputItem() {
                                public String getOutputPath() {
                                    return destination + packageName + ".6";
                                }

                                public VirtualFile getSourceFile() {
                                    return sourceFile;
                                }
                            });

                }

                outputItems.add(
                        new OutputItem() {
                            public String getOutputPath() {
                                return destination + packageName + ".a";
                            }

                            public VirtualFile getSourceFile() {
                                return moduleOutputDirectory.findFileByRelativePath("/go-binaries/packages/" + packageName + ".6");
                            }
                        }
                );

                sink.add(destination, outputItems, new VirtualFile[]{});
            }
        }
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
        context.getProgressIndicator().setText("Packging library .. " + libraryOutput);

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
        return "gopack";
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
}
