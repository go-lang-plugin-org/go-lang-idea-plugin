package ro.redeul.google.go.template;

import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.module.WebModuleBuilder;
import com.intellij.platform.ProjectTemplate;
import com.intellij.platform.ProjectTemplatesFactory;
import com.intellij.util.PlatformUtils;
import org.jetbrains.annotations.NotNull;

public class GoTemplatesFactory extends ProjectTemplatesFactory {

  @NotNull
  @Override
  public String[] getGroups() {
      if (PlatformUtils.isIntelliJ()) {
          return new String[]{};
      }

      return new String[]{WebModuleBuilder.GROUP_NAME};
  }

  @NotNull
  @Override
  public ProjectTemplate[] createTemplates(String group, WizardContext context) {
      if (PlatformUtils.isIntelliJ()) {
          return ProjectTemplate.EMPTY_ARRAY;
      }

      return new ProjectTemplate[]{
              new GoApplicationGenerator()
      };
  }
}
