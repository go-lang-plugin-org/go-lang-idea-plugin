package com.goide.jps.model;

import org.jetbrains.jps.model.JpsDummyElement;
import org.jetbrains.jps.model.ex.JpsElementTypeWithDummyProperties;
import org.jetbrains.jps.model.module.JpsModuleType;

public class JpsGoModuleType extends JpsElementTypeWithDummyProperties implements JpsModuleType<JpsDummyElement> {
  public static final JpsGoModuleType INSTANCE = new JpsGoModuleType();
  private JpsGoModuleType() {}
}
