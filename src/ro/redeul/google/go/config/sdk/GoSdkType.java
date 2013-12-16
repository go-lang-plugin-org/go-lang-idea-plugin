package ro.redeul.google.go.config.sdk;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.ProjectBundle;
import com.intellij.openapi.projectRoots.*;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.vfs.StandardFileSystems;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.xmlb.XmlSerializer;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.GoBundle;
import ro.redeul.google.go.GoIcons;
import ro.redeul.google.go.config.ui.GoSdkConfigurable;
import ro.redeul.google.go.sdk.GoSdkUtil;

import javax.swing.*;

import java.io.File;

import static java.lang.String.format;

public class GoSdkType extends SdkType {

    public static final String GO_SDK_NAME = "Go SDK";

    private GoSdkData sdkData;

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
        return GoSdkUtil.resolvePotentialGoogleGoHomePath();
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

    public Icon getIconForExpandedTreeNode() {
        return GoIcons.GO_ICON_16x16;
    }

    @Override
    public String suggestSdkName(String currentSdkName, String sdkHome) {

        StringBuilder builder = new StringBuilder();

        builder.append(GO_SDK_NAME);
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

        return sdkData.VERSION_MAJOR;
    }

    @Override
    public AdditionalDataConfigurable createAdditionalDataConfigurable(SdkModel sdkModel, SdkModificator sdkModificator) {
        return new GoSdkConfigurable();
    }

    @Override
    public void setupSdkPaths(@NotNull Sdk sdk) {
        VirtualFile homeDirectory = sdk.getHomeDirectory();

        if (sdk.getSdkType() != this || homeDirectory == null) {
            return;
        }

        String path = homeDirectory.getPath();

        GoSdkData sdkData = GoSdkUtil.testGoogleGoSdk(path);

        if ( sdkData == null )
            return;

        String pkgType = format("pkg/%s_%s/", sdkData.TARGET_OS.getName(),
                sdkData.TARGET_ARCH.getName());

        final VirtualFile sdkSourcesRoot = homeDirectory.findFileByRelativePath("src/pkg/");

        if (sdkSourcesRoot != null) {
            sdkSourcesRoot.refresh(false, false);
        }

        String goPathFirst = System.getenv("GOPATH");

        VirtualFile goPathDirectory = null;
        VirtualFile pathSourcesRoot = null;

        if (goPathFirst != null &&
                !goPathFirst.equals("")) {

            // If there are multiple directories under GOPATH then we extract only the first one
            if (goPathFirst.contains(File.pathSeparator)) {
                goPathFirst = goPathFirst.split(File.pathSeparator)[0];
            }

            if ((new File(goPathFirst).exists())) {
                goPathDirectory = StandardFileSystems.local().findFileByPath(goPathFirst);

                if (goPathDirectory != null) {
                    pathSourcesRoot = goPathDirectory.findFileByRelativePath("src/");
                }
            }
        }

        final SdkModificator sdkModificator = sdk.getSdkModificator();
        final VirtualFile finalGoPathDirectory = goPathDirectory;
        final VirtualFile finalPathSourcesRoot = pathSourcesRoot;
        final String finalGoPathFirst = goPathFirst;

        ApplicationManager.getApplication().runWriteAction(new Runnable() {
            public void run() {
                sdkModificator.addRoot(sdkSourcesRoot, OrderRootType.CLASSES);
                sdkModificator.addRoot(sdkSourcesRoot, OrderRootType.SOURCES);

                // If we could detect the GOPATH properly, automatically add the first directory to the autocompletion path
                if (finalPathSourcesRoot != null) {
                    sdkModificator.addRoot(finalPathSourcesRoot, OrderRootType.CLASSES);
                    sdkModificator.addRoot(finalPathSourcesRoot, OrderRootType.SOURCES);
                }
            }
        });

        sdkModificator.setVersionString(sdkData.VERSION_MAJOR);
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
        return GO_SDK_NAME;
    }

    public String getSdkLongName() {
        if (sdkData == null) {
            return GO_SDK_NAME;
        }

        if (sdkData.VERSION_MAJOR.equals("")) {
            return GO_SDK_NAME;
        }

        return GO_SDK_NAME.concat(" ").concat(sdkData.VERSION_MAJOR);
    }


    @Override
    public boolean isRootTypeApplicable(OrderRootType type) {
        return type == OrderRootType.CLASSES || type == OrderRootType.SOURCES;
    }

    public static boolean isInstance(Sdk sdk) {
        return sdk != null && sdk.getSdkType() == GoSdkType.getInstance();
    }
}
