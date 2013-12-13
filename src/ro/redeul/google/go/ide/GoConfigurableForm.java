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
    private JCheckBox enableOnTheFlyImportOptimization;

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

        return false;
    }

    public void apply(GoProjectSettings.GoProjectSettingsBean settingsBean, GoSettings goSettings) {
        settingsBean.enableOptimizeImports = enableImportsOptimizer.isSelected();
        goSettings.OPTIMIZE_IMPORTS_ON_THE_FLY = enableOnTheFlyImportOptimization.isSelected();
    }

    public void reset(GoProjectSettings.GoProjectSettingsBean settingsBean, GoSettings goSettings) {
        enableOnTheFlyImportOptimization.setSelected(goSettings.OPTIMIZE_IMPORTS_ON_THE_FLY);
        enableImportsOptimizer.setSelected(settingsBean.enableOptimizeImports);
    }

}
