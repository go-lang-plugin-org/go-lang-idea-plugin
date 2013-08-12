package ro.redeul.google.go.compilation;

import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.openapi.compiler.CompileContext;
import com.intellij.openapi.compiler.CompileScope;
import com.intellij.openapi.compiler.TranslatingCompiler;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.Chunk;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.GoFileType;
import ro.redeul.google.go.config.sdk.GoSdkData;
import ro.redeul.google.go.config.sdk.GoTargetOs;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;

public class GoInstallCompiler implements TranslatingCompiler {

    Project project;

    public GoInstallCompiler(Project project) {
        this.project = project;
    }

    @Override
    public boolean isCompilableFile(VirtualFile virtualFile, CompileContext compileContext) {
        return virtualFile.getFileType() == GoFileType.INSTANCE;
    }

    @Override
    public void compile(CompileContext compileContext, Chunk<Module> moduleChunk, VirtualFile[] virtualFiles, OutputSink outputSink) {

        String basePath = compileContext.getProject().getBasePath();
        HashSet<String> packages = new HashSet<String>();

        for (int i = 0; i < virtualFiles.length; i++) {
            VirtualFile vf = virtualFiles[i];
            String fullPath = vf.getParent().getPath();
            String importPath = fullPath.substring(basePath.length() + 5);
            packages.add(importPath);
        }
        GeneralCommandLine command = new GeneralCommandLine();
        final Sdk projectSdk = ProjectRootManager.getInstance(project).getProjectSdk();
        final GoSdkData goSdkData = (GoSdkData) projectSdk.getSdkAdditionalData();
        command.setExePath(goSdkData.GO_BIN_PATH);
        command.addParameter("install");

        for (String pkg : packages) {
            command.addParameter(pkg);
        }

        command.setWorkDirectory(basePath);

        HashMap<String, String> envparams = new HashMap<String, String>();
        envparams.put("GOROOT", projectSdk.getHomePath());
        envparams.put("GOPATH", project.getBasePath());

        command.setEnvParams(envparams);

        CompilationTaskWorker compilationTaskWorker = new CompilationTaskWorker(
                new GoCompilerOutputStreamParser(basePath));
        compilationTaskWorker.executeTask(command, basePath, compileContext);
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Go Install Compiler";  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean validateConfiguration(CompileScope compileScope) {
        return true;
    }
}
