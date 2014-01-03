package com.goide.jps.model;

import com.intellij.util.xmlb.XmlSerializer;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.model.JpsProject;
import org.jetbrains.jps.model.serialization.JpsProjectExtensionSerializer;

public class JpsGoCompilerOptionsSerializer extends JpsProjectExtensionSerializer {
  public static final String COMPILER_OPTIONS_COMPONENT_NAME = "GoCompilerOptions";

  public JpsGoCompilerOptionsSerializer() {
    super("compiler.xml", COMPILER_OPTIONS_COMPONENT_NAME);
  }

  @Override
  public void loadExtension(@NotNull JpsProject project, @NotNull Element componentTag) {
    JpsGoCompilerOptionsExtension extension = JpsGoCompilerOptionsExtension.getOrCreateExtension(project);
    GoCompilerOptions options = XmlSerializer.deserialize(componentTag, GoCompilerOptions.class);
    if (options != null) {
      extension.setOptions(options);
    }
  }

  @Override
  public void saveExtension(@NotNull JpsProject project, @NotNull Element componentTag) {
  }
}
