/*
 * Copyright 2013-2016 Sergey Ignatov, Alexander Zolotov, Florin Patan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.goide.runconfig.testing;

import com.goide.runconfig.GoRunConfigurationTestCase;
import com.goide.runconfig.testing.frameworks.gobench.GobenchFramework;
import com.goide.runconfig.testing.frameworks.gobench.GobenchRunConfigurationProducer;
import com.goide.runconfig.testing.frameworks.gotest.GotestFramework;
import com.goide.runconfig.testing.frameworks.gotest.GotestRunConfigurationProducer;
import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.actions.RunConfigurationProducer;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

public class GoTestRunConfigurationProducerTest extends GoRunConfigurationTestCase {
  public void testDirectory() {
    PsiFile file = myFixture.configureByText("a.go", "package main");
    doTestProducedConfigurations(file.getParent());
  }

  public void testPackage_test() {
    PsiFile file = myFixture.addFileToProject("import/path/package_test.go", "package ma<caret>in; import `gopkg.in/check.v1`");
    myFixture.configureFromExistingVirtualFile(file.getVirtualFile());
    doTestProducedConfigurations(myFixture.getFile().findElementAt(myFixture.getCaretOffset()));
  }

  public void testFileWithoutTest_test() {
    doTestProducedConfigurations();
  }

  public void testNonTestFile() {
    doTestProducedConfigurations();
  }

  public void testFileWithTestsOnly_test() {
    doTestProducedConfigurations();
  }

  public void testFileWithBenchmarksOnly_test() {
    doTestProducedConfigurations();
  }

  public void testFileWithGocheckTestsOnly_test() {
    doTestProducedConfigurations();
  }

  public void testFileWithTestsAndBenchmarks_test() {
    doTestProducedConfigurations();
  }

  public void testSimpleFunction_test() {
    doTestProducedConfigurations();
  }

  public void testSimpleFunctionInFileWithTests_test() {
    doTestProducedConfigurations();
  }

  public void testBenchmarkFunction_test() {
    doTestProducedConfigurations();
  }

  public void testExampleFunction_test() {
    doTestProducedConfigurations();
  }

  public void testTestFunction_test() {
    doTestProducedConfigurations();
  }

  public void testGocheckMethod_test() {
    doTestProducedConfigurations();
  }

  public void testTestFunctionNonTestFile() {
    doTestProducedConfigurations();
  }

  public void testSameConfigurationOnFunction() {
    PsiFile file = myFixture.addFileToProject("import/path/a_test.go", "package main; func TestName() {<caret>}");
    myFixture.configureFromExistingVirtualFile(file.getVirtualFile());
    ConfigurationContext configurationContext = createConfigurationContext();
    RunConfigurationProducer<GoTestRunConfiguration> producer = new GotestRunConfigurationProducer();

    GoTestRunConfiguration runConfiguration = createPackageConfiguration(GotestFramework.INSTANCE, "^TestName$", "import/path");
    assertTrue(producer.isConfigurationFromContext(runConfiguration, configurationContext));

    runConfiguration = createPackageConfiguration(GotestFramework.INSTANCE, "otherPattern", "import/path");
    assertFalse(producer.isConfigurationFromContext(runConfiguration, configurationContext));

    runConfiguration = createPackageConfiguration(GobenchFramework.INSTANCE, "^TestName$", "import/path");
    assertFalse(producer.isConfigurationFromContext(runConfiguration, configurationContext));

    runConfiguration = createPackageConfiguration(GotestFramework.INSTANCE, "^TestName$", "import/path/other");
    assertFalse(producer.isConfigurationFromContext(runConfiguration, configurationContext));

    producer = new GobenchRunConfigurationProducer();
    runConfiguration = createFileConfiguration(GobenchFramework.INSTANCE, file.getVirtualFile().getPath());
    assertFalse(producer.isConfigurationFromContext(runConfiguration, configurationContext));
  }

  public void testSameConfigurationOnNonTestFunction() {
    PsiFile file = myFixture.addFileToProject("import/path/a_test.go", "package main; func SomeNonTestName() {<caret>}");
    myFixture.configureFromExistingVirtualFile(file.getVirtualFile());
    ConfigurationContext configurationContext = createConfigurationContext();
    GotestRunConfigurationProducer producer = new GotestRunConfigurationProducer();

    GoTestRunConfiguration runConfiguration = createFileConfiguration(GotestFramework.INSTANCE, file.getVirtualFile().getPath());
    assertTrue(producer.isConfigurationFromContext(runConfiguration, configurationContext));

    runConfiguration = createFileConfiguration(GotestFramework.INSTANCE, file.getVirtualFile().getPath() + "_vl");
    assertFalse(producer.isConfigurationFromContext(runConfiguration, configurationContext));

    runConfiguration = createFileConfiguration(GobenchFramework.INSTANCE, file.getVirtualFile().getPath());
    assertFalse(producer.isConfigurationFromContext(runConfiguration, configurationContext));
  }

  public void testSameConfigurationOnPackageClause() {
    PsiFile file = myFixture.addFileToProject("import/path/a_test.go", "packag<caret>e main; func SomeNonTestName() {}");
    myFixture.configureFromExistingVirtualFile(file.getVirtualFile());
    ConfigurationContext configurationContext = createConfigurationContext();
    GotestRunConfigurationProducer producer = new GotestRunConfigurationProducer();

    GoTestRunConfiguration runConfiguration = createPackageConfiguration(GotestFramework.INSTANCE, "", "import/path");
    assertTrue(producer.isConfigurationFromContext(runConfiguration, configurationContext));

    runConfiguration = createPackageConfiguration(GotestFramework.INSTANCE, "otherPattern", "import/path");
    assertFalse(producer.isConfigurationFromContext(runConfiguration, configurationContext));

    runConfiguration = createPackageConfiguration(GobenchFramework.INSTANCE, "", "import/path");
    assertFalse(producer.isConfigurationFromContext(runConfiguration, configurationContext));

    runConfiguration = createPackageConfiguration(GotestFramework.INSTANCE, "", "import/path/other");
    assertFalse(producer.isConfigurationFromContext(runConfiguration, configurationContext));
  }

  public void testSameConfigurationOnFile() {
    PsiFile file = myFixture.configureByText("a_test.go", "package main; <caret>\n\nfunc SomeNonTestName() {}");
    ConfigurationContext configurationContext = createConfigurationContext();
    GotestRunConfigurationProducer producer = new GotestRunConfigurationProducer();

    GoTestRunConfiguration runConfiguration = createFileConfiguration(GotestFramework.INSTANCE, file.getVirtualFile().getPath());
    assertTrue(producer.isConfigurationFromContext(runConfiguration, configurationContext));

    runConfiguration = createFileConfiguration(GotestFramework.INSTANCE, file.getVirtualFile().getPath() + "_vl");
    assertFalse(producer.isConfigurationFromContext(runConfiguration, configurationContext));

    runConfiguration = createFileConfiguration(GobenchFramework.INSTANCE, file.getVirtualFile().getPath());
    assertFalse(producer.isConfigurationFromContext(runConfiguration, configurationContext));
  }

  public void testSameConfigurationOnDirectory() {
    PsiFile file = myFixture.configureByText("a.go", "package main");
    PsiDirectory directory = file.getParent();
    assertNotNull(directory);
    ConfigurationContext configurationContext = new ConfigurationContext(directory);
    GotestRunConfigurationProducer producer = new GotestRunConfigurationProducer();

    GoTestRunConfiguration runConfiguration = createDirectoryConfiguration(GotestFramework.INSTANCE, directory.getVirtualFile().getPath(),
                                                                           directory.getVirtualFile().getPath());
    assertTrue(producer.isConfigurationFromContext(runConfiguration, configurationContext));

    runConfiguration = createDirectoryConfiguration(GotestFramework.INSTANCE, directory.getVirtualFile().getPath() + "_vl",
                                                    directory.getVirtualFile().getPath());
    assertFalse(producer.isConfigurationFromContext(runConfiguration, configurationContext));

    runConfiguration = createDirectoryConfiguration(GobenchFramework.INSTANCE, directory.getVirtualFile().getPath(),
                                                    directory.getVirtualFile().getPath());
    assertFalse(producer.isConfigurationFromContext(runConfiguration, configurationContext));

    runConfiguration = createDirectoryConfiguration(GotestFramework.INSTANCE, directory.getVirtualFile().getPath(),
                                                    directory.getVirtualFile().getPath() + "_vl");
    assertFalse(producer.isConfigurationFromContext(runConfiguration, configurationContext));
  }

  @NotNull
  private ConfigurationContext createConfigurationContext() {
    PsiElement at = myFixture.getFile().findElementAt(myFixture.getCaretOffset());
    assertNotNull(at);
    return new ConfigurationContext(at);
  }

  @NotNull
  private GoTestRunConfiguration createPackageConfiguration(GoTestFramework framework, String pattern, String importPath) {
    GoTestRunConfigurationType configurationType = GoTestRunConfigurationType.getInstance();
    GoTestRunConfiguration runConfiguration = new GoTestRunConfiguration(myFixture.getProject(), "name", configurationType);
    runConfiguration.setPattern(pattern);
    runConfiguration.setPackage(importPath);
    runConfiguration.setKind(GoTestRunConfiguration.Kind.PACKAGE);
    runConfiguration.setTestFramework(framework);
    return runConfiguration;
  }

  @NotNull
  private GoTestRunConfiguration createFileConfiguration(GoTestFramework framework, String path) {
    GoTestRunConfigurationType configurationType = GoTestRunConfigurationType.getInstance();
    GoTestRunConfiguration runConfiguration = new GoTestRunConfiguration(myFixture.getProject(), "name", configurationType);
    runConfiguration.setFilePath(path);
    runConfiguration.setKind(GoTestRunConfiguration.Kind.FILE);
    runConfiguration.setTestFramework(framework);
    return runConfiguration;
  }

  @NotNull
  private GoTestRunConfiguration createDirectoryConfiguration(GoTestFramework framework, String path, String workingDirectoryPath) {
    GoTestRunConfigurationType configurationType = GoTestRunConfigurationType.getInstance();
    GoTestRunConfiguration runConfiguration = new GoTestRunConfiguration(myFixture.getProject(), "name", configurationType);
    runConfiguration.setDirectoryPath(path);
    runConfiguration.setWorkingDirectory(workingDirectoryPath);
    runConfiguration.setKind(GoTestRunConfiguration.Kind.DIRECTORY);
    runConfiguration.setTestFramework(framework);
    return runConfiguration;
  }

  @Override
  protected String getBasePath() {
    return "testing/producer";
  }
}
