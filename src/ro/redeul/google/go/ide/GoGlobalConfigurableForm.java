package ro.redeul.google.go.ide;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.util.Comparing;
import ro.redeul.google.go.sdk.GoSdkUtil;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Author: Florin Patan <florinpatan@gmail.com>
 */
public class GoGlobalConfigurableForm {
   public JPanel componentPanel;
    private TextFieldWithBrowseButton goPath;
    private JButton importSysGoPath;
    private final GoGlobalSettings goGlobalSettings = GoGlobalSettings.getInstance();

    GoGlobalConfigurableForm() {
        goPath.addBrowseFolderListener("GOPATH directory", "GOPATH directory",
                null, new FileChooserDescriptor(false, true, false, false, false, false));

        importSysGoPath.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goPath.setText(GoSdkUtil.getSysGoPathPath().split(File.pathSeparator)[0]);
            }
        });
    }

    public void reset() {
        goPath.setText(goGlobalSettings.getGoPath());
    }

    public boolean isModified() {
        return ! (Comparing.equal(goPath.getText(), goGlobalSettings.getGoPath()));
    }

    public void apply() {
        if (!(new File(goPath.getText()).exists())) {
            Messages.showErrorDialog("Error while saving your settings. \nGOPATH doesn't exists.", "Error on Google Go Plugin");
            return;
        }

        goGlobalSettings.setGoPath(goPath.getText());
    }
}
