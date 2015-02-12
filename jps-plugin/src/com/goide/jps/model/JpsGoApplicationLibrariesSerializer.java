package com.goide.jps.model;

import com.goide.GoConstants;
import com.goide.GoLibrariesState;
import com.intellij.util.xmlb.XmlSerializer;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.model.JpsGlobal;
import org.jetbrains.jps.model.serialization.JpsGlobalExtensionSerializer;

public class JpsGoApplicationLibrariesSerializer extends JpsGlobalExtensionSerializer {
  public JpsGoApplicationLibrariesSerializer() {
    super(GoConstants.GO_LIBRARIES_CONFIG_FILE, GoConstants.GO_LIBRARIES_SERVICE_NAME);
  }

  @Override
  public void loadExtension(@NotNull JpsGlobal global, @NotNull Element componentTag) {
    GoLibrariesState librariesState = XmlSerializer.deserialize(componentTag, GoLibrariesState.class);
    JpsGoLibrariesExtensionService.getInstance().setApplicationLibrariesState(global, librariesState);
  }

  @Override
  public void saveExtension(@NotNull JpsGlobal global, @NotNull Element componentTag) {
    XmlSerializer.serializeInto(JpsGoLibrariesExtensionService.getInstance().getApplicationLibrariesState(global), componentTag);
  }
}
