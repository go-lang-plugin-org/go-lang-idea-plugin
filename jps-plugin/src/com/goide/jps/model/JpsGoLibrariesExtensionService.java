package com.goide.jps.model;

import com.goide.GoLibrariesState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jps.model.JpsGlobal;
import org.jetbrains.jps.model.JpsProject;
import org.jetbrains.jps.model.JpsSimpleElement;
import org.jetbrains.jps.model.module.JpsTypedModule;
import org.jetbrains.jps.service.JpsServiceManager;

public abstract class JpsGoLibrariesExtensionService {
  public static JpsGoLibrariesExtensionService getInstance() {
    return JpsServiceManager.getInstance().getService(JpsGoLibrariesExtensionService.class);
  }

  public abstract void setModuleLibrariesState(@NotNull JpsGoModuleProperties properties, @Nullable GoLibrariesState state);
  
  @NotNull
  public abstract GoLibrariesState getModuleLibrariesState(@NotNull JpsSimpleElement<JpsGoModuleProperties> properties);

  public abstract void setProjectLibrariesState(@NotNull JpsProject project, @Nullable GoLibrariesState state);

  @NotNull
  public abstract GoLibrariesState getProjectLibrariesState(@NotNull JpsProject project);

  public abstract void setApplicationLibrariesState(@NotNull JpsGlobal global, @Nullable GoLibrariesState state);

  @NotNull
  public abstract GoLibrariesState getApplicationLibrariesState(@NotNull JpsGlobal global);

  @NotNull
  public abstract String retrieveGoPath(@NotNull JpsTypedModule<JpsSimpleElement<JpsGoModuleProperties>> module);
}
