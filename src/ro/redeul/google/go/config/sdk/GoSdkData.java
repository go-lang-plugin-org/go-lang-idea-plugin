package ro.redeul.google.go.config.sdk;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.projectRoots.SdkAdditionalData;
import com.intellij.openapi.projectRoots.SdkModel;
import com.intellij.util.xmlb.XmlSerializerUtil;

public class GoSdkData implements SdkAdditionalData, PersistentStateComponent<GoSdkData> {

    public final static int LATEST_VERSION = 2;

    public String GO_HOME_PATH = "";
    public String GO_BIN_PATH = "";

    public GoTargetOs TARGET_OS = null;
    public GoTargetArch TARGET_ARCH = null;

    public String VERSION_MAJOR = "";
    public String VERSION_MINOR = "";

    public int version = 0;

    public GoSdkData() {
    }

    public GoSdkData(String homePath, String binaryPath, GoTargetOs targetOs, GoTargetArch targetArch, String versionMajor, String versionMinor) {
        this.GO_HOME_PATH = homePath;
        this.GO_BIN_PATH = binaryPath;
        this.TARGET_OS = targetOs;
        this.TARGET_ARCH = targetArch;
        this.VERSION_MAJOR = versionMajor;
        this.VERSION_MINOR = versionMinor;
    }

    @SuppressWarnings({"CloneDoesntCallSuperClone"})
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public void checkValid() throws ConfigurationException {
        if (version != GoSdkData.LATEST_VERSION) {
            throw new ConfigurationException(
                "SDK configuration needs to be upgraded");
        }
    }

    public void checkValid(SdkModel sdkModel) throws ConfigurationException {
        checkValid();
    }

    public GoSdkData getState() {
        return this;
    }

    public void loadState(GoSdkData state) {
        XmlSerializerUtil.copyBean(state, this);
    }

}
