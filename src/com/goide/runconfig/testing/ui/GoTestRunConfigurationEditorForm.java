package com.goide.runconfig.testing.ui;

import com.goide.GoModuleType;
import com.goide.runconfig.testing.GoTestRunConfiguration;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ui.configuration.ModulesCombobox;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.ListCellRendererWrapper;
import com.intellij.ui.RawCommandLineEditor;
import org.intellij.lang.regexp.RegExpLanguage;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GoTestRunConfigurationEditorForm extends SettingsEditor<GoTestRunConfiguration> {
  private JPanel component;
  private ModulesCombobox myComboModules;
  private RawCommandLineEditor myParamsField;
  private EditorTextField myPatternEditor;
  private TextFieldWithBrowseButton myWorkingDirectoryField;

  private JComboBox myTestKindComboBox;
  private JLabel myFileLabel;
  private TextFieldWithBrowseButton myFileField;
  private JLabel myPackageLabel;
  private EditorTextField myPackageField;
  private JLabel myDirectoryLabel;
  private TextFieldWithBrowseButton myDirectoryField;

  public GoTestRunConfigurationEditorForm(@NotNull Project project) {
    super(null);
    installTestKindComboBox();
    installFileChoosers(project);
  }

  private void onTestKindChanged() {
    GoTestRunConfiguration.Kind selectedKind = (GoTestRunConfiguration.Kind)myTestKindComboBox.getSelectedItem();
    if (selectedKind == null) {
      selectedKind = GoTestRunConfiguration.Kind.DIRECTORY;
    }
    boolean allInPackage = selectedKind == GoTestRunConfiguration.Kind.PACKAGE;
    boolean allInDirectory = selectedKind == GoTestRunConfiguration.Kind.DIRECTORY;
    boolean file = selectedKind == GoTestRunConfiguration.Kind.FILE;

    myPackageField.setVisible(allInPackage);
    myPackageLabel.setVisible(allInPackage);
    myDirectoryField.setVisible(allInDirectory);
    myDirectoryLabel.setVisible(allInDirectory);
    myFileField.setVisible(file);
    myFileLabel.setVisible(file);
  }

  @Override
  protected void resetEditorFrom(GoTestRunConfiguration configuration) {
    myTestKindComboBox.setSelectedItem(configuration.getKind());
    myPackageField.setText(configuration.getPackage());

    String directoryPath = configuration.getDirectoryPath();
    myDirectoryField.setText(directoryPath.isEmpty() ? configuration.getProject().getBasePath() : directoryPath);

    String filePath = configuration.getFilePath();
    myFileField.setText(filePath.isEmpty() ? configuration.getProject().getBasePath() : filePath);

    myComboModules.fillModules(configuration.getProject(), GoModuleType.getInstance());
    myComboModules.setSelectedModule(configuration.getConfigurationModule().getModule());
    myParamsField.setText(configuration.getParams());
    myPatternEditor.setText(configuration.getPattern());
    myWorkingDirectoryField.setText(configuration.getWorkingDirectory());
  }

  @Override
  protected void applyEditorTo(GoTestRunConfiguration configuration) throws ConfigurationException {
    configuration.setKind((GoTestRunConfiguration.Kind)myTestKindComboBox.getSelectedItem());
    configuration.setPackage(myPackageField.getText());
    configuration.setDirectoryPath(myDirectoryField.getText());
    configuration.setFilePath(myFileField.getText());

    configuration.setModule(myComboModules.getSelectedModule());
    configuration.setParams(myParamsField.getText());
    configuration.setPattern(myPatternEditor.getText());
    configuration.setWorkingDirectory(myWorkingDirectoryField.getText());
  }

  @NotNull
  @Override
  protected JComponent createEditor() {
    return component;
  }

  @Override
  protected void disposeEditor() {
    component.setVisible(false);
  }

  private void createUIComponents() {
    myPatternEditor = new EditorTextField("", null, RegExpLanguage.INSTANCE.getAssociatedFileType());
  }

  private static ListCellRendererWrapper<GoTestRunConfiguration.Kind> getTestKindListCellRendererWrapper() {
    return new ListCellRendererWrapper<GoTestRunConfiguration.Kind>() {
      @Override
      public void customize(JList list, GoTestRunConfiguration.Kind kind, int index, boolean selected, boolean hasFocus) {
        if (kind != null) {
          String kindName = StringUtil.capitalize(kind.toString().toLowerCase());
          setText(kindName);
        }
      }
    };
  }

  private void installFileChoosers(Project project) {
    FileChooserDescriptor chooseFileDescriptor = FileChooserDescriptorFactory.createSingleLocalFileDescriptor();
    chooseFileDescriptor.setRoots(project.getBaseDir());
    chooseFileDescriptor.setShowFileSystemRoots(false);
    myFileField.addBrowseFolderListener(new TextBrowseFolderListener(chooseFileDescriptor));

    FileChooserDescriptor chooseDirectoryDescriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
    chooseDirectoryDescriptor.setRoots(project.getBaseDir());
    chooseDirectoryDescriptor.setShowFileSystemRoots(false);
    myDirectoryField.addBrowseFolderListener(new TextBrowseFolderListener(chooseDirectoryDescriptor));
  }

  private void installTestKindComboBox() {
    myTestKindComboBox.removeAllItems();
    myTestKindComboBox.setRenderer(getTestKindListCellRendererWrapper());
    for (GoTestRunConfiguration.Kind kind : GoTestRunConfiguration.Kind.values()) {
      myTestKindComboBox.addItem(kind);
    }
    myTestKindComboBox.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        onTestKindChanged();
      }
    });
  }
}
