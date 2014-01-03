package com.goide.jps.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jps.model.JpsElementChildRole;
import org.jetbrains.jps.model.ex.JpsCompositeElementBase;
import org.jetbrains.jps.model.ex.JpsElementChildRoleBase;
import org.jetbrains.jps.model.module.JpsModule;

import java.util.Collections;
import java.util.List;

public class JpsGoModuleExtension extends JpsCompositeElementBase<JpsGoModuleExtension> {
  public static final JpsElementChildRole<JpsGoModuleExtension> ROLE = JpsElementChildRoleBase.create("Go");

  private final GoModuleExtensionProperties myProperties;

  public JpsGoModuleExtension(GoModuleExtensionProperties properties) {
    myProperties = properties;
  }

  public JpsGoModuleExtension(@NotNull JpsGoModuleExtension moduleExtension) {
    myProperties = new GoModuleExtensionProperties(moduleExtension.myProperties);
  }

  @NotNull
  @Override
  public JpsGoModuleExtension createCopy() {
    return new JpsGoModuleExtension(this);
  }

  public GoModuleExtensionProperties getProperties() {
    return myProperties;
  }

  public List<String> getIncludePaths() {
    return Collections.unmodifiableList(myProperties.myIncludePaths);
  }

  public List<String> getParseTransforms() {
    return Collections.unmodifiableList(myProperties.myParseTransforms);
  }

  @Nullable
  public static JpsGoModuleExtension getExtension(@Nullable JpsModule module) {
    return module != null ? module.getContainer().getChild(ROLE) : null;
  }
}
