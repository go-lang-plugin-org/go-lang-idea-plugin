package com.goide.jps.model;

import com.goide.GoConstants;
import com.goide.GoLibrariesState;
import com.intellij.util.xmlb.XmlSerializer;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.model.JpsProject;
import org.jetbrains.jps.model.serialization.JpsProjectExtensionSerializer;

public class JpsGoProjectLibrariesSerializer extends JpsProjectExtensionSerializer {
  public JpsGoProjectLibrariesSerializer() {
    super(GoConstants.GO_LIBRARIES_CONFIG_FILE, GoConstants.GO_LIBRARIES_SERVICE_NAME);
  }

  @Override
  public void loadExtension(@NotNull JpsProject project, @NotNull Element componentTag) {
    GoLibrariesState librariesState = XmlSerializer.deserialize(componentTag, GoLibrariesState.class);
    JpsGoLibrariesExtensionService.getInstance().setProjectLibrariesState(project, librariesState);
  }

  public void loadExtensionWithDefaultSettings(@NotNull JpsProject project) {
    JpsGoLibrariesExtensionService.getInstance().setProjectLibrariesState(project, new GoLibrariesState());
  }

  @Override
  public void saveExtension(@NotNull JpsProject project, @NotNull Element componentTag) {
    XmlSerializer.serializeInto(JpsGoLibrariesExtensionService.getInstance().getProjectLibrariesState(project), componentTag);
  }
}
