package com.goide.jps.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.model.JpsElementChildRole;
import org.jetbrains.jps.model.JpsProject;
import org.jetbrains.jps.model.ex.JpsCompositeElementBase;
import org.jetbrains.jps.model.ex.JpsElementChildRoleBase;

public class JpsGoCompilerOptionsExtension extends JpsCompositeElementBase<JpsGoCompilerOptionsExtension> {
  public static final JpsElementChildRole<JpsGoCompilerOptionsExtension> ROLE = JpsElementChildRoleBase.create("GoCompilerOptions");

  private GoCompilerOptions myOptions;

  public JpsGoCompilerOptionsExtension(GoCompilerOptions options) {
    myOptions = options;
  }

  @NotNull
  @Override
  public JpsGoCompilerOptionsExtension createCopy() {
    return new JpsGoCompilerOptionsExtension(new GoCompilerOptions(myOptions));
  }

  public GoCompilerOptions getOptions() {
    return myOptions;
  }

  public void setOptions(GoCompilerOptions options) {
    myOptions = options;
  }

  @NotNull
  public static JpsGoCompilerOptionsExtension getOrCreateExtension(@NotNull JpsProject project) {
    JpsGoCompilerOptionsExtension extension = project.getContainer().getChild(ROLE);
    if (extension == null) {
      extension = project.getContainer().setChild(ROLE, new JpsGoCompilerOptionsExtension(new GoCompilerOptions()));
    }
    return extension;
  }
}
