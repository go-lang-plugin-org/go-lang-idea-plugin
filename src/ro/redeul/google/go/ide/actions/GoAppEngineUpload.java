package ro.redeul.google.go.ide.actions;

import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.EmptyRunnable;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import ro.redeul.google.go.GoIcons;

/**
 * User: jhonny
 * Date: 21/07/11
 */
public class GoAppEngineUpload extends AnAction {

    private static final String ID = "Go App Engine Console";
    private static final String TITLE = "Go App Engine Console Output";
    private static ConsoleView consoleView;

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {

        final Project project = anActionEvent.getData(DataKeys.PROJECT);

        if (GoAppEngineUpload.consoleView == null)
            GoAppEngineUpload.consoleView = TextConsoleBuilderFactory.getInstance().createBuilder(project).getConsole();

        ProcessHandler processHandler = null;
        try {
            ToolWindowManager manager = ToolWindowManager.getInstance(project);
            ToolWindow window = manager.getToolWindow(ID);

            if (window == null) {
                window =
                        manager.registerToolWindow(ID, false, ToolWindowAnchor.BOTTOM);

                ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
                Content content = contentFactory.createContent(consoleView.getComponent(), "", false);
                window.getContentManager().addContent(content);
                window.setIcon(GoIcons.GAE_ICON_16x16);
                window.setToHideOnEmptyContent(true);
                window.setTitle(TITLE);

            }
            window.show(EmptyRunnable.getInstance());

            Runtime rt = Runtime.getRuntime();
            // TODO create this configuration
            Process proc = rt.exec("/opt/appengine-go/appcfg.py -e khronnuz@gmail.com --passin update /Users/jhonny/workspaces/gae/gae-go");
            String password = "xx";
            OSProcessHandler handler = new OSProcessHandler(proc, null);
            byte[] theByteArray = password.getBytes();
            handler.getProcessInput().write(theByteArray);
            consoleView.attachToProcess(handler);
            handler.startNotify();


        } catch (Exception e) {
            e.printStackTrace();
            Messages.showErrorDialog("Error while processing upload command.", "Error on App Engine Plugin");
        }


    }

}
