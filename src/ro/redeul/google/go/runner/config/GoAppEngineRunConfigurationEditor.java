package ro.redeul.google.go.runner.config;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.runner.GoAppEngineApplicationConfiguration;

import javax.swing.*;

/**
 * Author: Jhonny Everson
 * <p/>
 * Date: Aug 19, 2010
 * Time: 2:59:34 PM
 */
public class GoAppEngineRunConfigurationEditor extends SettingsEditor<GoAppEngineApplicationConfiguration> {

    @Override
    protected void resetEditorFrom(GoAppEngineApplicationConfiguration s) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void applyEditorTo(GoAppEngineApplicationConfiguration s) throws ConfigurationException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @NotNull
    @Override
    protected JComponent createEditor() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void disposeEditor() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
