package ro.redeul.google.go.config.sdk;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.projectRoots.SdkAdditionalData;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.sdk.GoSdkUtil;

import java.io.File;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 8/10/11
 * Time: 8:14 AM
 */
public class GoAppEngineSdkData implements SdkAdditionalData, PersistentStateComponent<GoAppEngineSdkData> {

    public final static int LATEST_VERSION = 4;

    public String SDK_HOME_PATH = "";
    public String GOAPP_BIN_PATH = "";
    public String GO_HOME_PATH = "";
    public String GO_GOPATH_PATH = "";

    public GoTargetOs TARGET_OS = null;
    public GoTargetArch TARGET_ARCH = null;

    public String VERSION_MAJOR = "";
    public String VERSION_MINOR = "";
    public String API_VERSIONS = "";

    public int version = 0;

    public GoAppEngineSdkData() {
    }

    public GoAppEngineSdkData(String sdkHomePath, String homePath, String goPath, GoTargetOs TARGET_OS, GoTargetArch TARGET_ARCH, String VERSION_MAJOR, String VERSION_MINOR) {
        this.SDK_HOME_PATH = sdkHomePath;
        this.GOAPP_BIN_PATH = GoSdkUtil.getGoAppBinPath(sdkHomePath);
        this.GO_HOME_PATH = homePath;
        this.GO_GOPATH_PATH = goPath;
        this.TARGET_OS = TARGET_OS;
        this.TARGET_ARCH = TARGET_ARCH;
        this.VERSION_MAJOR = VERSION_MAJOR;
        this.VERSION_MINOR = VERSION_MINOR;
        this.API_VERSIONS = "";
    }

    @SuppressWarnings({"CloneDoesntCallSuperClone"})
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public void loadState(GoAppEngineSdkData state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    public GoAppEngineSdkData getState() {
        return this;
    }

    public void checkValid() throws ConfigurationException {
        if (version != GoAppEngineSdkData.LATEST_VERSION) {
            throw new ConfigurationException("SDK configuration needs to be upgraded");
        }
    }

}
