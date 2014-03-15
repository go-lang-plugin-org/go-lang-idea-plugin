package ro.redeul.google.go.ide.actions;

import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.EmptyRunnable;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.GoIcons;
import ro.redeul.google.go.config.sdk.GoAppEngineSdkData;
import ro.redeul.google.go.sdk.GoSdkUtil;

import javax.swing.*;
import java.io.File;

/**
 * User: Florin Patan <florinpatan@gmail.com>
 * Date: 12/01/14
 */
public class GoAppEngineUpload extends AnAction {

    private static final String ID = "GaeConsole";
    private static final String TITLE = "Go AppEngine Upload";
    public static ConsoleView consoleView;
    public static Process proc = null;

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {

        final Project project = anActionEvent.getData(LangDataKeys.PROJECT);

        if (project == null) {
            return;
        }

        String projectDir = project.getBasePath();

        if (projectDir == null) {
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

        if (consoleView == null) {
            consoleView = TextConsoleBuilderFactory.getInstance().createBuilder(project).getConsole();
        }

        final File cwd = getUploadDir(projectDir);
        if (cwd == null) {
            return;
        }

        ToolWindowManager manager = ToolWindowManager.getInstance(project);
        ToolWindow window = manager.getToolWindow(ID);

        if (window == null) {
            window = manager.registerToolWindow(ID, false, ToolWindowAnchor.BOTTOM);
            ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
            Content content = contentFactory.createContent(consoleView.getComponent(), "", false);
            window.getContentManager().addContent(content);
            window.setIcon(GoIcons.GAE_ICON_13x13);
            window.setToHideOnEmptyContent(true);
            window.setTitle(TITLE);
        }
        window.show(EmptyRunnable.getInstance());

        if (!stopCurrentUpload()) {
            return;
        }

        try {
            String[] goEnv = GoSdkUtil.getExtendedGAEEnv(sdkData, projectDir, "");

            String command = String.format(
                    "%s deploy -oauth",
                    sdkData.GOAPP_BIN_PATH
            );

            consoleView.print(command + "\n", ConsoleViewContentType.NORMAL_OUTPUT);

            Runtime rt = Runtime.getRuntime();
            proc = rt.exec(command, goEnv, cwd);
            OSProcessHandler handler = new OSProcessHandler(proc, null);
            consoleView.attachToProcess(handler);
            handler.startNotify();
        } catch (Exception e) {
            e.printStackTrace();
            Messages.showErrorDialog("Error while processing upload command.", "Error on Go App Engine Plugin");
        }
    }

    public static boolean stopCurrentUpload() {
        if (proc == null) {
            return true;
        }

        boolean stillRunning = true;
        try {
            GoAppEngineUpload.proc.exitValue();
            stillRunning = false;
        } catch (IllegalThreadStateException ignored) {

        }

        if (!stillRunning) {
            return true;
        }

        if (Messages.showYesNoDialog(
                "An upload is already in progress.\nDo you want to cancel it?",
                "Upload to AppEngine in progress",
                GoIcons.GAE_ICON_13x13) == Messages.NO) {
            return false;
        }

        try {
            GoAppEngineUpload.proc.exitValue();
        } catch (IllegalThreadStateException ignored) {
            proc.destroy();
            consoleView.print("\nUpload process stopped\n", ConsoleViewContentType.NORMAL_OUTPUT);
        }

        return true;
    }

    @Nullable
    private File getUploadDir(String projectDir) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select target folder");
        fileChooser.setApproveButtonText("Upload folder");
        fileChooser.setApproveButtonMnemonic('U');
        fileChooser.setCurrentDirectory(new File(projectDir));
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        Integer openDialogResult = fileChooser.showOpenDialog(GoAppEngineUpload.consoleView.getComponent());

        if (openDialogResult == JFileChooser.CANCEL_OPTION) {
            return null;
        }

        File cwd = fileChooser.getSelectedFile();

        if (!GoSdkUtil.checkFolderExists(cwd)) {
            Messages.showErrorDialog("The selected directory: " + cwd.getPath() + " does not appear to be a valid directory",
                    "Error on Go App Engine Plugin");
            return null;
        }

        if (!GoSdkUtil.checkFileExists(cwd.getPath() + File.separator + "app.yaml")) {
            Messages.showErrorDialog("The selected directory: " + cwd.getPath() + " does not appear to contain an app.yaml file",
                    "Error on Go App Engine Plugin");
            return null;
        }

        return cwd;
    }

}
