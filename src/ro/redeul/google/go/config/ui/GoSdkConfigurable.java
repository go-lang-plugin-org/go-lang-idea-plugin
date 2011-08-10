package ro.redeul.google.go.config.ui;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.projectRoots.*;
import ro.redeul.google.go.config.sdk.GoSdkData;
import ro.redeul.google.go.sdk.GoSdkUtil;

import javax.swing.*;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Aug 19, 2010
 * Time: 6:37:17 AM
 */
public class GoSdkConfigurable implements AdditionalDataConfigurable {
    private JPanel component;
    private JLabel labelSdkVersion;
    private JLabel labelSdkTarget;
    private JLabel labelBinariesPath;

    private SdkModel model;
    private SdkModificator modifier;
    private Sdk sdk;

    public GoSdkConfigurable(final SdkModel model, final SdkModificator modifier) {
        this.model = model;
        this.modifier = modifier;
    }

    public void setSdk(Sdk sdk) {
        this.sdk = sdk;
    }

    public void removeSdk(Sdk sdk) {
        this.sdk = null;
    }

    public JComponent createComponent() {
        return component;
    }

    public boolean isModified() {
        return false;
    }

    public void apply() throws ConfigurationException {
        
    }

    public void reset() {
        SdkAdditionalData data = sdk.getSdkAdditionalData();
        if ( ! (data instanceof GoSdkData)) {
            return;
        }

        GoSdkData sdkData = (GoSdkData) data;

        labelSdkVersion.setText(sdkData.VERSION_MAJOR);
        labelSdkTarget.setText(String.format("%s-%s (%s, %s)",
                sdkData.TARGET_OS.getName(), sdkData.TARGET_ARCH.getName(),
                GoSdkUtil.getCompilerName(sdkData.TARGET_OS, sdkData.TARGET_ARCH),
                GoSdkUtil.getLinkerName(sdkData.TARGET_OS, sdkData.TARGET_ARCH)
        ));

        labelBinariesPath.setText(sdkData.BIN_PATH);
    }

    public void disposeUIResources() {
    }
}
