package ro.redeul.google.go.components;

import com.intellij.execution.process.OSProcessHandler;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileDocumentManagerAdapter;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.GoFileType;
import ro.redeul.google.go.config.sdk.GoSdkData;
import ro.redeul.google.go.ide.GoProjectSettings;
import ro.redeul.google.go.ide.ui.GoToolWindow;
import ro.redeul.google.go.sdk.GoSdkUtil;

/**
 * @author Mihai Claudiu Toader <mtoader@gmail.com>
 *         Date: Sep 7, 2010
 */
public class EditorTweakingComponent extends FileDocumentManagerAdapter {
    @Override
    public void beforeDocumentSaving(@NotNull final Document document) {

        if (!document.isWritable())
            return;

        final Project[] projects = ProjectManager.getInstance().getOpenProjects();
        if (projects.length == 0) {
            return;
        }

        final VirtualFile file = FileDocumentManager.getInstance().getFile(document);
        if (file == null || file.getFileType() != GoFileType.INSTANCE) {
            return;
        }

        Project project = null;
        for (Project possibleProject : projects) {
            if (ProjectRootManager.getInstance(possibleProject).getFileIndex().getSourceRootForFile(file) != null ||
                    ProjectRootManager.getInstance(possibleProject).getFileIndex().getContentRootForFile(file) != null) {
               project = possibleProject;
                break;
            }
        }

        if (project == null) {
            return;
        }

        GoProjectSettings.GoProjectSettingsBean settings = GoProjectSettings.getInstance(project).getState();

        final Project p = project;
        if (settings.goimportsOnSave) {
            ApplicationManager.getApplication().invokeLater(new Runnable() {
                @Override
                public void run() {
                    ProcessFileWithGoImports(p, file);
                }
            });
        } else if (settings.goFmtOnSave) {
            ApplicationManager.getApplication().invokeLater(new Runnable() {
                @Override
                public void run() {
                    ProcessFileWithGoFmt(p, file);
                }
            });
        }
    }

    private void ProcessFileWithGoImports(Project project, VirtualFile file) {
        GoToolWindow toolWindow = GoToolWindow.getInstance(project);
        toolWindow.setTitle("goimports (file)");

        String fileName = file.getCanonicalPath();

        String projectDir = project.getBasePath();
        if (projectDir == null) {
            return;
        }

        Sdk sdk = GoSdkUtil.getProjectSdk(project);
        if (sdk == null) {
            return;
        }

        String[] goEnv = GoSdkUtil.getGoEnv(sdk, projectDir);
        if (goEnv == null) {
            return;
        }

        try {
            String[] command = {GoSdkUtil.getGoImportsExec(), "-w", fileName};

            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec(command, goEnv);
            OSProcessHandler handler = new OSProcessHandler(proc, null);
            toolWindow.attachConsoleViewToProcess(handler);
            toolWindow.printNormalMessage(String.format("%s%n", StringUtil.join(command, " ")));
            handler.startNotify();

            if (proc.waitFor() == 0) {
                VirtualFileManager.getInstance().syncRefresh();
                file.refresh(false, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Messages.showErrorDialog("Error while processing goimports command.", "Error on goimports");
        }
    }

    private void ProcessFileWithGoFmt(Project project, VirtualFile file) {
        GoToolWindow toolWindow = GoToolWindow.getInstance(project);
        toolWindow.setTitle("go fmt (file)");

        String fileName = file.getCanonicalPath();

        String projectDir = project.getBasePath();
        if (projectDir == null) {
            return;
        }

        Sdk sdk = GoSdkUtil.getProjectSdk(project);
        if (sdk == null) {
            return;
        }

        String[] goEnv = GoSdkUtil.getGoEnv(sdk, projectDir);
        if (goEnv == null) {
            return;
        }

        String goExec = GoSdkUtil.getGoExecName(sdk);

        try {
            String[] command = {goExec, "fmt", fileName};

            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec(command, goEnv);
            OSProcessHandler handler = new OSProcessHandler(proc, null);
            toolWindow.attachConsoleViewToProcess(handler);
            toolWindow.printNormalMessage(String.format("%s%n", StringUtil.join(command, " ")));
            handler.startNotify();

            if (proc.waitFor() == 0) {
                VirtualFileManager.getInstance().syncRefresh();
                file.refresh(false, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Messages.showErrorDialog("Error while processing go fmt command.", "Error on go fmt");
        }
    }
}
