package ro.redeul.google.go.ide.actions;

import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.EmptyRunnable;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import ro.redeul.google.go.GoIcons;
import ro.redeul.google.go.config.sdk.GoAppEngineSdkData;
import ro.redeul.google.go.sdk.GoSdkUtil;

import java.io.File;

public class GAEDebugEnv extends GoCommonDebugAction {

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {

        final Project project = anActionEvent.getData(LangDataKeys.PROJECT);

        if (project == null) {
            return;
        }

        if (consoleView == null) {
            consoleView = TextConsoleBuilderFactory.getInstance().createBuilder(project).getConsole();
        }

        Sdk sdk = GoSdkUtil.getGoogleGAESdkForProject(project);
        if ( sdk == null ) {
            return;
        }

        final GoAppEngineSdkData sdkData = (GoAppEngineSdkData)sdk.getSdkAdditionalData();
        if ( sdkData == null ) {
            return;
        }

        String goExecName = sdkData.SDK_HOME_PATH + File.separator + "goapp";

        if (System.getProperty("os.name").toLowerCase().indexOf("win") >= 0) {
            goExecName = goExecName.concat(".exe");
        }

        String projectDir = project.getBasePath();

        if (projectDir == null) {
            return;
        }

        FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
        VirtualFile selectedFile = fileEditorManager.getSelectedFiles()[0];
        String fileName = selectedFile.getCanonicalPath();

        try {
            ToolWindowManager manager = ToolWindowManager.getInstance(project);
            ToolWindow window = manager.getToolWindow(ID);

            if (window == null) {
                window = manager.registerToolWindow(ID, false, ToolWindowAnchor.BOTTOM);

                ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
                Content content = contentFactory.createContent(consoleView.getComponent(), "go env", false);
                window.getContentManager().addContent(content);
                window.setIcon(GoIcons.GO_ICON_13x13);
                window.setToHideOnEmptyContent(true);
                window.setTitle(TITLE);

            }
            window.show(EmptyRunnable.getInstance());

            String[] goEnv = GoSdkUtil.getExtendedGAEEnv(sdkData, projectDir, "");

            String command = String.format(
                    "%s env",
                    goExecName
            );

            consoleView.clear();

            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec(command, goEnv);
            OSProcessHandler handler = new OSProcessHandler(proc, null);
            consoleView.attachToProcess(handler);
            consoleView.print(String.format("%s -> %s%n", "Project dir", projectDir), ConsoleViewContentType.NORMAL_OUTPUT);
            consoleView.print(String.format("%s%n", command), ConsoleViewContentType.NORMAL_OUTPUT);
            handler.startNotify();
        } catch (Exception e) {
            e.printStackTrace();
            Messages.showErrorDialog("Error while processing go env command.", "Error on go env");
        }
    }
}
