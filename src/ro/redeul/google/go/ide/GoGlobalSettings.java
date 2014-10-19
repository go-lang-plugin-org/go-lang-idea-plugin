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
        public String GO_GOROOT = "";
        public String GO_GOAPPENGINEROOT = "";
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

    public String getGoRoot() {
        return bean.GO_GOROOT;
    }

    public String getGoAppEngineRoot() {
        return bean.GO_GOAPPENGINEROOT;
    }

    public String getGoPath() {
        return bean.GO_GOPATH;
    }

    public void setPaths(String goRoot, String goAppEngineRoot, String goPath) {
        bean.GO_GOROOT = goRoot;
        bean.GO_GOAPPENGINEROOT = goAppEngineRoot;
        bean.GO_GOPATH = goPath;

        ProjectJdkTable jdkTable = ProjectJdkTable.getInstance();
        List<Sdk> sdkList = new ArrayList<Sdk>();

        if (!goRoot.equals("")) {
            sdkList.addAll(GoSdkUtil.getSdkOfType(GoSdkType.getInstance(), jdkTable));
            for (Sdk sdk : sdkList) {
                updateSDK(sdk, goRoot, goPath);
            }
        }

        sdkList.clear();

        if (!goAppEngineRoot.equals("")) {
            sdkList.addAll(GoSdkUtil.getSdkOfType(GoAppEngineSdkType.getInstance(), jdkTable));
            for (Sdk sdk : sdkList) {
                updateSDK(sdk, goAppEngineRoot + "/goroot", goPath);
            }
        }
    }

    private void updateSDK(Sdk sdk, String goRoot, String goPath) {
        final SdkModificator sdkModificator = sdk.getSdkModificator();
        final VirtualFile finalGoRoot = StandardFileSystems.local().findFileByPath(goRoot + "/src/pkg");
        final VirtualFile finalGoPath = StandardFileSystems.local().findFileByPath(goPath + "/src");

        ApplicationManager.getApplication().runWriteAction(new Runnable() {
            public void run() {
                sdkModificator.removeRoots(OrderRootType.SOURCES);
                sdkModificator.removeRoots(OrderRootType.CLASSES);

                sdkModificator.addRoot(finalGoRoot, OrderRootType.SOURCES);
                sdkModificator.addRoot(finalGoRoot, OrderRootType.CLASSES);
                sdkModificator.addRoot(finalGoPath, OrderRootType.CLASSES);
            }
        });

        sdkModificator.commitChanges();

        if (GoSdkUtil.getSdkSourcesRoot(sdk) != null) {
            GoSdkUtil.getSdkSourcesRoot(sdk).refresh(false, false);
        }
    }
}
