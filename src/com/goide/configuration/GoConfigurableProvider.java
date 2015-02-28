package com.goide.configuration;

import com.goide.sdk.GoSdkService;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurableProvider;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GoConfigurableProvider extends ConfigurableProvider {
  @NotNull private final Project myProject;

  public GoConfigurableProvider(@NotNull Project project) {
    myProject = project;
  }

  @Nullable
  @Override
  public Configurable createConfigurable() {
    Configurable librariesConfigurable = new GoLibrariesConfigurableProvider(myProject).createConfigurable();
    Configurable sdkConfigurable = GoSdkService.getInstance(myProject).createSdkConfigurable();
    return sdkConfigurable != null ? new GoCompositeConfigurable(sdkConfigurable, librariesConfigurable) : librariesConfigurable;
  }

  private static class GoCompositeConfigurable extends SearchableConfigurable.Parent.Abstract {

    private Configurable[] myConfigurables;

    public GoCompositeConfigurable(Configurable... configurables) {
      myConfigurables = configurables;
    }

    @Override
    protected Configurable[] buildConfigurables() {
      return myConfigurables;
    }

    @NotNull
    @Override
    public String getId() {
      return "go";
    }

    @Nls
    @Override
    public String getDisplayName() {
      return "Go";
    }

    @Nullable
    @Override
    public String getHelpTopic() {
      return null;
    }

    @Override
    public void disposeUIResources() {
      super.disposeUIResources();
      myConfigurables = null;
    }
  }
}
