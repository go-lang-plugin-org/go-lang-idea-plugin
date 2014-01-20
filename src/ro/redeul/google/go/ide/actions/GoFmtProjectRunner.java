package ro.redeul.google.go.ide.actions;

import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.EmptyRunnable;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import ro.redeul.google.go.GoIcons;
import ro.redeul.google.go.config.sdk.GoAppEngineSdkData;
import ro.redeul.google.go.config.sdk.GoAppEngineSdkType;
import ro.redeul.google.go.config.sdk.GoSdkData;
import ro.redeul.google.go.config.sdk.GoSdkType;
import ro.redeul.google.go.sdk.GoSdkUtil;

import java.io.File;

public class GoFmtProjectRunner extends AnAction {

    private static final String ID = "go fmt Console";
    private static final String TITLE = "Output";
    private static ConsoleView consoleView;

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {

        final Project project = anActionEvent.getData(LangDataKeys.PROJECT);

        if (project == null) {
            return;
        }

        if (consoleView == null) {
            consoleView = TextConsoleBuilderFactory.getInstance().createBuilder(project).getConsole();
        }

        String projectDir = project.getBasePath();

        if (projectDir == null) {
            return;
        }

        Sdk sdk = GoSdkUtil.getGoogleGoSdkForProject(project);
        if (sdk == null) {
            sdk = GoSdkUtil.getGoogleGAESdkForProject(project);
            if (sdk == null) {
                return;
            }
        }

        String[] goEnv;
        String goExecName;

        if (sdk.getSdkType() instanceof GoSdkType) {
            GoSdkData sdkData = (GoSdkData) sdk.getSdkAdditionalData();
            if (sdkData == null) {
                return;
            }

            goExecName = sdkData.GO_BIN_PATH;
            goEnv = GoSdkUtil.getExtendedGoEnv(sdkData, projectDir, "");
        } else if (sdk.getSdkAdditionalData() instanceof GoAppEngineSdkData) {
            GoAppEngineSdkData sdkData = (GoAppEngineSdkData) sdk.getSdkAdditionalData();
            if (sdkData == null) {
                return;
            }

            goExecName = sdkData.GOAPP_BIN_PATH;
            goEnv = GoSdkUtil.getExtendedGAEEnv(sdkData, projectDir, "");
        } else {
            return;
        }

        FileDocumentManager.getInstance().saveAllDocuments();
        FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);

        try {
            ToolWindowManager manager = ToolWindowManager.getInstance(project);
            ToolWindow window = manager.getToolWindow(ID);

            if (window == null) {
                window = manager.registerToolWindow(ID, false, ToolWindowAnchor.BOTTOM);

                ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
                Content content = contentFactory.createContent(consoleView.getComponent(), "", false);
                window.getContentManager().addContent(content);
                window.setIcon(GoIcons.GO_ICON_13x13);
                window.setToHideOnEmptyContent(true);
                window.setTitle(TITLE);

            }
            window.show(EmptyRunnable.getInstance());

            String command = String.format(
                    "%s fmt ./...",
                    goExecName
            );

            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec(command, goEnv, new File(projectDir));
            OSProcessHandler handler = new OSProcessHandler(proc, null);
            consoleView.attachToProcess(handler);
            consoleView.print(String.format("%s%n", command), ConsoleViewContentType.NORMAL_OUTPUT);
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
