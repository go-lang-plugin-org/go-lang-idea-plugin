package ro.redeul.google.go.config.ui;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.projectRoots.AdditionalDataConfigurable;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkAdditionalData;
import ro.redeul.google.go.config.sdk.GoAppEngineSdkData;

import javax.swing.*;

import static java.lang.String.format;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 8/14/11
 * Time: 2:24 AM
 */
public class GoAppEngineSdkConfigurable implements AdditionalDataConfigurable {
    private JLabel labelVersion;
    private JLabel labelTargetSystem;
    private JLabel labelTimestamp;
    private JLabel labelAppLevel;
    private JPanel component;

    private Sdk sdk;

    public GoAppEngineSdkConfigurable() {
    }

    @Override
    public void setSdk(Sdk sdk) {
        this.sdk = sdk;
    }

    @Override
    public JComponent createComponent() {
        return component;
    }

    @Override
    public boolean isModified() {
        return false;
    }

    @Override
    public void apply() throws ConfigurationException {

    }

    @Override
    public void reset() {
        SdkAdditionalData data = sdk.getSdkAdditionalData();
        if (!(data instanceof GoAppEngineSdkData)) {
            return;
        }

        GoAppEngineSdkData sdkData = (GoAppEngineSdkData) data;

        labelVersion.setText(sdkData.VERSION_MAJOR);
        labelTimestamp.setText(sdkData.VERSION_MINOR);
        labelAppLevel.setText(sdkData.API_VERSIONS);

        if (sdkData.TARGET_OS != null && sdkData.TARGET_ARCH != null)
            labelTargetSystem.setText(
                format("%s-%s",
                       sdkData.TARGET_OS.getName(),
                       sdkData.TARGET_ARCH.getName()
                ));
    }

    @Override
    public void disposeUIResources() {

    }
}
