package ro.redeul.google.go.ide;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.projectRoots.ProjectJdkTable;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkModificator;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.vfs.StandardFileSystems;
import com.intellij.openapi.vfs.VirtualFile;
import ro.redeul.google.go.config.sdk.GoAppEngineSdkType;
import ro.redeul.google.go.config.sdk.GoSdkType;
import ro.redeul.google.go.sdk.GoSdkUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Florin Patan <florinpatan@gmail.com>
 */
@State(
        name = "GoGlobalSettings",
        storages = {
            @Storage(id="other", file = "$APP_CONFIG$/go.global.settings.xml")
        }
)
public class GoGlobalSettings implements PersistentStateComponent<GoGlobalSettings.Bean> {

    private static final Logger LOG = Logger.getInstance(GoGlobalSettings.class.getName());

    public static class Bean {
        public String GO_GOPATH = "";
    }

    private Bean bean = new Bean();

    @Override
    public Bean getState() {
        return bean;
    }

    @Override
    public void loadState(Bean bean) {
        this.bean = bean;
    }

    public static GoGlobalSettings getInstance() {
        return ServiceManager.getService(GoGlobalSettings.class);
    }

    public String getGoPath() {
        return bean.GO_GOPATH;
    }

    public void setGoPath(String goPath) {
        bean.GO_GOPATH = goPath;

        ProjectJdkTable jdkTable = ProjectJdkTable.getInstance();
        List<Sdk> sdkList = new ArrayList<Sdk>();

        sdkList.addAll(GoSdkUtil.getSdkOfType(GoSdkType.getInstance(), jdkTable));
        sdkList.addAll(GoSdkUtil.getSdkOfType(GoAppEngineSdkType.getInstance(), jdkTable));

        for (Sdk sdk : sdkList) {
            updateSDK(sdk, goPath);
        }
    }

    private void updateSDK(Sdk sdk, String goPath) {
        final SdkModificator sdkModificator = sdk.getSdkModificator();
        final VirtualFile finalGoPath = StandardFileSystems.local().findFileByPath(goPath + "/src");

        ApplicationManager.getApplication().runWriteAction(new Runnable() {
            public void run() {
                sdkModificator.removeRoots(OrderRootType.CLASSES);
                sdkModificator.addRoot(sdkModificator.getRoots(OrderRootType.SOURCES)[0], OrderRootType.CLASSES);
                sdkModificator.addRoot(finalGoPath, OrderRootType.CLASSES);
            }
        });

        sdkModificator.commitChanges();

        if (GoSdkUtil.getSdkSourcesRoot(sdk) != null) {
            GoSdkUtil.getSdkSourcesRoot(sdk).refresh(false, false);
        }
    }
}
