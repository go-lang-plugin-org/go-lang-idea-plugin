package ro.redeul.google.go.ide;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.*;

import com.intellij.openapi.options.ConfigurationException;
import ro.redeul.google.go.options.GoSettings;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 5/29/11
 * Time: 11:33 AM
 */
public class GoConfigurableForm {

    public JPanel componentPanel;

    private JRadioButton internalBuildSystemRadioButton;
    private JRadioButton makefileBasedRadioButton;
    private JCheckBox enableImportsOptimizer;
    private JCheckBox enableOnTheFlyImportOptimization;
    private JRadioButton goInstallRadioButton;

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

        if (goSettings.OPTIMIZE_IMPORTS_ON_THE_FLY != enableOnTheFlyImportOptimization.isSelected()) {
            return true;
        }

        if ( settingsBean.enableOptimizeImports != enableImportsOptimizer.isSelected() ) {
            return true;
        }

        switch (settingsBean.BUILD_SYSTEM_TYPE) {
            case Internal:
                return !internalBuildSystemRadioButton.isSelected();
            case Makefile:
                return !makefileBasedRadioButton.isSelected();
            case Install:
                return !goInstallRadioButton.isSelected();
        }

        return false;
    }

    public void apply(GoProjectSettings.GoProjectSettingsBean settingsBean, GoSettings goSettings) throws ConfigurationException {
        if ( internalBuildSystemRadioButton.isSelected() ) {
            settingsBean.BUILD_SYSTEM_TYPE = GoProjectSettings.BuildSystemType.Internal;
        } else if ( makefileBasedRadioButton.isSelected() ) {
            settingsBean.BUILD_SYSTEM_TYPE = GoProjectSettings.BuildSystemType.Makefile;
        } else if ( goInstallRadioButton.isSelected() ) {
            settingsBean.BUILD_SYSTEM_TYPE = GoProjectSettings.BuildSystemType.Install;
        }

        settingsBean.enableOptimizeImports = enableImportsOptimizer.isSelected();
        goSettings.OPTIMIZE_IMPORTS_ON_THE_FLY = enableOnTheFlyImportOptimization.isSelected();
    }

    public void reset(GoProjectSettings.GoProjectSettingsBean settingsBean, GoSettings goSettings) {
        switch (settingsBean.BUILD_SYSTEM_TYPE) {
            case Internal:
                internalBuildSystemRadioButton.setSelected(true);
                break;
            case Makefile:
                makefileBasedRadioButton.setSelected(true);
                break;
            case Install:
                goInstallRadioButton.setSelected(true);
                break;
        }

        enableOnTheFlyImportOptimization.setSelected(goSettings.OPTIMIZE_IMPORTS_ON_THE_FLY);
        enableImportsOptimizer.setSelected(settingsBean.enableOptimizeImports);
    }

}
