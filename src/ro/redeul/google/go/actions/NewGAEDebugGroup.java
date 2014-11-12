package ro.redeul.google.go.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ProjectRootManager;
import ro.redeul.google.go.config.sdk.GoAppEngineSdkType;

public class NewGAEDebugGroup extends DefaultActionGroup
{
    private static final boolean debugEnabled = true;

    @Override
    public void update(AnActionEvent e)
    {
        super.update(e);

        final Module data = LangDataKeys.MODULE.getData(e.getDataContext());
        e.getPresentation().setVisible(data != null);

        if (!e.getPresentation().isVisible()) {
            return;
        }

        Project project = e.getProject();
        if ( project != null ) {
            Sdk sdk = ProjectRootManager.getInstance(project).getProjectSdk();
            e.getPresentation().setVisible(debugEnabled && sdk != null && sdk.getSdkType() instanceof GoAppEngineSdkType);
        }
    }
}
