package ro.redeul.google.go.ide;

import ro.redeul.google.go.options.GoSettings;

import javax.swing.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

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

    public void enableShowHide(){
        componentPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentMoved(ComponentEvent e) {
                System.out.println("Moved " + e);
            }

            public void componentHidden(ComponentEvent ce) {
                System.out.println("Component hidden!");
            }

            @Override
            public void componentShown(ComponentEvent e) {
                System.out.println("Component shown");
            }
        });
    }

    public boolean isModified(GoProjectSettings.GoProjectSettingsBean settingsBean,
                              GoSettings goSettings) {
        if ( settingsBean.enableOptimizeImports != enableImportsOptimizer.isSelected() ) {
            return true;
        }

        if ( settingsBean.appendSysGoPath != enableAppendSysGoPath.isSelected() ) {
            return true;
        }

        if ( settingsBean.prependSysGoPath != enablePrependSysGoPath.isSelected() ) {
            return true;
        }

        if ( settingsBean.goFmtOnSave != goFmtOnSave.isSelected() ) {
            return true;
        }

        if ( settingsBean.goimportsOnSave != goimportsOnSave.isSelected() ) {
            return true;
        }

        return false;
    }

    public void apply(GoProjectSettings.GoProjectSettingsBean settingsBean) {
        settingsBean.appendSysGoPath = enableAppendSysGoPath.isSelected();
        settingsBean.prependSysGoPath = enablePrependSysGoPath.isSelected();

        settingsBean.enableOptimizeImports = enableImportsOptimizer.isSelected();

        settingsBean.goFmtOnSave = goFmtOnSave.isSelected();
        settingsBean.goimportsOnSave = goimportsOnSave.isSelected();
    }

    public void reset(GoProjectSettings.GoProjectSettingsBean settingsBean, GoSettings goSettings) {
        radioGOPATHproject.setSelected(!settingsBean.appendSysGoPath && !settingsBean.prependSysGoPath);
        enableAppendSysGoPath.setSelected(settingsBean.appendSysGoPath);
        enablePrependSysGoPath.setSelected(settingsBean.prependSysGoPath);

        enableImportsOptimizer.setSelected(settingsBean.enableOptimizeImports);

        doNothingOnSave.setSelected(!settingsBean.goFmtOnSave && !settingsBean.goimportsOnSave);
        goFmtOnSave.setSelected(settingsBean.goFmtOnSave);
        goimportsOnSave.setSelected(settingsBean.goimportsOnSave);
    }

}
