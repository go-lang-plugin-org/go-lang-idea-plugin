package ro.redeul.google.go.ide.actions;

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

public class GAEDebugSDK extends GoCommonDebugAction {

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

        String goExecName = sdkData.GO_HOME_PATH + "/../goapp";

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

            toolWindow.printNormalMessage(String.format("%s -> %s%n", "Project dir", projectDir));
            toolWindow.printNormalMessage(String.format("%s -> %s%n", "GO_HOME_PATH", sdkData.GO_HOME_PATH));
            toolWindow.printNormalMessage(String.format("%s -> %s%n", "TARGET_OS", sdkData.TARGET_OS));
            toolWindow.printNormalMessage(String.format("%s -> %s%n", "TARGET_ARCH", sdkData.TARGET_ARCH));
            toolWindow.printNormalMessage(String.format("%s -> %s%n", "VERSION_MAJOR", sdkData.VERSION_MAJOR));
            toolWindow.printNormalMessage(String.format("%s -> %s%n", "VERSION_MINOR", sdkData.VERSION_MINOR));

            toolWindow.printNormalMessage(String.format("%s -> %n", "Extended Go Env"));

            String[] goEnv = GoSdkUtil.getExtendedGAEEnv(sdkData, projectDir, "");
            for (String goenv : goEnv) {
                toolWindow.printNormalMessage(String.format("%s%n", goenv));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Messages.showErrorDialog("Error while processing go env command.", "Error on go env");
        }
    }
}
