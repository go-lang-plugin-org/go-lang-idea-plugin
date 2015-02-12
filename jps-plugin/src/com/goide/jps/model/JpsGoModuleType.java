package com.goide.jps.model;

import com.goide.GoConstants;
import com.goide.GoLibrariesState;
import com.intellij.util.xmlb.XmlSerializer;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jps.model.JpsElementFactory;
import org.jetbrains.jps.model.JpsElementTypeWithDefaultProperties;
import org.jetbrains.jps.model.JpsSimpleElement;
import org.jetbrains.jps.model.ex.JpsElementTypeBase;
import org.jetbrains.jps.model.module.JpsModuleType;
import org.jetbrains.jps.model.serialization.module.JpsModulePropertiesSerializer;

public class JpsGoModuleType extends JpsElementTypeBase<JpsSimpleElement<JpsGoModuleProperties>>
  implements JpsModuleType<JpsSimpleElement<JpsGoModuleProperties>>, 
             JpsElementTypeWithDefaultProperties<JpsSimpleElement<JpsGoModuleProperties>> {
  
  public static final JpsGoModuleType INSTANCE = new JpsGoModuleType();

  @NotNull
  public static JpsModulePropertiesSerializer<JpsSimpleElement<JpsGoModuleProperties>> createModuleLibrariesSerializer() {
    return new JpsModulePropertiesSerializer<JpsSimpleElement<JpsGoModuleProperties>>(INSTANCE, GoConstants.MODULE_TYPE_ID,
                                                                                      GoConstants.GO_LIBRARIES_SERVICE_NAME) {
      @NotNull
      @Override
      public JpsSimpleElement<JpsGoModuleProperties> loadProperties(@Nullable Element componentElement) {
        JpsSimpleElement<JpsGoModuleProperties> result = INSTANCE.createDefaultProperties();
        if (componentElement != null) {
          GoLibrariesState librariesState = XmlSerializer.deserialize(componentElement, GoLibrariesState.class);
          JpsGoLibrariesExtensionService.getInstance().setModuleLibrariesState(result.getData(), librariesState);
        }
        return result;
      }

      @Override
      public void saveProperties(@NotNull JpsSimpleElement<JpsGoModuleProperties> properties, @NotNull Element componentElement) {
        XmlSerializer.serializeInto(JpsGoLibrariesExtensionService.getInstance().getModuleLibrariesState(properties), componentElement);
      }
    };
  }

  private JpsGoModuleType() {}

  @NotNull
  @Override
  public JpsSimpleElement<JpsGoModuleProperties> createDefaultProperties() {
    return JpsElementFactory.getInstance().createSimpleElement(new JpsGoModuleProperties());
  }
}
