package ro.redeul.google.go.imports;

import com.intellij.application.options.editor.AutoImportOptionsProvider;
import com.intellij.openapi.options.ConfigurationException;
import ro.redeul.google.go.options.GoSettings;

import javax.swing.*;

public class GoAutoImportOptionsProvider implements AutoImportOptionsProvider {

    private JPanel myPanel;
    private JCheckBox myShowAutoImportPopups;
    private JCheckBox myOptimizeImportsOnTheFly;

    @Override
    public JComponent createComponent() {
        return myPanel;
    }

    @Override
    public boolean isModified() {
        GoSettings s = GoSettings.getInstance();
        return s.OPTIMIZE_IMPORTS_ON_THE_FLY != myOptimizeImportsOnTheFly.isSelected() ||
            s.SHOW_IMPORT_POPUP != myShowAutoImportPopups.isSelected();
    }

    @Override
    public void apply() throws ConfigurationException {
        GoSettings s = GoSettings.getInstance();
        s.OPTIMIZE_IMPORTS_ON_THE_FLY = myOptimizeImportsOnTheFly.isSelected();
        s.SHOW_IMPORT_POPUP = myShowAutoImportPopups.isSelected();
    }

    @Override
    public void reset() {
        GoSettings s = GoSettings.getInstance();
        myOptimizeImportsOnTheFly.setSelected(s.OPTIMIZE_IMPORTS_ON_THE_FLY);
        myShowAutoImportPopups.setSelected(s.SHOW_IMPORT_POPUP);
    }

    @Override
    public void disposeUIResources() {
    }
}
