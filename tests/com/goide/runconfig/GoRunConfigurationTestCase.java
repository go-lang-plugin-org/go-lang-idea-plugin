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

package com.goide.runconfig;

import com.goide.GoCodeInsightFixtureTestCase;
import com.goide.SdkAware;
import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.actions.ConfigurationFromContext;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.util.JDOMUtil;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import org.jdom.Element;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SdkAware
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
}
