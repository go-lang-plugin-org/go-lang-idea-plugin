package ro.redeul.google.go.config.ui;

import com.intellij.facet.ui.FacetEditorTab;
import org.jetbrains.annotations.Nls;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Aug 19, 2010
 * Time: 4:02:33 AM
 * To change this template use File | Settings | File Templates.
 */
public class GoFacetTab extends FacetEditorTab {
    private JPanel rootPanel;
    private JComboBox comboBox1;

    @Nls
    public String getDisplayName() {
        return "Google go facet";
    }

    public JComponent createComponent() {
        return rootPanel;
    }

    public boolean isModified() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void reset() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void disposeUIResources() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}

