package ro.redeul.google.go.config.sdk;

import javax.swing.*;
import static java.lang.String.format;

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
import ro.redeul.google.go.config.ui.GoSdkConfigurable;
import ro.redeul.google.go.sdk.GoSdkUtil;
import ro.redeul.google.go.util.GoUtil;

public class GoSdkType extends SdkType {

    GoSdkData sdkData;

    public GoSdkType() {
        super("Google Go SDK");
    }

    public static GoSdkType getInstance() {
        return SdkType.findInstance(GoSdkType.class);
    }

    public GoSdkData getSdkData() {
        return sdkData;
    }

    public void setSdkData(GoSdkData sdkData) {
        this.sdkData = sdkData;
    }

    @Override
    public String suggestHomePath() {
        return GoUtil.resolvePotentialGoogleGoHomePath();
    }

    @Override
    public FileChooserDescriptor getHomeChooserDescriptor() {
        final FileChooserDescriptor descriptor = new FileChooserDescriptor(true, true, false, false, false, false) {
          public void validateSelectedFiles(VirtualFile[] files) throws Exception {
            if (files.length != 0){
              final String selectedPath = files[0].getPath();
              boolean valid = isValidSdkHome(selectedPath);
              if (!valid){
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

        descriptor.setTitle(GoBundle.message("go.sdk.configure.title", getPresentableName()));
        return descriptor;
    }

    @Override
    public boolean isValidSdkHome(String path) {
        sdkData = GoSdkUtil.testGoogleGoSdk(path);
        return sdkData != null;
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

        StringBuilder builder = new StringBuilder();

        builder.append("Go sdk");
        if ( getSdkData() != null ) {
            builder.append(" ").append(getSdkData().VERSION_MAJOR);
        }

        if ( sdkHome.matches(".*bundled/go-sdk/?$") ) {
            builder.append(" (bundled)");
        }

        return builder.toString();
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
    public AdditionalDataConfigurable createAdditionalDataConfigurable(SdkModel sdkModel, SdkModificator sdkModificator) {
        return new GoSdkConfigurable(sdkModel, sdkModificator);
    }

    @Override
    public void setupSdkPaths(Sdk sdk) {
        VirtualFile homeDirectory = sdk.getHomeDirectory();

        if (sdk.getSdkType() != this || homeDirectory == null) {
            return;
        }

        String path = homeDirectory.getPath();

        GoSdkData sdkData = GoSdkUtil.testGoogleGoSdk(path);

        if ( sdkData == null )
            return;

        final VirtualFile librariesRoot =
                homeDirectory.findFileByRelativePath(
                    format("pkg/%s_%s/", sdkData.TARGET_OS.getName(),
                           sdkData.TARGET_ARCH.getName()));

        final VirtualFile sourcesRoot = homeDirectory.findFileByRelativePath("src/pkg/");

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

        sdkModificator.setVersionString(
            format("%s%s",
                   sdkData.VERSION_MAJOR,
                   sdkData.VERSION_MINOR == null
                       ? ""
                       : " " + sdkData.VERSION_MINOR));
        sdkModificator.setSdkAdditionalData(sdkData);
        sdkModificator.commitChanges();
    }

    @Override
    public SdkAdditionalData loadAdditionalData(Element additional) {
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
        return "Go Sdk";
    }

    @Override
    public boolean isRootTypeApplicable(OrderRootType type) {
        return type == OrderRootType.CLASSES || type == OrderRootType.SOURCES;
    }

    public static boolean isInstance(Sdk sdk) {
        return sdk != null && sdk.getSdkType() == GoSdkType.getInstance();
    }
}
