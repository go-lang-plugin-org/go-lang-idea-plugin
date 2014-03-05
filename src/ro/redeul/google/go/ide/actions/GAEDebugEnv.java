package ro.redeul.google.go.ide.actions;

import com.intellij.execution.process.OSProcessHandler;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import ro.redeul.google.go.config.sdk.GoAppEngineSdkData;
import ro.redeul.google.go.ide.ui.GoToolWindow;
import ro.redeul.google.go.sdk.GoSdkUtil;

public class GAEDebugEnv extends GoCommonDebugAction {

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {

        final Project project = anActionEvent.getData(LangDataKeys.PROJECT);

        if (project == null) {
            return;
        }

        Sdk sdk = GoSdkUtil.getGoogleGAESdkForProject(project);
        if ( sdk == null ) {
            return;
        }

        final GoAppEngineSdkData sdkData = (GoAppEngineSdkData)sdk.getSdkAdditionalData();
        if ( sdkData == null ) {
            return;
        }

        String projectDir = project.getBasePath();

        if (projectDir == null) {
            return;
        }

        FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
        VirtualFile selectedFile = fileEditorManager.getSelectedFiles()[0];
        String fileName = selectedFile.getCanonicalPath();

        try {
            GoToolWindow toolWindow = this.getGoToolWindow(project);
            toolWindow.show();
            toolWindow.clearConsoleView();

            String[] goEnv = GoSdkUtil.getExtendedGAEEnv(sdkData, projectDir, "");

            String command = String.format(
                    "%s env",
                    sdkData.GOAPP_BIN_PATH
            );


            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec(command, goEnv);
            OSProcessHandler handler = new OSProcessHandler(proc, null);
            toolWindow.attachConsoleViewToProcess(handler);
            toolWindow.printNormalMessage(String.format("%s -> %s%n", "Project dir", projectDir));
            toolWindow.printNormalMessage(String.format("%s%n", command));
            handler.startNotify();
        } catch (Exception e) {
            e.printStackTrace();
            Messages.showErrorDialog("Error while processing go env command.", "Error on go env");
        }
    }
}
