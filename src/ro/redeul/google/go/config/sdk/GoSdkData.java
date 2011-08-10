package ro.redeul.google.go.config.sdk;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.projectRoots.SdkAdditionalData;
import com.intellij.openapi.projectRoots.SdkModel;
import com.intellij.util.xmlb.XmlSerializerUtil;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Aug 19, 2010
 * Time: 8:07:05 AM
 */
public class GoSdkData implements SdkAdditionalData, PersistentStateComponent<GoSdkData> {

    public String HOME_PATH = "";
    public String BIN_PATH = "";

    public Os TARGET_OS = null;
    public Arch TARGET_ARCH = null;

    public String VERSION_MAJOR = "";
    public String VERSION_MINOR = "";

    public GoSdkData() {
    }

    public GoSdkData(String homePath, String binaryPath, Os targetOs, Arch targetArch, String versionMajor, String versionMinor) {
        this.HOME_PATH = homePath;
        this.BIN_PATH = binaryPath;
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

    public void checkValid(SdkModel sdkModel) throws ConfigurationException {

    }

    public GoSdkData getState() {
        return this;
    }

    public void loadState(GoSdkData state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    public enum Os {
        Windows("windows"), Linux("linux"), Darwin("darwin"), FreeBsd("freebsd");

        String name;

        Os(String name) {
            this.name = name;
        }

        public static Os fromString(String name) {

            if ( name == null )
                return null;

            name = name.toLowerCase();

            for (Os os : Os.values()) {
                if ( os.getName().equalsIgnoreCase(name) ) {
                    return os;
                }
            }

            return null;
        }

        public String getName() {
            return name;
        }
    }

    public enum Arch {
        _386("386"), _amd64("amd64"), _arm("arm");

        String name;

        Arch(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public static Arch fromString(String string) {

            if ( string == null )
                return null;

            for (Arch arch : Arch.values()) {
                if  (arch.getName().equalsIgnoreCase(string) ) {
                    return arch;
                }
            }

            return null;
        }
    }
}
