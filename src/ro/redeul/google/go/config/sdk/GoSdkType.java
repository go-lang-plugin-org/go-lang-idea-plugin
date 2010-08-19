package ro.redeul.google.go.config.sdk;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.projectRoots.*;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.util.xmlb.XmlSerializer;
import org.jdom.Element;
import ro.redeul.google.go.GoIcons;
import ro.redeul.google.go.config.ui.GoSdkConfigurable;
import ro.redeul.google.go.util.GoSdkUtil;
import ro.redeul.google.go.util.GoUtil;

import javax.swing.*;
import java.io.File;
import java.util.Collection;
import java.util.List;

public class GoSdkType extends SdkType {

    GoSdkData sdkData;

    public GoSdkType() {
        super("Google Go SDK");
    }

    @Override
    public String suggestHomePath() {
        return GoUtil.resolveGoogleGoHomePath();
    }

    @Override
    public boolean isValidSdkHome(String path) {
        List<String> stringList = GoSdkUtil.testGoogleGoSdk(path);

        boolean isValid =
                stringList != null && stringList.size() == 5 &&
                        (stringList.get(0).equalsIgnoreCase(path) || stringList.get(0).equalsIgnoreCase(path + "/"));

        if (isValid) {
            sdkData = new GoSdkData();
            sdkData.BINARY_PATH = stringList.get(1);
            sdkData.TARGET_OS = stringList.get(2);
            sdkData.TARGET_ARCH = stringList.get(3);
            sdkData.VERSION = stringList.get(4);
        }

        return isValid;
    }

    @Override
    public Icon getIcon() {
        return GoIcons.GO_ICON_16x16;
    }

    @Override
    public String suggestSdkName(String currentSdkName, String sdkHome) {
        return "Go" + (sdkData.VERSION != null && sdkData.VERSION.trim().length() > 0 ? " (" + sdkData.VERSION + ")" : "");
    }

    @Override
    public AdditionalDataConfigurable createAdditionalDataConfigurable(SdkModel sdkModel, SdkModificator sdkModificator) {
        GoSdkConfigurable goConfigurable = new GoSdkConfigurable(sdkModel, sdkModificator);

        sdkModel.addListener(new SdkModel.Listener() {
            public void sdkAdded(Sdk sdk) {
//              if (sdk.getSdkType().equals(JavaSdk.getInstance())) {
//                goConfigurable.addJavaSdk(sdk);
//              }
            }

            public void beforeSdkRemove(Sdk sdk) {
//              if (sdk.getSdkType().equals(JavaSdk.getInstance())) {
//                goConfigurable.removeJavaSdk(sdk);
//              }
            }

            public void sdkChanged(Sdk sdk, String previousName) {
//              if (sdk.getSdkType().equals(JavaSdk.getInstance())) {
//                goConfigurable.updateJavaSdkList(sdk, previousName);
//              }
            }

            public void sdkHomeSelected(final Sdk sdk, final String newSdkHome) {
//              if (sdk.getSdkType() instanceof GoSdkType) {
//                goConfigurable.internalJdkUpdate(sdk);
//              }
            }
        });

        return goConfigurable;
    }

    @Override
    public boolean setupSdkPaths(Sdk sdk, SdkModel sdkModel) {

        if (sdk.getSdkType() != this) {
            return false;
        }

        Collection<File> googleGoPackages = GoSdkUtil.findGoogleSdkPackages(sdk.getHomePath());

        final SdkModificator sdkModificator = sdk.getSdkModificator();
        for (final File packageRoot : googleGoPackages) {

            if (packageRoot.exists() && packageRoot.isDirectory()) {
                ApplicationManager.getApplication().runWriteAction(new Runnable() {
                    public void run() {
                        sdkModificator.addRoot(LocalFileSystem.getInstance().refreshAndFindFileByIoFile(packageRoot), OrderRootType.CLASSES);
                        sdkModificator.addRoot(LocalFileSystem.getInstance().refreshAndFindFileByIoFile(packageRoot), OrderRootType.SOURCES);
                    }
                });
            }
        }

        sdkModificator.setVersionString(sdkData.VERSION);
        sdkModificator.setSdkAdditionalData(sdkData);
        sdkModificator.commitChanges();
        return true;
    }

    @Override
    public SdkAdditionalData loadAdditionalData(Sdk currentSdk, Element additional) {
        return XmlSerializer.deserialize(additional, GoSdkData.class);
    }

    @Override
    public void saveAdditionalData(SdkAdditionalData additionalData, Element additional) {
        if (additionalData instanceof GoSdkData) {
            XmlSerializer.serializeInto(additionalData, additional);
        }
    }

    @Override
    public String getPresentableName() {
        return "Go SDK";
    }
}
