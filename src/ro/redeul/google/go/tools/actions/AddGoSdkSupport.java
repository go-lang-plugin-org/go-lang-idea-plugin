package ro.redeul.google.go.tools.actions;

import com.intellij.ide.IdeView;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import ro.redeul.google.go.tools.dialogs.AddGoSdkDialogForm;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 7/26/11
 * Time: 11:20 AM
 */
public class AddGoSdkSupport extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        final DataContext dataContext = e.getDataContext();

        final IdeView view = LangDataKeys.IDE_VIEW.getData(dataContext);
        if (view == null) {
            return;
        }

        final Project project = PlatformDataKeys.PROJECT.getData(dataContext);

//        final PsiDirectory dir = view.getOrChooseDirectory();
//        if (dir == null || project == null) return;

        AddGoSdkDialogForm addGoSdkDialogForm = new AddGoSdkDialogForm(project);
        addGoSdkDialogForm.show();
    }
}
