package ro.redeul.google.go.ide.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.project.Project;
import ro.redeul.google.go.ide.ui.GoToolWindow;

public abstract class GoCommonDebugAction extends AnAction {
    protected static final String ID = "go Debug Console";
    protected static String TITLE = "";
    private GoToolWindow toolWindow;

    protected GoToolWindow getGoToolWindow(Project project) {
        if(toolWindow == null) {
            toolWindow = GoToolWindow.getInstance(project);
        }

        return toolWindow;
    }
}