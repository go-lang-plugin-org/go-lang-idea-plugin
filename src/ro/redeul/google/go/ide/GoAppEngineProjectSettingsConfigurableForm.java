package ro.redeul.google.go.ide;

import com.intellij.openapi.util.Comparing;

import javax.swing.*;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 8/15/11
 * Time: 9:56 AM
 */
public class GoAppEngineProjectSettingsConfigurableForm {
   public JPanel componentPanel;
    private JTextField textEmail;
    private JTextField textPassword;

    private final GoAppEngineSettings appEngineSettings = GoAppEngineSettings.getInstance();

    public void reset() {
        textEmail.setText(appEngineSettings.getEmail());
        textPassword.setText(appEngineSettings.getPassword());
    }

    public boolean isModified() {
        return ! (Comparing.equal( textEmail.getText(), appEngineSettings.getEmail()) &&
                        Comparing.equal(textPassword.getText(), appEngineSettings.getPassword()));
    }

    public void apply() {
        appEngineSettings.setEmail(textEmail.getText());
        appEngineSettings.setPassword(textPassword.getText());
    }
}
