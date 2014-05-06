package ro.redeul.google.go.config.sdk;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.projectRoots.SdkAdditionalData;
import com.intellij.util.xmlb.XmlSerializerUtil;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Aug 19, 2010
 * Time: 8:07:05 AM
 */
public class GoSdkData implements SdkAdditionalData, PersistentStateComponent<GoSdkData> {

    public final static int LATEST_VERSION = 4;

    public String GO_EXEC = "go";
    public String GO_GOROOT_PATH = "";
    public String GO_BIN_PATH = "";
    public String GO_GOPATH_PATH = "";

    public GoTargetOs TARGET_OS = null;
    public GoTargetArch TARGET_ARCH = null;

    public String VERSION_MAJOR = "";
    public String VERSION_MINOR = "";

    public int version = 0;

    public GoSdkData() {
    }

    public GoSdkData(String homePath, String binaryPath, String goPath, GoTargetOs targetOs, GoTargetArch targetArch, String versionMajor, String versionMinor) {
        this.GO_GOROOT_PATH = homePath;
        this.GO_BIN_PATH = binaryPath;
        this.GO_GOPATH_PATH = goPath;
        this.TARGET_OS = targetOs;
        this.TARGET_ARCH = targetArch;
        this.VERSION_MAJOR = versionMajor;
        this.VERSION_MINOR = versionMinor;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public void checkValid() throws ConfigurationException {
        if (version != GoSdkData.LATEST_VERSION) {
            throw new ConfigurationException("SDK configuration needs to be upgraded");
        }
    }

    public GoSdkData getState() {
        return this;
    }

    public void loadState(GoSdkData state) {
        XmlSerializerUtil.copyBean(state, this);
    }

}
