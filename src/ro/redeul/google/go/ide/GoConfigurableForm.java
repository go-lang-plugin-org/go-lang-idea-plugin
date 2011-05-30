package ro.redeul.google.go.ide;

import com.intellij.openapi.options.ConfigurationException;

import javax.swing.*;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 5/29/11
 * Time: 11:33 AM
 */
public class GoConfigurableForm {

    public JPanel componentPanel;

    public JRadioButton internalBuildSystemRadioButton;
    public JRadioButton makefileBasedRadioButton;

    public boolean isModified(GoProjectSettings.GoProjectSettingsBean settingsBean) {

        switch (settingsBean.BUILD_SYSTEM_TYPE) {
            case Internal:
                return !internalBuildSystemRadioButton.isSelected();
            case Makefile:
                return !makefileBasedRadioButton.isSelected();
        }

        return false;
    }

    public void apply(GoProjectSettings.GoProjectSettingsBean settingsBean) throws ConfigurationException {
        if ( internalBuildSystemRadioButton.isSelected() ) {
            settingsBean.BUILD_SYSTEM_TYPE = GoProjectSettings.BuildSystemType.Internal;
        } else if ( makefileBasedRadioButton.isSelected() ) {
            settingsBean.BUILD_SYSTEM_TYPE = GoProjectSettings.BuildSystemType.Makefile;
        }
    }

    public void reset(GoProjectSettings.GoProjectSettingsBean settingsBean) {
        switch (settingsBean.BUILD_SYSTEM_TYPE) {
            case Internal:
                internalBuildSystemRadioButton.setSelected(true);
                break;
            case Makefile:
                makefileBasedRadioButton.setSelected(true);
                break;
        }
    }
}
