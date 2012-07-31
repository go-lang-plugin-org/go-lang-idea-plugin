package ro.redeul.google.go.config.sdk;

import javax.swing.*;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.ProjectBundle;
import com.intellij.openapi.projectRoots.AdditionalDataConfigurable;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkAdditionalData;
import com.intellij.openapi.projectRoots.SdkModel;
import com.intellij.openapi.projectRoots.SdkModificator;
import com.intellij.openapi.projectRoots.SdkType;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.xmlb.XmlSerializer;
import org.jdom.Element;
import ro.redeul.google.go.GoBundle;
import ro.redeul.google.go.GoIcons;
import ro.redeul.google.go.config.ui.GoAppEngineSdkConfigurable;
import ro.redeul.google.go.sdk.GoSdkUtil;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 8/9/11
 * Time: 5:08 PM
 */
public class GoAppEngineSdkType extends SdkType {

    GoAppEngineSdkData sdkData;

    public GoAppEngineSdkType() {
        super("Google Go App Engine SDK");
    }

    public GoAppEngineSdkData getSdkData() {
        return sdkData;
    }

    public void setSdkData(GoAppEngineSdkData sdkData) {
        this.sdkData = sdkData;
    }

    @Override
    public String suggestHomePath() {
        return GoSdkUtil.resolvePotentialGoogleGoAppEngineHomePath();
    }

    @Override
    public boolean isValidSdkHome(String path) {
        sdkData = GoSdkUtil.testGoAppEngineSdk(path);
        return sdkData != null;
    }

    @Override
    public String suggestSdkName(String currentSdkName, String sdkHome) {
        StringBuilder builder = new StringBuilder();

        builder.append("Go App Engine");
        if ( getSdkData() != null ) {
            builder.append(" ").append(getSdkData().VERSION_MAJOR);
        }

        if ( sdkHome.matches(".*bundled/go-appengine-sdk/?$") ) {
            builder.append(" (bundled)");
        }

        return builder.toString();
    }

    @Override
    public AdditionalDataConfigurable createAdditionalDataConfigurable(SdkModel sdkModel, SdkModificator sdkModificator) {
        return new GoAppEngineSdkConfigurable(sdkModel, sdkModificator);
    }

    @Override
    public String getVersionString(Sdk sdk) {
        return getVersionString(sdk.getHomePath());
    }

    @Override
    public String getVersionString(String sdkHome) {
        if (!isValidSdkHome(sdkHome))
            return super.getVersionString(sdkHome);

        return sdkData.VERSION_MINOR;
    }

    @Override
    public void setupSdkPaths(Sdk sdk) {

        VirtualFile homeDirectory = sdk.getHomeDirectory();

        if (sdk.getSdkType() != this || homeDirectory == null) {
            return;
        }

        String path = homeDirectory.getPath();

        GoAppEngineSdkData sdkData = GoSdkUtil.testGoAppEngineSdk(path);

        if ( sdkData == null )
            return;

        final VirtualFile librariesRoot =
                homeDirectory.findFileByRelativePath(String.format("goroot/pkg/%s_%s/", sdkData.TARGET_OS.getName(), sdkData.TARGET_ARCH.getName()));

        final VirtualFile sourcesRoot = homeDirectory.findFileByRelativePath("goroot/src/pkg/");

        if (librariesRoot != null) {
            librariesRoot.refresh(false, false);
        }
        if (sourcesRoot != null) {
            sourcesRoot.refresh(false, false);
        }

        final SdkModificator sdkModificator = sdk.getSdkModificator();
        ApplicationManager.getApplication().runWriteAction(new Runnable() {
            public void run() {
                sdkModificator.addRoot(librariesRoot, OrderRootType.CLASSES);
                sdkModificator.addRoot(sourcesRoot, OrderRootType.CLASSES);
                sdkModificator.addRoot(sourcesRoot, OrderRootType.SOURCES);
            }
        });

        sdkModificator.setVersionString(String.format("%s %s", sdkData.VERSION_MAJOR, sdkData.VERSION_MINOR));
        sdkModificator.setSdkAdditionalData(sdkData);
        sdkModificator.commitChanges();
    }

    @Override
    public FileChooserDescriptor getHomeChooserDescriptor() {
        FileChooserDescriptor descriptor = new FileChooserDescriptor(true, true, false, false, false, false) {
            public void validateSelectedFiles(VirtualFile[] files) throws Exception {
                if (files.length != 0) {
                    final String selectedPath = files[0].getPath();
                    boolean valid = isValidSdkHome(selectedPath);
                    if (!valid) {
                        valid = isValidSdkHome(adjustSelectedSdkHome(selectedPath));
                        if (!valid) {
                            String message = files[0].isDirectory()
                                    ? ProjectBundle.message("sdk.configure.home.invalid.error", getPresentableName())
                                    : ProjectBundle.message("sdk.configure.home.file.invalid.error", getPresentableName());
                            throw new Exception(message);
                        }
                    }
                }
            }
        };

        descriptor.setTitle(GoBundle.message("go.sdk.appengine.configure.title", getPresentableName()));
        return descriptor;
    }

    @Override
    public String getPresentableName() {
        return "Go App Engine Sdk";
    }

    @Override
    public boolean isRootTypeApplicable(OrderRootType type) {
        return type == OrderRootType.CLASSES || type == OrderRootType.SOURCES;
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

    public static SdkType getInstance() {
        return SdkType.findInstance(GoAppEngineSdkType.class);
    }

    @Override
    public SdkAdditionalData loadAdditionalData(Element additional) {
        return XmlSerializer.deserialize(additional, GoAppEngineSdkData.class);
    }

    @Override
    public void saveAdditionalData(SdkAdditionalData additionalData, Element additional) {
        if (additionalData instanceof GoAppEngineSdkData) {
            XmlSerializer.serializeInto(additionalData, additional);
        }
    }
}
