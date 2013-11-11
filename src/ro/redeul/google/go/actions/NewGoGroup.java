package ro.redeul.google.go.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.module.Module;

public class NewGoGroup extends DefaultActionGroup
{
    @Override
    public void update(AnActionEvent e)
    {
        super.update(e);

        final Module data = LangDataKeys.MODULE.getData(e.getDataContext());
        e.getPresentation().setVisible(data != null);
    }
}
