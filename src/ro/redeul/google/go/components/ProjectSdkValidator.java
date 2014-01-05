package ro.redeul.google.go.components;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationListener;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.ProjectJdkTable;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkModificator;
import ro.redeul.google.go.config.sdk.GoAppEngineSdkData;
import ro.redeul.google.go.config.sdk.GoAppEngineSdkType;
import ro.redeul.google.go.config.sdk.GoSdkData;
import ro.redeul.google.go.config.sdk.GoSdkType;
import ro.redeul.google.go.lang.psi.typing.GoTypes;
import ro.redeul.google.go.sdk.GoSdkUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ProjectSdkValidator extends AbstractProjectComponent {
    private static final Logger LOG = Logger.getInstance(ProjectSdkValidator.class);

    public ProjectSdkValidator(Project project) {
        super(project);
    }


    @Override
    public void projectClosed() {
        //Cleanups fix issue pointing out on pr #354
        GoTypes.cachedTypes.clear();
        super.projectClosed();
    }

    @Override
    public void initComponent() {
        ProjectJdkTable jdkTable = ProjectJdkTable.getInstance();
        List<Sdk> sdkList = new ArrayList<Sdk>();

        sdkList.addAll(GoSdkUtil.getSdkOfType(GoSdkType.getInstance(), jdkTable));

        for (Sdk sdk : sdkList) {
            GoSdkData sdkData = (GoSdkData) sdk.getSdkAdditionalData();

            boolean needsUpgrade = sdkData == null;
            try {
                if (!needsUpgrade) {
                    sdkData.checkValid();
                }
            } catch (ConfigurationException ex) {
                needsUpgrade = true;
            }

            if (!needsUpgrade)
                continue;

            needsUpgrade = false;
            GoSdkData data = GoSdkUtil.testGoogleGoSdk(sdk.getHomePath());

            if (data == null)
                needsUpgrade = true;

            try {
                if (data != null) {
                    data.checkValid();
                }
            } catch (ConfigurationException ex) {
                needsUpgrade = true;
            }

            if (needsUpgrade) {
                Notifications.Bus.notify(
                        new Notification("GoLang SDK validator", "Corrupt Go SDK",
                                getContent("Go", sdk.getName()),
                                NotificationType.WARNING), myProject);
            }

            SdkModificator sdkModificator = sdk.getSdkModificator();
            sdkModificator.setSdkAdditionalData(data);
            sdkModificator.commitChanges();
        }

        sdkList.clear();
        sdkList.addAll(GoSdkUtil.getSdkOfType(GoAppEngineSdkType.getInstance(), jdkTable));

        for (Sdk sdk : sdkList) {
            GoAppEngineSdkData sdkData = (GoAppEngineSdkData) sdk.getSdkAdditionalData();

            if (sdkData == null || sdkData.TARGET_ARCH == null || sdkData.TARGET_OS == null) {
                Notifications.Bus.notify(
                        new Notification(
                                "GoLang SDK validator",
                                "Corrupt Go App Engine SDK",
                                getContent("Go App Engine", sdk.getName()),
                                NotificationType.WARNING
                        ), myProject);
            }
        }

        String sysGoRootPath = GoSdkUtil.getSysGoRootPath();
        if (sysGoRootPath.isEmpty())
            Notifications.Bus.notify(
                    new Notification(
                            "Go SDK validator",
                            "Problem with env variables",
                            getInvalidGOROOTEnvMessage(),
                            NotificationType.ERROR,
                            NotificationListener.URL_OPENING_LISTENER),
                    myProject);

        String systemGOPATH = GoSdkUtil.getSysGoPathPath();
        if (systemGOPATH.isEmpty())
            Notifications.Bus.notify(
                    new Notification(
                            "Go SDK validator",
                            "Problem with env variables",
                            getInvalidGOPATHEnvMessage(),
                            NotificationType.ERROR,
                            NotificationListener.URL_OPENING_LISTENER),
                    myProject);

        if (!sysGoRootPath.isEmpty() &&
                !systemGOPATH.isEmpty()) {

            // Ensure we always have the path separator at the end of it
            if (!sysGoRootPath.endsWith(File.separator)) {
                sysGoRootPath += File.separator;
            }

            // Get all the individual paths
            String[] goPaths = systemGOPATH.split(File.pathSeparator);

            for (String goPath : goPaths) {
                if (!goPath.endsWith(File.separator)) {
                    goPath += File.separator;
                }

                if (goPath.contains(sysGoRootPath)) {
                    Notifications.Bus.notify(
                            new Notification(
                                    "Go SDK validator",
                                    "Problem with env variables",
                                    getGOPATHinGOROOTEnvMessage(),
                                    NotificationType.ERROR,
                                    NotificationListener.URL_OPENING_LISTENER),
                            myProject);

                    // Only show the message once
                    break;
                }
            }
        }

        super.initComponent();
    }

    private String getContent(String type, String name) {
        return "<html>The attached " + type + " SDK named: <em>" + name + "</em> seems to be corrupt." +
                "<br/>Please update it by going to the project sdk editor remove it and add it again.</html>";
    }

    private String getInvalidGOROOTEnvMessage() {
        return "<html><em>GOROOT</em> environment variable is empty or could not be detected properly.<br/>" +
                "This means that some tools like <code>go run</code> or <code>go fmt</code> might not run properly.<br/>" +
                "See <a href='http://git.io/_bt0Hg'>instructions</a> on how to fix this.";
    }

    private String getInvalidGOPATHEnvMessage() {
        return "<html><em>GOPATH</em> environment variable is empty or could not be detected properly.<br/>" +
                "This means that some tools like <code>go run</code> or <code>go fmt</code> might not run properly.<br/>" +
                "See <a href='http://git.io/_bt0Hg'>instructions</a> on how to fix this.";
    }

    private String getGOPATHinGOROOTEnvMessage() {
        return "<html><em>GOPATH</em> environment variable seems to be inside <em>GOROOT</em>.<br/>" +
                "This means that your system settings are not correct.<br/>" +
                "See <a href='http://golang.org/doc/code.html#GOPATH'>instructions</a> on how to set it properly.";
    }
}
