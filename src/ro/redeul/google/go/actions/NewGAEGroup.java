package ro.redeul.google.go.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ProjectRootManager;
import ro.redeul.google.go.config.sdk.GoAppEngineSdkType;

public class NewGAEGroup extends DefaultActionGroup
{
    @Override
    public void update(AnActionEvent e)
    {
        super.update(e);

        Sdk sdk = ProjectRootManager.getInstance(e.getProject()).getProjectSdk();

        final Module data = LangDataKeys.MODULE.getData(e.getDataContext());
        e.getPresentation().setVisible(data != null &&
                sdk != null &&
                sdk.getSdkType() instanceof GoAppEngineSdkType);
    }
}
