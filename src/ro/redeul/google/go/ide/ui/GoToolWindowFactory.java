package ro.redeul.google.go.ide.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;

public class GoToolWindowFactory implements ToolWindowFactory {
    @Override
    public void createToolWindowContent(Project project, ToolWindow toolWindow) {
        GoToolWindow window = GoToolWindow.getInstance(project);
        window.initGoToolWindow(toolWindow);
    }
}
