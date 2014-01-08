package ro.redeul.google.go.template;

import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.module.WebModuleBuilder;
import com.intellij.openapi.module.WebModuleType;
import com.intellij.platform.ProjectTemplate;
import com.intellij.platform.ProjectTemplatesFactory;
import org.jetbrains.annotations.NotNull;

public class GoTemplatesFactory extends ProjectTemplatesFactory {

  @NotNull
  @Override
  public String[] getGroups() {
      return new String[]{WebModuleBuilder.GROUP_NAME};
  }

  @NotNull
  @Override
  public ProjectTemplate[] createTemplates(String group, WizardContext context) {

      // Hack to support IDEA IC and non-IDEA
      try {
          WebModuleType module = WebModuleType.getInstance();
      } catch (Exception ignored) {
          return ProjectTemplate.EMPTY_ARRAY;
      }

      return new ProjectTemplate[]{
              new GoApplicationGenerator()
      };
  }
}
