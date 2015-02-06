package com.goide.jps.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.model.JpsDummyElement;
import org.jetbrains.jps.model.JpsElementFactory;
import org.jetbrains.jps.model.JpsElementTypeWithDefaultProperties;
import org.jetbrains.jps.model.library.sdk.JpsSdkType;

public class JpsGoSdkType extends JpsSdkType<JpsDummyElement> implements JpsElementTypeWithDefaultProperties<JpsDummyElement> {
  public static final JpsGoSdkType INSTANCE = new JpsGoSdkType();

  @NotNull
  @Override
  public JpsDummyElement createDefaultProperties() {
    return JpsElementFactory.getInstance().createDummyElement();
  }
}
