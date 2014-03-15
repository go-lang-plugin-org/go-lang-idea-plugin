package ro.redeul.google.go.ide;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.diagnostic.Logger;

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
    }
}
