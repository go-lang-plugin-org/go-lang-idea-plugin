package ro.redeul.google.go.ide;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.util.Comparing;

import javax.swing.*;
import java.io.File;

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
    private TextFieldWithBrowseButton gaePath;
    private final GoAppEngineSettings appEngineSettings = GoAppEngineSettings.getInstance();

    GoAppEngineProjectSettingsConfigurableForm() {
        gaePath.addBrowseFolderListener("Go App Engine directory", "Go App Engine directory",
                null, new FileChooserDescriptor(false, true, false, false, false, false));
    }

    public void reset() {
        textEmail.setText(appEngineSettings.getEmail());
        textPassword.setText(appEngineSettings.getPassword());
        gaePath.setText(appEngineSettings.getGaePath());
    }

    public boolean isModified() {
        return ! (Comparing.equal( textEmail.getText(), appEngineSettings.getEmail()) &&
                        Comparing.equal(textPassword.getText(), appEngineSettings.getPassword()) &&
                        Comparing.equal(gaePath.getText(), appEngineSettings.getGaePath()));
    }

    public void apply() {
        appEngineSettings.setEmail(textEmail.getText());
        appEngineSettings.setPassword(textPassword.getText());
        appEngineSettings.setGaePath(gaePath.getText());

        if (!(new File(gaePath.getText()).exists())) {
            Messages.showErrorDialog("Error while saving your settings. \nGo App Engine path doesn't exists.", "Error on Go App Engine Plugin");
        }
    }
}
