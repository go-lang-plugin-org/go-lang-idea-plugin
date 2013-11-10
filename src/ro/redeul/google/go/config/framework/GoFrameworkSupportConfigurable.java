package ro.redeul.google.go.config.framework;

import com.intellij.ide.util.frameworkSupport.FrameworkSupportConfigurable;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.libraries.Library;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.config.ui.GoFrameworkSuportEditor;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Aug 19, 2010
 * Time: 2:58:12 AM
 * To change this template use File | Settings | File Templates.
 */
class GoFrameworkSupportConfigurable extends FrameworkSupportConfigurable {
    
    private final GoFrameworkSuportEditor editor;

    public GoFrameworkSupportConfigurable(GoFrameworkSuportEditor editor) {
        this.editor = editor;
    }
    
    @Override
    public JComponent getComponent() {
        return editor.getComponent();
    }

    @Override
    public void addSupport(@NotNull Module module, @NotNull ModifiableRootModel model, @Nullable Library library) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
