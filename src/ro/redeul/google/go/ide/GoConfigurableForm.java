package ro.redeul.google.go.ide;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import ro.redeul.google.go.options.GoSettings;
import ro.redeul.google.go.sdk.GoSdkUtil;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 5/29/11
 * Time: 11:33 AM
 */
public class GoConfigurableForm {

    public JPanel componentPanel;

    private JCheckBox enableImportsOptimizer;
    private JRadioButton radioGOPATHproject;
    private JRadioButton enablePrependSysGoPath;
    private JRadioButton enableAppendSysGoPath;
    private JRadioButton doNothingOnSave;
    private JRadioButton goFmtOnSave;
    private JRadioButton goimportsOnSave;
    private TextFieldWithBrowseButton goimportsPath;

    public GoConfigurableForm() {
        goimportsPath.addBrowseFolderListener("goimports directory", "Select the goimports directory",
                null, new FileChooserDescriptor(false, true, false, false, false, false));

        doNothingOnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goimportsPath.setEnabled(goimportsOnSave.isSelected());
            }
        });

        goFmtOnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goimportsPath.setEnabled(goimportsOnSave.isSelected());
            }
        });

        goimportsOnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goimportsPath.setEnabled(goimportsOnSave.isSelected());
            }
        });
    }

    public boolean isModified(GoProjectSettings.GoProjectSettingsBean settingsBean,
                              GoSettings goSettings) {
        if (settingsBean.enableOptimizeImports != enableImportsOptimizer.isSelected()) {
            return true;
        }

        if (settingsBean.useGoPath != enableAppendSysGoPath.isSelected()) {
            return true;
        }

        if (settingsBean.prependGoPath != enablePrependSysGoPath.isSelected()) {
            return true;
        }

        if (settingsBean.goFmtOnSave != goFmtOnSave.isSelected()) {
            return true;
        }

        if (settingsBean.goimportsOnSave != goimportsOnSave.isSelected()) {
            return true;
        }

        if (settingsBean.goimportsPath.equals(goimportsPath.getText())) {
            return true;
        }

        return false;
    }

    public void apply(GoProjectSettings.GoProjectSettingsBean settingsBean) throws ConfigurationException {
        String goimportsExecName = File.separator + "goimports";
        if (GoSdkUtil.isHostOsWindows()) {
            goimportsExecName += ".exe";
        }

        if (goimportsOnSave.isSelected () &&
                !(new File(goimportsPath.getText() + goimportsExecName).exists())) {
            throw new ConfigurationException("goimports could not be found at the desired location");
        }

        settingsBean.useGoPath = enableAppendSysGoPath.isSelected();
        settingsBean.prependGoPath = enablePrependSysGoPath.isSelected();

        settingsBean.enableOptimizeImports = enableImportsOptimizer.isSelected();

        settingsBean.goFmtOnSave = goFmtOnSave.isSelected();
        settingsBean.goimportsOnSave = goimportsOnSave.isSelected();
        settingsBean.goimportsPath = goimportsPath.getText();
    }

    public void reset(GoProjectSettings.GoProjectSettingsBean settingsBean, GoSettings goSettings) {
        radioGOPATHproject.setSelected(!settingsBean.useGoPath && !settingsBean.prependGoPath);
        enableAppendSysGoPath.setSelected(settingsBean.useGoPath);
        enablePrependSysGoPath.setSelected(settingsBean.prependGoPath);

        enableImportsOptimizer.setSelected(settingsBean.enableOptimizeImports);

        doNothingOnSave.setSelected(!settingsBean.goFmtOnSave && !settingsBean.goimportsOnSave);
        goFmtOnSave.setSelected(settingsBean.goFmtOnSave);
        goimportsOnSave.setSelected(settingsBean.goimportsOnSave);
        goimportsPath.setEnabled(settingsBean.goimportsOnSave);
        goimportsPath.setText(settingsBean.goimportsPath);
    }

}
