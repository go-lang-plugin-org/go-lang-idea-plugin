package ro.redeul.google.go.config.sdk;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.projectRoots.*;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.xmlb.XmlSerializer;
import org.jdom.Element;
import ro.redeul.google.go.GoIcons;
import ro.redeul.google.go.config.ui.GoSdkConfigurable;
import ro.redeul.google.go.util.GoSdkUtil;
import ro.redeul.google.go.util.GoUtil;

import javax.swing.*;

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
        String[] stringList = GoSdkUtil.testGoogleGoSdk(path);

        boolean isValid =
                stringList != null && stringList.length == 5 &&
                        (stringList[0].equalsIgnoreCase(path) || stringList[0].equalsIgnoreCase(path + "/"));

        if (isValid) {
            sdkData = new GoSdkData();
            sdkData.BINARY_PATH = stringList[1];
            sdkData.TARGET_OS = stringList[2];
            sdkData.TARGET_ARCH = stringList[3];
            sdkData.VERSION = stringList[4];
        }

        return isValid;
    }

    @Override
    public Icon getIcon() {
        return GoIcons.GO_ICON_16x16;
    }

    @Override
    public Icon getIconForAddAction() {
        return GoIcons.GO_ICON_16x16;
    }

    @Override
    public Icon getIconForExpandedTreeNode() {
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
    public boolean setupSdkPaths(final Sdk sdk, SdkModel sdkModel) {

        VirtualFile homeDirectory = sdk.getHomeDirectory();

        if (sdk.getSdkType() != this || homeDirectory == null ) {
            return false;
        }

        final VirtualFile librariesRoot = homeDirectory.findFileByRelativePath(String.format("pkg/%s_%s/", sdkData.TARGET_OS, sdkData.TARGET_ARCH));
        final VirtualFile sourcesRoot = homeDirectory.findFileByRelativePath("src/pkg/");

        if ( librariesRoot != null ) {
            librariesRoot.refresh(false, false);
        }
        if ( sourcesRoot != null ) {
            sourcesRoot.refresh(false, false);
        }

        final SdkModificator sdkModificator = sdk.getSdkModificator();
        ApplicationManager.getApplication().runWriteAction(new Runnable() {
            public void run() {
                sdkModificator.addRoot(librariesRoot, OrderRootType.CLASSES);
                sdkModificator.addRoot(sourcesRoot, OrderRootType.SOURCES);
            }
        });

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
