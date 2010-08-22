package ro.redeul.google.go.config.ui;

import com.intellij.openapi.project.Project;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Aug 19, 2010
 * Time: 2:56:55 AM
 * To change this template use File | Settings | File Templates.
 */
public class GoFrameworkSuportEditor {

    private Project project;

    private JComponent component;

    public GoFrameworkSuportEditor(Project project) {
        this.project = project;
    }

    public JComponent getComponent() {
        return component;
    }
}
