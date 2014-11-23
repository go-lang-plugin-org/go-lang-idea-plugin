package ro.redeul.google.go.ide;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.projectRoots.ProjectJdkTable;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.vfs.VirtualFile;
import ro.redeul.google.go.config.sdk.GoAppEngineSdkType;
import ro.redeul.google.go.config.sdk.GoSdkType;
import ro.redeul.google.go.options.GoSettings;
import ro.redeul.google.go.sdk.GoSdkUtil;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: Florin Patan <florinpatan@gmail.com>
 */
public class GoGlobalConfigurableForm {
    public JPanel componentPanel;
    private TextFieldWithBrowseButton goPath;
    private JButton importSysGo;
    private JCheckBox enableOnTheFlyImportOptimization;
    private TextFieldWithBrowseButton goRoot;
    private TextFieldWithBrowseButton goAppEngineRoot;
    private final GoGlobalSettings goGlobalSettings = GoGlobalSettings.getInstance();
    private final GoSettings goSettings = GoSettings.getInstance();

    GoGlobalConfigurableForm() {
        goRoot.addBrowseFolderListener("GOROOT directory", "Select the GOROOT directory of your GO setup",
                null, new FileChooserDescriptor(false, true, false, false, false, false));
        goAppEngineRoot.addBrowseFolderListener("GOAPPENGINEROOT directory", "Select the GOAPPENGINEROOT directory of your GO setup",
                null, new FileChooserDescriptor(false, true, false, false, false, false));
        goPath.addBrowseFolderListener("GOPATH directory", "Select the GOPATH directory of your GO setup",
                null, new FileChooserDescriptor(false, true, false, false, false, false));

        importSysGo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goRoot.setText(GoSdkUtil.getSysGoRootPath());
                goAppEngineRoot.setText(GoSdkUtil.getAppEngineDevServer());
                goPath.setText(GoSdkUtil.getSysGoPathPath().split(File.pathSeparator)[0]);
            }
        });

        populateFromSDKs();
    }

    private void populateFromSDKs() {
        ProjectJdkTable jdkTable = ProjectJdkTable.getInstance();
        List<Sdk> sdkList = new ArrayList<Sdk>();
        sdkList.addAll(GoSdkUtil.getSdkOfType(GoSdkType.getInstance(), jdkTable));
        sdkList.addAll(GoSdkUtil.getSdkOfType(GoAppEngineSdkType.getInstance(), jdkTable));

        if (sdkList.size() == 0) {
            return;
        }

        if (goRoot.getText().equals("")) {
            sdkList.addAll(GoSdkUtil.getSdkOfType(GoSdkType.getInstance(), jdkTable));
            if (sdkList.size() > 0) {
                goRoot.setText(sdkList.get(0).getHomePath());
            }
        }

        if (goAppEngineRoot.getText().equals("")) {
            sdkList.clear();
            sdkList.addAll(GoSdkUtil.getSdkOfType(GoAppEngineSdkType.getInstance(), jdkTable));
            if (sdkList.size() > 0) {
                goAppEngineRoot.setText(sdkList.get(0).getHomePath());
            }
        }

        if (goPath.getText().equals("")) {
            sdkList.clear();
            sdkList.addAll(GoSdkUtil.getSdkOfType(GoSdkType.getInstance(), jdkTable));
            if (sdkList.size() > 0) {
                VirtualFile[] goRoots = sdkList.get(0).getSdkModificator().getRoots(OrderRootType.CLASSES);
                if (goRoots.length < 2) {
                    return;
                }

                String goPathString = goRoots[1].getCanonicalPath();
                if (goPathString == null) {
                    return;
                }

                goPath.setText(goPathString.substring(0, goPathString.length() - 4));
            }
        }
    }

    public void reset() {
        goRoot.setText(goGlobalSettings.getGoRoot());
        goAppEngineRoot.setText(goGlobalSettings.getGoAppEngineRoot());
        goPath.setText(goGlobalSettings.getGoPath());

        if (goSettings.getState() != null) {
            enableOnTheFlyImportOptimization.setSelected(goSettings.getState().OPTIMIZE_IMPORTS_ON_THE_FLY);
        }

        populateFromSDKs();
    }

    public boolean isModified() {
        if (goSettings.getState() != null) {
            if (enableOnTheFlyImportOptimization.isSelected() != goSettings.getState().OPTIMIZE_IMPORTS_ON_THE_FLY) {
                return true;
            }
        }

        if (!Comparing.equal(goRoot.getText(), goGlobalSettings.getGoRoot())) {
            return true;
        }

        if (!Comparing.equal(goAppEngineRoot.getText(), goGlobalSettings.getGoAppEngineRoot())) {
            return true;
        }

        return !Comparing.equal(goPath.getText(), goGlobalSettings.getGoPath());
    }

    public void apply() {
        String goRootStr = handleGoRoot(goRoot.getText());
        String goAppEngineRootStr = handleGoAppEngineRoot(goAppEngineRoot.getText());
        String goPathStr = handleGoPath(goPath.getText());

        if (goRootStr.equals("") && !goRoot.getText().equals("")) {
            return;
        }

        if (goAppEngineRootStr.equals("") && !goAppEngineRoot.getText().equals("")) {
            return;
        }

        if (goRootStr.equals(goAppEngineRootStr)) {
            Messages.showErrorDialog("Error while saving your settings. \nGOROOT is the same as GO AppEngine SDK root", "Error on Google Go Plugin");
            return;
        }

        if (goPathStr.equals("")) {
            return;
        }

        goGlobalSettings.setPaths(goRootStr, goAppEngineRootStr, goPathStr);
        goSettings.OPTIMIZE_IMPORTS_ON_THE_FLY = enableOnTheFlyImportOptimization.isSelected();
    }

    private String handleGoRoot(String goRootStr) {
        if (goRootStr.equals("")) {
            return "";
        }

        if (goRootStr.endsWith(File.separator)) {
            goRootStr = goRootStr.substring(0, goRootStr.length() - 1);
            goRoot.setText(goRootStr);
        }

        if (!(new File(goRootStr).exists())) {
            Messages.showErrorDialog("Error while saving your settings. \nGOROOT doesn't exists.", "Error on Google Go Plugin");
            return "";
        }

        if (!(new File(goRootStr.concat("/bin")).exists())) {
            Messages.showErrorDialog("Error while saving your settings. \nGOROOT/bin doesn't exists.", "Error on Google Go Plugin");
            return "";
        }

        String goExecName = GoSdkUtil.findGoExecutable(goRootStr);
        if (goExecName.equals("")) {
            Messages.showErrorDialog("Error while saving your settings. \nGOROOT" + goExecName + " doesn't exists.", "Error on Google Go Plugin");
            return "";
        }

        String goPackagesDir = GoSdkUtil.computeGoBuiltinPackagesPath(goRootStr);
        if (goPackagesDir == null || !(new File(goPackagesDir).exists())) {
            Messages.showErrorDialog("Error while saving your settings. \nPackages could not be found.", "Error on Google Go Plugin");
            return "";
        }

        return goRootStr;
    }

    private String handleGoAppEngineRoot(String goAppEngineRootStr) {
        if (goAppEngineRootStr.equals("")) {
            return "";
        }

        if (goAppEngineRootStr.endsWith(File.separator)) {
            goAppEngineRootStr = goAppEngineRootStr.substring(0, goAppEngineRootStr.length() - 1);
            goAppEngineRoot.setText(goAppEngineRootStr);
        }

        if (!(new File(goAppEngineRootStr).exists())) {
            Messages.showErrorDialog("Error while saving your settings. \nGOAPPENGINEROOT doesn't exists.", "Error on Google Go Plugin");
            return "";
        }

        if (!(new File(goAppEngineRootStr.concat("/appcfg.py")).exists())) {
            Messages.showErrorDialog("Error while saving your settings. \nGOAPPENGINEROOT/appcfg.py doesn't exists.", "Error on Google Go Plugin");
            return "";
        }

        if (!(new File(goAppEngineRootStr.concat("/goroot/bin")).exists())) {
            Messages.showErrorDialog("Error while saving your settings. \nnGOAPPENGINEROOT/goroot/bin doesn't exists.", "Error on Google Go Plugin");
            return "";
        }

        String goExecName = GoSdkUtil.isHostOsWindows() ? "/goroot/bin/go.exe" : "/goroot/bin/go";
        if (new File(goAppEngineRootStr.concat(goExecName)).exists()) {
            Messages.showErrorDialog("Error while saving your settings. \nGOAPPENGINEROOT/goroot" + goExecName + " doesn't exists.", "Error on Google Go Plugin");
            return "";
        }

        if (!(new File(goAppEngineRootStr.concat("/goroot/src/pkg")).exists())) {
            Messages.showErrorDialog("Error while saving your settings. \nGOAPPENGINEROOT/src/pkg doesn't exists.", "Error on Google Go Plugin");
            return "";
        }

        return goAppEngineRootStr;
    }

    private String handleGoPath(String goPathStr) {
        if (goPathStr.endsWith(File.separator)) {
            goPathStr = goPathStr.substring(0, goPathStr.length() - 1);
            goPath.setText(goPathStr);
        }

        if (!(new File(goPathStr).exists())) {
            Messages.showErrorDialog("Error while saving your settings. \nGOPATH doesn't exists.", "Error on Google Go Plugin");
            return "";
        }

        String goExecName = GoSdkUtil.isHostOsWindows() ? "/bin/go.exe" : "/bin/go";
        if (new File(goPathStr.concat(goExecName)).exists()) {
            Messages.showErrorDialog("Error while saving your settings. \nGOPATH/bin/go exists. Are you sure this is not GOROOT?", "Error on Google Go Plugin");
            return "";
        }

        if (!(new File(goPathStr.concat("/src")).exists())) {
            Messages.showErrorDialog("Error while saving your settings. \nGOPATH/src doesn't exists.", "Error on Google Go Plugin");
            return "";
        }

        return goPathStr;
    }
}
