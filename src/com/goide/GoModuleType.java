package com.goide;

import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.ProjectJdkForModuleStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.module.ModuleTypeManager;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class GoModuleType extends ModuleType<GoModuleBuilder> {
  public static final String MODULE_TYPE_ID = "GO_MODULE";

  public GoModuleType() {
    super(MODULE_TYPE_ID);
  }

  @NotNull
  public static GoModuleType getInstance() {
    return (GoModuleType)ModuleTypeManager.getInstance().findByID(MODULE_TYPE_ID);
  }

  @NotNull
  @Override
  public GoModuleBuilder createModuleBuilder() {
    return new GoModuleBuilder();
  }

  @NotNull
  @Override
  public String getName() {
    return "Go Module";
  }

  @NotNull
  @Override
  public String getDescription() {
    return "Go modules are used for developing <b>Go</b> applications.";
  }

  @Nullable
  @Override
  public Icon getBigIcon() {
    return GoIcons.MODULE_ICON;
  }

  @Nullable
  @Override
  public Icon getNodeIcon(boolean isOpened) {
    return GoIcons.ICON;
  }

  @NotNull
  @Override
  public ModuleWizardStep[] createWizardSteps(@NotNull WizardContext wizardContext,
                                              @NotNull final GoModuleBuilder moduleBuilder,
                                              @NotNull ModulesProvider modulesProvider) {
    return new ModuleWizardStep[]{new ProjectJdkForModuleStep(wizardContext, GoSdkType.getInstance()) {
      public void updateDataModel() {
        super.updateDataModel();
        moduleBuilder.setModuleJdk(getJdk());
      }
    }};
  }
}
