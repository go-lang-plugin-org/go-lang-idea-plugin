package ro.redeul.google.go.config.ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;
import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

import com.intellij.facet.ui.FacetEditorTab;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.ui.ProjectJdksEditor;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.ComboboxWithBrowseButton;
import org.jetbrains.annotations.Nls;
import ro.redeul.google.go.config.facet.GoFacetConfiguration;
import ro.redeul.google.go.config.sdk.GoSdkType;
import ro.redeul.google.go.sdk.GoSdkUtil;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Aug 19, 2010
 * Time: 4:02:33 AM
 * To change this template use File | Settings | File Templates.
 */
public class GoFacetTab extends FacetEditorTab {
    private JPanel rootPanel;
    private ComboboxWithBrowseButton goSdks;

    private GoFacetConfiguration facetConfiguration;
    private boolean modified;

    public GoFacetTab(GoFacetConfiguration facetConfiguration, List<Sdk> SDKs) {
        this.facetConfiguration = facetConfiguration;
        final DefaultComboBoxModel model = new DefaultComboBoxModel(SDKs.toArray());

        goSdks.getComboBox().setModel(model);

        goSdks.getComboBox().setRenderer(new BasicComboBoxRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if ( value != null && Sdk.class.cast(value) != null ) {
                    value = ((Sdk)value).getName();
                }

                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });

        if ( facetConfiguration.getSdk() != null ) {
            goSdks.getComboBox().setSelectedItem(facetConfiguration.getSdk());
        }

        goSdks.getComboBox().addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                setModified();
            }
        });

        goSdks.getButton().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ProjectJdksEditor editor = new ProjectJdksEditor(null, rootPanel);
                editor.show();
                if ( editor.getExitCode() == DialogWrapper.OK_EXIT_CODE ) {
                    model.removeAllElements();

                    List<Sdk> libraries = GoSdkUtil.getSdkOfType(GoSdkType.getInstance());
                    for (Sdk library : libraries) {
                        model.addElement(library);
                    }
                }
            }
        });

        modified = false;
    }

    private void setModified() {
        modified = true;
    }

    @Nls
    public String getDisplayName() {
        return "Google Go facet";
    }

    public JComponent createComponent() {
        return rootPanel;
    }

    public boolean isModified() {
        return modified;
    }

    public void reset() {
        if ( facetConfiguration.getSdk() != null ) {
            goSdks.getComboBox().setSelectedItem(facetConfiguration.getSdk());
        }
    }

    public void disposeUIResources() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void apply() throws ConfigurationException {
        facetConfiguration.setGoSdkName(((Sdk)goSdks.getComboBox().getSelectedItem()).getName());
        modified = false;
    }
}

