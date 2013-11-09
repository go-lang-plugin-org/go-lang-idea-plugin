package ro.redeul.google.go.config.framework;

import com.intellij.ide.util.frameworkSupport.FrameworkSupportConfigurable;
import com.intellij.ide.util.frameworkSupport.FrameworkSupportModel;
import com.intellij.ide.util.frameworkSupport.FrameworkSupportProvider;
import com.intellij.openapi.module.ModuleType;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.GoIcons;
import ro.redeul.google.go.config.ui.GoFrameworkSuportEditor;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Aug 19, 2010
 * Time: 2:52:34 AM
 * To change this template use File | Settings | File Templates.
 */
public class GoFrameworkSupportProvider extends FrameworkSupportProvider {

    public GoFrameworkSupportProvider() {
        super("google.ro", "Google Go language");
    }

    @Override
    public Icon getIcon() {
        return GoIcons.GO_ICON_16x16;
    }

    @NotNull
    @Override
    public FrameworkSupportConfigurable createConfigurable(@NotNull FrameworkSupportModel model) {
        return new GoFrameworkSupportConfigurable(new GoFrameworkSuportEditor());
    }

    @Override
    public boolean isEnabledForModuleType(@NotNull ModuleType moduleType) {
        return true;
    }
}
