package com.goide.configuration;

import com.goide.sdk.GoSdkService;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurableProvider;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

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
    if (sdkConfigurable != null) {
      return new GoCompositeConfigurable(sdkConfigurable, librariesConfigurable);
    }
    else {
      return new GoCompositeConfigurable(librariesConfigurable);
    }
  }

  private static class GoCompositeConfigurable extends SearchableConfigurable.Parent.Abstract {

    private Configurable[] myConfigurables;

    public GoCompositeConfigurable(Configurable... configurables) {
      myConfigurables = configurables;
    }

    @Override
    protected Configurable[] buildConfigurables() {
      return myConfigurables.length == 1 ? new Configurable[0] : myConfigurables;
    }

    @Override
    public boolean isModified() {
      return myConfigurables.length == 1 ? myConfigurables[0].isModified() : super.isModified();
    }

    @Override
    public void apply() throws ConfigurationException {
      if (myConfigurables.length == 1) {
        myConfigurables[0].apply();
      }
      else {
        super.apply();
      }
    }

    @Override
    public void reset() {
      if (myConfigurables.length == 1) {
        myConfigurables[0].reset();
      }
      else {
        super.reset();
      }
    }

    @Override
    public JComponent createComponent() {
      return myConfigurables.length == 1 ? myConfigurables[0].createComponent() : super.createComponent();
    }

    @Override
    public boolean hasOwnContent() {
      return myConfigurables.length == 1;
    }

    @NotNull
    @Override
    public String getId() {
      return "go";
    }

    @Nls
    @Override
    public String getDisplayName() {
      return myConfigurables.length == 1 ? myConfigurables[0].getDisplayName() : "Go";
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
