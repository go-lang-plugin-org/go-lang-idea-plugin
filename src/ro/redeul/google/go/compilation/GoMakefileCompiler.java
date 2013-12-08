package ro.redeul.google.go.compilation;

import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.openapi.compiler.CompileContext;
import com.intellij.openapi.compiler.CompileScope;
import com.intellij.openapi.compiler.CompilerMessageCategory;
import com.intellij.openapi.compiler.TranslatingCompiler;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.CompilerProjectExtension;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.Chunk;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.GoBundle;
import ro.redeul.google.go.GoFileType;
import ro.redeul.google.go.config.sdk.GoSdkData;
import ro.redeul.google.go.sdk.GoSdkTool;
import ro.redeul.google.go.sdk.GoSdkUtil;
import ro.redeul.google.go.util.ProcessUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Go Makefile compiler implementation.
 * <p/>
 * Author: Alexandre Normand
 * Date: 11-05-28
 * Time: 8:45 PM
 */
public class GoMakefileCompiler implements TranslatingCompiler {

    private static final Logger LOG =
        Logger.getInstance("#ro.redeul.google.go.compilation.GoMakefileCompiler");

    private final Project project;

    public GoMakefileCompiler(Project project) {
        this.project = project;
    }

    @NotNull
    public String getDescription() {
        return "Go Makefile Compiler";
    }

    public boolean validateConfiguration(CompileScope scope) {

        // Check for project sdk only if experimental make system is enabled (for now)
        Sdk projectSdk = ProjectRootManager.getInstance(project).getProjectSdk();
        if (projectSdk == null) {
            Messages.showErrorDialog(
                    project,
                    GoBundle.message("cannot.compile.no.go.project.sdk", project.getName()),
                    GoBundle.message("cannot.compile"));
            return false;

        } else if (!(projectSdk.getSdkAdditionalData() instanceof GoSdkData)) {
            Messages.showErrorDialog(
                    project,
                    GoBundle.message("cannot.compile.invalid.project.sdk", project.getName()),
                    GoBundle.message("cannot.compile"));
            return false;
        }

        return true;
    }

    public boolean isCompilableFile(VirtualFile file, CompileContext context) {
        return file.getFileType() == GoFileType.INSTANCE;
    }

    public void compile(CompileContext context, Chunk<Module> moduleChunk, VirtualFile[] files, OutputSink sink) {
        make(context, moduleChunk);
    }

    private void make(final CompileContext context, final Chunk<Module> moduleChunk) {
        String basePath = project.getBaseDir().getPath();
        File makeFile = new File(basePath, "/Makefile");
        if (!makeFile.exists()) {
            // TODO Generate the Makefile
            context.addMessage(CompilerMessageCategory.ERROR, "Makefile doesn't exist at " + makeFile.getPath(), null, -1, -1);
        } else {
            GeneralCommandLine command = new GeneralCommandLine();
            final Sdk projectSdk = ProjectRootManager.getInstance(project).getProjectSdk();
            final GoSdkData goSdkData = goSdkData(projectSdk);
            command.setExePath(getMakeBinary(projectSdk));
            command.addParameter("-C");
            command.addParameter(project.getBaseDir().getPath());
            command.addParameter("-f");
            command.addParameter(makeFile.getPath());
            command.addParameter("-e");
            command.addParameter("install");
            command.addParameter("clean");
            command.getEnvironment().putAll(new HashMap<String, String>() {{
                put("GOROOT", projectSdk.getHomePath());
                put("GOARCH", goSdkData.TARGET_ARCH.getName());
                put("GOOS", goSdkData.TARGET_OS.getName());
                put("GOBIN", goSdkData.GO_BIN_PATH);
                put("PATH", System.getenv("PATH") + File.pathSeparator + goSdkData.GO_BIN_PATH);
                put("TARGDIR", getOutputPath(context, moduleChunk));
            }});

            CompilationTaskWorker compilationTaskWorker = new CompilationTaskWorker(
                    new MakeOutputStreamParser(projectSdk, goSdkData, basePath));
            compilationTaskWorker.executeTask(command, basePath, context);
        }
    }

    private String getOutputPath(CompileContext context, Chunk<Module> moduleChunk) {
        if (moduleChunk.getNodes().isEmpty()) {
            context.addMessage(CompilerMessageCategory.WARNING, "No module defined, running application might not function properly.",
                    null, -1, -1);
            return CompilerProjectExtension.getInstance(project).getCompilerOutput().getPath() + "/go-bins";
        }
        else {
            // TODO This is a hack to keep GoMakefileCompiler compatible with the way Runner expects binaries, we
            // use any module assuming the path is the same for all
            Module firstModule = moduleChunk.getNodes().iterator().next();
            return context.getModuleOutputDirectory(firstModule).getPath() + "/go-bins";
        }

    }

    private static class MakeOutputStreamParser implements ProcessUtil.StreamParser<List<CompilerMessage>> {

        private final static Pattern pattern = Pattern.compile("([^:]+):(\\d+): ((?:(?:.)|(?:\\n(?!/)))+)", Pattern.UNIX_LINES);
        private final Sdk projectSdk;
        private final GoSdkData goSdkData;
        private final String basePath;

        public MakeOutputStreamParser(Sdk projectSdk, GoSdkData goSdkData, String basePath) {
            this.goSdkData = goSdkData;
            this.projectSdk = projectSdk;
            this.basePath = basePath;
        }

        public List<CompilerMessage> parseStream(String data) {

            List<CompilerMessage> messages = new ArrayList<CompilerMessage>();

            Matcher matcher = pattern.matcher(data);

            if (matcher.find()) {
                String filename = matcher.group(1);
                String url = CompilationTaskWorker.generateFileUrl(basePath, filename);
                messages.add(
                        new CompilerMessage(
                                CompilerMessageCategory.ERROR, matcher.group(3), url, Integer.parseInt(matcher.group(2)), -1));
            } else {

                // Ignore error lines that start with the compiler as the line that follows this contains the error that
                // we're interested in. Otherwise, the error is more severe and we should display it
                if (!StringUtils.startsWith(data, GoSdkUtil.getCompilerName(goSdkData.TARGET_ARCH))) {
                    messages.add(new CompilerMessage(CompilerMessageCategory.ERROR, data, null, -1, -1));
                }
            }

            return messages;
        }
    }

    private String getMakeBinary(Sdk sdk) {
        GoSdkData goSdkData = goSdkData(sdk);
        return goSdkData.GO_BIN_PATH + "/" + GoSdkUtil.getToolName(goSdkData.TARGET_ARCH, GoSdkTool.GoMake);
    }

    private GoSdkData goSdkData(Sdk sdk) {
        return (GoSdkData) sdk.getSdkAdditionalData();
    }
}
