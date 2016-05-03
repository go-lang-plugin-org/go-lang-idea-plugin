package com.goide.runconfig;

import com.goide.runconfig.application.GoApplicationConfiguration;
import com.goide.runconfig.application.GoApplicationRunConfigurationProducer;
import com.goide.runconfig.application.GoApplicationRunConfigurationType;
import com.goide.runconfig.file.GoRunFileConfiguration;
import com.goide.runconfig.file.GoRunFileConfigurationProducer;
import com.goide.runconfig.file.GoRunFileConfigurationType;
import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;

public class GoRunConfigurationProducerTest extends GoRunConfigurationTestCase {
  public void testTestFile_test() {
    doTestProducedConfigurations();
  }

  public void testFileWithMain() {
    doTestProducedConfigurations();
  }

  public void testFileWithMainButNotMainPackage() {
    doTestProducedConfigurations();
  }

  public void testFileWithoutMain() {
    doTestProducedConfigurations();
  }

  public void testPackageClause() {
    doTestProducedConfigurations();
  }

  public void testDirectory() {
    PsiFile file = myFixture.addFileToProject("import/path/a.go", "package main; func main(){}");
    doTestProducedConfigurations(file.getParent());
  }

  public void testSameRunFileConfigurationOnFile() {
    PsiFile file = myFixture.configureByText("a.go", "package main; <caret>\nfunc main() {}");
    PsiElement at = file.findElementAt(myFixture.getCaretOffset());
    assertNotNull(at);
    ConfigurationContext configurationContext = new ConfigurationContext(at);
    GoRunFileConfigurationProducer producer = new GoRunFileConfigurationProducer();

    GoRunFileConfiguration runConfiguration = createFileConfiguration(file.getVirtualFile().getPath());
    assertTrue(producer.isConfigurationFromContext(runConfiguration, configurationContext));

    runConfiguration = createFileConfiguration(file.getVirtualFile().getPath() + "_vl");
    assertFalse(producer.isConfigurationFromContext(runConfiguration, configurationContext));
  }

  public void testSameRunApplicationConfigurationOnFile() {
    PsiFile file = myFixture.configureByText("a.go", "package main; <caret>\nfunc main() {}");
    PsiElement at = file.findElementAt(myFixture.getCaretOffset());
    assertNotNull(at);
    ConfigurationContext configurationContext = new ConfigurationContext(at);
    GoRunFileConfigurationProducer producer = new GoRunFileConfigurationProducer();

    GoRunFileConfiguration runConfiguration = createFileConfiguration(file.getVirtualFile().getPath());
    assertTrue(producer.isConfigurationFromContext(runConfiguration, configurationContext));

    runConfiguration = createFileConfiguration(file.getVirtualFile().getPath() + "_vl");
    assertFalse(producer.isConfigurationFromContext(runConfiguration, configurationContext));
  }

  public void testSameRunApplicationConfigurationOnPackage() {
    PsiFile file = myFixture.configureByText("a.go", "package main; <caret>\nfunc main() {}");
    PsiElement at = file.findElementAt(myFixture.getCaretOffset());
    assertNotNull(at);
    ConfigurationContext configurationContext = new ConfigurationContext(at);
    GoApplicationRunConfigurationProducer producer = new GoApplicationRunConfigurationProducer();

    GoApplicationConfiguration runConfiguration = createRunAppFileConfiguration(file.getVirtualFile().getPath());
    assertTrue(producer.isConfigurationFromContext(runConfiguration, configurationContext));

    runConfiguration = createRunAppFileConfiguration(file.getVirtualFile().getPath() + "_vl");
    assertFalse(producer.isConfigurationFromContext(runConfiguration, configurationContext));
  }

  public void testSameRunApplicationConfigurationOnDirectory() {
    PsiFile file = myFixture.addFileToProject("import/path/a.go", "package main; func main(){}");
    PsiDirectory directory = file.getParent();
    assertNotNull(directory);
    ConfigurationContext configurationContext = new ConfigurationContext(directory);
    GoApplicationRunConfigurationProducer producer = new GoApplicationRunConfigurationProducer();

    GoApplicationConfiguration runConfiguration = createRunAppPackageConfiguration("import/path");
    assertTrue(producer.isConfigurationFromContext(runConfiguration, configurationContext));

    runConfiguration = createRunAppPackageConfiguration("import/path/other");
    assertFalse(producer.isConfigurationFromContext(runConfiguration, configurationContext));
  }

  private GoRunFileConfiguration createFileConfiguration(String filePath) {
    GoRunFileConfigurationType type = GoRunFileConfigurationType.getInstance();
    GoRunFileConfiguration result = new GoRunFileConfiguration(myFixture.getProject(), "run file", type);
    result.setFilePath(filePath);
    return result;
  }

  private GoApplicationConfiguration createRunAppFileConfiguration(String filePath) {
    GoRunFileConfigurationType type = GoRunFileConfigurationType.getInstance();
    GoApplicationConfiguration result = new GoApplicationConfiguration(myFixture.getProject(), "run file", type);
    result.setKind(GoApplicationConfiguration.Kind.FILE);
    result.setFilePath(filePath);
    return result;
  }

  private GoApplicationConfiguration createRunAppPackageConfiguration(String importPath) {
    GoApplicationRunConfigurationType type = GoApplicationRunConfigurationType.getInstance();
    GoApplicationConfiguration result = new GoApplicationConfiguration(myFixture.getProject(), "run package", type);
    result.setKind(GoApplicationConfiguration.Kind.PACKAGE);
    result.setPackage(importPath);
    return result;
  }


  @Override
  protected String getBasePath() {
    return "running/producer";
  }
}
