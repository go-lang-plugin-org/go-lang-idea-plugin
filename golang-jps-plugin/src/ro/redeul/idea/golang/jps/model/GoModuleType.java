/*
* Copyright 2012 Midokura Europe SARL
*/
package ro.redeul.idea.golang.jps.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.model.JpsDummyElement;
import org.jetbrains.jps.model.JpsElementFactory;
import org.jetbrains.jps.model.JpsElementTypeWithDefaultProperties;
import org.jetbrains.jps.model.module.JpsModuleType;

public class GoModuleType extends JpsModuleType<JpsDummyElement>
    implements JpsElementTypeWithDefaultProperties<JpsDummyElement> {

    public static final GoModuleType INSTANCE = new GoModuleType();

    @NotNull
    @Override
    public JpsDummyElement createDefaultProperties() {
	return JpsElementFactory.getInstance().createDummyElement();
    }
}
