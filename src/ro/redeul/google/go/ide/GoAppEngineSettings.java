package ro.redeul.google.go.ide;

import com.intellij.ide.passwordSafe.MasterPasswordUnavailableException;
import com.intellij.ide.passwordSafe.PasswordSafe;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.Comparing;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 5/29/11
 * Time: 11:41 AM
 */
@State(
        name = "GoAppEngineSettings",
        storages = {
            @Storage(id="other", file = "$APP_CONFIG$/go_app_engine_settings.xml")
        }
)
public class GoAppEngineSettings implements PersistentStateComponent<GoAppEngineSettings.Bean> {

    private static final Logger LOG = Logger.getInstance(GoAppEngineSettings.class.getName());

    private String myPassword = "";

    private String gaePath = "";

    private final String GO_APP_ENGINE_SETTINGS_PASSWORD_KEY = "GO_APP_ENGINE_SETTINGS_PASSWORD_KEY";

    public static class Bean {
        public String GO_APP_ENGINE_EMAIL = "";
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

    public static GoAppEngineSettings getInstance() {
        return ServiceManager.getService(GoAppEngineSettings.class);
    }

    public String getEmail() {
        return bean.GO_APP_ENGINE_EMAIL;
    }

    public void setEmail(String email) {
        bean.GO_APP_ENGINE_EMAIL = email;
    }

    public String getPassword() {
        if (myPassword == null) {
            try {
                myPassword = PasswordSafe.getInstance().getPassword(ProjectManager.getInstance().getDefaultProject(), GoAppEngineSettings.class, GO_APP_ENGINE_SETTINGS_PASSWORD_KEY);
            } catch (Exception e) {
                myPassword = "";
            }
        }

        return myPassword;
    }

    public void setPassword(String password) {
        if (!Comparing.equal(myPassword, password)) {
            try {
                PasswordSafe.getInstance().storePassword(ProjectManager.getInstance().getDefaultProject(), GoAppEngineSettings.class, GO_APP_ENGINE_SETTINGS_PASSWORD_KEY, password);
            } catch (MasterPasswordUnavailableException e) {
                // Ignore
            } catch (Exception e) {
                Messages.showErrorDialog("Error happened while storing credentials for app engine", "Error");
                LOG.error(e);
            }
        }

        myPassword = password;
    }

    public String getGaePath() {
        return this.gaePath;
    }

    public void setGaePath(String gaePath) {
        this.gaePath = gaePath;
    }
}
