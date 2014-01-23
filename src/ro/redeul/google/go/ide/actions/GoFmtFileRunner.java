package ro.redeul.google.go.ide.actions;

import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.EmptyRunnable;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import ro.redeul.google.go.runner.GoCommonConsoleView;
import ro.redeul.google.go.sdk.GoSdkUtil;

public class GoFmtFileRunner extends AnAction {

    private static final String TITLE = "go fmt (file)";

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {

        final Project project = anActionEvent.getData(LangDataKeys.PROJECT);

        if (project == null) {
            return;
        }

        if (GoCommonConsoleView.consoleView == null) {
            GoCommonConsoleView.consoleView = TextConsoleBuilderFactory.getInstance().createBuilder(project).getConsole();
        }
        ConsoleView consoleView = GoCommonConsoleView.consoleView;

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

        FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
        VirtualFile selectedFile = fileEditorManager.getSelectedFiles()[0];
        String fileName = selectedFile.getCanonicalPath();
        Document doc = FileDocumentManager.getInstance().getDocument(selectedFile);
        if (doc != null) {
            FileDocumentManager.getInstance().saveDocument(doc);
        }
        try {
            ToolWindowManager manager = ToolWindowManager.getInstance(project);
            ToolWindow window = manager.getToolWindow(GoCommonConsoleView.ID);

            if (window == null) {
                window = manager.registerToolWindow(GoCommonConsoleView.ID, false, ToolWindowAnchor.BOTTOM);

                ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
                Content content = contentFactory.createContent(consoleView.getComponent(), "", false);
                window.getContentManager().addContent(content);
                window.setIcon(GoSdkUtil.getProjectIcon(sdk));
                window.setToHideOnEmptyContent(true);
            }
            window.setTitle(TITLE);
            window.show(EmptyRunnable.getInstance());

            String command = String.format(
                    "%s fmt %s",
                    goExecName,
                    fileName
            );

            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec(command, goEnv);
            OSProcessHandler handler = new OSProcessHandler(proc, null);
            consoleView.attachToProcess(handler);
            consoleView.print(String.format("%s%n", command), ConsoleViewContentType.NORMAL_OUTPUT);
            handler.startNotify();

            if (proc.waitFor() == 0) {
                VirtualFileManager.getInstance().syncRefresh();
                selectedFile.refresh(false, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Messages.showErrorDialog("Error while processing go fmt command.", "Error on go fmt");
        }
    }
}
