package ro.redeul.google.go.config.sdk;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.projectRoots.SdkAdditionalData;
import com.intellij.openapi.projectRoots.SdkModel;
import com.intellij.util.xmlb.XmlSerializerUtil;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Aug 19, 2010
 * Time: 8:07:05 AM
 * To change this template use File | Settings | File Templates.
 */
public class GoSdkData implements SdkAdditionalData, PersistentStateComponent<GoSdkData> {

    public String TARGET_ARCH = "";
    public String TARGET_OS = "";    
    public String BINARY_PATH = "";
    public String VERSION = "";

    public GoSdkData() {
    }

    @SuppressWarnings({"CloneDoesntCallSuperClone"})
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public void checkValid(SdkModel sdkModel) throws ConfigurationException {

    }

    public GoSdkData getState() {
        return this;
    }

    public void loadState(GoSdkData state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}
