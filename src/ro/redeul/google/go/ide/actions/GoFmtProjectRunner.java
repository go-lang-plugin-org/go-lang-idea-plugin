package ro.redeul.google.go.ide.actions;

import com.intellij.execution.process.OSProcessHandler;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFileManager;
import ro.redeul.google.go.ide.ui.GoToolWindow;
import ro.redeul.google.go.sdk.GoSdkUtil;

import java.io.File;

public class GoFmtProjectRunner extends AnAction {

    private static final String TITLE = "go fmt (project)";

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {

        final Project project = anActionEvent.getData(LangDataKeys.PROJECT);

        if (project == null) {
            return;
        }

        GoToolWindow toolWindow = GoToolWindow.getInstance(project);
        toolWindow.setTitle(TITLE);

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

        String goExecName = GoSdkUtil.getGoExecName(sdk);
        if (goExecName == null) {
            return;
        }

        FileDocumentManager.getInstance().saveAllDocuments();

        try {
            String command = String.format(
                    "%s fmt ./...",
                    goExecName
            );

            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec(command, goEnv, new File(projectDir));
            OSProcessHandler handler = new OSProcessHandler(proc, null);
            toolWindow.attachConsoleViewToProcess(handler);
            toolWindow.printNormalMessage(String.format("%s%n", command));
            handler.startNotify();

            if (proc.waitFor() == 0) {
                VirtualFileManager.getInstance().syncRefresh();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Messages.showErrorDialog("Error while processing go fmt command.", "Error on go fmt");
        }
    }
}
