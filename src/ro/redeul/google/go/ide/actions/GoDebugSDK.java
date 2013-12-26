package ro.redeul.google.go.ide.actions;

import com.intellij.execution.filters.TextConsoleBuilderFactory;
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
import ro.redeul.google.go.config.sdk.GoSdkData;
import ro.redeul.google.go.sdk.GoSdkUtil;

public class GoDebugSDK extends GoCommonDebugAction {

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {

        final Project project = anActionEvent.getData(LangDataKeys.PROJECT);

        if (project == null) {
            return;
        }

        if (consoleView == null) {
            consoleView = TextConsoleBuilderFactory.getInstance().createBuilder(project).getConsole();
        }

        Sdk sdk = GoSdkUtil.getGoogleGoSdkForProject(project);
        if ( sdk == null ) {
            return;
        }

        final GoSdkData sdkData = (GoSdkData)sdk.getSdkAdditionalData();
        if ( sdkData == null ) {
            return;
        }

        String goExecName = sdkData.GO_BIN_PATH;

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

            consoleView.clear();

            consoleView.print(String.format("%s -> %s%n", "Project dir", projectDir), ConsoleViewContentType.NORMAL_OUTPUT);
            consoleView.print(String.format("%s -> %s%n", "GO_GOROOT_PATH", sdkData.GO_GOROOT_PATH), ConsoleViewContentType.NORMAL_OUTPUT);
            consoleView.print(String.format("%s -> %s%n", "GO_BIN_PATH", sdkData.GO_BIN_PATH), ConsoleViewContentType.NORMAL_OUTPUT);
            consoleView.print(String.format("%s -> %s%n", "GO_GOPATH_PATH", sdkData.GO_GOPATH_PATH), ConsoleViewContentType.NORMAL_OUTPUT);
            consoleView.print(String.format("%s -> %s%n", "TARGET_OS", sdkData.TARGET_OS), ConsoleViewContentType.NORMAL_OUTPUT);
            consoleView.print(String.format("%s -> %s%n", "TARGET_ARCH", sdkData.TARGET_ARCH), ConsoleViewContentType.NORMAL_OUTPUT);
            consoleView.print(String.format("%s -> %s%n", "VERSION_MAJOR", sdkData.VERSION_MAJOR), ConsoleViewContentType.NORMAL_OUTPUT);
            consoleView.print(String.format("%s -> %s%n", "VERSION_MINOR", sdkData.VERSION_MINOR), ConsoleViewContentType.NORMAL_OUTPUT);

            consoleView.print(String.format("%s -> %n", "Extended Go Env"), ConsoleViewContentType.NORMAL_OUTPUT);

            String[] goEnv = GoSdkUtil.getExtendedGoEnv(sdkData, projectDir, "");
            for (String goenv : goEnv) {
                consoleView.print(String.format("%s%n", goenv), ConsoleViewContentType.NORMAL_OUTPUT);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Messages.showErrorDialog("Error while processing go env command.", "Error on go env");
        }
    }
}
