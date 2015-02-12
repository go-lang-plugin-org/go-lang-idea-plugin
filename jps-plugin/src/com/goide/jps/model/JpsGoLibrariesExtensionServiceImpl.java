package com.goide.jps.model;

import com.goide.GoEnvironmentUtil;
import com.goide.GoLibrariesState;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jps.model.JpsElementChildRole;
import org.jetbrains.jps.model.JpsGlobal;
import org.jetbrains.jps.model.JpsProject;
import org.jetbrains.jps.model.JpsSimpleElement;
import org.jetbrains.jps.model.ex.JpsElementChildRoleBase;
import org.jetbrains.jps.model.module.JpsTypedModule;

import java.io.File;
import java.util.Collection;

public class JpsGoLibrariesExtensionServiceImpl extends JpsGoLibrariesExtensionService {
  private static final JpsElementChildRole<JpsGoLibraries> LIBRARIES_ROLE = JpsElementChildRoleBase.create("go.libraries.role");

  @Override
  public void setModuleLibrariesState(@NotNull JpsGoModuleProperties properties, @Nullable GoLibrariesState state) {
    properties.setLibrariesState(state);
  }

  @NotNull
  @Override
  public GoLibrariesState getModuleLibrariesState(@NotNull JpsSimpleElement<JpsGoModuleProperties> properties) {
    return properties.getData().getLibrariesState();
  }

  @Override
  public void setProjectLibrariesState(@NotNull JpsProject project, @Nullable GoLibrariesState state) {
    project.getContainer().setChild(LIBRARIES_ROLE, new JpsGoLibraries(state));
  }

  @NotNull
  @Override
  public GoLibrariesState getProjectLibrariesState(@NotNull JpsProject project) {
    final JpsGoLibraries child = project.getContainer().getChild(LIBRARIES_ROLE);
    return child != null ? child.getState() : new GoLibrariesState();
  }

  @Override
  public void setApplicationLibrariesState(@NotNull JpsGlobal global, @Nullable GoLibrariesState state) {
    global.getContainer().setChild(LIBRARIES_ROLE, new JpsGoLibraries(state));
  }

  @NotNull
  @Override
  public GoLibrariesState getApplicationLibrariesState(@NotNull JpsGlobal global) {
    final JpsGoLibraries child = global.getContainer().getChild(LIBRARIES_ROLE);
    return child != null ? child.getState() : new GoLibrariesState();
  }

  @NotNull
  @Override
  public String retrieveGoPath(@NotNull JpsTypedModule<JpsSimpleElement<JpsGoModuleProperties>> module) {
    Collection<String> parts = ContainerUtil.newLinkedHashSet();
    ContainerUtil.addIfNotNull(parts, GoEnvironmentUtil.retrieveGoPathFromEnvironment());
    ContainerUtil.addAllNotNull(parts, getModuleLibrariesState(module.getProperties()).getPaths());
    ContainerUtil.addAllNotNull(parts, getProjectLibrariesState(module.getProject()).getPaths());
    ContainerUtil.addAllNotNull(parts, getApplicationLibrariesState(module.getProject().getModel().getGlobal()).getPaths());
    return StringUtil.join(parts, File.pathSeparator);
  }
}
