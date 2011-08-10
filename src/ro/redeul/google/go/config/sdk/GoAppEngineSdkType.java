package ro.redeul.google.go.config.sdk;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.ProjectBundle;
import com.intellij.openapi.projectRoots.*;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.vfs.VirtualFile;
import org.jdom.Element;
import ro.redeul.google.go.GoBundle;
import ro.redeul.google.go.GoIcons;
import ro.redeul.google.go.util.GoUtil;

import javax.swing.*;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 8/9/11
 * Time: 5:08 PM
 */
public class GoAppEngineSdkType extends SdkType {

    public GoAppEngineSdkType() {
        super("Google Go App Engine SDK");
    }

    @Override
    public String suggestHomePath() {
        return GoUtil.resolvePotentialGoogleGoAppEngineHomePath();
    }

    @Override
    public boolean isValidSdkHome(String path) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String suggestSdkName(String currentSdkName, String sdkHome) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public AdditionalDataConfigurable createAdditionalDataConfigurable(SdkModel sdkModel, SdkModificator sdkModificator) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void saveAdditionalData(SdkAdditionalData additionalData, Element additional) {
        //To change body of implemented methods use File | Settings | File Templates.
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
}
