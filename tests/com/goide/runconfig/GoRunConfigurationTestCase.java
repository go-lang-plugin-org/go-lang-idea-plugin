package com.goide.runconfig;

import com.goide.GoCodeInsightFixtureTestCase;
import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.actions.ConfigurationFromContext;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.util.JDOMUtil;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.testFramework.LightProjectDescriptor;
import org.jdom.Element;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class GoRunConfigurationTestCase extends GoCodeInsightFixtureTestCase {
  protected void doTestProducedConfigurations() {
    VirtualFile file = myFixture.copyFileToProject(getTestName(true) + ".go", "import/path/" + getTestName(true) + ".go");
    myFixture.configureFromExistingVirtualFile(file);
    doTestProducedConfigurations(myFixture.getFile().findElementAt(myFixture.getCaretOffset()));
  }

  protected void doTestProducedConfigurations(@Nullable PsiElement context) {
    assertNotNull(context);
    ConfigurationContext configurationContext = new ConfigurationContext(context);
    List<ConfigurationFromContext> configurationAndSettings = configurationContext.getConfigurationsFromContext();
    Element configurationsElement = new Element("configurations");
    if (configurationAndSettings != null) {
      for (ConfigurationFromContext setting : configurationAndSettings) {
        try {
          RunConfiguration configuration = setting.getConfiguration();
          Element configurationElement = new Element("configurations");
          configurationElement.setAttribute("name", configuration.getName());
          configurationElement.setAttribute("class", configuration.getClass().getSimpleName());
          configuration.writeExternal(configurationElement);
          configurationsElement.addContent(configurationElement);
        }
        catch (WriteExternalException e) {
          throw new RuntimeException(e);
        }
      }
    }
    assertSameLinesWithFile(getTestDataPath() + "/" + getTestName(true) + ".xml", JDOMUtil.writeElement(configurationsElement));
  }

  @Override
  public void setUp() throws Exception {
    super.setUp();
    setUpProjectSdk();
  }

  @Override
  protected LightProjectDescriptor getProjectDescriptor() {
    return createMockProjectDescriptor();
  }
}
