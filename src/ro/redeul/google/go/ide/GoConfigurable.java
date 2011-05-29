package ro.redeul.google.go.ide;

import com.intellij.openapi.compiler.*;
import com.intellij.openapi.compiler.Compiler;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.GoFileType;
import ro.redeul.google.go.GoIcons;
import ro.redeul.google.go.compilation.GoCompiler;
import ro.redeul.google.go.compilation.GoMakefileCompiler;
import ro.redeul.google.go.components.GoCompilerLoader;

import javax.swing.*;
import java.util.Arrays;
import java.util.HashSet;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 5/29/11
 * Time: 11:19 AM
 */
public class GoConfigurable implements SearchableConfigurable {

    GoConfigurableForm goConfigurableForm;

    Project project;

    public GoConfigurable(Project project) {
        this.project = project;
    }

    @NotNull
    @Override
    public String getId() {
        return getHelpTopic();
    }

    @Override
    public Runnable enableSearch(String option) {
        return null;
    }

    @Nls
    @Override
    public String getDisplayName() {
        return "Go Settings";
    }

    @Override
    public Icon getIcon() {
        return GoIcons.GO_ICON_16x16;
    }

    @Override
    public String getHelpTopic() {
        return "reference.settingsdialog.project.go";
    }

    @Override
    public JComponent createComponent() {
        goConfigurableForm = new GoConfigurableForm();
        return goConfigurableForm.componentPanel;
    }

    @Override
    public boolean isModified() {
        return goConfigurableForm != null && goConfigurableForm.isModified(getProjectSettings().getState());
    }

    @Override
    public void apply() throws ConfigurationException {
        GoProjectSettings.GoProjectSettingsBean bean = new GoProjectSettings.GoProjectSettingsBean();

        if ( goConfigurableForm != null ) {
            goConfigurableForm.apply(bean);
            getProjectSettings().loadState(bean);
            applyCompilerSettings(bean);
        }
    }

    private void applyCompilerSettings(GoProjectSettings.GoProjectSettingsBean bean) {
        // Remove current GoCompilers and add the currently configured
        CompilerManager compilerManager = CompilerManager.getInstance(project);
        Compiler[] compilers = compilerManager.getCompilers(GoCompiler.class);
        for (Compiler compiler : compilers) {
            compilerManager.removeCompiler(compiler);
        }
        compilers = compilerManager.getCompilers(GoMakefileCompiler.class);
        for (Compiler compiler : compilers) {
            compilerManager.removeCompiler(compiler);
        }

        switch (bean.BUILD_SYSTEM_TYPE) {
        case Internal:
            compilerManager.addTranslatingCompiler(
                    new GoCompiler(project),
                    new HashSet<FileType>(Arrays.asList(GoFileType.GO_FILE_TYPE)),
                    new HashSet<FileType>(Arrays.asList(FileType.EMPTY_ARRAY)));

            break;
        case Makefile:
            compilerManager.addTranslatingCompiler(
                    new GoMakefileCompiler(project),
                    new HashSet<FileType>(Arrays.asList(GoFileType.GO_FILE_TYPE)),
                    new HashSet<FileType>(Arrays.asList(FileType.EMPTY_ARRAY)));
            break;
        }
    }

    private GoProjectSettings getProjectSettings() {
        return GoProjectSettings.getInstance(project);
    }

    @Override
    public void reset() {
        if ( goConfigurableForm != null ) {
            goConfigurableForm.reset(getProjectSettings().getState());
        }
    }

    @Override
    public void disposeUIResources() {
        goConfigurableForm.componentPanel = null;
        goConfigurableForm = null;
    }
}
