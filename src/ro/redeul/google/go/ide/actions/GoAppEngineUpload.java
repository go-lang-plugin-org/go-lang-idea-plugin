package ro.redeul.google.go.ide.actions;

import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.EmptyRunnable;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import ro.redeul.google.go.GoIcons;
import ro.redeul.google.go.ide.GoAppEngineSettings;

import java.io.File;

/**
 * User: jhonny
 * Date: 21/07/11
 */
public class GoAppEngineUpload extends AnAction {

    private static final String ID = "Go App Engine Console";
    private static final String TITLE = "Go App Engine Console Output";
    private static ConsoleView consoleView;

    private final GoAppEngineSettings appEngineSettings = GoAppEngineSettings.getInstance();

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {

        final Project project = anActionEvent.getData(LangDataKeys.PROJECT);

        if (project == null) {
            return;
        }

        if (GoAppEngineUpload.consoleView == null) {
            GoAppEngineUpload.consoleView = TextConsoleBuilderFactory.getInstance().createBuilder(project).getConsole();
        }

        if (appEngineSettings.getEmail().equals("")) {
            Messages.showErrorDialog("Your e-mail address is empty. \nPlease check your Go App Engine settings.", "Error on Go App Engine Plugin");
            return;
        }

        if (appEngineSettings.getPassword().equals("")) {
            Messages.showErrorDialog("Your password is empty. \nPlease check your Go App Engine settings.", "Error on Go App Engine Plugin");
            return;
        }

        if (appEngineSettings.getGaePath().equals("")) {
            Messages.showErrorDialog("Your Go App Engine path is empty. \nPlease check your Go App Engine settings.", "Error on Go App Engine Plugin");
            return;
        }

        if (!(new File(appEngineSettings.getGaePath()).exists())) {
            Messages.showErrorDialog("Your Go App Engine path doesn't exists anymore. \nPlease check your Go App Engine settings.", "Error on Go App Engine Plugin");
        }

        ProcessHandler processHandler = null;
        try {
            ToolWindowManager manager = ToolWindowManager.getInstance(project);
            ToolWindow window = manager.getToolWindow(ID);

            if (window == null) {
                window = manager.registerToolWindow(ID, false, ToolWindowAnchor.BOTTOM);

                ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
                Content content = contentFactory.createContent(consoleView.getComponent(), "", false);
                window.getContentManager().addContent(content);
                window.setIcon(GoIcons.GAE_ICON_16x16);
                window.setToHideOnEmptyContent(true);
                window.setTitle(TITLE);

            }
            window.show(EmptyRunnable.getInstance());

            String username = appEngineSettings.getEmail();

            String command = String.format(
                    "%s/appcfg.py -e %s --passin update %s",
                    appEngineSettings.getGaePath(),
                    username,
                    project.getBasePath()
            );


            Runtime rt = Runtime.getRuntime();

            Process proc = rt.exec(command);
            String password = appEngineSettings.getPassword();
            OSProcessHandler handler = new OSProcessHandler(proc, null);
            byte[] theByteArray = password.getBytes();
            handler.getProcessInput().write(theByteArray);
            consoleView.attachToProcess(handler);
            handler.startNotify();


        } catch (Exception e) {
            e.printStackTrace();
            Messages.showErrorDialog("Error while processing upload command.", "Error on Go App Engine Plugin");
        }


    }

}
