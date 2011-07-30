package ro.redeul.google.go.tools.dialogs;

import com.intellij.CommonBundle;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 7/26/11
 * Time: 11:28 AM
 */
public class AddGoSdkDialogForm extends DialogWrapper {

    private JPanel contentPanel;

    public AddGoSdkDialogForm(Project project, boolean canBeParent) {
        super(project, canBeParent);
    }

    public AddGoSdkDialogForm(Project project) {
        super(project);
    }

    public AddGoSdkDialogForm(boolean canBeParent) {
        super(canBeParent);
    }

    public AddGoSdkDialogForm(boolean canBeParent, boolean toolkitModalIfPossible) {
        super(canBeParent, toolkitModalIfPossible);
    }

    public AddGoSdkDialogForm(Component parent, boolean canBeParent) {
        super(parent, canBeParent);
    }

    {
        init();
    }

    @Override
    protected JComponent createCenterPanel() {
        return contentPanel;
    }

    protected Action[] createActions() {
        Action[] actions =
                new Action[]{
                        new AbstractAction(CommonBundle.getOkButtonText()) {
                            public void actionPerformed(ActionEvent e) {
//                                HttpConfigurable.getInstance().PROXY_LOGIN = panel.getLogin();
//                                HttpConfigurable.getInstance().setPlainProxyPassword(String.valueOf(panel.getPassword()));
//                                HttpConfigurable.getInstance().PROXY_AUTHENTICATION = true;
//                                HttpConfigurable.getInstance().KEEP_PROXY_PASSWORD = panel.isRememberPassword();

                                dispose();
                            }
                        },
                        new AbstractAction(CommonBundle.getCancelButtonText()) {
                            public void actionPerformed(ActionEvent e) {
//                                HttpConfigurable.getInstance().PROXY_AUTHENTICATION = false;
                                dispose();
                            }
                        }
                };
        actions[0].putValue(Action.DEFAULT, Boolean.TRUE.toString());
        return actions;
    }
}
