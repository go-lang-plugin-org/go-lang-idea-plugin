package ro.redeul.google.go.ide;

import javax.swing.*;

import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.openapi.project.Project;

/**
 * User: mtoader
 * Date: 1/2/11
 * Time: 11:06 AM
 */
public class GoModuleWizardStep extends ModuleWizardStep {
    private JCheckBox checkBox1;
    private JPanel panel1;
    private JTextField textField1;

    public GoModuleWizardStep(GoModuleBuilder moduleBuilder, Project project) {
        //To change body of created methods use File | Settings | File Templates.
    }

    @Override
    public JComponent getComponent() {
        return panel1;
    }

    @Override
    public void updateDataModel() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
