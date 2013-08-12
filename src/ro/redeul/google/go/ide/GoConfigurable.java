package ro.redeul.google.go.ide;

import java.util.Arrays;
import java.util.HashSet;
import javax.swing.*;

import com.intellij.openapi.compiler.Compiler;
import com.intellij.openapi.compiler.CompilerManager;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.GoFileType;
import ro.redeul.google.go.GoIcons;
import ro.redeul.google.go.compilation.GoInstallCompiler;
import ro.redeul.google.go.compilation.GoCompiler;
import ro.redeul.google.go.compilation.GoMakefileCompiler;
import ro.redeul.google.go.options.GoSettings;

public class GoConfigurable implements SearchableConfigurable {

    GoConfigurableForm form;

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

    public Icon getIcon() {
        return GoIcons.GO_ICON_16x16;
    }

    @Override
    @NotNull
    public String getHelpTopic() {
        return "reference.settingsdialog.project.go";
    }

    @Override
    public JComponent createComponent() {
        form = new GoConfigurableForm();
        form.enableShowHide();
        return form.componentPanel;
    }

    @Override
    public boolean isModified() {
        return form != null &&
            form.isModified(getProjectSettings().getState(),
                            GoSettings.getInstance().getState());
    }

    @Override
    public void apply() throws ConfigurationException {
        GoProjectSettings.GoProjectSettingsBean projectSettings = new GoProjectSettings.GoProjectSettingsBean();
        GoSettings settings = GoSettings.getInstance().getState();

        if ( form != null ) {
            form.apply(projectSettings, settings);
            GoSettings.getInstance().loadState(settings);
            getProjectSettings().loadState(projectSettings);
            applyCompilerSettings(projectSettings);
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
        compilers = compilerManager.getCompilers(GoInstallCompiler.class);
        for (Compiler compiler : compilers) {
            compilerManager.removeCompiler(compiler);
        }

        switch (bean.BUILD_SYSTEM_TYPE) {
        case Internal:
            compilerManager.addTranslatingCompiler(
                    new GoCompiler(project),
                    new HashSet<FileType>(Arrays.asList(GoFileType.INSTANCE)),
                    new HashSet<FileType>(Arrays.asList(FileType.EMPTY_ARRAY)));

            break;
        case Makefile:
            compilerManager.addTranslatingCompiler(
                    new GoMakefileCompiler(project),
                    new HashSet<FileType>(Arrays.asList(GoFileType.INSTANCE)),
                    new HashSet<FileType>(Arrays.asList(FileType.EMPTY_ARRAY)));
            break;
        case Install:
            compilerManager.addTranslatingCompiler(
                    new GoInstallCompiler(project),
                    new HashSet<FileType>(Arrays.asList(GoFileType.INSTANCE)),
                    new HashSet<FileType>(Arrays.asList(FileType.EMPTY_ARRAY)));
        }
    }

    private GoProjectSettings getProjectSettings() {
        return GoProjectSettings.getInstance(project);
    }

    @Override
    public void reset() {
        if ( form != null ) {
            form.reset(getProjectSettings().getState(), GoSettings.getInstance().getState());
        }
    }

    @Override
    public void disposeUIResources() {
        form.componentPanel = null;
        form = null;
    }
}
