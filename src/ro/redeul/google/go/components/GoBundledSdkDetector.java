package ro.redeul.google.go.components;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.projectRoots.ProjectJdkTable;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkModificator;
import com.intellij.openapi.projectRoots.impl.ProjectJdkImpl;
import com.intellij.openapi.projectRoots.impl.SdkConfigurationUtil;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.config.sdk.GoSdkType;

import java.util.Arrays;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 7/26/11
 * Time: 1:17 PM
 */
public class GoBundledSdkDetector implements ApplicationComponent {

    @Override
    public void initComponent() {
        final ProjectJdkTable jdkTable = ProjectJdkTable.getInstance();

        Sdk[] sdks = jdkTable.getAllJdks();

        for (Sdk sdk : sdks) {
            System.out.println("Sdk: " + sdk.getName());
        }

        String homePath = PathManager.getHomePath() + "/bundled-go/go-sdk";

        final ProjectJdkImpl newJdk;
        try {
            final GoSdkType goSdkType = GoSdkType.getInstance();
            String newSdkName = SdkConfigurationUtil.createUniqueSdkName(goSdkType, homePath, Arrays.asList(sdks));
            newJdk = new ProjectJdkImpl(newSdkName, goSdkType);
            newJdk.setHomePath(homePath);
            System.out.println("new jdk: " + newJdk);
            ApplicationManager.getApplication().runWriteAction(new Runnable() {
                @Override
                public void run() {
                    final SdkModificator sdkModificator = newJdk.getSdkModificator();
                    goSdkType.setupSdkPaths(newJdk);
                    jdkTable.addJdk(newJdk);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Override
    public void disposeComponent() {
        System.out.println("dispose");
    }

    @NotNull
    @Override
    public String getComponentName() {
        return "GoBundledSdkDetector";
    }
}
