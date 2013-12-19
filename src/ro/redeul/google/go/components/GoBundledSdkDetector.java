package ro.redeul.google.go.components;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.projectRoots.ProjectJdkTable;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.impl.ProjectJdkImpl;
import com.intellij.openapi.projectRoots.impl.SdkConfigurationUtil;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.config.sdk.GoSdkData;
import ro.redeul.google.go.config.sdk.GoSdkType;
import ro.redeul.google.go.sdk.GoSdkUtil;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 7/26/11
 * Time: 1:17 PM
 */
public class GoBundledSdkDetector implements ApplicationComponent {

    private static final Logger LOG = Logger.getInstance("#ro.redeul.google.go.components.GoBundledSdkDetector");

    @Override
    public void initComponent() {
        final ProjectJdkTable jdkTable = ProjectJdkTable.getInstance();

        List<Sdk> goSdks = GoSdkUtil.getSdkOfType(GoSdkType.getInstance());

        String homePath = PathManager.getHomePath() + "/bundled/go-sdk";

        File bundledGoSdkHomePath = new File(homePath);
        if ( ! bundledGoSdkHomePath.exists() || ! bundledGoSdkHomePath.isDirectory() ) {
            return;
        }

        LOG.debug("Bundled Go SDK path exists: " + homePath);

        for (Sdk sdk : goSdks) {
            if (sdk.getHomePath() == null) {
                continue;
            }

            if ( homePath.startsWith(sdk.getHomePath()) ) {
                LOG.debug("Bundled Go SDK at registered already with name: " + sdk.getName());
                return;
            }
        }

        // validate the sdk
        GoSdkData sdkData = GoSdkUtil.testGoogleGoSdk(homePath);

        if ( sdkData == null ) {
            // skip since the folder isn't a proper go sdk
            return;
        }

        LOG.info("We have a bundled go sdk (at " + homePath + ") that is not in the jdk table. Attempting to add");
        try {
            final ProjectJdkImpl bundledGoSdk;
            final GoSdkType goSdkType = GoSdkType.getInstance();

            goSdkType.setSdkData(sdkData);
            String newSdkName = SdkConfigurationUtil.createUniqueSdkName(goSdkType, sdkData.GO_GOROOT_PATH, Arrays.asList(jdkTable.getAllJdks()));
            bundledGoSdk = new ProjectJdkImpl(newSdkName, goSdkType);
            bundledGoSdk.setHomePath(homePath);
            ApplicationManager.getApplication().runWriteAction(new Runnable() {
                @Override
                public void run() {
                    goSdkType.setupSdkPaths(bundledGoSdk);
                    jdkTable.addJdk(bundledGoSdk);
                }
            });

        } catch (Exception e) {
            LOG.error("Exception while adding the bundled sdk", e);
        }
    }

    @Override
    public void disposeComponent() {
    }

    @NotNull
    @Override
    public String getComponentName() {
        return "GoBundledSdkDetector";
    }
}
