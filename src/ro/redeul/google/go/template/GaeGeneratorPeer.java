package ro.redeul.google.go.template;

import com.intellij.ide.util.projectWizard.SettingsStep;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.platform.WebProjectGenerator;
import com.intellij.util.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.GoBundle;
import ro.redeul.google.go.config.sdk.GoAppEngineSdkData;
import ro.redeul.google.go.config.sdk.GoAppEngineSdkType;
import ro.redeul.google.go.options.GoSettings;
import ro.redeul.google.go.sdk.GoSdkUtil;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class GaeGeneratorPeer implements WebProjectGenerator.GeneratorPeer<GoAppEngineSdkData> {
    private JPanel myMainPanel;
    private TextFieldWithBrowseButton mySdkPath;
    private JButton getFromSystem;
    private JLabel labelSdkVersion;
    private JLabel labelSdkTarget;
    private JLabel labelBinariesPath;
    private TextFieldWithBrowseButton gopathPath;

    public GaeGeneratorPeer() {
        mySdkPath.getButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FileChooser
                        .chooseFile(FileChooserDescriptorFactory.createSingleFolderDescriptor(), null, mySdkPath, null, new Consumer<VirtualFile>() {
                            @Override
                            public void consume(@NotNull VirtualFile file) {
                                mySdkPath.setText(FileUtil.toSystemDependentName(file.getPath()));
                            }
                        });
            }
        });

        gopathPath.getButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FileChooser
                        .chooseFile(FileChooserDescriptorFactory.createSingleFolderDescriptor(), null, gopathPath, null, new Consumer<VirtualFile>() {
                            @Override
                            public void consume(@NotNull VirtualFile file) {
                                gopathPath.setText(FileUtil.toSystemDependentName(file.getPath()));
                            }
                        });
            }
        });

        mySdkPath.setText(FileUtil.toSystemDependentName(GoSettings.getInstance().goRoot));
        if (mySdkPath.getText().equals("")) {
            mySdkPath.setText(GoSdkUtil.getGaeExePath());
        }

        gopathPath.setText(FileUtil.toSystemDependentName(GoSettings.getInstance().goPath));
        if (gopathPath.getText().equals("")) {
            gopathPath.setText(GoSdkUtil.getSysGoPathPath().split(File.pathSeparator)[0]);
        }
    }

    @NotNull
    @Override
    public JComponent getComponent() {
        return myMainPanel;
    }

    @Override
    public void buildUI(@NotNull SettingsStep settingsStep) {
        settingsStep.addSettingsField(GoBundle.message("go.sdk.configure.title"), mySdkPath);
    }

    @NotNull
    @Override
    public GoAppEngineSdkData getSettings() {
        GoAppEngineSdkData sdkData = getSdkData();
        return sdkData != null ? sdkData : new GoAppEngineSdkData();
    }

    @Nullable
    @Override
    public ValidationInfo validate() {
        if (getSdkData() == null) {
            return new ValidationInfo(GoBundle.message("error.invalid.sdk.path", mySdkPath.getText()));
        }

        String goSdkPath = mySdkPath.getText();

        GoAppEngineSdkType goSdk = new GoAppEngineSdkType();
        if (!goSdk.isValidSdkHome(goSdkPath)) {
            return new ValidationInfo(GoBundle.message("error.invalid.sdk.path", mySdkPath.getText()));
        }

        GoAppEngineSdkData goSdkData = GoSdkUtil.testGoAppEngineSdk(goSdkPath);

        if (goSdkData == null) {
            return new ValidationInfo(GoBundle.message("error.invalid.sdk.path", mySdkPath.getText()));
        }

        goSdkData.GO_GOPATH_PATH = gopathPath.getText();

        labelSdkVersion.setText(goSdkData.VERSION_MAJOR);
        if (goSdkData.TARGET_OS != null && goSdkData.TARGET_ARCH != null) {
            labelSdkTarget.setText(
                    String.format("%s-%s (%s, %s)",
                            goSdkData.TARGET_OS.getName(),
                            goSdkData.TARGET_ARCH.getName(),
                            GoSdkUtil.getCompilerName(goSdkData.TARGET_ARCH),
                            GoSdkUtil.getLinkerName(goSdkData.TARGET_ARCH)
                    ));
        } else {
            labelSdkTarget.setText("Unknown target");
        }

        labelBinariesPath.setText(goSdkData.GOAPP_BIN_PATH);

        return null;
    }

    @Nullable
    private GoAppEngineSdkData getSdkData() {
        return GoSdkUtil.testGoAppEngineSdk(FileUtil.toSystemIndependentName(mySdkPath.getText()));
    }

    @Override
    public boolean isBackgroundJobRunning() {
        return false;
    }

    @Override
    public void addSettingsStateListener(@NotNull WebProjectGenerator.SettingsStateListener listener) {
    }
}
